package com

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.WebDriverRunner
import com.config.Config
import com.handlers.AbstractDriverHandler
import com.handlers.andr.LocalAppiumDriverHandler
import com.handlers.desk.DockerDriverHandler
import com.handlers.desk.LocalDriverHandler
import io.qameta.allure.Allure
import io.qameta.allure.Attachment
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.lang.invoke.MethodHandles.lookup
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*


class UIExtension : /*ParameterResolver,*/ BeforeEachCallback, AfterEachCallback, ExtensionContext.Store.CloseableResource {
    private val log = LoggerFactory.getLogger(lookup().lookupClass())
    private var started = false
    private lateinit var driverHandler: AbstractDriverHandler

    @Synchronized
    override fun beforeEach(context: ExtensionContext) {
        // next line to com.UIExtension.close could work
        if (!started) {
            context.root.getStore(GLOBAL).put("unique name", this); started = true
        }

        val annotation =
                context.testMethod.get().annotations.find { it is ConcreteDriver } as? ConcreteDriver
        val appiumTest =
                context.testMethod.get().annotations.any { it is AppiumDriver }

        val (driverType, driverVersion) =
                if (annotation != null) {
                    Pair(annotation.type, annotation.version)
                } else {
                    Pair(Config.driverType, Config.driverVersion)
                }
        Configuration.timeout = 5000
        driverHandler =
                if (Config.localTest) {
                    if (appiumTest)
                        LocalAppiumDriverHandler()
                    else
                        LocalDriverHandler(driverType, driverVersion)
                } else {
                    if (appiumTest)
                        TODO("Implement Docker android handler")
                    else
                        DockerDriverHandler(driverType, driverVersion)
                }

        driverHandler.prepareDriver()
    }

    override fun afterEach(context: ExtensionContext) {
        val sessionID = (WebDriverRunner.getWebDriver() as RemoteWebDriver).sessionId.toString()
        val testFailed = context.executionException.isPresent
        if (testFailed) {
            screen()
        }
        driverHandler.close()
        if (!Config.localTest && Config.enableVideo) {
            log.info("Video processing")
            if (testFailed) {
                log.info("Test was failed. Attaching video.")
                attachVideo(sessionID)
            }
            log.info("Delete video on docker host machine.")
            deleteVideoFile(sessionID)
        }
    }

    @Attachment(type = "image/png")
    fun screen(): ByteArray {
        val screenshots: String = Selenide.screenshot("screen" + Random().nextInt())!!
        log.info("Screen: {}", screenshots)
        return FileUtils.readFileToByteArray(File(screenshots))
    }

    private fun deleteVideoFile(sessionId: String) {
        val connection = waitVideoFileCreated(sessionId) as HttpURLConnection
        connection.requestMethod = "DELETE"
        connection.responseCode
    }

    private fun waitVideoFileCreated(sessionId: String): URLConnection? {
        val url = URL("http://${Config.dockerAddress}:${Config.dockerPort}/video/$sessionId.mp4")
        val startTime = System.currentTimeMillis()
        var stream: URLConnection? = null
        while (stream == null || startTime + 2 * 60 * 1000 < System.currentTimeMillis()) {
            try {
                url.openStream()
                stream = url.openConnection()
            } catch (_: FileNotFoundException) {
            }
        }
        return stream
    }

    private fun attachVideo(sessionID: String) {
        val connection = waitVideoFileCreated(sessionID)
        if (connection == null) {
            System.err.println("Can't obtain video."); return
        }

        Allure.addAttachment("Video", "video/mp4",
                connection.getInputStream(), "mp4")
    }

    override fun close() {
        driverHandler.closeEnvironment()
    }
}
package com.handlers.andr

import com.config.Config
import com.handlers.AbstractDriverSetup
import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.service.local.flags.AndroidServerFlag
import io.appium.java_client.service.local.flags.GeneralServerFlag
import io.github.bonigarcia.wdm.WebDriverManager
import java.io.File
import java.net.URL

object LocalAppiumDriverSetup : AbstractDriverSetup() {
    private val service: AppiumDriverLocalService

    init {
        val ver = Config.mobileChromeVersion
        WebDriverManager.chromedriver().driverVersion(ver).setup()
        log.info("Chrome driver {} have been setup.", ver)
        val chromeBinaryPath = WebDriverManager.chromedriver().binaryPath
        log.info("start server")
        val logsFolder = File(System.getProperty("user.dir"), "logs")
        service = AppiumServiceBuilder()
                .apply {
                    withArgument(AndroidServerFlag.CHROME_DRIVER_EXECUTABLE, chromeBinaryPath)
                    withArgument(GeneralServerFlag.LOG_LEVEL, "warn")
                }
                .withLogFile(File(logsFolder, "appium.log"))
                .build()
    }

    override fun action() {
        service.start()
    }

    override fun closeEnvironment() {
        service.stop()
    }

    fun getAppiumUrl(): URL {
        return service.url
    }
}
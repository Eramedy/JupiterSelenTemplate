package com.config

import org.apache.commons.configuration2.CompositeConfiguration
import org.apache.commons.configuration2.SystemConfiguration
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles


object Config {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    val config = CompositeConfiguration()

    init {
        config.addConfiguration(SystemConfiguration())

        val propFileName = "app.properties"
        val resourceAsStream = Config::class.java.classLoader.getResource(propFileName)
        if (resourceAsStream != null) config.addConfiguration(Configurations().properties(resourceAsStream))
    }

    val dockSocket: String =
            config.get(String::class.java, "docker.socket", "/var/run/docker.sock")

    val enableVideo: Boolean =
            config.get(Boolean::class.java, "docker.video.record", true)

    val localTest: Boolean =
            config.get(Boolean::class.java, "execute.local", false)

    val dockerHubURL: String =
            config.get(String::class.java, "docker.hub.url", "https://hub.docker.com/")

    /**
     * 0 - nothing;
     * 1 - kill;
     * 2 - rm
     * */
    val dockerAfterTest: Int =
            config.get(Int::class.java, "docker.selenoid.container.after.test", 2)

    val selenoidBrowsersLimit: Int =
            config.get(Int::class.java, "docker.browsers.limit", 10)

    val driverType: BrowserType =
            config.get(BrowserType::class.java, "driver.type", BrowserType.CHROME)

    val driverVersion: String =
            config.get(String::class.java, "driver.version", "default")

    val selenoidImage: String =
            config.get(String::class.java, "driver.selenoid.image",
                    "aerokube/selenoid:latest-release")

    val selenoidVideoImage: String =
            config.get(String::class.java, "driver.selenoid.video.image",
                    "selenoid/video-recorder:latest-release")

    val dockerAddress: String =
            config.get(String::class.java, "docker.sel.address", "localhost")

    val selenoidUIImage: String =
            config.get(String::class.java, "driver.selenoid.ui.image",
                    "aerokube/selenoid-ui:latest-release")

    val selenoidUIPort: Int =
            config.get(Int::class.java, "docker.sel.UI.port", 8956)

    val dockerPort: Int =
            config.get(Int::class.java, "docker.sel.port", 4444)

    val mobileChromeVersion: String =
            config.get(String::class.java, "driver.mobile.version", "84.0.4147.30")
}
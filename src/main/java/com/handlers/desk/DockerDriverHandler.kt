package com.handlers.desk

import com.SelenoidHelper
import com.codeborne.selenide.SelenideConfig
import com.config.BrowserType
import com.config.Config
import com.dockerAPI.browserJsonModel.BrowserConf
import com.dockerAPI.browserJsonModel.BrowserConfBuilder
import com.handlers.AbstractDriverHandler
import org.openqa.selenium.remote.DesiredCapabilities

class DockerDriverHandler(override val type: BrowserType, version: String) : AbstractDriverHandler() {
    override val driverSetup = DockerDriverSetup
    override val version: String

    init {
        this.version = if (version == "default") getDefaultVersion(BrowserConfBuilder.instance(), type) else version
    }

    private fun getDefaultVersion(conf: BrowserConf, browserType: BrowserType): String {
        val get = conf.get(browserType)
                ?: throw RuntimeException("$browserType's versions is empty.")

        return get.default
    }

    private fun prepareDriverImage() {
        val conf = BrowserConfBuilder.instance()

        val image = resolveImageName(conf, type, version)
        SelenoidHelper.pullContainer(image)
    }

    override fun getSelenideConfig(): SelenideConfig {
        prepareDriverImage()
        val config = SelenideConfig()
        config.startMaximized(true)
        val capabilities = DesiredCapabilities()
        config.browserVersion(version)
        capabilities.setCapability("enableVNC", true)
        capabilities.setCapability("enableVideo", Config.enableVideo)
        config.browserCapabilities(capabilities)
        config.browser(type.name.toLowerCase())
        config.remote("http://${Config.dockerAddress}:${Config.dockerPort}/wd/hub")
        log.info("Docker address: {}, port: {}, record video: {}", Config.dockerAddress, Config.dockerPort, Config.enableVideo)
        return config
    }

    private fun resolveImageName(conf: BrowserConf, browserType: BrowserType, version: String): String {
        val get = conf.get(browserType) ?: throw RuntimeException("$browserType's versions is empty.")
        val browserVersion = get.versions[version] ?: throw RuntimeException("No such version: $version")
        return browserVersion.image
    }

    override fun closeEnvironment() {
        DockerDriverSetup.closeEnvironment()
    }
}
package com.handlers.desk

import com.codeborne.selenide.SelenideConfig
import com.config.BrowserType
import com.handlers.AbstractDriverHandler
import io.github.bonigarcia.wdm.WebDriverManager

class LocalDriverHandler(override val type: BrowserType, override val version: String) : AbstractDriverHandler() {
    override val driverSetup = NothingSetup

    private fun prepareDriver() {
        val driverManager = WebDriverManager.getInstance(type.clazz)
        if (version == "default") driverManager.setup()
        else driverManager.driverVersion(version).setup()
    }

    override fun getSelenideConfig(): SelenideConfig {
        prepareDriver()

        val config = SelenideConfig()
        config.startMaximized(true)
        config.browser(type.name.toLowerCase())

        return config
    }

    override fun closeEnvironment() {}
}
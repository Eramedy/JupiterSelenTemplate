package com.handlers.desk

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.SelenideConfig
import com.config.BrowserType
import com.handlers.AbstractDriverHandler
import io.github.bonigarcia.wdm.WebDriverManager

class LocalDriverHandler(override val type: BrowserType, override val version: String) : AbstractDriverHandler() {
    override val driverSetup = NothingSetup

    override fun config() {
        Configuration.startMaximized = true
        Configuration.browser = type.name.toLowerCase()
        val driverManager = WebDriverManager.getInstance(type.clazz)

        if (version == "default") driverManager.setup()
        else driverManager.driverVersion(version).setup()
    }

    override fun closeEnvironment() {}
}
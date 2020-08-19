package com.handlers

import com.codeborne.selenide.SelenideConfig
import com.codeborne.selenide.SelenideDriver
import com.codeborne.selenide.WebDriverRunner
import com.config.BrowserType
import com.config.Config
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.SessionId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

abstract class AbstractDriverHandler {
    private var driver: SelenideDriver? = null
    val sessionID: SessionId
        get() {
            return (getDriver().webDriver as RemoteWebDriver).sessionId
        }

    private val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    abstract val type: BrowserType
    abstract val version: String
    abstract val driverSetup: AbstractDriverSetup

    abstract fun getSelenideConfig(): SelenideConfig
    fun createDriver() {
        driverSetup.setup()

        log.info("Configure driver: {} {}", type, version)
        val config = getSelenideConfig()
        config.timeout(Config.defaultTimeout)
        driver = SelenideDriver(config)

    }

    fun getDriver(): SelenideDriver {
        if (driver == null)
            throw IllegalStateException("Driver haven't been created. First invoke createDriver()")

        return driver as SelenideDriver
    }

    fun close() {
        WebDriverRunner.closeWebDriver()
    }

    abstract fun closeEnvironment()
}
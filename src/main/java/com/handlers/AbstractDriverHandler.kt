package com.handlers

import com.codeborne.selenide.WebDriverRunner
import com.config.BrowserType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

abstract class AbstractDriverHandler {
    private val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    abstract val type: BrowserType
    abstract val version: String
    abstract val driverSetup: AbstractDriverSetup

    abstract fun config()

    fun close() {
        WebDriverRunner.closeWebDriver()
    }

    abstract fun closeEnvironment()

    fun prepareDriver() {
        driverSetup.setup()

        log.info("Configure driver: {} {}", type, version)
        config()
    }
}
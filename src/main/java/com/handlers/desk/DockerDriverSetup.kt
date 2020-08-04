package com.handlers.desk

import com.SelenoidHelper
import com.config.Config
import com.dockerAPI.browserJsonModel.BrowserConf
import com.dockerAPI.browserJsonModel.BrowserConfBuilder
import com.handlers.AbstractDriverSetup

object DockerDriverSetup : AbstractDriverSetup() {
    private var environmentHasNotBeenStarted = true

    override fun action() {
        if (environmentHasNotBeenStarted) {
            log.info("Environment have not started yet. Initializing.")
            startEnv()
            environmentHasNotBeenStarted = false
        }
    }

    private fun startEnv() {
        val browserConfigs: BrowserConf = BrowserConfBuilder.instance()
        if (Config.enableVideo) SelenoidHelper.pullContainer(Config.selenoidVideoImage)

        SelenoidHelper.startSelenoidContainer(browserConfigs)
        SelenoidHelper.startSelenoidUIContainer()
    }

    override fun closeEnvironment() {
        SelenoidHelper.shutdown()
    }
}
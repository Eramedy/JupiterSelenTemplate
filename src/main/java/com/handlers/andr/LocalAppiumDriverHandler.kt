package com.handlers.andr

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.WebDriverProvider
import com.config.BrowserType
import com.handlers.AbstractDriverHandler
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidElement
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import java.net.URL

class LocalAppiumDriverHandler : AbstractDriverHandler() {
    override val type: BrowserType = BrowserType.CHROME
    override val version: String = "default"
    override val driverSetup = LocalAppiumDriverSetup

    override fun config() {
        Configuration.browser = AndroidDriverProvider::class.java.name
        val url = driverSetup.getAppiumUrl()
        Configuration.browserCapabilities.setCapability("appiumUrl", url)
    }

    override fun closeEnvironment() {
        driverSetup.closeEnvironment()
    }
}

private class AndroidDriverProvider : WebDriverProvider {
    override fun createDriver(desiredCapabilities: DesiredCapabilities): WebDriver {
        desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
        desiredCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator")
        desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UIAutomator2")
        desiredCapabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome")
        Configuration.startMaximized = false
        Configuration.browserSize = null

        val url: URL = desiredCapabilities.getCapability("appiumUrl") as URL
        return AndroidDriver<AndroidElement>(url, desiredCapabilities)
    }
}

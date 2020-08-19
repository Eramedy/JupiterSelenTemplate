package com.handlers.andr


import com.codeborne.selenide.SelenideConfig
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

    override fun getSelenideConfig(): SelenideConfig {
        val config = SelenideConfig()
        config.browser(AndroidDriverProvider::class.java.name)
        val url = driverSetup.getAppiumUrl()
        config.browserCapabilities().setCapability("appiumUrl", url)
        config.startMaximized(false)
        config.browserSize(null)

        return config
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

        val url: URL = desiredCapabilities.getCapability("appiumUrl") as URL
        return AndroidDriver<AndroidElement>(url, desiredCapabilities)
    }
}

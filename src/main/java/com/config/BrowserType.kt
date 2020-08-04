package com.config

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver

enum class BrowserType(val clazz: Class<out WebDriver>) {
    CHROME(ChromeDriver::class.java), FIREFOX(FirefoxDriver::class.java), EDGE(EdgeDriver::class.java)
}
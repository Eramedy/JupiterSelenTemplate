package com.util

import com.codeborne.selenide.SelenideDriver
import com.config.Config
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait


fun waitPageComplete(driver: SelenideDriver) {
    val wait = WebDriverWait(driver.webDriver, Config.defaultTimeout)
    val jsExecutor = driver.webDriver as JavascriptExecutor

    val pageStateComplete: ExpectedCondition<Boolean> = ExpectedCondition<Boolean> {
        jsExecutor.executeScript("return document.readyState")
                .toString() == "complete"
    }
    wait.until(pageStateComplete)
}
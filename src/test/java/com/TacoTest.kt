package com

import com.codeborne.selenide.Condition.text
import com.codeborne.selenide.Condition.value
import com.codeborne.selenide.SelenideDriver
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By

@ExtendWith(SelenideExtension::class)
class TacoTest {
    @Test
    fun uiTest(driver: SelenideDriver) {
        driver.open("https://localhost:8443")
        driver.findAll("a").find(text("design your masterpiece")).click()
        driver.find("#username").sendKeys("q")
        driver.find("#password").sendKeys("q")
        driver.`$$`("input").find(value("Login")).click()
        driver.findAll(By.name("ingredients")).shouldHaveSize(10)
    }
}
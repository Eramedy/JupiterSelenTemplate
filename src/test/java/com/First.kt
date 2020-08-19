package com

import com.codeborne.selenide.Condition.text
import com.codeborne.selenide.SelenideDriver
import com.util.waitPageComplete
import io.qameta.allure.Step
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(SelenideExtension::class)
class First {

    private lateinit var driver: SelenideDriver

    @Test
    fun uiTest(driver: SelenideDriver) {
        this.driver = driver
        open()
        step1()
    }

    @Step("Here we go")
    private fun open() {
        driver.open("https://www.phptravels.net/home")

        waitPageComplete(driver)
    }

    @Step("step1")
    private fun step1() {
        val element = driver.find(".dropdown-login")
        element.click()
        element.findAll(".dropdown-menu a").findBy(text("Login")).click()
        driver.find("[name='username']").sendKeys("user@phptravels.com")
        driver.find("[name='password']").sendKeys("demouser")
        driver.find("[type='submit']").click()
        driver.find(".container").shouldHave(text("Hi, Demo User"))
    }
}
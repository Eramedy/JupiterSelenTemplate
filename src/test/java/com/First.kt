package com

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.*
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.Selenide.*
import io.qameta.allure.Step
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By


@ExtendWith(UIExtension::class)
class First {

    @Test
    fun uiTest() {
        open()
        sleep(20000)
        step1()
    }

    @Step("step1")
    private fun step1() {
        val element = element(".dropdown-login")
        element.click()
        element.findAll(".dropdown-menu a").findBy(text("Login")).click()
        element("[name='username']").sendKeys("user@phptravels.com")
        element("[name='password']").sendKeys("demouser")
        element("[type='submit']").click()
        element(".container").shouldHave(text("Hi, Demo User"))
    }

    @Step("Here we go")
    private fun open() {
        open("https://www.phptravels.net/")
    }
}
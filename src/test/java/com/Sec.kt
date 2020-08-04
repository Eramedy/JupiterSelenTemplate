package com

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(UIExtension::class)
class Sec {
    @Test
    @AppiumDriver
    fun mobileExample() {
        open("http://the-internet.herokuapp.com")
        val wrappedElement = elements("li").findBy(Condition.text("Floating Menu"))
        wrappedElement.`$`("a").click()
        element("#menu").`$$`("li").shouldHaveSize(4)
    }
}
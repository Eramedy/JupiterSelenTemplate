package com

import com.config.BrowserType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ConcreteDriver(val type: BrowserType, val version: String = "default")
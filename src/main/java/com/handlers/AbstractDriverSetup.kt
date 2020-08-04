package com.handlers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles

/**Descendant mast be static object*/
abstract class AbstractDriverSetup {
    protected val log: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    private var alreadyPrepared: MutableList<String> = mutableListOf()

    abstract fun action()

    @Synchronized
    fun setup() {
        val driverInfo = this::class.java.name
        if (driverInfo !in alreadyPrepared) {
            log.info("{} are preparing environment", this::class.java.name)
            action()
            alreadyPrepared.add(driverInfo)
        }
    }

    abstract fun closeEnvironment()
}
package com.handlers.desk

import com.handlers.AbstractDriverSetup

object NothingSetup : AbstractDriverSetup() {
    override fun action() {}

    override fun closeEnvironment() {}
}
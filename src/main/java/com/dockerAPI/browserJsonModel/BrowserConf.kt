package com.dockerAPI.browserJsonModel

import com.config.BrowserType
import com.google.gson.GsonBuilder

internal class BrowserConf internal constructor(
        var firefox: Firefox? = null,
        var chrome: Chrome? = null
) {
    fun get(type: BrowserType): IDockerBrowser? {
        return when (type) {
            BrowserType.FIREFOX -> firefox
            BrowserType.CHROME -> chrome
            else -> throw RuntimeException()
        }
    }

    companion object {
        fun parseString(strJson: String): BrowserConf {
            return GsonBuilder().disableHtmlEscaping().create().fromJson(strJson, BrowserConf::class.java)
        }
    }

    override fun toString(): String {
        return GsonBuilder().create().toJson(this)
    }
}


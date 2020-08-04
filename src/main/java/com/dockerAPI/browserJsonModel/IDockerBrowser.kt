package com.dockerAPI.browserJsonModel

interface IDockerBrowser {
    val default: String
    val versions: MutableMap<String, out IDockerBrowserVersion>
}

data class Firefox(
        override val default: String,
        override val versions: MutableMap<String, FirefoxVersion>
) : IDockerBrowser

data class Chrome(
        override val default: String,
        override val versions: MutableMap<String, ChromeVersion>
) : IDockerBrowser


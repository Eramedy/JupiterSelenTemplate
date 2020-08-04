package com.dockerAPI.browserJsonModel

interface IDockerBrowserVersion {
    val image: String
}

@Suppress("unused")
class FirefoxVersion(
        ver: String,
        private val port: String = "4444",
        private val path: String = "/wd/hub"
) : IDockerBrowserVersion {
    override val image = "selenoid/vnc:firefox_$ver"
}

@Suppress("unused")
class ChromeVersion(
        ver: String,
        private val port: String = "4444",
        private val path: String = "/"
) : IDockerBrowserVersion {
    override val image = "selenoid/vnc:chrome_$ver"
}
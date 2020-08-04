package com.dockerAPI.browserJsonModel

import com.dockerAPI.DockerHubService
import com.google.gson.JsonSyntaxException
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.lang.invoke.MethodHandles
import java.math.BigDecimal
import java.nio.charset.Charset

object BrowserConfBuilder {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    private const val timeoutMillisecond: Long = 1000 * 3600 * 24

    private var obj: BrowserConf? = null

    @Synchronized
    internal fun instance(): BrowserConf {
        if (obj == null) {
            obj = prepareJson()
        }
        return obj!!
    }

    private fun prepareJson(): BrowserConf {
        val selenoidDirName = "selenoid_config"
        val timestampFilename = "timestamp"
        val configFilename = "config.json"
        val tempDir = System.getProperty("java.io.tmpdir")

        val dir = File(tempDir, selenoidDirName)
        val timestampFile = File(dir, timestampFilename)
        var broConfig: BrowserConf? = null
        val configFile = File(dir, configFilename)

        val updateConfig = dir.isDirectory
                && timeIsNotOut(timestampFile)
                && configFile.isFile

        if (updateConfig) {
            try {
                val content = configFile.readText()
                broConfig = BrowserConf.parseString(content)
            } catch (e: JsonSyntaxException) {
                log.error("Gibberish in config file.")
            }
        }

        // update config if is null yet
        if (broConfig == null) {
            try {
                broConfig = downloadAndParseJson()
            } catch (e: Exception) {
                log.error("Can't download or parse config from DockerHub", e); throw e
            }
            dir.mkdir()
            val newMilliSeconds = System.currentTimeMillis().toString()
            rewriteFile(timestampFile, newMilliSeconds)
            rewriteFile(configFile, broConfig.toString())
        }
        return broConfig
    }

    private fun rewriteFile(file: File, str: String) {
        try {
            file.writeText(str)
        } catch (fnf: FileNotFoundException) {
            log.error("Can't write '{}' file", file.name, fnf)
        }
    }

    private fun timeIsNotOut(timestampFile: File): Boolean {
        if (!timestampFile.isFile) return false
        val lines = timestampFile.readLines(Charset.defaultCharset())
        if (lines.size != 1) return false
        val value: Long
        try {
            value = lines[0].toLong()
        } catch (nf: NumberFormatException) {
            return false
        }
        if (value + timeoutMillisecond < System.currentTimeMillis()) return false

        return true
    }

    private fun downloadAndParseJson(): BrowserConf {
        val list = DockerHubService().getList()
        val chromeVersions = mutableMapOf<String, ChromeVersion>()
        val firefoxVersions = mutableMapOf<String, FirefoxVersion>()
        val chromePrefix = "chrome"
        val firefoxPrefix = "firefox"
        list.filter { it.name.startsWith(chromePrefix + "_") || it.name.startsWith(firefoxPrefix + "_") }
        list.forEach { result ->
            val name = result.name
            val version = name.split("_")[1]
            when {
                name.startsWith(chromePrefix + "_") -> {
                    chromeVersions[version] = ChromeVersion(version)
                }
                name.startsWith(firefoxPrefix + "_") -> {
                    firefoxVersions[version] = FirefoxVersion(version)
                }
            }
        }

        val browsers = BrowserConf()
        val chromeBiggestVersion = chromeVersions.maxBy { BigDecimal(it.key) }
        if (chromeBiggestVersion != null) browsers.chrome = Chrome(chromeBiggestVersion.key, chromeVersions)

        val firefoxBiggestVersion = firefoxVersions.maxBy { BigDecimal(it.key) }
        if (firefoxBiggestVersion != null) browsers.firefox = Firefox(firefoxBiggestVersion.key, firefoxVersions)

        return browsers
    }
}

package com

import com.config.Config
import com.dockerAPI.browserJsonModel.BrowserConf
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.core.DockerClientBuilder
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.lang.invoke.MethodHandles
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit


object SelenoidHelper {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    private const val selenoidContainerName = "selenoid"
    private const val selenoidUIContainerName = "selenoid-ui"

    private val client: DockerClient = DockerClientBuilder.getInstance().build()
//            DockerClientBuilder.getInstance().withDockerHttpClient(
//            JerseyDockerHttpClient.Builder()
//                    .dockerHost(URI(Config.dockSocket))
//                    .build()
//    ).build().also { log.info("Docker client on socket {} created.", Config.dockSocket) }

    init {
        if (!InetAddress.getByName(Config.dockerAddress).isReachable(2_000)) {
            log.error("Node by address '{}' is not reachable.", Config.dockerAddress)
            throw RuntimeException("Node by address '${Config.dockerAddress}' is not reachable.")
        }
    }

    internal fun startSelenoidContainer(json: BrowserConf) {
        pullContainer(Config.selenoidImage)

        //<editor-fold desc="delete selenoid container if exists">
        client.listContainersCmd().withShowAll(true).exec()
                .find { it.names.any { name -> name == "/$selenoidContainerName" } }
                ?.also { removeForce(it.id) }
        //</editor-fold>

        val overwrittenCMD = javaClass.classLoader.getResource("selenoid_cmd")!!.readText()
                .replace("%config.json%", json.toString())
                .replace("%limit%", Config.selenoidBrowsersLimit.toString())

        val selenoid: CreateContainerResponse = client
                .createContainerCmd(Config.selenoidImage)
                .withEntrypoint()
                .withCmd("sh", "-c", overwrittenCMD)
                .withHostConfig(HostConfig.newHostConfig()
                        .withBinds(Bind.parse("/opt/selenoid/video/:/opt/selenoid/video/"),
                                Bind.parse("/var/run/docker.sock:/var/run/docker.sock"))
                        .withPortBindings(PortBinding.parse("${Config.dockerPort}:4444"))
                )
                .withName(selenoidContainerName)
                .withEnv("OVERRIDE_VIDEO_OUTPUT_DIR=`pwd`/video/")
                .withEnv("TZ=${TimeZone.getDefault().id}")
                .exec()
        client.startContainerCmd(selenoid.id).exec()
    }

    fun startSelenoidUIContainer() {
        pullContainer(Config.selenoidUIImage)

        val uiContainer = client.listContainersCmd()
                .withShowAll(true).exec().firstOrNull { "/$selenoidUIContainerName" in it.names }
        val selenoidUIPort = Config.selenoidUIPort
        if (uiContainer != null) {
            if (uiContainer.state == "running" && uiContainer.ports.any { it.publicPort == selenoidUIPort }) {
                log.info("Selenoid UI have been started on port", selenoidUIPort)
                return
            } else {
                log.info("Removing Selenoid UI container")
                removeForce(uiContainer.id)
            }
        }

        val selenoid: CreateContainerResponse = client
                .createContainerCmd(Config.selenoidUIImage)
                .withHostConfig(HostConfig.newHostConfig()
                        .withPortBindings(PortBinding.parse("$selenoidUIPort:8080"))
                )
                .withName(selenoidUIContainerName)
                .withCmd("--selenoid-uri", "http://${getDockerGatewayAddress()}:${Config.dockerPort}")
                .exec()
        client.startContainerCmd(selenoid.id).exec()
        log.info("Selenoid UI started on port: {}", selenoidUIPort)
    }

    private fun removeForce(containerName: String) {
        client.removeContainerCmd(containerName).withForce(true).exec()
        log.info("{} was removed", containerName)
    }

    private fun getDockerGatewayAddress(): String {
        val networkSettings = client.inspectContainerCmd(selenoidContainerName).exec()
                .networkSettings.toString()
        val found = "gateway=([\\d.]+)".toRegex().find(networkSettings)
        if (found == null || found.groups.size < 2) {
            log.error("Can't find gateway of {} container", selenoidContainerName)
            throw RuntimeException()
        }
        return found.groupValues[1]
    }

    fun pullContainer(image: String) {
        val imageFound = client.listImagesCmd().exec()
                .any { it.repoTags.any { innerIt -> innerIt == image } }
        if (imageFound) {
            log.info("Image {} already has been.", image)
        } else {
            log.info("Pulling docker image {}", image)
            client.pullImageCmd(image)
                    .exec(PullImageResultCallback())
                    .awaitCompletion(30, TimeUnit.MINUTES)
            log.info("Pulling done.")
        }
    }

    fun shutdown() {
        waitUntilDriversBeenClosed()
        when (Config.dockerAfterTest) {
            1 -> {
                client.killContainerCmd(selenoidContainerName).exec()
                log.info("{} was killed", selenoidContainerName)
                client.killContainerCmd(selenoidUIContainerName).exec()
                log.info("{} was killed", selenoidUIContainerName)
            }
            2 -> {
                removeForce(selenoidContainerName)
                removeForce(selenoidUIContainerName)
            }
        }
    }

    private fun waitUntilDriversBeenClosed() {
        val startTime = System.currentTimeMillis()
        val timeout = 45_000
        do {
            log.info("Check that drivers been closed")
            val list = client.listContainersCmd().exec()
            if (!list.any {
                        val image = it.image
                        image.startsWith("selenoid/vnc:") || image.startsWith("selenoid/video-recorder:")
                    }) return
            sleep(3_000)
        } while (startTime + timeout > System.currentTimeMillis())

        log.warn("Containers didn't stopped in {} milliseconds", timeout)
    }
}
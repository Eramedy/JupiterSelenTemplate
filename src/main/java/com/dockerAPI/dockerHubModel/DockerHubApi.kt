package com.dockerAPI.dockerHubModel

data class DockerHubApi(
        val count: Int,
        val next: String?,
        val previous: String?,
        val results: List<Result>
)

data class Result(
        val creator: Int,
        val full_size: Int,
        val id: Int,
        val image_id: Any,
        val images: List<Image>,
        val last_updated: String,
        val last_updater: Int,
        val last_updater_username: String,
        val name: String,
        val repository: Int,
        val v2: Boolean
)

data class Image(
        val architecture: String,
        val digest: String,
        val features: String,
        val os: String,
        val os_features: String,
        val os_version: Any,
        val size: Int,
        val variant: Any
)
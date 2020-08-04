package com.dockerAPI

import java.io.IOException

class DockerAPIException : IOException {
    constructor() : super()
    constructor(message: String) : super(message)
}
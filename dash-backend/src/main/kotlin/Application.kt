package com.example

import com.example.config.configureFirebase
import com.example.plugins.configureFirebasePlugin
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureFirebase()
    configureSerialization()
    configureMonitoring()
    configureFirebasePlugin()
    configureRouting()
}

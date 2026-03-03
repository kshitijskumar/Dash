package org.example.dash.data.network

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual fun getHttpClientEngine(): HttpClientEngine = Darwin.create()

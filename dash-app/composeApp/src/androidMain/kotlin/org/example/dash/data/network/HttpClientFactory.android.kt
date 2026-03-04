package org.example.dash.data.network

import io.ktor.client.engine.*
import io.ktor.client.engine.android.*

actual fun getHttpClientEngine(): HttpClientEngine = Android.create()

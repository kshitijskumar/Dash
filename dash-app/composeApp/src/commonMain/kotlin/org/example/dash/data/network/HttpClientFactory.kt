package org.example.dash.data.network

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import org.example.dash.utils.AppJson
import org.example.dash.utils.Constants

expect fun getHttpClientEngine(): HttpClientEngine

object HttpClientFactory {
    
    fun create(): HttpClient {
        return HttpClient(getHttpClientEngine()) {
            install(ContentNegotiation) {
                json(AppJson)
            }
            
            install(Logging) {
                logger = Logger.DEFAULT
                // BODY/ALL logging can consume/transform fetch streams on Web/WASM and
                // surface false content-length mismatch errors while deserializing.
                level = LogLevel.HEADERS
            }
            
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }
            
            defaultRequest {
                url(Constants.DASH_BASE_URL)
            }
        }
    }
}

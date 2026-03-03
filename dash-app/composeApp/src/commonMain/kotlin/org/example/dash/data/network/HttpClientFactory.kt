package org.example.dash.data.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.dash.utils.AppJson
import org.example.dash.utils.Constants

object HttpClientFactory {
    
    fun create(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(AppJson)
            }
            
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
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

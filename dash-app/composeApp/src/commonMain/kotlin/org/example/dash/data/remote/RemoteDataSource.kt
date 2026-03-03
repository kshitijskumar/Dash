package org.example.dash.data.remote

import io.ktor.client.*
import org.example.dash.data.network.HttpClientFactory

class RemoteDataSource(
    private val httpClient: HttpClient = HttpClientFactory.create()
) {
    
}

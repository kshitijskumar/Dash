package org.example.dash.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.example.dash.data.model.DashLinksResponse
import org.example.dash.data.network.HttpClientFactory

class RemoteDataSource(
    private val httpClient: HttpClient = HttpClientFactory.create()
) {
    
    suspend fun getDashLinks(userId: String, token: String): DashLinksResponse {
        val url = USER_LINKS_DATA
            .replace(PATH_USERID, userId)
            .replace(PATH_TOKEN, token)

        return httpClient.get(url).body()
    }

    companion object {
        private const val PATH_USERID = "{USER_ID}"
        private const val PATH_TOKEN = "{TOKEN}"
        private const val USER_LINKS_DATA = "dashls/$PATH_USERID/$PATH_TOKEN"
    }
}

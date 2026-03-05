package org.example.dash.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.example.dash.data.model.AddLinkRequest
import org.example.dash.data.model.AddLinkResponse
import org.example.dash.data.model.DashLinksResponse
import org.example.dash.data.network.HttpClientFactory
import org.example.dash.utils.AppJson

class RemoteDataSource(
    private val httpClient: HttpClient = HttpClientFactory.create()
) {
    
    suspend fun getDashLinks(userId: String, token: String): DashLinksResponse {
        val url = USER_LINKS_DATA
            .replace(PATH_USERID, userId)
            .replace(PATH_TOKEN, token)

        println("RemoteDataSource: Making request to: $url")
        val response = httpClient.get(url).body<DashLinksResponse>()
        println("RemoteDataSource: Got response: $response")
        return response
    }
    
    suspend fun addDashLink(userId: String, token: String, request: AddLinkRequest): AddLinkResponse {
        val url = ADD_LINK_URL
            .replace(PATH_USERID, userId)
            .replace(PATH_TOKEN, token)

        println("RemoteDataSource: Adding link to: $url with request: $request")
        val response = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(AppJson.encodeToString(request))
        }.body<AddLinkResponse>()
        println("RemoteDataSource: Got response: $response")
        return response
    }

    companion object {
        private const val PATH_USERID = "{USER_ID}"
        private const val PATH_TOKEN = "{TOKEN}"
        private const val USER_LINKS_DATA = "dashls/$PATH_USERID/$PATH_TOKEN"
        private const val ADD_LINK_URL = "dashls/$PATH_USERID/$PATH_TOKEN/link"
    }
}

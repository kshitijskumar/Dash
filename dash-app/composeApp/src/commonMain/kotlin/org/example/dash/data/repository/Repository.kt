package org.example.dash.data.repository

import org.example.dash.data.model.DashLinksResponse
import org.example.dash.data.remote.RemoteDataSource

class Repository(
    private val remoteDataSource: RemoteDataSource = RemoteDataSource()
) {
    
    suspend fun getDashLinks(userId: String, token: String): DashLinksResponse {
        return remoteDataSource.getDashLinks(userId, token)
    }
}

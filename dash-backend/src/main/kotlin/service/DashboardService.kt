package com.example.service

import com.example.api.dto.AddLinkRequest
import com.example.api.dto.AllDataResponse
import com.example.api.dto.DashboardRequestModel
import com.example.api.dto.DashboardResponseModel
import com.example.api.dto.DocumentData
import com.example.api.utils.JsonConverter
import com.example.data.repository.DashboardRepository
import com.example.domain.exceptions.DashboardNotFoundException
import com.example.domain.exceptions.InvalidUrlException
import com.example.domain.models.Link
import java.net.URL
import java.util.UUID

class DashboardService(
    private val repository: DashboardRepository
) {
    
    suspend fun getUserDashboard(request: DashboardRequestModel): DashboardResponseModel {
        val dashboardData = repository.findByUserIdAndToken(request.userId, request.token)
            ?: throw DashboardNotFoundException(request.userId)
        
        return DashboardResponseModel(
            userId = dashboardData.userId,
            links = dashboardData.links
        )
    }
    
    suspend fun getAllData(): AllDataResponse {
        val documents = repository.findAll()
        
        val documentDataList = documents.map { doc ->
            val dataMap = (doc.data ?: emptyMap()).mapValues { (_, value) -> 
                JsonConverter.convertToJsonElement(value)
            }
            DocumentData(
                id = doc.id,
                data = dataMap
            )
        }
        
        return AllDataResponse(
            collection = "data",
            count = documentDataList.size,
            documents = documentDataList
        )
    }
    
    suspend fun addLink(userId: String, token: String, request: AddLinkRequest): Link {
        validateUrl(request.url)
        
        val linkId = UUID.randomUUID().toString()
        val link = Link(
            id = linkId,
            name = request.name,
            url = request.url
        )
        
        val success = repository.addLink(userId, token, link)
        if (!success) {
            throw DashboardNotFoundException(userId)
        }
        
        return link
    }
    
    private fun validateUrl(urlString: String) {
        try {
            val url = URL(urlString)
            val protocol = url.protocol.lowercase()
            if (protocol != "http" && protocol != "https") {
                throw InvalidUrlException(urlString)
            }
        } catch (e: Exception) {
            if (e is InvalidUrlException) throw e
            throw InvalidUrlException(urlString)
        }
    }
}

package com.example.service

import com.example.api.dto.AllDataResponse
import com.example.api.dto.DashboardRequestModel
import com.example.api.dto.DashboardResponseModel
import com.example.api.dto.DocumentData
import com.example.api.utils.JsonConverter
import com.example.data.repository.DashboardRepository
import com.example.domain.exceptions.DashboardNotFoundException

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
}

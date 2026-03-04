package com.example.data.repository

import com.example.domain.models.DashboardData
import com.example.domain.models.Link
import com.google.cloud.firestore.DocumentSnapshot

interface DashboardRepository {
    suspend fun findByUserIdAndToken(userId: String, token: String): DashboardData?
    
    suspend fun findAll(): List<DocumentSnapshot>
    
    suspend fun addLink(userId: String, token: String, link: Link): Boolean
}

package com.example.config

import com.example.data.datasource.FirestoreDataSource
import com.example.data.repository.DashboardRepository
import com.example.data.repository.FirestoreDashboardRepository
import com.example.service.DashboardService

object DependencyInjection {
    
    private lateinit var dashboardServiceInstance: DashboardService
    
    fun initialize() {
        val firestore = FirebaseService.getFirestore()
        
        val firestoreDataSource = FirestoreDataSource(firestore)
        val dashboardRepository: DashboardRepository = FirestoreDashboardRepository(firestoreDataSource)
        
        dashboardServiceInstance = DashboardService(dashboardRepository)
    }
    
    fun getDashboardService(): DashboardService {
        if (!::dashboardServiceInstance.isInitialized) {
            throw IllegalStateException("DependencyInjection not initialized. Call initialize() first.")
        }
        return dashboardServiceInstance
    }
}

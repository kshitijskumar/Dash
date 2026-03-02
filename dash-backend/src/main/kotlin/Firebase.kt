package com.example

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import io.ktor.server.application.*
import java.io.ByteArrayInputStream

object FirebaseService {
    private var firestore: Firestore? = null
    private var initialized = false
    
    fun initialize(projectId: String, serviceAccountJson: String) {
        if (FirebaseApp.getApps().isEmpty()) {
            val serviceAccountStream = ByteArrayInputStream(serviceAccountJson.toByteArray())
            val credentials = GoogleCredentials.fromStream(serviceAccountStream)
            
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build()
            
            FirebaseApp.initializeApp(options)
        }
        
        firestore = FirestoreClient.getFirestore()
        initialized = true
    }
    
    fun getFirestore(): Firestore {
        return firestore ?: throw IllegalStateException("Firebase has not been initialized")
    }
    
    fun isInitialized(): Boolean = initialized
}

fun Application.configureFirebase() {
    try {
        val projectId = environment.config.propertyOrNull("firebase.projectId")?.getString()
        val serviceAccountJson = environment.config.propertyOrNull("firebase.serviceAccountJson")?.getString()
        
        if (projectId != null && serviceAccountJson != null) {
            FirebaseService.initialize(projectId, serviceAccountJson)
            log.info("Firebase initialized with project ID: $projectId")
        } else {
            log.warn("Firebase configuration not found. Firebase services will not be available.")
        }
    } catch (e: Exception) {
        log.error("Failed to initialize Firebase: ${e.message}")
        log.warn("Firebase services will not be available.")
    }
}

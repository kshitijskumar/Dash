package com.example

import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreRepository {
    private val firestore = FirebaseService.getFirestore()
    
    fun getFirestore(): Firestore {
        return firestore
    }
}

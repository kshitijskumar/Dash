package com.example.data.datasource

import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QuerySnapshot

class FirestoreDataSource(private val firestore: Firestore) {
    
    private val collectionName = "data"
    
    fun queryByUserIdAndToken(userId: String, token: String): QuerySnapshot {
        return firestore.collection(collectionName)
            .whereEqualTo("userId", userId)
            .whereEqualTo("token", token)
            .limit(1)
            .get()
            .get()
    }
    
    fun getAllDocuments(): List<DocumentSnapshot> {
        val querySnapshot = firestore.collection(collectionName).get().get()
        return querySnapshot.documents
    }
}

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
    
    fun addLinkToUser(userId: String, token: String, linkData: Map<String, Any>): Boolean {
        val querySnapshot = firestore.collection(collectionName)
            .whereEqualTo("userId", userId)
            .whereEqualTo("token", token)
            .limit(1)
            .get()
            .get()
        
        if (querySnapshot.isEmpty) {
            return false
        }
        
        val document = querySnapshot.documents[0]
        document.reference.update("links", com.google.cloud.firestore.FieldValue.arrayUnion(linkData))
            .get()
        
        return true
    }
}

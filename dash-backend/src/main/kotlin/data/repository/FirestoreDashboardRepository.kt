package com.example.data.repository

import com.example.data.datasource.FirestoreDataSource
import com.example.domain.models.DashboardData
import com.example.domain.models.Link
import com.google.cloud.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreDashboardRepository(
    private val dataSource: FirestoreDataSource
) : DashboardRepository {
    
    override suspend fun findByUserIdAndToken(userId: String, token: String): DashboardData? {
        return withContext(Dispatchers.IO) {
            val querySnapshot = dataSource.queryByUserIdAndToken(userId, token)
            
            if (querySnapshot.isEmpty) {
                null
            } else {
                val doc = querySnapshot.documents[0]
                mapToDashboardData(doc)
            }
        }
    }
    
    override suspend fun findAll(): List<DocumentSnapshot> {
        return withContext(Dispatchers.IO) {
            dataSource.getAllDocuments()
        }
    }
    
    override suspend fun addLink(userId: String, token: String, link: Link): Boolean {
        return withContext(Dispatchers.IO) {
            val linkData = mapOf(
                "id" to link.id,
                "name" to link.name,
                "url" to link.url
            )
            dataSource.addLinkToUser(userId, token, linkData)
        }
    }
    
    private fun mapToDashboardData(doc: DocumentSnapshot): DashboardData {
        val data = doc.data ?: emptyMap()
        val userId = data["userId"]?.toString() ?: ""
        val token = data["token"]?.toString() ?: ""
        
        val linksRaw = data["links"] as? List<*> ?: emptyList<Any>()
        val links = linksRaw.mapNotNull { linkItem ->
            when (linkItem) {
                is Map<*, *> -> {
                    val id = linkItem["id"]?.toString()
                    val name = linkItem["name"]?.toString()
                    val url = linkItem["url"]?.toString()
                    
                    if (id != null && name != null && url != null) {
                        Link(id = id, name = name, url = url)
                    } else null
                }
                else -> null
            }
        }
        
        return DashboardData(
            id = doc.id,
            userId = userId,
            token = token,
            links = links
        )
    }
}

package org.example.dash.domain.usecase

import kotlinx.coroutines.CancellationException
import org.example.dash.data.mapper.toDomain
import org.example.dash.data.model.AddLinkRequest
import org.example.dash.data.repository.Repository
import org.example.dash.domain.model.DashLinkDomain
import org.example.dash.domain.model.Result

class AddLinkUseCase(
    private val repository: Repository = Repository()
) {
    
    suspend fun invoke(
        userId: String,
        token: String,
        name: String,
        url: String
    ): Result<DashLinkDomain> {
        return try {
            // Validate inputs
            if (name.isBlank()) {
                return Result.Error(IllegalArgumentException("Link name cannot be empty"))
            }
            
            if (name.length > 100) {
                return Result.Error(IllegalArgumentException("Link name is too long (max 100 characters)"))
            }
            
            if (url.isBlank()) {
                return Result.Error(IllegalArgumentException("URL cannot be empty"))
            }
            
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return Result.Error(IllegalArgumentException("URL must start with http:// or https://"))
            }
            
            val request = AddLinkRequest(name = name, url = url)
            val response = repository.addDashLink(userId, token, request)
            
            Result.Success(response.link.toDomain())
        } catch (e: CancellationException) {
            // Rethrow cancellation to propagate it properly
            throw e
        } catch (e: Exception) {
            println("AddLinkUseCase: Error adding link: ${e.message}")
            Result.Error(e, e.message ?: "Failed to add link. Please try again.")
        }
    }
}

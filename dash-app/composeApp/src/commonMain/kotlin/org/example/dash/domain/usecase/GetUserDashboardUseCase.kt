package org.example.dash.domain.usecase

import kotlinx.coroutines.CancellationException
import org.example.dash.data.mapper.toDomain
import org.example.dash.data.repository.Repository
import org.example.dash.domain.model.Result
import org.example.dash.domain.model.UserDashboard

class GetUserDashboardUseCase(
    private val repository: Repository = Repository()
) {
    
    suspend operator fun invoke(userId: String, token: String): Result<UserDashboard> {
        return try {
            if (userId.isBlank()) {
                return Result.Error(
                    exception = IllegalArgumentException("User ID cannot be empty"),
                    message = "User ID cannot be empty"
                )
            }
            
            if (token.isBlank()) {
                return Result.Error(
                    exception = IllegalArgumentException("Token cannot be empty"),
                    message = "Token cannot be empty"
                )
            }
            
            val response = repository.getDashLinks(userId, token)
            val userDashboard = response.toDomain()
            Result.Success(userDashboard)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Something went wrong"
            )
        }
    }
}

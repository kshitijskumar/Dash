package com.example.api.dto

data class DashboardRequestModel(
    val userId: String,
    val token: String
) {
    init {
        require(userId.isNotBlank()) { "userId cannot be blank" }
        require(token.isNotBlank()) { "token cannot be blank" }
    }
}

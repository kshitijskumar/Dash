package com.example.domain.exceptions

sealed class DomainException(message: String) : Exception(message)

class DashboardNotFoundException(userId: String) : 
    DomainException("Dashboard not found for userId: $userId")

class InvalidCredentialsException : 
    DomainException("Invalid userId or token provided")

class FirebaseNotInitializedException : 
    DomainException("Firebase service is not initialized")

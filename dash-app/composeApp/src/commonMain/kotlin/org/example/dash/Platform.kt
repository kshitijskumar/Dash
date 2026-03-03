package org.example.dash

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
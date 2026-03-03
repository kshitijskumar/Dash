package org.example.dash.utils

import kotlinx.serialization.json.Json

val AppJson = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}
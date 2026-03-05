package com.example.api.dto

import com.example.domain.models.Link
import kotlinx.serialization.Serializable

@Serializable
data class AddLinkResponse(
    val link: Link,
    val message: String
)

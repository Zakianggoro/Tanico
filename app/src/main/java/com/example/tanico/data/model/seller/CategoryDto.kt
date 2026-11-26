package com.example.tanico.data.model.seller

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Table: categories */
@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("created_at") val createdAt: String
)

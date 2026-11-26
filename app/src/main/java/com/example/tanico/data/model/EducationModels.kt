package com.example.tanico.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleCategory(
    val id: String,
    val name: String,
    val description: String? = null
)

@Serializable
data class Article(
    val id: String,
    @SerialName("category_id")
    val categoryId: String,
    val title: String,
    val content: String,
    @SerialName("image_path")
    val imagePath: String? = null,
    val status: String = "published",
    @SerialName("published_at")
    val publishedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("like_count")
    val likeCount: Int = 0,
    @SerialName("is_liked_by_current_user")
    val isLikedByCurrentUser: Boolean = false
)

@Serializable
data class WasteType(
    val id: String,
    val name: String,
    val unit: String,
    val description: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true
)

@Serializable
data class WastePriceEstimate(
    val id: String,
    @SerialName("waste_type_id")
    val wasteTypeId: String,
    @SerialName("estimated_price")
    val estimatedPrice: Double,
    @SerialName("effective_at")
    val effectiveAt: String,
    @SerialName("source_note")
    val sourceNote: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

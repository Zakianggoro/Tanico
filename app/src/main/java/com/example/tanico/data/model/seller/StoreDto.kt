package com.example.tanico.data.model.seller

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Table: stores (owner_id is UNIQUE: one store per seller). */
@Serializable
data class StoreDto(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("store_name") val storeName: String,
    val description: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class StoreWriteDto(
    @SerialName("owner_id") val ownerId: String? = null,
    @SerialName("store_name") val storeName: String,
    val description: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double
)
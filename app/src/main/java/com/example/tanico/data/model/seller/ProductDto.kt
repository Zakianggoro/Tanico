package com.example.tanico.data.model.seller

import com.example.tanico.data.model.BigDecimalSerializer
import java.math.BigDecimal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String,
    @SerialName("store_id") val storeId: String,
    @SerialName("category_id") val categoryId: String,
    val name: String,
    val description: String? = null,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    val stock: Int,
    val unit: String,
    @SerialName("weight_per_unit_kg")
    @Serializable(with = BigDecimalSerializer::class) val weightPerUnitKg: BigDecimal,
    val status: String,
    @SerialName("product_images") val productImages: List<ProductImageDto> = emptyList(),
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class ProductWriteDto(
    @SerialName("store_id") val storeId: String? = null,
    @SerialName("category_id") val categoryId: String,
    val name: String,
    val description: String?,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    val stock: Int,
    val unit: String,
    @SerialName("weight_per_unit_kg")
    @Serializable(with = BigDecimalSerializer::class) val weightPerUnitKg: BigDecimal,
    val status: String
)

@Serializable
data class ProductImageDto(
    val id: String,
    @SerialName("product_id") val productId: String,
    @SerialName("storage_path") val storagePath: String,
    @SerialName("is_primary") val isPrimary: Boolean,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class ProductImageWriteDto(
    @SerialName("product_id") val productId: String,
    @SerialName("storage_path") val storagePath: String,
    @SerialName("is_primary") val isPrimary: Boolean
)

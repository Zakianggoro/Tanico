package com.example.tanico.data.model.seller

import com.example.tanico.data.model.BigDecimalSerializer
import java.math.BigDecimal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Table: products. Query with `select=*,product_images(*)` to fill [productImages]. */
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

/**
 * Insert payload. Separate from [ProductUpdateDto] on purpose: a single DTO with a
 * nullable store_id could serialize "store_id": null on UPDATE depending on Json config.
 * RLS verifies the caller owns [storeId].
 */
@Serializable
data class ProductInsertDto(
    @SerialName("store_id") val storeId: String,
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

/** Update payload: store_id is never sent, so a product cannot change store. */
@Serializable
data class ProductUpdateDto(
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

/** Table: product_images (only the storage path is stored). */
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

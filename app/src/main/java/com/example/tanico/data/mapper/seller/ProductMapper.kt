package com.example.tanico.data.mapper.seller

import com.example.tanico.data.mapper.toInstant
import com.example.tanico.data.model.seller.ProductDto
import com.example.tanico.data.model.seller.ProductImageDto
import com.example.tanico.data.model.seller.ProductInsertDto
import com.example.tanico.data.model.seller.ProductUpdateDto
import com.example.tanico.domain.model.Product
import com.example.tanico.domain.model.ProductImage
import com.example.tanico.domain.model.ProductInput
import com.example.tanico.domain.model.ProductStatus

fun ProductDto.toDomain() = Product(
    id = id,
    storeId = storeId,
    categoryId = categoryId,
    name = name,
    description = description,
    price = price,
    stock = stock,
    unit = unit,
    weightPerUnitKg = weightPerUnitKg,
    // Fail loudly on an unknown status instead of silently picking a default.
    status = ProductStatus.fromValue(status)
        ?: error("Unknown product status from server: $status"),
    images = productImages.map { it.toDomain() },
    createdAt = createdAt.toInstant(),
    updatedAt = updatedAt.toInstant()
)

fun ProductImageDto.toDomain() = ProductImage(
    id = id,
    productId = productId,
    storagePath = storagePath,
    isPrimary = isPrimary,
    createdAt = createdAt.toInstant()
)

fun ProductInput.toInsertDto(storeId: String) = ProductInsertDto(
    storeId = storeId,
    categoryId = categoryId,
    name = name.trim(),
    description = description?.trim()?.ifEmpty { null },
    price = price,
    stock = stock,
    unit = unit.trim(),
    weightPerUnitKg = weightPerUnitKg,
    status = status.value
)

fun ProductInput.toUpdateDto() = ProductUpdateDto(
    categoryId = categoryId,
    name = name.trim(),
    description = description?.trim()?.ifEmpty { null },
    price = price,
    stock = stock,
    unit = unit.trim(),
    weightPerUnitKg = weightPerUnitKg,
    status = status.value
)

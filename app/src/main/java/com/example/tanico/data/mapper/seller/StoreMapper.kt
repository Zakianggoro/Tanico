package com.example.tanico.data.mapper.seller

import com.example.tanico.data.mapper.toInstant
import com.example.tanico.data.model.seller.CategoryDto
import com.example.tanico.data.model.seller.StoreDto
import com.example.tanico.data.model.seller.StoreWriteDto
import com.example.tanico.domain.model.Category
import com.example.tanico.domain.model.Store
import com.example.tanico.domain.model.StoreInput

fun StoreDto.toDomain() = Store(
    id = id,
    ownerId = ownerId,
    storeName = storeName,
    description = description,
    address = address,
    latitude = latitude,
    longitude = longitude,
    createdAt = createdAt.toInstant(),
    updatedAt = updatedAt.toInstant()
)

fun StoreInput.toWriteDto(ownerId: String? = null) = StoreWriteDto(
    ownerId = ownerId,
    storeName = storeName.trim(),
    description = description?.trim()?.ifEmpty { null },
    address = address.trim(),
    latitude = latitude,
    longitude = longitude
)

fun CategoryDto.toDomain() = Category(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt.toInstant()
)
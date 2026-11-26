package com.example.tanico.data.mapper.seller

import com.example.tanico.data.mapper.toInstant
import com.example.tanico.data.model.seller.CategoryDto
import com.example.tanico.domain.model.Category

fun CategoryDto.toDomain() = Category(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt.toInstant()
)

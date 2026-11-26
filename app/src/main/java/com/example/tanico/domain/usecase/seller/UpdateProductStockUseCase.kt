package com.example.tanico.domain.usecase.seller

import com.example.tanico.domain.model.Product
import com.example.tanico.domain.repository.SellerProductRepository

class UpdateProductStockUseCase(private val repository: SellerProductRepository) {
    suspend operator fun invoke(productId: String, newStock: Int): Result<Product> = validated(
        block = { requireNonNegative("stock", newStock) },
        action = { repository.updateStock(productId, newStock) }
    )
}

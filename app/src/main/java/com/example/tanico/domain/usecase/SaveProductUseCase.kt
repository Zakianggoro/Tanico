package com.example.tanico.domain.usecase

import com.example.tanico.domain.model.Product
import com.example.tanico.domain.model.ProductInput
import com.example.tanico.domain.repository.SellerProductRepository

/**
 * Client-side validation for UX only. The server must re-validate (AGENTS.md: never trust client values).
 *
 * TODO: weight rule is an open decision ("whether product weight is mandatory"). We only
 * reject negative values for now; confirm whether weight must be > 0.
 */
class SaveProductUseCase(private val repository: SellerProductRepository) {
    suspend operator fun invoke(input: ProductInput): Result<Product> = validated(
        block = {
            requireNotBlank("name", input.name)
            requireNotBlank("categoryId", input.categoryId)
            requireNotBlank("unit", input.unit)
            requireNonNegative("price", input.price)
            requireNonNegative("stock", input.stock)
            requireNonNegative("weightPerUnitKg", input.weightPerUnitKg)
        },
        action = { repository.saveProduct(input) }
    )
}

package com.example.tanico.feature.marketplace

import com.example.tanico.domain.model.MarketplaceProduct
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuyerCartViewModelTest {
    @Test
    fun addingProductCombinesQuantitiesAndCapsAtAvailableStock() {
        val viewModel = BuyerCartViewModel()
        val product = product(stock = 3)

        viewModel.addProduct(product, quantity = 2)
        viewModel.addProduct(product, quantity = 4)

        assertEquals(1, viewModel.uiState.value.items.size)
        assertEquals(3, viewModel.uiState.value.items.single().quantity)
        assertEquals(3, viewModel.uiState.value.productCount)
        assertEquals(9_000.0, viewModel.uiState.value.subtotal, 0.0)
    }

    @Test
    fun settingQuantityToZeroRemovesProduct() {
        val viewModel = BuyerCartViewModel()
        val product = product(stock = 10)

        viewModel.addProduct(product)
        viewModel.setQuantity(product.id, 0)

        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun changingCartOwnerClearsSessionCart() {
        val viewModel = BuyerCartViewModel()
        viewModel.bindToUser("buyer-1")
        viewModel.addProduct(product(stock = 10))

        viewModel.bindToUser("buyer-2")

        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    private fun product(stock: Int) = MarketplaceProduct(
        id = "product-1",
        categoryName = "Sekam Padi",
        name = "Sekam Padi Kering",
        description = null,
        price = 3_000.0,
        stock = stock,
        unit = "kg",
        weightPerUnitKg = 1.0
    )
}

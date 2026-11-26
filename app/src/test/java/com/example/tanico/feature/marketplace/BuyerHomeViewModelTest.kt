package com.example.tanico.feature.marketplace

import com.example.tanico.domain.model.MarketplaceProduct
import com.example.tanico.domain.repository.MarketplaceRepository
import com.example.tanico.feature.auth.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class BuyerHomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadsProductsFromMarketplaceRepository() = runTest {
        val product = MarketplaceProduct(
            id = "product-1",
            categoryName = "Sekam Padi",
            name = "Sekam Padi Kering",
            description = null,
            price = 3_000.0,
            stock = 1_000,
            unit = "kg",
            weightPerUnitKg = 1.0
        )
        val viewModel = BuyerHomeViewModel(FakeMarketplaceRepository(listOf(product)))

        viewModel.load()
        advanceUntilIdle()

        assertEquals(listOf(product), viewModel.uiState.value.products)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.message)
    }

    private class FakeMarketplaceRepository(
        private val products: List<MarketplaceProduct>
    ) : MarketplaceRepository {
        override suspend fun loadActiveProducts(): Result<List<MarketplaceProduct>> =
            Result.success(products)
    }
}

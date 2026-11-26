package com.example.tanico.feature.marketplace

import com.example.tanico.domain.model.MarketplaceProduct
import com.example.tanico.domain.model.DeviceCoordinates
import com.example.tanico.domain.repository.MarketplaceRepository
import com.example.tanico.feature.auth.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BuyerHomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadsNearbyProductsUsingActivityLocation() = runTest {
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
        val repository = FakeMarketplaceRepository(listOf(product))
        val viewModel = BuyerHomeViewModel(repository)
        val coordinates = DeviceCoordinates(latitude = -7.25, longitude = 112.75)

        viewModel.beginLocationRequest()
        viewModel.loadAtLocation(coordinates)
        advanceUntilIdle()

        assertEquals(listOf(product), viewModel.uiState.value.products)
        assertEquals(coordinates, repository.requestedCoordinates)
        assertEquals(coordinates, viewModel.uiState.value.coordinates)
        assertEquals(BuyerHomeViewModel.NEARBY_RADIUS_KM, repository.requestedRadiusKm!!, 0.0)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun retryReusesCachedLocationUntilViewModelReset() = runTest {
        val repository = FakeMarketplaceRepository(emptyList())
        val viewModel = BuyerHomeViewModel(repository)
        val coordinates = DeviceCoordinates(latitude = -6.2, longitude = 106.8)

        viewModel.loadAtLocation(coordinates)
        advanceUntilIdle()

        assertTrue(viewModel.retry())
        advanceUntilIdle()

        assertEquals(2, repository.requestCount)
        assertEquals(coordinates, repository.requestedCoordinates)
        viewModel.reset()
        assertFalse(viewModel.retry())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun calibrationReplacesCachedLocation() = runTest {
        val repository = FakeMarketplaceRepository(emptyList())
        val viewModel = BuyerHomeViewModel(repository)
        val firstLocation = DeviceCoordinates(latitude = -6.2, longitude = 106.8)
        val recalibratedLocation = DeviceCoordinates(latitude = -6.3, longitude = 106.9)

        viewModel.loadAtLocation(firstLocation)
        advanceUntilIdle()
        viewModel.beginLocationRequest()
        viewModel.loadAtLocation(recalibratedLocation)
        advanceUntilIdle()

        assertEquals(recalibratedLocation, viewModel.uiState.value.coordinates)
        assertEquals(recalibratedLocation, repository.requestedCoordinates)
        assertEquals(2, repository.requestCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun debugFallbackLoadsAllProductsWithoutLocation() = runTest {
        val repository = FakeMarketplaceRepository(emptyList())
        val viewModel = BuyerHomeViewModel(repository)

        viewModel.locationUnavailable("Lokasi emulator tidak tersedia.")
        viewModel.loadAllActiveProductsForDebug()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isShowingAllProductsForDebug)
        assertEquals(1, repository.allProductsRequestCount)
        assertEquals(null, viewModel.uiState.value.coordinates)
    }

    private class FakeMarketplaceRepository(
        private val products: List<MarketplaceProduct>
    ) : MarketplaceRepository {
        var requestedCoordinates: DeviceCoordinates? = null
        var requestedRadiusKm: Double? = null
        var requestCount: Int = 0
        var allProductsRequestCount: Int = 0

        override suspend fun loadAllActiveProductsForDebug(): Result<List<MarketplaceProduct>> {
            allProductsRequestCount += 1
            return Result.success(products)
        }

        override suspend fun loadNearbyActiveProducts(
            latitude: Double,
            longitude: Double,
            radiusKm: Double
        ): Result<List<MarketplaceProduct>> {
            requestCount += 1
            requestedCoordinates = DeviceCoordinates(latitude, longitude)
            requestedRadiusKm = radiusKm
            return Result.success(products)
        }
    }
}

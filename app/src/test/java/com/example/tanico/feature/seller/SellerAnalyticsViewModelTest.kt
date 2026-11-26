package com.example.tanico.feature.seller

import com.example.tanico.domain.model.SellerAnalytics
import com.example.tanico.domain.repository.SellerAnalyticsRepository
import com.example.tanico.domain.repository.SellerStoreRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SellerAnalyticsViewModelTest {
    @Test
    fun storeCreationRejectsOutOfRangeLatitude() {
        val storeRepository = FakeSellerStoreRepository()
        val viewModel = SellerAnalyticsViewModel(FakeSellerAnalyticsRepository(), storeRepository)

        viewModel.createStore(
            ownerId = "seller-id",
            storeName = "Toko Tani",
            address = "Jalan Tani 1",
            latitudeText = "91",
            longitudeText = "112.6"
        )

        assertEquals(
            "Latitude toko harus berupa angka antara -90 dan 90.",
            viewModel.uiState.value.storeCreationMessage
        )
        assertFalse(storeRepository.createStoreCalled)
    }

    private class FakeSellerAnalyticsRepository : SellerAnalyticsRepository {
        override suspend fun loadOverview(userId: String): Result<SellerAnalytics?> =
            Result.success(null)
    }

    private class FakeSellerStoreRepository : SellerStoreRepository {
        var createStoreCalled = false

        override suspend fun createStore(
            ownerId: String,
            storeName: String,
            address: String,
            latitude: Double,
            longitude: Double
        ): Result<Unit> {
            createStoreCalled = true
            return Result.success(Unit)
        }
    }
}

package com.example.tanico.feature.profile

import com.example.tanico.domain.model.BuyerDeliveryAddress
import com.example.tanico.domain.repository.BuyerAddressRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BuyerAddressViewModelTest {
    @Test
    fun requiresAddressPinBeforeSavingDeliveryAddress() {
        val repository = FakeBuyerAddressRepository()
        val viewModel = BuyerAddressViewModel(repository)
        viewModel.updateRecipientName("Pembeli TANICO")
        viewModel.updatePhone("081234567890")
        viewModel.updateAddressLine("Jalan Tani 1")
        viewModel.updateCity("Kota Malang")

        viewModel.save(userId = "buyer-id", isPreviewMode = false)

        assertEquals("Pilih titik lokasi alamat pada peta.", viewModel.uiState.value.message)
        assertFalse(repository.saveCalled)
    }

    private class FakeBuyerAddressRepository : BuyerAddressRepository {
        var saveCalled = false

        override suspend fun loadDefault(userId: String): Result<BuyerDeliveryAddress?> =
            Result.success(null)

        override suspend fun saveDefault(
            address: BuyerDeliveryAddress
        ): Result<BuyerDeliveryAddress> {
            saveCalled = true
            return Result.success(address)
        }
    }
}

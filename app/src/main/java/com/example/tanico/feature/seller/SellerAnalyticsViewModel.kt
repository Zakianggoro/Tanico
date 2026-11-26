package com.example.tanico.feature.seller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tanico.core.AppContainer
import com.example.tanico.domain.model.SellerAnalytics
import com.example.tanico.domain.repository.SellerAnalyticsRepository
import com.example.tanico.domain.repository.SellerStoreRepository
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class SellerAnalyticsViewModelFactory(
    private val repository: SellerAnalyticsRepository = AppContainer.sellerAnalyticsRepository,
    private val storeRepository: SellerStoreRepository = AppContainer.sellerStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SellerAnalyticsViewModel::class.java))
        return SellerAnalyticsViewModel(repository, storeRepository) as T
    }
}

data class SellerAnalyticsUiState(
    val isLoading: Boolean = true,
    val analytics: SellerAnalytics? = null,
    val storeMissing: Boolean = false,
    val message: String? = null,
    val isCreatingStore: Boolean = false,
    val storeCreationMessage: String? = null
)

class SellerAnalyticsViewModel(
    private val repository: SellerAnalyticsRepository,
    private val storeRepository: SellerStoreRepository
) : ViewModel() {
    private val mutableUiState = mutableStateOf(SellerAnalyticsUiState())
    val uiState: State<SellerAnalyticsUiState> = mutableUiState

    fun load(userId: String) {
        viewModelScope.launch {
            mutableUiState.value = SellerAnalyticsUiState(isLoading = true)
            refreshOverview(userId)
        }
    }

    fun createStore(
        ownerId: String,
        storeName: String,
        address: String,
        latitudeText: String,
        longitudeText: String
    ) {
        if (storeName.isBlank()) {
            showStoreCreationMessage("Masukkan nama toko.")
            return
        }
        if (address.isBlank()) {
            showStoreCreationMessage("Masukkan alamat lengkap toko.")
            return
        }
        val latitude = latitudeText.trim().toDoubleOrNull()
            ?.takeIf { it.isFinite() && it in -90.0..90.0 }
        if (latitude == null) {
            showStoreCreationMessage("Latitude toko harus berupa angka antara -90 dan 90.")
            return
        }
        val longitude = longitudeText.trim().toDoubleOrNull()
            ?.takeIf { it.isFinite() && it in -180.0..180.0 }
        if (longitude == null) {
            showStoreCreationMessage("Longitude toko harus berupa angka antara -180 dan 180.")
            return
        }

        viewModelScope.launch {
            mutableUiState.value = mutableUiState.value.copy(
                isCreatingStore = true,
                storeCreationMessage = null
            )
            storeRepository.createStore(
                ownerId = ownerId,
                storeName = storeName.trim(),
                address = address.trim(),
                latitude = latitude,
                longitude = longitude
            ).onSuccess {
                mutableUiState.value = SellerAnalyticsUiState(isLoading = true)
                refreshOverview(ownerId)
            }.onFailure { exception ->
                mutableUiState.value = mutableUiState.value.copy(
                    isCreatingStore = false,
                    storeCreationMessage = exception.message?.takeIf(String::isNotBlank)
                        ?: "Informasi toko tidak dapat disimpan. Coba lagi."
                )
            }
        }
    }

    private fun showStoreCreationMessage(message: String) {
        mutableUiState.value = mutableUiState.value.copy(storeCreationMessage = message)
    }

    private suspend fun refreshOverview(userId: String) {
        repository.loadOverview(userId)
            .onSuccess { analytics ->
                mutableUiState.value = SellerAnalyticsUiState(
                    isLoading = false,
                    analytics = analytics,
                    storeMissing = analytics == null
                )
            }
            .onFailure { exception ->
                mutableUiState.value = SellerAnalyticsUiState(
                    isLoading = false,
                    message = exception.message?.takeIf(String::isNotBlank)
                        ?: "Data analitik tidak dapat dimuat. Coba lagi."
                )
            }
    }
}

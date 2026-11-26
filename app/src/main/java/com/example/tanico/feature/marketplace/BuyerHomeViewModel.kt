package com.example.tanico.feature.marketplace

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tanico.BuildConfig
import com.example.tanico.core.AppContainer
import com.example.tanico.domain.model.DeviceCoordinates
import com.example.tanico.domain.model.MarketplaceProduct
import com.example.tanico.domain.repository.MarketplaceRepository
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class BuyerHomeViewModelFactory(
    private val repository: MarketplaceRepository = AppContainer.marketplaceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(BuyerHomeViewModel::class.java))
        return BuyerHomeViewModel(repository) as T
    }
}

data class BuyerHomeUiState(
    val isLoading: Boolean = false,
    val isLocationLoading: Boolean = false,
    val products: List<MarketplaceProduct> = emptyList(),
    val message: String? = null,
    val coordinates: DeviceCoordinates? = null,
    val isShowingAllProductsForDebug: Boolean = false,
    val locationMessage: String? = null,
    val isLocationPermissionDenied: Boolean = false,
    val canRetry: Boolean = true
)

class BuyerHomeViewModel(
    private val repository: MarketplaceRepository
) : ViewModel() {
    private val mutableUiState = mutableStateOf(BuyerHomeUiState())
    val uiState: State<BuyerHomeUiState> = mutableUiState
    private var cachedCoordinates: DeviceCoordinates? = null

    fun beginLocationRequest() {
        mutableUiState.value = mutableUiState.value.copy(
            isLoading = true,
            isLocationLoading = true,
            message = null,
            isShowingAllProductsForDebug = false,
            locationMessage = null,
            isLocationPermissionDenied = false
        )
    }

    fun locationUnavailable(
        message: String,
        permissionDenied: Boolean = false,
        canRetry: Boolean = true
    ) {
        mutableUiState.value = mutableUiState.value.copy(
            isLoading = false,
            isLocationLoading = false,
            locationMessage = message,
            isLocationPermissionDenied = permissionDenied,
            canRetry = canRetry
        )
    }

    fun loadAtLocation(coordinates: DeviceCoordinates) {
        cachedCoordinates = coordinates
        mutableUiState.value = mutableUiState.value.copy(
            coordinates = coordinates,
            isLocationLoading = false,
            locationMessage = null,
            isLocationPermissionDenied = false
        )
        loadNearbyProducts(coordinates, alreadyLoading = true)
    }

    fun retry(): Boolean {
        if (mutableUiState.value.isShowingAllProductsForDebug) {
            loadAllActiveProductsForDebug()
            return true
        }
        val coordinates = cachedCoordinates ?: return false
        loadNearbyProducts(coordinates)
        return true
    }

    fun loadAllActiveProductsForDebug() {
        check(BuildConfig.DEBUG) { "Unfiltered product loading is available only in debug builds." }
        if (mutableUiState.value.isLoading) return

        mutableUiState.value = mutableUiState.value.copy(
            isLoading = true,
            message = null,
            isShowingAllProductsForDebug = true
        )
        viewModelScope.launch {
            repository.loadAllActiveProductsForDebug()
                .onSuccess { products ->
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        products = products,
                        message = null,
                        isShowingAllProductsForDebug = true
                    )
                }
                .onFailure { exception ->
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        message = exception.message?.takeIf(String::isNotBlank)
                            ?: "Produk belum dapat dimuat. Coba lagi."
                    )
                }
        }
    }

    private fun loadNearbyProducts(
        coordinates: DeviceCoordinates,
        alreadyLoading: Boolean = false
    ) {
        if (mutableUiState.value.isLoading && !alreadyLoading) return
        mutableUiState.value = mutableUiState.value.copy(isLoading = true, message = null)
        viewModelScope.launch {
            repository.loadNearbyActiveProducts(
                latitude = coordinates.latitude,
                longitude = coordinates.longitude,
                radiusKm = NEARBY_RADIUS_KM
            )
                .onSuccess { products ->
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        products = products,
                        message = null
                    )
                }
                .onFailure { exception ->
                    mutableUiState.value = mutableUiState.value.copy(
                        isLoading = false,
                        message = exception.message?.takeIf(String::isNotBlank)
                            ?: "Produk belum dapat dimuat. Coba lagi."
                    )
                }
        }
    }

    fun reset() {
        cachedCoordinates = null
        mutableUiState.value = BuyerHomeUiState()
    }

    companion object {
        const val NEARBY_RADIUS_KM = 25.0
    }
}

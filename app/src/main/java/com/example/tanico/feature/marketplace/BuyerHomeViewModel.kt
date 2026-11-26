package com.example.tanico.feature.marketplace

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tanico.core.AppContainer
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
    val products: List<MarketplaceProduct> = emptyList(),
    val message: String? = null
)

class BuyerHomeViewModel(
    private val repository: MarketplaceRepository
) : ViewModel() {
    private val mutableUiState = mutableStateOf(BuyerHomeUiState())
    val uiState: State<BuyerHomeUiState> = mutableUiState

    fun load() {
        if (mutableUiState.value.isLoading) return
        mutableUiState.value = mutableUiState.value.copy(isLoading = true, message = null)
        viewModelScope.launch {
            repository.loadActiveProducts()
                .onSuccess { products ->
                    mutableUiState.value = BuyerHomeUiState(
                        isLoading = false,
                        products = products
                    )
                }
                .onFailure { exception ->
                    mutableUiState.value = BuyerHomeUiState(
                        isLoading = false,
                        message = exception.message?.takeIf(String::isNotBlank)
                            ?: "Produk belum dapat dimuat. Coba lagi."
                    )
                }
        }
    }

    fun reset() {
        mutableUiState.value = BuyerHomeUiState()
    }
}

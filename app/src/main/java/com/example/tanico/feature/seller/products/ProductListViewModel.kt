package com.example.tanico.feature.seller.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tanico.domain.model.Product
import com.example.tanico.domain.repository.SellerProductRepository
import com.example.tanico.domain.usecase.seller.UpdateProductStockUseCase
import com.example.tanico.feature.seller.toSellerMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductListUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val errorMessage: String? = null,
    val stockEditing: Product? = null,
    val stockInput: String = "",
    val stockError: String? = null,
    val confirmDeactivate: Product? = null,
    val isActionRunning: Boolean = false,
    val message: String? = null
)

class ProductListViewModel(
    private val repository: SellerProductRepository,
    private val updateProductStock: UpdateProductStockUseCase = UpdateProductStockUseCase(repository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            repository.getMyProducts().fold(
                onSuccess = { list -> _uiState.update { it.copy(isLoading = false, products = list) } },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.toSellerMessage("Gagal memuat produk"))
                    }
                }
            )
        }
    }

    // ----- stok -----
    fun onEditStock(product: Product) = _uiState.update {
        it.copy(stockEditing = product, stockInput = product.stock.toString(), stockError = null)
    }

    fun onStockInputChange(text: String) = _uiState.update {
        it.copy(stockInput = text.filter(Char::isDigit), stockError = null)
    }

    fun onDismissStock() = _uiState.update { it.copy(stockEditing = null, stockError = null) }

    fun onSaveStock() {
        val state = _uiState.value
        val product = state.stockEditing ?: return
        if (state.isActionRunning) return
        val newStock = state.stockInput.toIntOrNull()
        if (newStock == null) {
            _uiState.update { it.copy(stockError = "Masukkan angka stok yang valid") }
            return
        }
        _uiState.update { it.copy(isActionRunning = true) }
        viewModelScope.launch {
            updateProductStock(product.id, newStock).fold(
                onSuccess = { updated ->
                    _uiState.update { s ->
                        s.copy(
                            products = s.products.map { if (it.id == updated.id) updated else it },
                            stockEditing = null,
                            isActionRunning = false,
                            message = "Stok diperbarui"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isActionRunning = false, stockError = e.toSellerMessage("Gagal menyimpan stok"))
                    }
                }
            )
        }
    }

    // ----- nonaktifkan -----
    fun onAskDeactivate(product: Product) = _uiState.update { it.copy(confirmDeactivate = product) }

    fun onDismissDeactivate() = _uiState.update { it.copy(confirmDeactivate = null) }

    fun onConfirmDeactivate() {
        val state = _uiState.value
        val product = state.confirmDeactivate ?: return
        if (state.isActionRunning) return
        _uiState.update { it.copy(isActionRunning = true) }
        viewModelScope.launch {
            repository.deactivateProduct(product.id).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isActionRunning = false, confirmDeactivate = null, message = "Produk dinonaktifkan")
                    }
                    load()
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isActionRunning = false,
                            confirmDeactivate = null,
                            message = e.toSellerMessage("Gagal menonaktifkan produk")
                        )
                    }
                }
            )
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }
}

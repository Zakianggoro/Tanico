package com.example.tanico.feature.seller.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tanico.domain.model.Category
import com.example.tanico.domain.model.Product
import com.example.tanico.domain.model.ProductInput
import com.example.tanico.domain.model.ProductStatus
import com.example.tanico.domain.repository.SellerProductRepository
import com.example.tanico.domain.usecase.seller.SaveProductUseCase
import com.example.tanico.domain.usecase.seller.ValidationException
import com.example.tanico.feature.seller.toSellerMessage
import com.example.tanico.feature.seller.validationMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductFormUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isEditing: Boolean = false,
    val categories: List<Category> = emptyList(),
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val stock: String = "",
    val unit: String = "",
    val weight: String = "",
    val categoryId: String? = null,
    val status: ProductStatus = ProductStatus.ACTIVE,
    val fieldErrors: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
    val saved: Boolean = false
)

/** [productId] == null means creating a new product. */
class ProductFormViewModel(
    private val repository: SellerProductRepository,
    private val productId: String?,
    private val saveProduct: SaveProductUseCase = SaveProductUseCase(repository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormUiState(isEditing = productId != null))
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val categories = repository.getCategories().getOrElse { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.toSellerMessage("Gagal memuat kategori"))
                }
                return@launch
            }
            val product: Product? = if (productId != null) {
                repository.getProduct(productId).getOrElse { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.toSellerMessage("Gagal memuat produk"))
                    }
                    return@launch
                }
            } else {
                null
            }
            _uiState.update { s ->
                s.copy(
                    isLoading = false,
                    categories = categories,
                    categoryId = product?.categoryId ?: s.categoryId ?: categories.firstOrNull()?.id,
                    name = product?.name ?: s.name,
                    description = product?.description ?: s.description,
                    price = product?.price?.stripTrailingZeros()?.toPlainString() ?: s.price,
                    stock = product?.stock?.toString() ?: s.stock,
                    unit = product?.unit ?: s.unit,
                    weight = product?.weightPerUnitKg?.stripTrailingZeros()?.toPlainString() ?: s.weight,
                    status = product?.status ?: s.status
                )
            }
        }
    }

    fun onNameChange(v: String) = edit("name") { it.copy(name = v) }
    fun onDescriptionChange(v: String) = edit("description") { it.copy(description = v) }
    fun onPriceChange(v: String) = edit("price") { it.copy(price = v) }
    fun onStockChange(v: String) = edit("stock") { it.copy(stock = v.filter(Char::isDigit)) }
    fun onUnitChange(v: String) = edit("unit") { it.copy(unit = v) }
    fun onWeightChange(v: String) = edit("weightPerUnitKg") { it.copy(weight = v) }
    fun onCategorySelected(id: String) = edit("categoryId") { it.copy(categoryId = id) }
    fun onStatusSelected(status: ProductStatus) = edit("status") { it.copy(status = status) }

    private fun edit(field: String, change: (ProductFormUiState) -> ProductFormUiState) =
        _uiState.update { change(it).copy(fieldErrors = it.fieldErrors - field, errorMessage = null) }

    fun onSave() {
        val s = _uiState.value
        if (s.isSaving) return

        val errors = mutableMapOf<String, String>()
        val price = s.price.trim().replace(',', '.').toBigDecimalOrNull()
        val stock = s.stock.trim().toIntOrNull()
        val weight = s.weight.trim().replace(',', '.').toBigDecimalOrNull()
        val categoryId = s.categoryId
        if (price == null) errors["price"] = "Harga harus berupa angka"
        if (stock == null) errors["stock"] = "Stok harus berupa bilangan bulat"
        if (weight == null) errors["weightPerUnitKg"] = "Berat harus berupa angka (kg)"
        if (categoryId == null) errors["categoryId"] = "Pilih kategori"
        if (price == null || stock == null || weight == null || categoryId == null) {
            _uiState.update { it.copy(fieldErrors = errors) }
            return
        }

        val input = ProductInput(
            id = productId,
            categoryId = categoryId,
            name = s.name,
            description = s.description.trim().ifEmpty { null },
            price = price,
            stock = stock,
            unit = s.unit,
            weightPerUnitKg = weight,
            status = s.status
        )
        _uiState.update { it.copy(isSaving = true, fieldErrors = emptyMap(), errorMessage = null) }
        viewModelScope.launch {
            saveProduct(input).fold(
                onSuccess = { _uiState.update { it.copy(isSaving = false, saved = true) } },
                onFailure = { e ->
                    _uiState.update {
                        if (e is ValidationException) {
                            it.copy(
                                isSaving = false,
                                fieldErrors = mapOf(e.field to validationMessage(e.field, e.reason))
                            )
                        } else {
                            it.copy(isSaving = false, errorMessage = e.toSellerMessage("Gagal menyimpan produk"))
                        }
                    }
                }
            )
        }
    }
}

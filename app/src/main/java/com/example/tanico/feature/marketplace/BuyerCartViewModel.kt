package com.example.tanico.feature.marketplace

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.tanico.domain.model.MarketplaceProduct

data class BuyerCartItem(
    val product: MarketplaceProduct,
    val quantity: Int
)

data class BuyerCartUiState(
    val items: List<BuyerCartItem> = emptyList()
) {
    val productCount: Int
        get() = items.sumOf(BuyerCartItem::quantity)

    val subtotal: Double
        get() = items.sumOf { it.product.price * it.quantity }
}

class BuyerCartViewModel : ViewModel() {
    private val mutableUiState = mutableStateOf(BuyerCartUiState())
    val uiState: State<BuyerCartUiState> = mutableUiState

    private var boundUserId: String? = null

    fun bindToUser(userId: String?) {
        if (boundUserId == userId) return
        boundUserId = userId
        mutableUiState.value = BuyerCartUiState()
    }

    fun addProduct(product: MarketplaceProduct, quantity: Int = 1) {
        if (product.stock <= 0 || quantity <= 0) return

        val currentItems = mutableUiState.value.items
        val existingItem = currentItems.firstOrNull { it.product.id == product.id }
        val updatedQuantity = ((existingItem?.quantity ?: 0) + quantity)
            .coerceAtMost(product.stock)
        val updatedItems = if (existingItem == null) {
            currentItems + BuyerCartItem(product, updatedQuantity)
        } else {
            currentItems.map { item ->
                if (item.product.id == product.id) {
                    item.copy(product = product, quantity = updatedQuantity)
                } else {
                    item
                }
            }
        }
        mutableUiState.value = BuyerCartUiState(updatedItems)
    }

    fun setQuantity(productId: String, quantity: Int) {
        val currentItems = mutableUiState.value.items
        val item = currentItems.firstOrNull { it.product.id == productId } ?: return
        val boundedQuantity = quantity.coerceAtMost(item.product.stock)
        val updatedItems = if (boundedQuantity <= 0) {
            currentItems.filterNot { it.product.id == productId }
        } else {
            currentItems.map {
                if (it.product.id == productId) it.copy(quantity = boundedQuantity) else it
            }
        }
        mutableUiState.value = BuyerCartUiState(updatedItems)
    }

    fun removeProduct(productId: String) {
        mutableUiState.value = BuyerCartUiState(
            mutableUiState.value.items.filterNot { it.product.id == productId }
        )
    }

    fun clear() {
        mutableUiState.value = BuyerCartUiState()
    }
}

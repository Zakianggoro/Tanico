package com.example.tanico.feature.seller.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tanico.domain.model.SellerOrder
import com.example.tanico.domain.repository.SellerOrderRepository
import com.example.tanico.feature.seller.toSellerMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** PROPOSED status value, must match the server RPC (see seller_order_actions migration). */
internal const val STATUS_PENDING = "pending"

data class IncomingOrdersUiState(
    val isLoading: Boolean = true,
    val orders: List<SellerOrder> = emptyList(),
    val errorMessage: String? = null,
    val processingOrderId: String? = null,
    val rejecting: SellerOrder? = null,
    val rejectReason: String = "",
    val message: String? = null
)

class IncomingOrdersViewModel(
    private val repository: SellerOrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomingOrdersUiState())
    val uiState: StateFlow<IncomingOrdersUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            repository.getMyOrders().fold(
                onSuccess = { list -> _uiState.update { it.copy(isLoading = false, orders = list) } },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.toSellerMessage("Gagal memuat pesanan"))
                    }
                }
            )
        }
    }

    fun onAccept(order: SellerOrder) = runAction(order.id, "Pesanan diterima") {
        repository.acceptOrder(order.id)
    }

    fun onAskReject(order: SellerOrder) = _uiState.update { it.copy(rejecting = order, rejectReason = "") }
    fun onRejectReasonChange(text: String) = _uiState.update { it.copy(rejectReason = text) }
    fun onDismissReject() = _uiState.update { it.copy(rejecting = null) }

    fun onConfirmReject() {
        val state = _uiState.value
        val order = state.rejecting ?: return
        val reason = state.rejectReason.trim().ifEmpty { null }
        _uiState.update { it.copy(rejecting = null) }
        runAction(order.id, "Pesanan ditolak, stok dikembalikan") {
            repository.rejectOrder(order.id, reason)
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    private fun runAction(orderId: String, successMessage: String, call: suspend () -> Result<Unit>) {
        if (_uiState.value.processingOrderId != null) return // block double taps
        _uiState.update { it.copy(processingOrderId = orderId) }
        viewModelScope.launch {
            call().fold(
                onSuccess = {
                    _uiState.update { it.copy(processingOrderId = null, message = successMessage) }
                    load()
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(processingOrderId = null, message = e.toSellerMessage("Gagal memproses pesanan"))
                    }
                }
            )
        }
    }
}

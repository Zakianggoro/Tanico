package com.example.tanico.feature.seller.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tanico.core.AppContainer
import com.example.tanico.domain.model.SellerOrder
import com.example.tanico.feature.seller.label
import com.example.tanico.feature.seller.toDisplayDate
import com.example.tanico.feature.seller.toRupiah

/** Entry point of the seller "Pesanan" tab. */
@Composable
fun SellerOrdersScreen(modifier: Modifier = Modifier) {
    val viewModel: IncomingOrdersViewModel = viewModel(
        factory = viewModelFactory {
            initializer { IncomingOrdersViewModel(AppContainer.sellerOrderRepository) }
        }
    )
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pesanan Masuk", style = MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick = viewModel::load) { Text("Muat ulang") }
            }
            Spacer(Modifier.height(12.dp))

            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            when {
                state.isLoading && state.orders.isEmpty() ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                state.orders.isEmpty() && state.errorMessage == null ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada pesanan masuk.")
                    }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.orders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            isProcessing = state.processingOrderId == order.id,
                            actionsEnabled = state.processingOrderId == null,
                            onAccept = { viewModel.onAccept(order) },
                            onReject = { viewModel.onAskReject(order) }
                        )
                    }
                }
            }
        }
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
    }

    state.rejecting?.let { order ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissReject,
            title = { Text("Tolak pesanan?") },
            text = {
                Column {
                    Text("Stok produk akan dikembalikan. Alasan akan dikirim ke pembeli.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.rejectReason,
                        onValueChange = viewModel::onRejectReasonChange,
                        label = { Text("Alasan (opsional)") }
                    )
                }
            },
            confirmButton = { TextButton(onClick = viewModel::onConfirmReject) { Text("Tolak") } },
            dismissButton = { TextButton(onClick = viewModel::onDismissReject) { Text("Batal") } }
        )
    }
}

@Composable
private fun OrderCard(
    order: SellerOrder,
    isProcessing: Boolean,
    actionsEnabled: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Pesanan #${order.id.take(8)}", style = MaterialTheme.typography.titleMedium)
            Text(order.createdAt.toDisplayDate(), style = MaterialTheme.typography.bodySmall)
            Text(
                "Status: ${order.status.label()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            order.items.forEach { item ->
                Text("• ${item.productName} × ${item.quantity} ${item.unit} (${item.subtotal.toRupiah()})")
            }
            Text("Subtotal: ${order.subtotal.toRupiah()}")
            Text("Ongkir: ${order.shippingFee.toRupiah()}")
            Text("Total: ${order.total.toRupiah()}", style = MaterialTheme.typography.titleSmall)

            if (order.status.value == STATUS_PENDING) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAccept, enabled = actionsEnabled) {
                        Text(if (isProcessing) "Memproses..." else "Terima")
                    }
                    OutlinedButton(onClick = onReject, enabled = actionsEnabled) { Text("Tolak") }
                }
            }
        }
    }
}

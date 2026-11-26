package com.example.tanico.feature.seller.products

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tanico.domain.model.Product
import com.example.tanico.domain.model.ProductStatus
import com.example.tanico.feature.seller.label
import com.example.tanico.feature.seller.toRupiah

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
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
                Text("Produk Saya", style = MaterialTheme.typography.titleLarge)
                Button(onClick = onAddProduct) { Text("Tambah produk") }
            }
            Spacer(Modifier.height(12.dp))

            state.errorMessage?.let { error ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f))
                    TextButton(onClick = viewModel::load) { Text("Coba lagi") }
                }
            }

            when {
                state.isLoading && state.products.isEmpty() ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                state.products.isEmpty() && state.errorMessage == null ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada produk. Ketuk \"Tambah produk\" untuk memulai.")
                    }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onEdit = { onEditProduct(product) },
                            onEditStock = { viewModel.onEditStock(product) },
                            onDeactivate = { viewModel.onAskDeactivate(product) }
                        )
                    }
                }
            }
        }
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
    }

    state.stockEditing?.let { product ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissStock,
            title = { Text("Ubah stok ${product.name}") },
            text = {
                OutlinedTextField(
                    value = state.stockInput,
                    onValueChange = viewModel::onStockInputChange,
                    label = { Text("Stok (${product.unit})") },
                    isError = state.stockError != null,
                    supportingText = { state.stockError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::onSaveStock, enabled = !state.isActionRunning) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = viewModel::onDismissStock) { Text("Batal") } }
        )
    }

    state.confirmDeactivate?.let { product ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissDeactivate,
            title = { Text("Nonaktifkan produk?") },
            text = { Text("${product.name} tidak akan tampil lagi di katalog pembeli. Riwayat pesanan tetap tersimpan.") },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDeactivate, enabled = !state.isActionRunning) {
                    Text("Nonaktifkan")
                }
            },
            dismissButton = { TextButton(onClick = viewModel::onDismissDeactivate) { Text("Batal") } }
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onEdit: () -> Unit,
    onEditStock: () -> Unit,
    onDeactivate: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text("${product.price.toRupiah()} / ${product.unit}")
            Text("Stok: ${product.stock} ${product.unit}")
            Text(
                "Status: ${product.status.label()}",
                style = MaterialTheme.typography.labelMedium,
                color = if (product.status == ProductStatus.ACTIVE) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Row {
                TextButton(onClick = onEdit) { Text("Ubah") }
                TextButton(onClick = onEditStock) { Text("Ubah stok") }
                if (product.status != ProductStatus.INACTIVE) {
                    TextButton(onClick = onDeactivate) { Text("Nonaktifkan") }
                }
            }
        }
    }
}

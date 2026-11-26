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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tanico.domain.model.ProductStatus
import com.example.tanico.feature.seller.label

@Composable
fun ProductFormScreen(
    viewModel: ProductFormViewModel,
    onClose: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    if (state.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                if (state.isEditing) "Ubah Produk" else "Tambah Produk",
                style = MaterialTheme.typography.titleLarge
            )

            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = viewModel::load) { Text("Muat ulang") }
            }

            FormField("Nama produk", state.name, viewModel::onNameChange, state.fieldErrors["name"])
            FormField(
                "Deskripsi (opsional)", state.description, viewModel::onDescriptionChange,
                state.fieldErrors["description"], singleLine = false
            )
            FormField(
                "Harga (Rp)", state.price, viewModel::onPriceChange, state.fieldErrors["price"],
                KeyboardType.Decimal
            )
            FormField(
                "Stok", state.stock, viewModel::onStockChange, state.fieldErrors["stock"],
                KeyboardType.Number
            )
            FormField(
                "Satuan (mis. kg, karung, ikat)", state.unit, viewModel::onUnitChange,
                state.fieldErrors["unit"]
            )
            FormField(
                "Berat per satuan (kg)", state.weight, viewModel::onWeightChange,
                state.fieldErrors["weightPerUnitKg"], KeyboardType.Decimal
            )

            Text("Kategori", style = MaterialTheme.typography.labelLarge)
            if (state.categories.isEmpty()) {
                Text("Belum ada kategori. Tambahkan kategori di database terlebih dahulu.")
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.categories, key = { it.id }) { category ->
                        FilterChip(
                            selected = state.categoryId == category.id,
                            onClick = { viewModel.onCategorySelected(category.id) },
                            label = { Text(category.name) }
                        )
                    }
                }
            }
            state.fieldErrors["categoryId"]?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Text("Status", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ProductStatus.entries.toList(), key = { it.value }) { status ->
                    FilterChip(
                        selected = state.status == status,
                        onClick = { viewModel.onStatusSelected(status) },
                        label = { Text(status.label()) }
                    )
                }
            }

            Text(
                "Hanya produk berstatus Aktif yang tampil di marketplace pembeli.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::onSave, enabled = !state.isSaving) {
                    Text(if (state.isSaving) "Menyimpan..." else "Simpan")
                }
                OutlinedButton(onClick = onClose, enabled = !state.isSaving) { Text("Batal") }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        modifier = Modifier.fillMaxWidth()
    )
}

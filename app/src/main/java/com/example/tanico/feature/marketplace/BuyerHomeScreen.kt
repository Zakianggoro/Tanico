package com.example.tanico.feature.marketplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tanico.BuildConfig
import com.example.tanico.domain.model.DeviceCoordinates
import com.example.tanico.domain.model.MarketplaceProduct
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.roundToLong

@Composable
fun BuyerHomeScreen(
    fullName: String,
    state: BuyerHomeUiState,
    onRetry: () -> Unit,
    onBrowseStores: () -> Unit,
    onCalibrateLocation: () -> Unit,
    onShowAllProductsForDebug: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    cartItemCount: Int,
    onOpenCart: () -> Unit,
    onOpenProduct: (MarketplaceProduct) -> Unit,
    onAddToCart: (MarketplaceProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Semua") }
    val categories = listOf("Semua") + state.products
        .map(MarketplaceProduct::categoryName)
        .distinct()
    val activeCategory = selectedCategory.takeIf(categories::contains) ?: "Semua"
    val normalizedQuery = searchQuery.trim().lowercase(Locale.getDefault())
    val filteredProducts = state.products.filter { product ->
        (activeCategory == "Semua" || product.categoryName == activeCategory) &&
            (normalizedQuery.isBlank() ||
                product.name.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                product.categoryName.lowercase(Locale.getDefault()).contains(normalizedQuery))
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "TANICO  •  Beranda",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Halo, ${fullName.ifBlank { "Mitra TANICO" }}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            OutlinedButton(onClick = onOpenCart) {
                Text("Keranjang $cartItemCount")
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Cari produk atau kategori") },
            shape = RoundedCornerShape(16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "⌖",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            "Lokasi perangkat",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            state.coordinates?.let(::formatCoordinates)
                                ?: if (state.isLocationLoading) "Mengambil lokasi..." else "Belum tersedia",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            when {
                                state.isLocationLoading -> "Mengambil lokasi satu kali..."
                                state.isShowingAllProductsForDebug ->
                                    "Mode debug: menampilkan semua produk aktif tanpa filter jarak."
                                state.locationMessage != null -> state.locationMessage
                                else -> "Diambil saat aplikasi dibuka atau dikalibrasi, tanpa pelacakan aktif."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.locationMessage != null &&
                                !state.isShowingAllProductsForDebug
                            ) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (state.isLocationPermissionDenied) {
                        TextButton(
                            onClick = onOpenLocationSettings,
                            enabled = !state.isLoading
                        ) {
                            Text("Pengaturan")
                        }
                    }
                    if (state.canRetry) {
                        TextButton(
                            onClick = onCalibrateLocation,
                            enabled = !state.isLoading
                        ) {
                            Text(if (state.coordinates == null) "Kalibrasi lokasi" else "Kalibrasi ulang")
                        }
                    }
                }
                if (
                    BuildConfig.DEBUG &&
                    state.coordinates == null &&
                    state.locationMessage != null &&
                    !state.isShowingAllProductsForDebug
                ) {
                    TextButton(
                        onClick = onShowAllProductsForDebug,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Debug: tampilkan semua produk aktif")
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Manfaatkan hasil samping pertanian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (state.isShowingAllProductsForDebug) {
                        "Mode debug aktif: produk ditampilkan tanpa filter jarak."
                    } else {
                        "Temukan produk aktif dari penjual dalam radius 25 km dari lokasi Anda."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        TextButton(
            onClick = onBrowseStores,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Jelajahi toko  →")
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Kategori limbah",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = activeCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Produk tersedia",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "${filteredProducts.size} produk",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.message != null -> Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Produk terdekat belum dapat dimuat",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(state.message, style = MaterialTheme.typography.bodyMedium)
                    if (state.canRetry) {
                        TextButton(onClick = onRetry) { Text("Coba lagi") }
                    }
                }
            }

            state.coordinates == null &&
                state.locationMessage != null &&
                !state.isShowingAllProductsForDebug -> Unit

            state.products.isEmpty() -> Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        if (state.isShowingAllProductsForDebug) {
                            "Belum ada produk aktif"
                        } else {
                            "Belum ada produk terdekat"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        if (state.isShowingAllProductsForDebug) {
                            "Belum ada produk aktif yang dipublikasikan."
                        } else {
                            "Belum ada produk aktif dalam jarak 25 km dari lokasi Anda."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            filteredProducts.isEmpty() -> Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    "Produk tidak ditemukan. Coba kata kunci atau kategori lain.",
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> filteredProducts.chunked(2).forEach { productsInRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    productsInRow.forEach { product ->
                        BuyerProductCard(
                            product = product,
                            onOpenProduct = { onOpenProduct(product) },
                            onAddToCart = { onAddToCart(product) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (productsInRow.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun BuyerProductCard(
    product: MarketplaceProduct,
    onOpenProduct: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌾", style = MaterialTheme.typography.displaySmall)
                }
            }
            Text(
                product.categoryName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${formatProductPrice(product.price)} / ${product.unit}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Stok ${product.stock} ${product.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            product.distanceKm?.let { distanceKm ->
                Text(
                    formatDistance(distanceKm),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenProduct,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Text("Detail")
                }
                Button(
                    onClick = onAddToCart,
                    enabled = product.stock > 0,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Text("Tambah")
                }
            }
        }
    }
}

internal fun formatProductPrice(price: Double): String = NumberFormat
    .getCurrencyInstance(Locale.forLanguageTag("id-ID"))
    .apply {
        currency = Currency.getInstance("IDR")
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }
    .format(price.roundToLong())

private fun formatCoordinates(coordinates: DeviceCoordinates): String =
    String.format(
        Locale.US,
        "%.5f, %.5f",
        coordinates.latitude,
        coordinates.longitude
    )

internal fun formatDistance(distanceKm: Double): String = NumberFormat
    .getNumberInstance(Locale.forLanguageTag("id-ID"))
    .apply {
        maximumFractionDigits = 1
        minimumFractionDigits = 1
    }
    .format(distanceKm) + " km"

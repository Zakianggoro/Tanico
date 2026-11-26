package com.example.tanico.feature.marketplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    onCalibrateLocation: () -> Unit,
    onOpenLocationSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "TANICO  •  Beranda",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Halo, ${fullName.ifBlank { "Mitra TANICO" }}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

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
                                state.locationMessage != null -> state.locationMessage
                                else -> "Diambil saat aplikasi dibuka atau dikalibrasi, tanpa pelacakan aktif."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.locationMessage != null) {
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
                    "Temukan produk aktif dari penjual dalam radius 25 km dari lokasi Anda.",
                    style = MaterialTheme.typography.bodyMedium
                )
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
                "${state.products.size} produk",
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

            state.coordinates == null && state.locationMessage != null -> Unit

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
                        "Belum ada produk terdekat",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Belum ada produk aktif dalam jarak 25 km dari lokasi Anda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> state.products.forEach { product ->
                BuyerProductCard(product)
            }
        }
    }
}

@Composable
private fun BuyerProductCard(product: MarketplaceProduct) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(82.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌾", style = MaterialTheme.typography.headlineMedium)
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    product.categoryName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                product.description?.takeIf(String::isNotBlank)?.let { description ->
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    "${formatProductPrice(product.price)}/${product.unit}  •  Stok ${product.stock} ${product.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                product.distanceKm?.let { distanceKm ->
                    Text(
                        "Sekitar ${formatDistance(distanceKm)} dari lokasi Anda",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatProductPrice(price: Double): String = NumberFormat
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

private fun formatDistance(distanceKm: Double): String = NumberFormat
    .getNumberInstance(Locale.forLanguageTag("id-ID"))
    .apply {
        maximumFractionDigits = 1
        minimumFractionDigits = 1
    }
    .format(distanceKm) + " km"

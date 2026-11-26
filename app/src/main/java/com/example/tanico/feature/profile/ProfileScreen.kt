package com.example.tanico.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tanico.core.ui.TanicoPrimaryButton
import com.example.tanico.core.ui.TanicoSecondaryButton
import com.example.tanico.core.ui.TanicoMapPinPicker
import com.example.tanico.core.ui.TanicoTextField
import com.example.tanico.domain.model.UserRole
import com.example.tanico.feature.seller.SellerAnalyticsUiState
import com.example.tanico.feature.seller.SellerRevenueChart
import com.example.tanico.feature.seller.formatRupiah

@Composable
fun ProfileScreen(
    role: UserRole,
    isPreviewMode: Boolean,
    fullName: String,
    phoneNumber: String,
    email: String,
    location: String,
    storeName: String,
    storeAddress: String,
    storeLatitude: String,
    storeLongitude: String,
    isEditing: Boolean,
    isLoading: Boolean,
    message: String?,
    analyticsState: SellerAnalyticsUiState,
    onAnalyticsRetry: () -> Unit,
    onFullNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onLocationChanged: (String) -> Unit,
    onStoreNameChanged: (String) -> Unit,
    onStoreAddressChanged: (String) -> Unit,
    onStoreLatitudeChanged: (String) -> Unit,
    onStoreLongitudeChanged: (String) -> Unit,
    onCreateStore: (String, String, String, String) -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onSignOut: () -> Unit,
    onExitPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                Text("Profil Saya", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = if (role == UserRole.Buyer) {
                        "Kelola informasi akun pembeli Anda."
                    } else {
                        "Kelola informasi akun dan toko Anda."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!isEditing) {
                TextButton(onClick = onEdit, enabled = !isLoading) {
                    Text("Ubah")
                }
            }
        }

        ProfileIdentityCard(
            role = role,
            fullName = fullName,
            location = location,
            storeName = storeName,
            isPreviewMode = isPreviewMode
        )

        if (role == UserRole.Seller) {
            SellerDashboardSections(
                isPreviewMode = isPreviewMode,
                state = analyticsState,
                onRetry = onAnalyticsRetry,
                onEditProfile = onEdit,
                onOpenAnalytics = onOpenAnalytics
            )
        }

        ProfileSectionCard(title = "Informasi Akun") {
            if (isEditing) {
                TanicoTextField(
                    value = fullName,
                    onValueChange = onFullNameChanged,
                    label = "Nama lengkap",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                if (isPreviewMode) {
                    TanicoTextField(
                        value = phoneNumber,
                        onValueChange = onPhoneNumberChanged,
                        label = "Nomor telepon",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    TanicoTextField(
                        value = email,
                        onValueChange = onEmailChanged,
                        label = "Alamat email",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    TanicoTextField(
                        value = location,
                        onValueChange = onLocationChanged,
                        label = "Domisili",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                } else {
                    ProfileDetail(label = "Nomor telepon", value = phoneNumber)
                    ProfileDetail(label = "Alamat email", value = email)
                    ProfileDetail(label = "Domisili", value = location)
                }
            } else {
                ProfileDetail(label = "Nama lengkap", value = fullName)
                ProfileDetail(label = "Nomor telepon", value = phoneNumber)
                ProfileDetail(label = "Alamat email", value = email)
                ProfileDetail(label = "Domisili", value = location)
            }
        }

        if (role == UserRole.Buyer) {
            ProfileSectionCard(title = "Alamat Pengiriman") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Belum ada alamat tersimpan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Alamat pengiriman dapat dikelola di sini saat fitur alamat tersedia.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            ProfileSectionCard(title = "Informasi Toko") {
                if (analyticsState.storeMissing && !isPreviewMode) {
                    Text(
                        "Lengkapi detail dan koordinat lokasi toko yang sebenarnya untuk mengaktifkan toko.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TanicoTextField(
                        value = storeName,
                        onValueChange = onStoreNameChanged,
                        label = "Nama toko",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !analyticsState.isCreatingStore
                    )
                    TanicoTextField(
                        value = storeAddress,
                        onValueChange = onStoreAddressChanged,
                        label = "Alamat lengkap toko",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !analyticsState.isCreatingStore
                    )
                    TanicoMapPinPicker(
                        latitude = storeLatitude,
                        longitude = storeLongitude,
                        onLocationSelected = { latitude, longitude ->
                            onStoreLatitudeChanged(latitude)
                            onStoreLongitudeChanged(longitude)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    analyticsState.storeCreationMessage?.let { error ->
                        Text(
                            error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    TanicoPrimaryButton(
                        text = if (analyticsState.isCreatingStore) "Menyimpan..." else "Buat Toko",
                        onClick = {
                            onCreateStore(
                                storeName,
                                storeAddress,
                                storeLatitude,
                                storeLongitude
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !analyticsState.isCreatingStore
                    )
                } else if (isPreviewMode && isEditing) {
                    TanicoTextField(
                        value = storeName,
                        onValueChange = onStoreNameChanged,
                        label = "Nama toko",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    TanicoTextField(
                        value = storeAddress,
                        onValueChange = onStoreAddressChanged,
                        label = "Alamat operasional toko",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    TanicoMapPinPicker(
                        latitude = storeLatitude,
                        longitude = storeLongitude,
                        onLocationSelected = { latitude, longitude ->
                            onStoreLatitudeChanged(latitude)
                            onStoreLongitudeChanged(longitude)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ProfileDetail(label = "Nama toko", value = storeName)
                    ProfileDetail(label = "Alamat operasional", value = storeAddress)
                    if (!isPreviewMode) {
                        Text(
                            "Pengaturan toko akan tersedia pada tahap onboarding penjual.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        message?.let {
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (isEditing) {
            TanicoPrimaryButton(
                text = if (isLoading) "Menyimpan..." else if (isPreviewMode) "Simpan pratinjau" else "Simpan perubahan",
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
        }
        TanicoSecondaryButton(
            text = if (isPreviewMode) "Kembali ke Login" else "Keluar dari Akun",
            onClick = if (isPreviewMode) onExitPreview else onSignOut,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
    }
}

@Composable
private fun SellerDashboardSections(
    isPreviewMode: Boolean,
    state: SellerAnalyticsUiState,
    onRetry: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenAnalytics: () -> Unit
) {
    val analytics = state.analytics
    val revenueText = when {
        isPreviewMode -> "Tidak tersedia"
        state.isLoading -> "Memuat..."
        analytics != null -> formatRupiah(analytics.completedRevenue)
        state.storeMissing -> "Toko belum dibuat"
        else -> "Belum tersedia"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "OMZET PRODUK SELESAI",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
            )
            Text(
                revenueText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Subtotal produk dari pesanan selesai, tidak termasuk ongkir.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.88f)
            )
            if (!isPreviewMode) {
                TextButton(onClick = onOpenAnalytics) {
                    Text("Lihat analitik", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }

    ProfileSectionCard(title = "Ringkasan Penjualan") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SellerMetricCard(
                label = "Pesanan selesai",
                value = analytics?.completedOrderCount?.toString() ?: "—",
                modifier = Modifier.weight(1f)
            )
            SellerMetricCard(
                label = "Menunggu diproses",
                value = analytics?.pendingOrderCount?.toString() ?: "—",
                modifier = Modifier.weight(1f)
            )
        }

        when {
            isPreviewMode -> Text(
                "Data penjualan hanya tersedia untuk akun penjual yang terhubung.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.isLoading -> Text(
                "Memuat ringkasan penjualan...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.message != null -> {
                Text(
                    state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = onRetry) {
                    Text("Coba lagi")
                }
            }
            state.storeMissing -> Text(
                "Profil toko belum tersedia. Ringkasan akan muncul setelah toko dibuat.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    ProfileSectionCard(title = "Aksi Cepat") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onEditProfile) {
                Text("Ubah Profil")
            }
            TextButton(onClick = onOpenAnalytics, enabled = !isPreviewMode) {
                Text("Analitik")
            }
        }
    }

    ProfileSectionCard(title = "Tren Omzet Produk") {
        if (analytics != null) {
            Text(
                "Enam bulan terakhir",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SellerRevenueChart(analytics.monthlyRevenue)
            Text(
                "Omzet bukan laba atau saldo yang dapat ditarik.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                when {
                    isPreviewMode -> "Data analitik tidak tersedia dalam mode pratinjau."
                    state.isLoading -> "Memuat tren omzet..."
                    state.storeMissing -> "Grafik akan tersedia setelah profil toko dibuat."
                    state.message != null -> "Tren omzet belum dapat dimuat."
                    else -> "Grafik akan tampil setelah data penjualan tersedia."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    ProfileSectionCard(title = "Pesanan Terbaru") {
        Text(
            when {
                isPreviewMode -> "Data pesanan tidak ditampilkan dalam mode pratinjau."
                analytics?.totalOrderCount == 0 -> "Belum ada data pesanan."
                analytics != null -> "Ringkasan tersedia di Analitik. Daftar dan tindakan pesanan belum tersedia di sini."
                state.storeMissing -> "Pesanan akan tampil setelah profil toko tersedia."
                else -> "Informasi pesanan belum tersedia."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (!isPreviewMode && (analytics?.totalOrderCount ?: 0) > 0) {
            TextButton(onClick = onOpenAnalytics) {
                Text("Buka analitik")
            }
        }
    }
}

@Composable
private fun SellerMetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProfileIdentityCard(
    role: UserRole,
    fullName: String,
    location: String,
    storeName: String,
    isPreviewMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profileInitials(fullName),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = fullName.ifBlank { "Profil TANICO" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = location.ifBlank { "Domisili belum diisi" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (role == UserRole.Buyer) {
                        "Pembeli TANICO"
                    } else {
                        storeName.ifBlank { "Penjual TANICO" }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = role.displayName(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                if (isPreviewMode) {
                    Text(
                        "Mode pratinjau · belum terhubung ke akun",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
private fun ProfileDetail(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.ifBlank { "Belum diisi" },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun profileInitials(fullName: String): String = fullName
    .trim()
    .split(Regex("\\s+"))
    .mapNotNull(String::firstOrNull)
    .take(2)
    .joinToString("")
    .uppercase()
    .ifBlank { "T" }

private fun UserRole.displayName(): String = when (this) {
    UserRole.Buyer -> "Pembeli / Bio-Industri"
    UserRole.Seller -> "Penjual / Petani & Pengepul"
}

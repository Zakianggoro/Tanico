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
import com.example.tanico.feature.seller.formatCompactRupiah
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

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
    buyerAddressState: BuyerAddressUiState,
    onBuyerAddressRetry: () -> Unit,
    onBuyerAddressEdit: () -> Unit,
    onBuyerAddressCancel: () -> Unit,
    onBuyerAddressSave: () -> Unit,
    onBuyerRecipientChanged: (String) -> Unit,
    onBuyerPhoneChanged: (String) -> Unit,
    onBuyerAddressLineChanged: (String) -> Unit,
    onBuyerCityChanged: (String) -> Unit,
    onBuyerPostalCodeChanged: (String) -> Unit,
    onBuyerLatitudeChanged: (String) -> Unit,
    onBuyerLongitudeChanged: (String) -> Unit,
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
                onEditProfile = onEdit
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
            BuyerAddressSection(
                state = buyerAddressState,
                onRetry = onBuyerAddressRetry,
                onEdit = onBuyerAddressEdit,
                onCancel = onBuyerAddressCancel,
                onSave = onBuyerAddressSave,
                onRecipientChanged = onBuyerRecipientChanged,
                onPhoneChanged = onBuyerPhoneChanged,
                onAddressLineChanged = onBuyerAddressLineChanged,
                onCityChanged = onBuyerCityChanged,
                onPostalCodeChanged = onBuyerPostalCodeChanged,
                onLatitudeChanged = onBuyerLatitudeChanged,
                onLongitudeChanged = onBuyerLongitudeChanged
            )
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
private fun BuyerAddressSection(
    state: BuyerAddressUiState,
    onRetry: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onRecipientChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onAddressLineChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onPostalCodeChanged: (String) -> Unit,
    onLatitudeChanged: (String) -> Unit,
    onLongitudeChanged: (String) -> Unit
) {
    ProfileSectionCard(title = "Alamat Pengiriman") {
        when {
            state.isLoading -> Text(
                "Memuat alamat pengiriman...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            state.isEditing -> {
                if (state.address == null) {
                    Text(
                        "Tambahkan alamat utama agar pesanan dapat dikirim ke lokasi yang benar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TanicoTextField(
                    value = state.recipientName,
                    onValueChange = onRecipientChanged,
                    label = "Nama penerima",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                )
                TanicoTextField(
                    value = state.phone,
                    onValueChange = onPhoneChanged,
                    label = "Nomor telepon penerima",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                )
                TanicoTextField(
                    value = state.addressLine,
                    onValueChange = onAddressLineChanged,
                    label = "Alamat lengkap",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                )
                TanicoTextField(
                    value = state.city,
                    onValueChange = onCityChanged,
                    label = "Kota/Kabupaten",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                )
                TanicoTextField(
                    value = state.postalCode,
                    onValueChange = onPostalCodeChanged,
                    label = "Kode pos (opsional)",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                )
                TanicoMapPinPicker(
                    latitude = state.latitude,
                    longitude = state.longitude,
                    onLocationSelected = { latitude, longitude ->
                        onLatitudeChanged(latitude)
                        onLongitudeChanged(longitude)
                    },
                    locationLabel = "alamat pengiriman",
                    modifier = Modifier.fillMaxWidth()
                )
                state.message?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                TanicoPrimaryButton(
                    text = if (state.isSaving) "Menyimpan..." else "Simpan Alamat",
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                )
                if (state.address != null) {
                    TanicoSecondaryButton(
                        text = "Batal",
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving
                    )
                }
            }

            state.address == null -> {
                state.message?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onRetry) { Text("Coba lagi") }
                } ?: Text(
                    "Belum ada alamat pengiriman tersimpan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (state.message == null) {
                    TextButton(onClick = onEdit) { Text("Tambah alamat") }
                }
            }

            else -> {
                Text(
                    state.address.recipientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                ProfileDetail(label = "Nomor telepon", value = state.address.phone)
                ProfileDetail(label = "Alamat", value = state.address.address)
                ProfileDetail(
                    label = "Kota/Kabupaten",
                    value = listOfNotNull(state.address.city, state.address.postalCode)
                        .filter(String::isNotBlank)
                        .joinToString(" ")
                )
                TextButton(onClick = onEdit) { Text("Ubah alamat") }
                state.message?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun SellerDashboardSections(
    isPreviewMode: Boolean,
    state: SellerAnalyticsUiState,
    onRetry: () -> Unit,
    onEditProfile: () -> Unit
) {
    val analytics = state.analytics
    val unavailableValue = when {
        isPreviewMode -> "Tidak tersedia"
        state.isLoading -> "Memuat..."
        state.storeMissing -> "Belum ada toko"
        else -> "—"
    }
    val revenue = analytics?.currentMonthCompletedRevenue
    val revenueText = revenue?.let(::formatCompactRupiah) ?: unavailableValue
    val completedOrdersText = analytics?.currentMonthCompletedOrderCount?.toString() ?: unavailableValue
    val wasteText = analytics?.let { formatWasteTons(it.currentMonthDivertedWasteKg) } ?: unavailableValue

    ProfileSectionCard(title = "Ringkasan Bulan Ini") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            SellerMetricCard(
                label = "Omzet selesai",
                value = revenueText,
                supportingText = analytics?.let {
                    revenueComparisonLabel(
                        it.currentMonthCompletedRevenue,
                        it.previousMonthCompletedRevenue
                    )
                },
                modifier = Modifier.weight(1f)
            )
            SellerMetricCard(
                label = "Pesanan usai",
                value = completedOrdersText,
                supportingText = "Bulan ini",
                modifier = Modifier.weight(1f)
            )
            SellerMetricCard(
                label = "Limbah salur",
                value = wasteText,
                supportingText = "Bulan ini",
                modifier = Modifier.weight(1f)
            )
        }
        when {
            state.isLoading -> Text(
                "Memuat ringkasan penjualan...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.message != null -> {
                Text(
                    state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = onRetry) { Text("Coba lagi") }
            }
            state.storeMissing -> Text(
                "Ringkasan akan muncul setelah toko dibuat.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            analytics != null -> Text(
                "Omzet dan berat dihitung dari pesanan selesai. Omzet tidak termasuk ongkir.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    ProfileSectionCard(title = "Aksi Cepat Petani") {
        SellerQuickActionCard(
            title = "Ubah profil",
            description = "Kelola informasi akun dan toko",
            onClick = onEditProfile,
            modifier = Modifier.fillMaxWidth()
        )
    }

    ProfileSectionCard(title = "Ringkasan Penjualan (6 Bulan)") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            SellerMetricCard(
                label = "Omzet selesai",
                value = analytics?.let { formatCompactRupiah(it.completedRevenue) } ?: unavailableValue,
                supportingText = "Total",
                modifier = Modifier.weight(1f)
            )
            SellerMetricCard(
                label = "Pesanan selesai",
                value = analytics?.completedOrderCount?.toString() ?: unavailableValue,
                supportingText = "Selesai",
                modifier = Modifier.weight(1f)
            )
            SellerMetricCard(
                label = "Menunggu",
                value = analytics?.pendingOrderCount?.toString() ?: unavailableValue,
                supportingText = "Diproses",
                modifier = Modifier.weight(1f)
            )
        }
    }

    ProfileSectionCard(title = "Tren Penjualan Limbah") {
        if (analytics != null) {
            Text(
                "Statistik enam bulan terakhir (Rupiah)",
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
}

@Composable
private fun SellerMetricCard(
    label: String,
    value: String,
    supportingText: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            supportingText?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SellerQuickActionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatWasteTons(kilograms: Double): String {
    val tons = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
        .apply { maximumFractionDigits = 1 }
        .format(kilograms / 1_000.0)
    return "$tons ton"
}

private fun revenueComparisonLabel(currentRevenue: Long, previousRevenue: Long): String {
    if (previousRevenue <= 0L) {
        return if (currentRevenue > 0L) "Belum ada pembanding" else "Belum ada omzet bulan lalu"
    }
    val percentageChange = ((currentRevenue.toDouble() / previousRevenue) * 100 - 100).roundToInt()
    return when {
        percentageChange > 0 -> "+$percentageChange% dari bulan lalu"
        percentageChange < 0 -> "$percentageChange% dari bulan lalu"
        else -> "Sama dengan bulan lalu"
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

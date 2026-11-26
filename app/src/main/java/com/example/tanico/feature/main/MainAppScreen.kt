package com.example.tanico.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tanico.core.ui.TanicoEmptyState
import com.example.tanico.domain.model.UserRole
import com.example.tanico.feature.marketplace.BuyerHomeScreen
import com.example.tanico.feature.marketplace.BuyerHomeUiState
import com.example.tanico.feature.profile.BuyerAddressUiState
import com.example.tanico.feature.profile.ProfileScreen
import com.example.tanico.feature.seller.SellerAnalyticsUiState

enum class MainSection(val label: String, val marker: String) {
    Beranda("Beranda", "⌂"),
    Notifikasi("Notifikasi", "●"),
    Pembelajaran("Pembelajaran", "▤"),
    Profil("Profil", "○")
}

@Composable
fun MainAppScreen(
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
    editingProfile: Boolean,
    isLoading: Boolean,
    message: String?,
    analyticsState: SellerAnalyticsUiState,
    onAnalyticsRetry: () -> Unit,
    buyerHomeState: BuyerHomeUiState,
    onBuyerHomeRetry: () -> Unit,
    onBuyerHomeCalibrateLocation: () -> Unit,
    onBuyerHomeOpenLocationSettings: () -> Unit,
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
    onEditProfile: () -> Unit,
    onSaveProfile: () -> Unit,
    onSignOut: () -> Unit,
    onExitPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sections = listOf(
        MainSection.Beranda,
        MainSection.Notifikasi,
        MainSection.Pembelajaran,
        MainSection.Profil
    )
    var selectedSection by rememberSaveable(role) {
        mutableStateOf(MainSection.Beranda.name)
    }
    val section = MainSection.values().firstOrNull { it.name == selectedSection }
        ?: MainSection.Beranda

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                sections.forEach { item ->
                    NavigationBarItem(
                        selected = section == item,
                        onClick = { selectedSection = item.name },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        icon = { Text(item.marker) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (section) {
            MainSection.Profil -> ProfileScreen(
                role = role,
                isPreviewMode = isPreviewMode,
                fullName = fullName,
                phoneNumber = phoneNumber,
                email = email,
                location = location,
                storeName = storeName,
                storeAddress = storeAddress,
                storeLatitude = storeLatitude,
                storeLongitude = storeLongitude,
                isEditing = editingProfile,
                isLoading = isLoading,
                message = message,
                analyticsState = analyticsState,
                onAnalyticsRetry = onAnalyticsRetry,
                buyerAddressState = buyerAddressState,
                onBuyerAddressRetry = onBuyerAddressRetry,
                onBuyerAddressEdit = onBuyerAddressEdit,
                onBuyerAddressCancel = onBuyerAddressCancel,
                onBuyerAddressSave = onBuyerAddressSave,
                onBuyerRecipientChanged = onBuyerRecipientChanged,
                onBuyerPhoneChanged = onBuyerPhoneChanged,
                onBuyerAddressLineChanged = onBuyerAddressLineChanged,
                onBuyerCityChanged = onBuyerCityChanged,
                onBuyerPostalCodeChanged = onBuyerPostalCodeChanged,
                onBuyerLatitudeChanged = onBuyerLatitudeChanged,
                onBuyerLongitudeChanged = onBuyerLongitudeChanged,
                onFullNameChanged = onFullNameChanged,
                onPhoneNumberChanged = onPhoneNumberChanged,
                onEmailChanged = onEmailChanged,
                onLocationChanged = onLocationChanged,
                onStoreNameChanged = onStoreNameChanged,
                onStoreAddressChanged = onStoreAddressChanged,
                onStoreLatitudeChanged = onStoreLatitudeChanged,
                onStoreLongitudeChanged = onStoreLongitudeChanged,
                onCreateStore = onCreateStore,
                onEdit = onEditProfile,
                onSave = onSaveProfile,
                onSignOut = onSignOut,
                onExitPreview = onExitPreview,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            MainSection.Beranda -> if (role == UserRole.Buyer) {
                BuyerHomeScreen(
                    fullName = fullName,
                    state = buyerHomeState,
                    onRetry = onBuyerHomeRetry,
                    onCalibrateLocation = onBuyerHomeCalibrateLocation,
                    onOpenLocationSettings = onBuyerHomeOpenLocationSettings,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            } else {
                HomeScreen(
                    role = role,
                    fullName = fullName,
                    isPreviewMode = isPreviewMode,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            MainSection.Notifikasi -> TanicoEmptyState(
                title = "Belum ada notifikasi",
                message = "Notifikasi akun akan tampil di sini setelah layanan TANICO terhubung.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            MainSection.Pembelajaran -> TanicoEmptyState(
                title = "Pembelajaran TANICO",
                message = "Artikel dan materi edukasi akan tersedia pada tahap pengembangan berikutnya.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun HomeScreen(
    role: UserRole,
    fullName: String,
    isPreviewMode: Boolean,
    modifier: Modifier = Modifier
) {
    val roleTitle = if (role == UserRole.Buyer) "Pembeli / Bio-Industri" else "Penjual / Petani & Pengepul"
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "TANICO  •  Beranda",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Halo, ${fullName.ifBlank { roleTitle }}",
            style = MaterialTheme.typography.headlineMedium
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isPreviewMode) "Mode pratinjau" else "Akun ${roleTitle}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = if (isPreviewMode) {
                        "Data dan transaksi belum tersedia. Pratinjau ini tidak membuat akun."
                    } else if (role == UserRole.Seller) {
                        "Ringkasan penjualan dan tren omzet tersedia di tab Profil."
                    } else {
                        "Akun Anda siap digunakan. Fitur marketplace akan hadir pada tahap berikutnya."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = if (isPreviewMode) {
                "Gunakan tab Profil untuk melihat tampilan profil ${if (role == UserRole.Buyer) "pembeli" else "penjual"}."
            } else {
                "Gunakan tab Profil untuk mengelola informasi akun Anda."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

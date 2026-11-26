package com.example.tanico.feature.main

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tanico.core.ui.TanicoEmptyState
import com.example.tanico.domain.model.MarketplaceProduct
import com.example.tanico.domain.model.UserRole
import com.example.tanico.feature.marketplace.BuyerCartScreen
import com.example.tanico.feature.marketplace.BuyerCartUiState
import com.example.tanico.feature.marketplace.BuyerCheckoutReviewScreen
import com.example.tanico.feature.marketplace.BuyerHomeScreen
import com.example.tanico.feature.marketplace.BuyerHomeUiState
import com.example.tanico.feature.marketplace.BuyerProductDetailScreen
import com.example.tanico.feature.marketplace.BuyerStoreDirectoryScreen
import com.example.tanico.feature.marketplace.BuyerStorefrontScreen
import com.example.tanico.feature.profile.BuyerAddressUiState
import com.example.tanico.feature.profile.ProfileScreen
import com.example.tanico.feature.seller.SellerAnalyticsUiState
import com.example.tanico.feature.education.ui.EducationFeatureScreen

enum class MainSection(val label: String, val marker: String) {
    Beranda("Beranda", "⌂"),
    Notifikasi("Notifikasi", "●"),
    Pembelajaran("Pembelajaran", "▤"),
    Profil("Profil", "○")
}

private enum class BuyerStorePage {
    Home,
    Directory,
    Storefront,
    ProductDetail,
    Cart,
    Checkout
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
    onBuyerHomeShowAllProductsForDebug: () -> Unit,
    onBuyerHomeOpenLocationSettings: () -> Unit,
    buyerCartState: BuyerCartUiState,
    onBuyerCartAdd: (MarketplaceProduct, Int) -> Unit,
    onBuyerCartSetQuantity: (String, Int) -> Unit,
    onBuyerCartRemoveProduct: (String) -> Unit,
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
    var buyerStorePage by rememberSaveable(role) {
        mutableStateOf(BuyerStorePage.Home)
    }
    var selectedProductId by rememberSaveable(role) {
        mutableStateOf<String?>(null)
    }
    val section = MainSection.values().firstOrNull { it.name == selectedSection }
        ?: MainSection.Beranda
    BackHandler(
        enabled = role == UserRole.Buyer &&
            section == MainSection.Beranda &&
            buyerStorePage != BuyerStorePage.Home
    ) {
        buyerStorePage = when (buyerStorePage) {
            BuyerStorePage.Storefront -> BuyerStorePage.Directory
            BuyerStorePage.Directory -> BuyerStorePage.Home
            BuyerStorePage.ProductDetail -> BuyerStorePage.Home
            BuyerStorePage.Checkout -> BuyerStorePage.Cart
            BuyerStorePage.Cart -> BuyerStorePage.Home
            BuyerStorePage.Home -> BuyerStorePage.Home
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                sections.forEach { item ->
                    NavigationBarItem(
                        selected = section == item,
                        onClick = {
                            selectedSection = item.name
                            buyerStorePage = BuyerStorePage.Home
                            selectedProductId = null
                        },
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
                when (buyerStorePage) {
                    BuyerStorePage.Home -> BuyerHomeScreen(
                        fullName = fullName,
                        state = buyerHomeState,
                        onRetry = onBuyerHomeRetry,
                        onBrowseStores = { buyerStorePage = BuyerStorePage.Directory },
                        onCalibrateLocation = onBuyerHomeCalibrateLocation,
                        onShowAllProductsForDebug = onBuyerHomeShowAllProductsForDebug,
                        onOpenLocationSettings = onBuyerHomeOpenLocationSettings,
                        cartItemCount = buyerCartState.productCount,
                        onOpenCart = { buyerStorePage = BuyerStorePage.Cart },
                        onOpenProduct = { product ->
                            selectedProductId = product.id
                            buyerStorePage = BuyerStorePage.ProductDetail
                        },
                        onAddToCart = { product -> onBuyerCartAdd(product, 1) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )

                    BuyerStorePage.Directory -> BuyerStoreDirectoryScreen(
                        onBack = { buyerStorePage = BuyerStorePage.Home },
                        onOpenPreviewStore = { buyerStorePage = BuyerStorePage.Storefront },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )

                    BuyerStorePage.Storefront -> BuyerStorefrontScreen(
                        onBack = { buyerStorePage = BuyerStorePage.Directory },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )

                    BuyerStorePage.ProductDetail -> {
                        val product = buyerHomeState.products
                            .firstOrNull { it.id == selectedProductId }
                        if (product != null) {
                            BuyerProductDetailScreen(
                                product = product,
                                cartItemCount = buyerCartState.productCount,
                                onBack = { buyerStorePage = BuyerStorePage.Home },
                                onAddToCart = onBuyerCartAdd,
                                onOpenCart = { buyerStorePage = BuyerStorePage.Cart },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                TextButton(onClick = { buyerStorePage = BuyerStorePage.Home }) {
                                    Text("← Kembali ke Beranda")
                                }
                                TanicoEmptyState(
                                    title = "Produk tidak tersedia",
                                    message = "Katalog produk berubah atau produk ini tidak lagi tersedia."
                                )
                            }
                        }
                    }

                    BuyerStorePage.Cart -> BuyerCartScreen(
                        state = buyerCartState,
                        onBack = { buyerStorePage = BuyerStorePage.Home },
                        onSetQuantity = onBuyerCartSetQuantity,
                        onRemoveProduct = onBuyerCartRemoveProduct,
                        onContinueToCheckout = { buyerStorePage = BuyerStorePage.Checkout },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )

                    BuyerStorePage.Checkout -> BuyerCheckoutReviewScreen(
                        state = buyerCartState,
                        onBack = { buyerStorePage = BuyerStorePage.Cart },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
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

            MainSection.Pembelajaran -> EducationFeatureScreen(
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

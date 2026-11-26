package com.example.tanico

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tanico.data.location.AndroidDeviceLocationProvider
import com.example.tanico.core.navigation.AppDestination
import com.example.tanico.core.session.AppSessionState
import com.example.tanico.core.ui.TanicoLoadingIndicator
import com.example.tanico.domain.model.UserRole
import com.example.tanico.feature.auth.AuthEntryScreen
import com.example.tanico.feature.auth.AuthPage
import com.example.tanico.feature.auth.AuthUiState
import com.example.tanico.feature.auth.AuthViewModel
import com.example.tanico.feature.auth.AuthViewModelFactory
import com.example.tanico.feature.auth.EmailVerificationScreen
import com.example.tanico.feature.auth.RegistrationScreen
import com.example.tanico.feature.main.MainAppScreen
import com.example.tanico.feature.marketplace.BuyerHomeViewModel
import com.example.tanico.feature.marketplace.BuyerHomeViewModelFactory
import com.example.tanico.feature.marketplace.BuyerCartViewModel
import com.example.tanico.feature.profile.BuyerAddressViewModel
import com.example.tanico.feature.profile.BuyerAddressViewModelFactory
import com.example.tanico.feature.seller.SellerAnalyticsViewModel
import com.example.tanico.feature.seller.SellerAnalyticsViewModelFactory
import com.example.tanico.ui.theme.TanicoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TanicoTheme {
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())
                val analyticsViewModel: SellerAnalyticsViewModel =
                    viewModel(factory = SellerAnalyticsViewModelFactory())
                val buyerHomeViewModel: BuyerHomeViewModel =
                    viewModel(factory = BuyerHomeViewModelFactory())
                val buyerCartViewModel: BuyerCartViewModel = viewModel()
                val buyerAddressViewModel: BuyerAddressViewModel =
                    viewModel(factory = BuyerAddressViewModelFactory())
                val state by authViewModel.uiState
                val analyticsState by analyticsViewModel.uiState
                val buyerHomeState by buyerHomeViewModel.uiState
                val buyerCartState by buyerCartViewModel.uiState
                val buyerAddressState by buyerAddressViewModel.uiState
                TanicoApp(
                    state = state,
                    viewModel = authViewModel,
                    analyticsState = analyticsState,
                    analyticsViewModel = analyticsViewModel,
                    buyerHomeState = buyerHomeState,
                    buyerHomeViewModel = buyerHomeViewModel,
                    buyerCartState = buyerCartState,
                    buyerCartViewModel = buyerCartViewModel,
                    buyerAddressState = buyerAddressState,
                    buyerAddressViewModel = buyerAddressViewModel
                )
            }
        }
    }
}

@Composable
private fun TanicoApp(
    state: AuthUiState,
    viewModel: AuthViewModel,
    analyticsState: com.example.tanico.feature.seller.SellerAnalyticsUiState,
    analyticsViewModel: SellerAnalyticsViewModel,
    buyerHomeState: com.example.tanico.feature.marketplace.BuyerHomeUiState,
    buyerHomeViewModel: BuyerHomeViewModel,
    buyerCartState: com.example.tanico.feature.marketplace.BuyerCartUiState,
    buyerCartViewModel: BuyerCartViewModel,
    buyerAddressState: com.example.tanico.feature.profile.BuyerAddressUiState,
    buyerAddressViewModel: BuyerAddressViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationProvider = remember(context.applicationContext) {
        AndroidDeviceLocationProvider(context.applicationContext)
    }
    val hasLocationPermission = {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }
    val fetchBuyerLocation = {
        buyerHomeViewModel.beginLocationRequest()
        coroutineScope.launch {
            locationProvider.getCurrentLocation()
                .onSuccess(buyerHomeViewModel::loadAtLocation)
                .onFailure { exception ->
                    buyerHomeViewModel.locationUnavailable(
                        exception.message ?: "Lokasi belum dapat diperoleh. Coba lagi."
                    )
                }
        }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it } || hasLocationPermission()) {
            fetchBuyerLocation()
        } else {
            buyerHomeViewModel.locationUnavailable(
                message = "Izinkan akses lokasi untuk menampilkan produk di sekitar Anda.",
                permissionDenied = true
            )
        }
    }
    val requestBuyerLocation = {
        if (hasLocationPermission()) {
            if (!buyerHomeViewModel.retry()) fetchBuyerLocation()
        } else {
            buyerHomeViewModel.beginLocationRequest()
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }
    val calibrateBuyerLocation: () -> Unit = {
        if (hasLocationPermission()) {
            fetchBuyerLocation()
        } else {
            buyerHomeViewModel.beginLocationRequest()
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }
    val sellerUserId = (state.sessionState as? AppSessionState.Authenticated)
        ?.profile
        ?.takeIf { it.role == UserRole.Seller && !state.isPreviewMode }
        ?.id
    val buyerProfile = (state.sessionState as? AppSessionState.Authenticated)
        ?.profile
        ?.takeIf { it.role == UserRole.Buyer && !state.isPreviewMode }
    val isBuyerMain = (state.destination as? AppDestination.Main)?.role == UserRole.Buyer
    val cartOwnerId = buyerProfile?.id ?: if (isBuyerMain && state.isPreviewMode) "preview" else null
    LaunchedEffect(sellerUserId) {
        sellerUserId?.let(analyticsViewModel::load)
    }
    LaunchedEffect(buyerProfile?.id, isBuyerMain, state.isPreviewMode) {
        when {
            buyerProfile != null && isBuyerMain -> {
                if (!buyerHomeViewModel.retry()) requestBuyerLocation()
            }
            isBuyerMain && state.isPreviewMode -> buyerHomeViewModel.locationUnavailable(
                message = "Masuk dengan akun pembeli untuk melihat produk terdekat.",
                canRetry = false
            )
            else -> buyerHomeViewModel.reset()
        }
    }
    LaunchedEffect(cartOwnerId) {
        buyerCartViewModel.bindToUser(cartOwnerId)
    }
    LaunchedEffect(buyerProfile?.id, isBuyerMain, state.isPreviewMode) {
        when {
            buyerProfile != null -> buyerAddressViewModel.load(
                userId = buyerProfile.id,
                fullName = state.fullName,
                phone = state.phoneNumber,
                city = state.location
            )
            isBuyerMain && state.isPreviewMode -> buyerAddressViewModel.preparePreview(
                fullName = state.fullName,
                phone = state.phoneNumber,
                city = state.location
            )
            else -> buyerAddressViewModel.reset()
        }
    }

    if (state.sessionState == AppSessionState.Loading && !state.isPreviewMode) {
        TanicoLoadingIndicator(Modifier.fillMaxSize())
        return
    }

    when (val destination = state.destination) {
        AppDestination.Authentication -> when (state.authPage) {
            AuthPage.Login -> AuthEntryScreen(
                state = state,
                onLoginIdentifierChanged = viewModel::updateLoginIdentifier,
                onPasswordChanged = viewModel::updatePassword,
                onLogin = viewModel::submitLogin,
                onRegister = viewModel::showRegistration,
                onPreview = viewModel::enterPreview
            )

            AuthPage.Registration -> RegistrationScreen(
                state = state,
                onRoleSelected = viewModel::selectRole,
                onFullNameChanged = viewModel::updateFullName,
                onPhoneNumberChanged = viewModel::updatePhoneNumber,
                onEmailChanged = viewModel::updateEmail,
                onLocationChanged = viewModel::updateLocation,
                onStoreNameChanged = viewModel::updateStoreName,
                onStoreAddressChanged = viewModel::updateStoreAddress,
                onStoreLatitudeChanged = viewModel::updateStoreLatitude,
                onStoreLongitudeChanged = viewModel::updateStoreLongitude,
                onPasswordChanged = viewModel::updatePassword,
                onConfirmPasswordChanged = viewModel::updateConfirmPassword,
                onTermsAcceptedChanged = viewModel::updateTermsAccepted,
                onSubmit = viewModel::submitRegistration,
                onShowLogin = viewModel::showLogin,
                onPreview = viewModel::enterPreview
            )

            AuthPage.EmailVerification -> EmailVerificationScreen(
                message = state.message.orEmpty(),
                onBackToLogin = viewModel::showLogin
            )

        }

        is AppDestination.Main -> MainAppScreen(
            role = destination.role,
            isPreviewMode = state.isPreviewMode,
            fullName = state.fullName,
            phoneNumber = state.phoneNumber,
            email = state.email,
            location = state.location,
            storeName = state.storeName,
            storeAddress = state.storeAddress,
            storeLatitude = state.storeLatitude,
            storeLongitude = state.storeLongitude,
            editingProfile = state.editingProfile,
            isLoading = state.isLoading,
            message = state.message,
            analyticsState = analyticsState,
            onAnalyticsRetry = { sellerUserId?.let(analyticsViewModel::load) },
            buyerHomeState = buyerHomeState,
            onBuyerHomeRetry = {
                if (!buyerHomeViewModel.retry()) requestBuyerLocation()
            },
            onBuyerHomeCalibrateLocation = calibrateBuyerLocation,
            onBuyerHomeShowAllProductsForDebug =
                buyerHomeViewModel::loadAllActiveProductsForDebug,
            onBuyerHomeOpenLocationSettings = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                )
            },
            buyerCartState = buyerCartState,
            onBuyerCartAdd = buyerCartViewModel::addProduct,
            onBuyerCartSetQuantity = buyerCartViewModel::setQuantity,
            onBuyerCartRemoveProduct = buyerCartViewModel::removeProduct,
            buyerAddressState = buyerAddressState,
            onBuyerAddressRetry = {
                buyerProfile?.let { profile ->
                    buyerAddressViewModel.load(
                        userId = profile.id,
                        fullName = state.fullName,
                        phone = state.phoneNumber,
                        city = state.location
                    )
                }
            },
            onBuyerAddressEdit = buyerAddressViewModel::startEditing,
            onBuyerAddressCancel = buyerAddressViewModel::cancelEditing,
            onBuyerAddressSave = {
                buyerAddressViewModel.save(
                    userId = buyerProfile?.id,
                    isPreviewMode = state.isPreviewMode
                )
            },
            onBuyerRecipientChanged = buyerAddressViewModel::updateRecipientName,
            onBuyerPhoneChanged = buyerAddressViewModel::updatePhone,
            onBuyerAddressLineChanged = buyerAddressViewModel::updateAddressLine,
            onBuyerCityChanged = buyerAddressViewModel::updateCity,
            onBuyerPostalCodeChanged = buyerAddressViewModel::updatePostalCode,
            onBuyerLatitudeChanged = buyerAddressViewModel::updateLatitude,
            onBuyerLongitudeChanged = buyerAddressViewModel::updateLongitude,
            onFullNameChanged = viewModel::updateFullName,
            onPhoneNumberChanged = viewModel::updatePhoneNumber,
            onEmailChanged = viewModel::updateEmail,
            onLocationChanged = viewModel::updateLocation,
            onStoreNameChanged = viewModel::updateStoreName,
            onStoreAddressChanged = viewModel::updateStoreAddress,
            onStoreLatitudeChanged = viewModel::updateStoreLatitude,
            onStoreLongitudeChanged = viewModel::updateStoreLongitude,
            onCreateStore = { name, address, latitude, longitude ->
                sellerUserId?.let { id ->
                    analyticsViewModel.createStore(
                        ownerId = id,
                        storeName = name,
                        address = address,
                        latitudeText = latitude,
                        longitudeText = longitude
                    )
                }
            },
            onEditProfile = viewModel::startEditingProfile,
            onSaveProfile = viewModel::saveProfile,
            onSignOut = viewModel::signOut,
            onExitPreview = viewModel::exitPreview
        )
    }
}

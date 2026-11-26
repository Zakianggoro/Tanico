package com.example.tanico.feature.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tanico.core.AppContainer
import com.example.tanico.domain.model.BuyerDeliveryAddress
import com.example.tanico.domain.repository.BuyerAddressRepository
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class BuyerAddressViewModelFactory(
    private val repository: BuyerAddressRepository = AppContainer.buyerAddressRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(BuyerAddressViewModel::class.java))
        return BuyerAddressViewModel(repository) as T
    }
}

data class BuyerAddressUiState(
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val address: BuyerDeliveryAddress? = null,
    val recipientName: String = "",
    val phone: String = "",
    val addressLine: String = "",
    val city: String = "",
    val postalCode: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val message: String? = null
)

class BuyerAddressViewModel(
    private val repository: BuyerAddressRepository
) : ViewModel() {
    private val mutableUiState = mutableStateOf(BuyerAddressUiState())
    val uiState: State<BuyerAddressUiState> = mutableUiState

    fun load(userId: String, fullName: String, phone: String, city: String) {
        mutableUiState.value = BuyerAddressUiState(isLoading = true)
        viewModelScope.launch {
            repository.loadDefault(userId)
                .onSuccess { address ->
                    mutableUiState.value = BuyerAddressUiState(
                        isLoading = false,
                        isEditing = address == null,
                        address = address,
                        recipientName = address?.recipientName ?: fullName,
                        phone = address?.phone ?: phone,
                        addressLine = address?.address.orEmpty(),
                        city = address?.city ?: city,
                        postalCode = address?.postalCode.orEmpty(),
                        latitude = address?.latitude?.toString().orEmpty(),
                        longitude = address?.longitude?.toString().orEmpty()
                    )
                }
                .onFailure { exception ->
                    mutableUiState.value = BuyerAddressUiState(
                        isLoading = false,
                        message = exception.message?.takeIf(String::isNotBlank)
                            ?: "Alamat pengiriman tidak dapat dimuat. Coba lagi."
                    )
                }
        }
    }

    fun preparePreview(fullName: String, phone: String, city: String) {
        mutableUiState.value = BuyerAddressUiState(
            isLoading = false,
            isEditing = true,
            recipientName = fullName,
            phone = phone,
            city = city
        )
    }

    fun reset() {
        mutableUiState.value = BuyerAddressUiState(isLoading = false)
    }

    fun startEditing() {
        mutableUiState.value = mutableUiState.value.copy(isEditing = true, message = null)
    }

    fun cancelEditing() {
        val state = mutableUiState.value
        val address = state.address
        if (address == null) return
        mutableUiState.value = state.copy(
            isEditing = false,
            recipientName = address.recipientName,
            phone = address.phone,
            addressLine = address.address,
            city = address.city,
            postalCode = address.postalCode.orEmpty(),
            latitude = address.latitude.toString(),
            longitude = address.longitude.toString(),
            message = null
        )
    }

    fun updateRecipientName(value: String) = update { it.copy(recipientName = value, message = null) }
    fun updatePhone(value: String) = update { it.copy(phone = value, message = null) }
    fun updateAddressLine(value: String) = update { it.copy(addressLine = value, message = null) }
    fun updateCity(value: String) = update { it.copy(city = value, message = null) }
    fun updatePostalCode(value: String) = update { it.copy(postalCode = value, message = null) }
    fun updateLatitude(value: String) = update { it.copy(latitude = value, message = null) }
    fun updateLongitude(value: String) = update { it.copy(longitude = value, message = null) }

    fun save(userId: String?, isPreviewMode: Boolean) {
        val state = mutableUiState.value
        if (state.isSaving) return
        val validationMessage = when {
            state.recipientName.isBlank() -> "Masukkan nama penerima."
            state.phone.isBlank() -> "Masukkan nomor telepon penerima."
            state.addressLine.isBlank() -> "Masukkan alamat pengiriman."
            state.city.isBlank() -> "Masukkan kota atau kabupaten."
            !isPreviewMode && userId.isNullOrBlank() -> "Sesi pembeli tidak ditemukan. Masuk kembali."
            else -> null
        }
        if (validationMessage != null) {
            mutableUiState.value = state.copy(message = validationMessage)
            return
        }

        val latitude = coordinate(state.latitude, -90.0, 90.0)
        if (latitude == null) {
            mutableUiState.value = state.copy(message = "Pilih titik lokasi alamat pada peta.")
            return
        }
        val longitude = coordinate(state.longitude, -180.0, 180.0)
        if (longitude == null) {
            mutableUiState.value = state.copy(message = "Pilih titik lokasi alamat pada peta.")
            return
        }

        val address = BuyerDeliveryAddress(
            id = state.address?.id,
            userId = userId.orEmpty(),
            recipientName = state.recipientName.trim(),
            phone = state.phone.trim(),
            address = state.addressLine.trim(),
            city = state.city.trim(),
            postalCode = state.postalCode.trim().takeIf(String::isNotEmpty),
            latitude = latitude,
            longitude = longitude
        )
        if (isPreviewMode) {
            mutableUiState.value = state.copy(
                isEditing = false,
                address = address,
                message = "Alamat hanya tersimpan sementara selama pratinjau."
            )
            return
        }

        mutableUiState.value = state.copy(isSaving = true, message = null)
        viewModelScope.launch {
            repository.saveDefault(address)
                .onSuccess { savedAddress ->
                    mutableUiState.value = state.copy(
                        isEditing = false,
                        isSaving = false,
                        address = savedAddress,
                        message = "Alamat pengiriman berhasil disimpan."
                    )
                }
                .onFailure { exception ->
                    mutableUiState.value = mutableUiState.value.copy(
                        isSaving = false,
                        message = exception.message?.takeIf(String::isNotBlank)
                            ?: "Alamat pengiriman tidak dapat disimpan. Coba lagi."
                    )
                }
        }
    }

    private fun coordinate(value: String, min: Double, max: Double): Double? =
        value.trim().toDoubleOrNull()?.takeIf { it.isFinite() && it in min..max }

    private inline fun update(transform: (BuyerAddressUiState) -> BuyerAddressUiState) {
        mutableUiState.value = transform(mutableUiState.value)
    }
}

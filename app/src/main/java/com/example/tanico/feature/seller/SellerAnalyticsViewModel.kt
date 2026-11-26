package com.example.tanico.feature.seller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tanico.core.AppContainer
import com.example.tanico.domain.model.SellerAnalytics
import com.example.tanico.domain.repository.SellerAnalyticsRepository
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class SellerAnalyticsViewModelFactory(
    private val repository: SellerAnalyticsRepository = AppContainer.sellerAnalyticsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SellerAnalyticsViewModel::class.java))
        return SellerAnalyticsViewModel(repository) as T
    }
}

data class SellerAnalyticsUiState(
    val isLoading: Boolean = true,
    val analytics: SellerAnalytics? = null,
    val storeMissing: Boolean = false,
    val message: String? = null
)

class SellerAnalyticsViewModel(
    private val repository: SellerAnalyticsRepository
) : ViewModel() {
    private val mutableUiState = mutableStateOf(SellerAnalyticsUiState())
    val uiState: State<SellerAnalyticsUiState> = mutableUiState

    fun load(userId: String) {
        viewModelScope.launch {
            mutableUiState.value = SellerAnalyticsUiState(isLoading = true)
            repository.loadOverview(userId)
                .onSuccess { analytics ->
                    mutableUiState.value = SellerAnalyticsUiState(
                        isLoading = false,
                        analytics = analytics,
                        storeMissing = analytics == null
                    )
                }
                .onFailure { exception ->
                    mutableUiState.value = SellerAnalyticsUiState(
                        isLoading = false,
                        message = exception.message?.takeIf(String::isNotBlank)
                            ?: "Data analitik tidak dapat dimuat. Coba lagi."
                    )
                }
        }
    }
}


package com.example.tanico.feature.Notification.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tanico.core.AppContainer
import com.example.tanico.data.model.AppNotification
import com.example.tanico.feature.Notification.data.NotificationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }
}

class NotificationsViewModel(
    private val userId: String?,
    private val repository: NotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NotificationsUiState()
    )

    val uiState: StateFlow<NotificationsUiState> =
        _uiState.asStateFlow()

    init {
        if (userId.isNullOrBlank()) {
            _uiState.value = NotificationsUiState(
                isLoading = false,
                errorMessage = "Masuk ke akun untuk melihat notifikasi."
            )
        } else {
            loadNotifications()
        }
    }

    fun loadNotifications() {
        val id = userId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            repository.loadNotifications(id)
                .onSuccess { notifications ->
                    _uiState.value = NotificationsUiState(
                        notifications = notifications,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                            ?: "Notifikasi gagal dimuat."
                    )
                }
        }
    }

    fun markAsRead(notificationId: String) {
        val id = userId ?: return

        viewModelScope.launch {
            repository.markAsRead(id, notificationId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        notifications = _uiState.value.notifications.map {
                            if (it.id == notificationId) {
                                it.copy(isRead = true)
                            } else {
                                it
                            }
                        }
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                            ?: "Status notifikasi gagal diperbarui."
                    )
                }
        }
    }

    fun markAllAsRead() {
        val id = userId ?: return

        viewModelScope.launch {
            repository.markAllAsRead(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        notifications = _uiState.value.notifications.map {
                            it.copy(isRead = true)
                        }
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                            ?: "Notifikasi gagal diperbarui."
                    )
                }
        }
    }
}

class NotificationsViewModelFactory(
    private val userId: String?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(
                NotificationsViewModel::class.java
            )
        ) {
            return NotificationsViewModel(
                userId = userId,
                repository = AppContainer.notificationsRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}

//package com.example.tanico.feature.Notification.data
//
//import com.example.tanico.data.model.AppNotification
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
////data contoh
//
//class DemoNotificationsRepository : NotificationsRepository {
//    private val _notifications = MutableStateFlow(
//        listOf(
//            AppNotification("notification-1", "demo-user", title = "Pesanan baru masuk", message = "Ada pesanan baru untuk produk limbah pertanianmu.", type = "order_created", createdAt = "Hari ini, 09.30"),
//            AppNotification("notification-2", "demo-user", title = "Pesanan sedang diproses", message = "Pesananmu telah diterima dan sedang diproses penjual.", type = "order_processing", createdAt = "Kemarin, 16.20"),
//            AppNotification("notification-3", "demo-user", title = "Selamat datang di TANICO!", message = "Mulai manfaatkan limbah pertanian dan temukan informasi menarik di TANICO.", type = "info", isRead = true, createdAt = "Kemarin, 08.00")
//        )
//    )
//    override val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()
//
//    override suspend fun markAsRead(notificationId: String) {
//        _notifications.value = _notifications.value.map {
//            if (it.id == notificationId) it.copy(isRead = true) else it
//        }
//    }
//
//    override suspend fun markAllAsRead() {
//        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
//    }
//}
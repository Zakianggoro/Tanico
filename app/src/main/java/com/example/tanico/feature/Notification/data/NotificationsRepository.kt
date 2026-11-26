
package com.example.tanico.feature.Notification.data

import com.example.tanico.data.model.AppNotification

interface NotificationsRepository {

    suspend fun loadNotifications(
        userId: String
    ): Result<List<AppNotification>>

    suspend fun markAsRead(
        userId: String,
        notificationId: String
    ): Result<Unit>

    suspend fun markAllAsRead(
        userId: String
    ): Result<Unit>
}

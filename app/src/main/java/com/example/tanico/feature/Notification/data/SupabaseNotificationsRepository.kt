
package com.example.tanico.feature.Notification.data

import com.example.tanico.data.model.AppNotification
import com.example.tanico.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseNotificationsRepository(
    private val clientProvider: SupabaseClientProvider
) : NotificationsRepository {

    override suspend fun loadNotifications(
        userId: String
    ): Result<List<AppNotification>> = try {
        check(clientProvider.isConfigured) {
            "Supabase belum dikonfigurasi. Periksa URL dan publishable key."
        }

        val rows = clientProvider.client
            .from("notifications")
            .select {
                filter {
                    eq("user_id", userId)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<NotificationRow>()

        Result.success(
            rows.map { row ->
                AppNotification(
                    id = row.id,
                    userId = row.userId,
                    orderId = row.orderId,
                    title = row.title,
                    message = row.message,
                    type = row.type,
                    isRead = row.isRead,
                    createdAt = row.createdAt
                )
            }
        )
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    override suspend fun markAsRead(
        userId: String,
        notificationId: String
    ): Result<Unit> = try {
        check(clientProvider.isConfigured)

        clientProvider.client
            .from("notifications")
            .update(NotificationReadUpdate(isRead = true)) {
                filter {
                    eq("id", notificationId)
                    eq("user_id", userId)
                }
            }

        Result.success(Unit)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    override suspend fun markAllAsRead(
        userId: String
    ): Result<Unit> = try {
        check(clientProvider.isConfigured)

        clientProvider.client
            .from("notifications")
            .update(NotificationReadUpdate(isRead = true)) {
                filter {
                    eq("user_id", userId)
                    eq("is_read", false)
                }
            }

        Result.success(Unit)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.failure(exception)
    }

    @Serializable
    private data class NotificationRow(
        val id: String,
        @SerialName("user_id")
        val userId: String,
        @SerialName("order_id")
        val orderId: String? = null,
        val title: String,
        val message: String,
        val type: String,
        @SerialName("is_read")
        val isRead: Boolean = false,
        @SerialName("created_at")
        val createdAt: String
    )

    @Serializable
    private data class NotificationReadUpdate(
        @SerialName("is_read")
        val isRead: Boolean
    )
}

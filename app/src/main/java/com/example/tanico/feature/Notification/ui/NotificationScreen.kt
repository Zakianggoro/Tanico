
package com.example.tanico.feature.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.tanico.data.model.AppNotification
import com.example.tanico.feature.Notification.ui.NotificationsViewModel
import com.example.tanico.feature.Notification.ui.NotificationsViewModelFactory

// Warna khusus halaman notifikasi
private val ForestGreen = Color(0xFF355E3B)
private val FreshGreen = Color(0xFF8BAF75)
private val MainText = Color(0xFF303B2F)
private val MutedText = Color(0xFF858B80)
private val NaturalBeige = Color(0xFFE6DDC9)
private val WarmCream = Color(0xFFF8F7F0)

@Composable
fun NotificationsFeatureScreen(
    userId: String?,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = viewModel(
        key = "notifications-${userId ?: "preview"}",
        factory = NotificationsViewModelFactory(userId)
    )
) {
    val state by viewModel.uiState.collectAsState()

    var selectedNotification by remember {
        mutableStateOf<AppNotification?>(null)
    }

    var showReadHistory by remember {
        mutableStateOf(false)
    }

    val currentNotification = selectedNotification?.let { selected ->
        state.notifications.firstOrNull {
            it.id == selected.id
        } ?: selected
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (currentNotification != null) {
            NotificationDetailScreen(
                notification = currentNotification,
                onBack = {
                    selectedNotification = null
                },
                onMarkRead = {
                    viewModel.markAsRead(currentNotification.id)
                }
            )
        } else {
            NotificationListScreen(
                notifications = state.notifications,
                unreadCount = state.unreadCount,
                isLoading = state.isLoading,
                onNotificationClick = { notification ->
                    selectedNotification = notification
                    viewModel.markAsRead(notification.id)
                },
                onMarkAllRead = viewModel::markAllAsRead,
                showReadHistory = showReadHistory,
                onHistoryTabChanged = {
                    showReadHistory = it
                }
            )
        }
    }
}

@Composable
private fun NotificationListScreen(
    notifications: List<AppNotification>,
    unreadCount: Int,
    isLoading: Boolean,
    onNotificationClick: (AppNotification) -> Unit,
    onMarkAllRead: () -> Unit,
    showReadHistory: Boolean,
    onHistoryTabChanged: (Boolean) -> Unit
) {
    val unreadNotifications = notifications.filter { !it.isRead }
    val readNotifications = notifications.filter { it.isRead }

    val visibleNotifications =
        if (showReadHistory) readNotifications else unreadNotifications

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Aktivitas",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )

                Text(
                    text = "Kabar terbaru seputar pesananmu",
                    fontSize = 12.sp,
                    color = MutedText
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♧",
                    fontSize = 22.sp,
                    color = ForestGreen
                )

                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(11.dp)
                            .background(
                                Color(0xFFE47755),
                                CircleShape
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE7EDE1)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pusat notifikasi",
                        color = ForestGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (unreadCount == 0) {
                            "Belum ada notifikasi baru"
                        } else {
                            "$unreadCount notifikasi belum dibuka"
                        },
                        color = MutedText,
                        fontSize = 11.sp
                    )
                }

                if (unreadCount > 0) {
                    TextButton(onClick = onMarkAllRead) {
                        Text(
                            text = "Baca semua",
                            color = ForestGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Pemberitahuan",
                color = MainText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${notifications.size} pesan",
                color = MutedText,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(9.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !showReadHistory,
                onClick = {
                    onHistoryTabChanged(false)
                },
                label = {
                    Text("Belum dibaca ($unreadCount)")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ForestGreen,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = MutedText
                ),
                border = null
            )

            FilterChip(
                selected = showReadHistory,
                onClick = {
                    onHistoryTabChanged(true)
                },
                label = {
                    Text("Riwayat (${readNotifications.size})")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ForestGreen,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = MutedText
                ),
                border = null
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ForestGreen
                    )
                }
            }

            visibleNotifications.isEmpty() -> {
                EmptyNotificationsState(showReadHistory)
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = visibleNotifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = {
                                onNotificationClick(notification)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val isOrder = notification.type.startsWith("order")

    val accent = if (isOrder) {
        ForestGreen
    } else {
        Color(0xFFB77935)
    }

    val iconBackground = if (notification.isRead) {
        Color(0xFFECEBE6)
    } else if (isOrder) {
        Color(0xFFDCE9D8)
    } else {
        Color(0xFFFFEED9)
    }

    val cardBackground = if (notification.isRead) {
        Color(0xFFFDFCF9)
    } else {
        Color(0xFFF0F5EC)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 0.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        iconBackground,
                        RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isOrder) {
                        "📦"
                    } else {
                        notificationIcon(notification.type)
                    },
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isOrder) "PESANAN" else "INFORMASI",
                        color = accent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = if (notification.isRead) {
                            "SUDAH DIBACA"
                        } else {
                            "BARU"
                        },
                        color = if (notification.isRead) {
                            MutedText
                        } else {
                            ForestGreen
                        },
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.title,
                    color = MainText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    color = MutedText,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(9.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.createdAt,
                        color = MutedText,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = if (notification.isRead) {
                            "Buka riwayat  ›"
                        } else {
                            "Buka pesan  ›"
                        },
                        color = if (notification.isRead) {
                            MutedText
                        } else {
                            ForestGreen
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationDetailScreen(
    notification: AppNotification,
    onBack: () -> Unit,
    onMarkRead: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(horizontal = 16.dp)
            .padding(top = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    color = ForestGreen,
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Detail pesan",
                    color = MainText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Informasi dari TANICO",
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(
                            FreshGreen.copy(alpha = 0.14f),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = notificationIcon(notification.type),
                        fontSize = 29.sp
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = if (notification.type.startsWith("order")) {
                        "PEMBARUAN PESANAN"
                    } else {
                        "INFORMASI"
                    },
                    color = ForestGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.title,
                    color = MainText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 27.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.createdAt,
                    color = MutedText,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(17.dp))

                Divider(color = NaturalBeige.copy(alpha = 0.7f))

                Spacer(modifier = Modifier.height(17.dp))

                Text(
                    text = "Hai, ada informasi untukmu!",
                    color = MainText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.message,
                    color = MutedText,
                    fontSize = 14.sp,
                    lineHeight = 23.sp
                )

                if (!notification.orderId.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(18.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = WarmCream
                        ),
                        shape = RoundedCornerShape(13.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(13.dp)
                        ) {
                            Text(
                                text = "Nomor pesanan",
                                color = MutedText,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = notification.orderId,
                                color = MainText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Status pesan",
                        color = MutedText,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = if (notification.isRead) {
                            "Sudah dibaca ✓"
                        } else {
                            "Belum dibaca"
                        },
                        color = ForestGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (!notification.isRead) {
            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onMarkRead,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen
                )
            ) {
                Text(
                    text = "Tandai sudah dibaca",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState(showReadHistory: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔔",
                fontSize = 34.sp
            )
        }

        Spacer(modifier = Modifier.height(13.dp))

        Text(
            text = if (showReadHistory) {
                "Riwayat masih kosong"
            } else {
                "Belum ada notifikasi baru"
            },
            color = MainText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = if (showReadHistory) {
                "Pesan yang sudah kamu buka akan tersimpan di bagian ini."
            } else {
                "Notifikasi baru akan muncul di sini."
            },
            color = MutedText,
            fontSize = 12.sp
        )
    }
}

private fun notificationIcon(type: String): String {
    return when {
        type.startsWith("order") -> "📦"
        type == "info" -> "🌱"
        else -> "🔔"
    }
}
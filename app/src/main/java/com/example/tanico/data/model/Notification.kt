package com.example.tanico.data.model

data class AppNotification(
    val id: String,
    val userId: String,
    val orderId: String? = null,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean = false,
    val createdAt: String
)
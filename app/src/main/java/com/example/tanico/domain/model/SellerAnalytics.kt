package com.example.tanico.domain.model

import java.time.YearMonth

data class SellerAnalytics(
    val completedRevenue: Long,
    val completedOrderCount: Int,
    val pendingOrderCount: Int,
    val totalOrderCount: Int,
    val monthlyRevenue: List<MonthlyRevenue>
)

data class MonthlyRevenue(
    val month: YearMonth,
    val amount: Long
)

package com.example.tanico.domain.usecase

import com.example.tanico.domain.model.MonthlyRevenue
import com.example.tanico.domain.model.SellerAnalytics
import com.example.tanico.domain.model.SellerOrderMetric
import java.time.YearMonth
import kotlin.math.roundToLong

fun calculateSellerAnalytics(
    orders: List<SellerOrderMetric>,
    currentMonth: YearMonth
): SellerAnalytics {
    val firstMonth = currentMonth.minusMonths(5)
    val datedOrders = orders.map { order ->
        order to YearMonth.parse(order.createdAt.take(7))
    }.filter { (_, month) -> month in firstMonth..currentMonth }
    val completed = datedOrders.filter { (order, _) -> order.status == "completed" }
    val completedByMonth = completed.groupBy({ it.second }, { it.first })
    val monthlyRevenue = (0..5).map { offset ->
        val month = firstMonth.plusMonths(offset.toLong())
        MonthlyRevenue(
            month = month,
            amount = completedByMonth[month].orEmpty().sumOf { it.subtotal.roundToLong() }
        )
    }

    return SellerAnalytics(
        completedRevenue = completed.sumOf { (order, _) -> order.subtotal.roundToLong() },
        completedOrderCount = completed.size,
        pendingOrderCount = datedOrders.count { (order, _) -> order.status == "pending" },
        totalOrderCount = datedOrders.size,
        monthlyRevenue = monthlyRevenue
    )
}

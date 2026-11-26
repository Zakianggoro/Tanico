package com.example.tanico.domain

import com.example.tanico.domain.model.SellerOrderMetric
import com.example.tanico.domain.usecase.calculateSellerAnalytics
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateSellerAnalyticsTest {
    @Test
    fun countsOnlyCompletedOrdersAsRevenueAndKeepsPendingSeparate() {
        val analytics = calculateSellerAnalytics(
            orders = listOf(
                SellerOrderMetric("completed", 1_250_000.0, "2026-03-10T12:00:00Z"),
                SellerOrderMetric("pending", 300_000.0, "2026-03-12T12:00:00Z"),
                SellerOrderMetric("cancelled", 700_000.0, "2026-02-20T12:00:00Z"),
                SellerOrderMetric("completed", 900_000.0, "2025-09-10T12:00:00Z")
            ),
            currentMonth = YearMonth.of(2026, 3)
        )

        assertEquals(1_250_000L, analytics.completedRevenue)
        assertEquals(1, analytics.completedOrderCount)
        assertEquals(1, analytics.pendingOrderCount)
        assertEquals(3, analytics.totalOrderCount)
        assertEquals(1_250_000L, analytics.monthlyRevenue.last().amount)
    }
}

package com.example.tanico.domain

import com.example.tanico.domain.model.SellerOrderMetric
import com.example.tanico.domain.usecase.calculateSellerAnalytics
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateSellerAnalyticsTest {
    @Test
    fun calculatesCompletedSalesAndDivertedWeightForCurrentMonth() {
        val analytics = calculateSellerAnalytics(
            orders = listOf(
                SellerOrderMetric("completed", 1_250_000.0, "2026-03-10T12:00:00Z", 1_600.0),
                SellerOrderMetric("pending", 300_000.0, "2026-03-12T12:00:00Z", 300.0),
                SellerOrderMetric("cancelled", 700_000.0, "2026-02-20T12:00:00Z", 500.0),
                SellerOrderMetric("completed", 500_000.0, "2026-02-10T12:00:00Z", 1_000.0),
                SellerOrderMetric("completed", 900_000.0, "2025-09-10T12:00:00Z", 1_200.0)
            ),
            currentMonth = YearMonth.of(2026, 3)
        )

        assertEquals(1_750_000L, analytics.completedRevenue)
        assertEquals(2, analytics.completedOrderCount)
        assertEquals(1, analytics.pendingOrderCount)
        assertEquals(4, analytics.totalOrderCount)
        assertEquals(1_250_000L, analytics.monthlyRevenue.last().amount)
        assertEquals(1_250_000L, analytics.currentMonthCompletedRevenue)
        assertEquals(500_000L, analytics.previousMonthCompletedRevenue)
        assertEquals(1, analytics.currentMonthCompletedOrderCount)
        assertEquals(1_600.0, analytics.currentMonthDivertedWasteKg, 0.0)
    }
}

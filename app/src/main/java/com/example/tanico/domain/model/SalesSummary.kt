package com.example.tanico.domain.model

import java.math.BigDecimal
import java.time.Instant

/**
Tipe datanya masih belum ditentukan, nunggu tabel
database jadi dulu 
*/

data class SalesSummary(
    val periodStart: Instant?,
    val periodEnd: Instant?,
    val totalOrders: Int,
    val totalItemsSold: Int,
    val totalSales: BigDecimal,              // IDR
    val topProducts: List<TopProductSales> = emptyList()
)

data class TopProductSales(
    val productId: String?,
    val productName: String,
    val quantitySold: Int,
    val totalSales: BigDecimal
)
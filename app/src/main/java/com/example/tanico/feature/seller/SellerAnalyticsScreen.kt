package com.example.tanico.feature.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tanico.core.ui.TanicoEmptyState
import com.example.tanico.core.ui.TanicoErrorState
import com.example.tanico.core.ui.TanicoLoadingIndicator
import com.example.tanico.domain.model.MonthlyRevenue
import com.example.tanico.domain.model.SellerAnalytics
import java.text.DateFormatSymbols
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun SellerAnalyticsScreen(
    state: SellerAnalyticsUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading -> TanicoLoadingIndicator(modifier)
        state.message != null -> TanicoErrorState(state.message, modifier, onRetry)
        state.storeMissing -> TanicoEmptyState(
            title = "Profil toko belum tersedia",
            message = "Lengkapi profil toko penjual sebelum melihat ringkasan penjualan.",
            modifier = modifier
        )
        state.analytics == null || state.analytics.totalOrderCount == 0 -> TanicoEmptyState(
            title = "Belum ada data pesanan",
            message = "Ringkasan akan diperbarui setelah toko menerima pesanan.",
            modifier = modifier
        )
        else -> AnalyticsContent(state.analytics, modifier)
    }
}

@Composable
private fun AnalyticsContent(analytics: SellerAnalytics, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Analitik Penjualan", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Ringkasan enam bulan terakhir",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        MetricCard("Omzet produk selesai", formatRupiah(analytics.completedRevenue))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                label = "Pesanan selesai",
                value = analytics.completedOrderCount.toString(),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Menunggu diproses",
                value = analytics.pendingOrderCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Omzet produk bulanan", style = MaterialTheme.typography.titleLarge)
                RevenueChart(analytics.monthlyRevenue)
            }
        }
        Text(
            "Omzet dihitung dari subtotal produk berstatus pesanan selesai, tanpa ongkir. Angka ini bukan laba atau saldo tersedia.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RevenueChart(months: List<MonthlyRevenue>) {
    val maxAmount = months.maxOfOrNull { it.amount }?.coerceAtLeast(1L) ?: 1L
    val monthNames = DateFormatSymbols.getInstance(Locale.forLanguageTag("id-ID")).shortMonths
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        months.forEach { monthlyRevenue ->
            val fraction = monthlyRevenue.amount.toFloat() / maxAmount.toFloat()
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height((fraction * 112f).coerceAtLeast(6f).dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                        )
                )
                Text(
                    monthNames[monthlyRevenue.month.monthValue - 1],
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatRupiah(amount: Long): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
    .apply {
        currency = Currency.getInstance("IDR")
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }
    .format(amount)

package com.example.tanico.feature.seller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.tanico.feature.seller.orders.SellerOrdersScreen
import com.example.tanico.feature.seller.products.SellerProductsScreen

/**
 * Seller home: tabs Produk / Pesanan (+ Analitik when [analyticsContent] is given, so the
 * existing SellerAnalyticsScreen can be plugged in without changing it).
 */
@Composable
fun SellerWorkspaceScreen(
    modifier: Modifier = Modifier,
    analyticsContent: (@Composable () -> Unit)? = null
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val titles = buildList {
        add("Produk")
        add("Pesanan")
        if (analyticsContent != null) add("Analitik")
    }

    Column(modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab.coerceIn(0, titles.lastIndex)) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                0 -> SellerProductsScreen()
                1 -> SellerOrdersScreen()
                else -> analyticsContent?.invoke()
            }
        }
    }
}

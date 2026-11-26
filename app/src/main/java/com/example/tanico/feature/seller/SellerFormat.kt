package com.example.tanico.feature.seller

import com.example.tanico.domain.model.OrderStatus
import com.example.tanico.domain.model.ProductStatus
import com.example.tanico.domain.usecase.seller.ValidationException
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val INDONESIA: Locale = Locale.forLanguageTag("id-ID")

private val DISPLAY_DATE: DateTimeFormatter =
    DateTimeFormatter.ofPattern("d MMM yyyy HH:mm", INDONESIA).withZone(ZoneId.systemDefault())

internal fun BigDecimal.toRupiah(): String =
    NumberFormat.getCurrencyInstance(INDONESIA).apply { maximumFractionDigits = 0 }.format(this)

internal fun Instant.toDisplayDate(): String = DISPLAY_DATE.format(this)

internal fun ProductStatus.label(): String = when (this) {
    ProductStatus.DRAFT -> "Draf"
    ProductStatus.ACTIVE -> "Aktif"
    ProductStatus.INACTIVE -> "Nonaktif"
    ProductStatus.OUT_OF_STOCK -> "Stok habis"
}

/** Status values are PROPOSED (not final in the shared docs); unknown values are shown raw. */
internal fun OrderStatus.label(): String = when (value) {
    "pending" -> "Menunggu konfirmasi"
    "accepted" -> "Diterima"
    "rejected" -> "Ditolak"
    "processing" -> "Diproses"
    "shipped" -> "Dikirim"
    "completed" -> "Selesai"
    else -> value
}

internal fun validationMessage(field: String, reason: ValidationException.Reason): String {
    val label = when (field) {
        "name" -> "Nama produk"
        "categoryId" -> "Kategori"
        "unit" -> "Satuan"
        "price" -> "Harga"
        "stock" -> "Stok"
        "weightPerUnitKg" -> "Berat per satuan"
        else -> field
    }
    return when (reason) {
        ValidationException.Reason.REQUIRED -> "$label wajib diisi"
        ValidationException.Reason.NEGATIVE -> "$label tidak boleh negatif"
        ValidationException.Reason.OUT_OF_RANGE -> "$label di luar rentang yang valid"
    }
}

/** Shows the technical cause too, which helps while the feature is still being wired up. */
internal fun Throwable.toSellerMessage(fallback: String): String = when (this) {
    is ValidationException -> validationMessage(field, reason)
    else -> "$fallback${message?.let { ": $it" }.orEmpty()}".take(240)
}

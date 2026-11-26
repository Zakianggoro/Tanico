package com.example.tanico.domain.model

/**
 * Product publication status.
 * Values follow table-definitions.md: draft, active, inactive, out_of_stock.
 */
enum class ProductStatus(val value: String) {
    DRAFT("draft"),
    ACTIVE("active"),
    INACTIVE("inactive"),
    OUT_OF_STOCK("out_of_stock");

    companion object {
        /** Returns null for unknown values so the data layer decides how to handle them. */
        fun fromValue(value: String): ProductStatus? = entries.firstOrNull { it.value == value }
    }
}

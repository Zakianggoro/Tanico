package com.example.tanico.feature.education.domain

//menghitung estimasi limbah
class EstimateWasteValueUseCase {
    operator fun invoke(quantity: Double, estimatedUnitPrice: Double): Double {
        require(quantity > 0) { "Jumlah limbah harus lebih dari 0." }
        require(estimatedUnitPrice >= 0) { "Harga estimasi tidak boleh negatif." }
        return quantity * estimatedUnitPrice
    }
}

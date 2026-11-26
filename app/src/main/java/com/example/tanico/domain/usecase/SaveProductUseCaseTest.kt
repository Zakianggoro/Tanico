package com.example.tanico.domain.usecase

import com.example.tanico.domain.model.Category
import com.example.tanico.domain.model.Product
import com.example.tanico.domain.model.ProductInput
import com.example.tanico.domain.model.ProductStatus
import com.example.tanico.domain.repository.SellerProductRepository
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveProductUseCaseTest {

    private class FakeRepository : SellerProductRepository {
        var saveCalls = 0
        override suspend fun saveProduct(input: ProductInput): Result<Product> {
            saveCalls++
            return Result.failure(NotImplementedError("not needed for validation tests"))
        }
        override suspend fun getMyProducts() = Result.success(emptyList<Product>())
        override suspend fun getProduct(productId: String): Result<Product> =
            Result.failure(NotImplementedError())
        override suspend fun getCategories() = Result.success(emptyList<Category>())
        override suspend fun updateStock(productId: String, newStock: Int): Result<Product> =
            Result.failure(NotImplementedError())
        override suspend fun deactivateProduct(productId: String) = Result.success(Unit)
    }

    private fun validInput() = ProductInput(
        id = null,
        categoryId = "category-1",
        name = "Jerami padi",
        description = null,
        price = BigDecimal("15000"),
        stock = 10,
        unit = "karung",
        weightPerUnitKg = BigDecimal("20"),
        status = ProductStatus.ACTIVE
    )

    private fun assertRejected(input: ProductInput, field: String) = runTest {
        val repository = FakeRepository()
        val result = SaveProductUseCase(repository)(input)
        val error = result.exceptionOrNull()
        assertTrue(error is ValidationException)
        assertEquals(field, (error as ValidationException).field)
        assertEquals("repository must not be called", 0, repository.saveCalls)
    }

    @Test fun blankName_isRejected() = assertRejected(validInput().copy(name = "  "), "name")
    @Test fun blankUnit_isRejected() = assertRejected(validInput().copy(unit = ""), "unit")
    @Test fun negativePrice_isRejected() =
        assertRejected(validInput().copy(price = BigDecimal("-1")), "price")
    @Test fun negativeStock_isRejected() = assertRejected(validInput().copy(stock = -1), "stock")
    @Test fun negativeWeight_isRejected() =
        assertRejected(validInput().copy(weightPerUnitKg = BigDecimal("-0.5")), "weightPerUnitKg")

    @Test fun validInput_reachesRepositoryOnce() = runTest {
        val repository = FakeRepository()
        SaveProductUseCase(repository)(validInput())
        assertEquals(1, repository.saveCalls)
    }
}

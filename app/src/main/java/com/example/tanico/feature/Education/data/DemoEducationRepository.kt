package com.example.tanico.feature.education.data

import com.example.tanico.data.model.Article
import com.example.tanico.data.model.ArticleCategory
import com.example.tanico.data.remote.RemoteResult
import com.example.tanico.data.model.WastePriceEstimate
import com.example.tanico.data.model.WasteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

//sumber data sementara

class DemoEducationRepository : EducationRepository {
    private val _categories = MutableStateFlow(
        listOf(
            ArticleCategory("cat-jerami", "Jerami", "Pemanfaatan jerami padi"),
            ArticleCategory("cat-sekam", "Sekam", "Pemanfaatan sekam padi"),
            ArticleCategory("cat-tongkol", "Tongkol Jagung", "Pemanfaatan tongkol jagung"),
            ArticleCategory("cat-lingkungan", "Lingkungan", "Pengelolaan limbah pertanian")
        )
    )
    override val categories: StateFlow<List<ArticleCategory>> = _categories.asStateFlow()

    private val _articles = MutableStateFlow(
        listOf(
            Article("article-1", "cat-jerami", "Mengolah Jerami Padi Menjadi Pakan Ternak", "Jerami padi merupakan salah satu limbah pertanian yang jumlahnya cukup banyak setelah panen. Daripada dibakar, jerami dapat diolah menjadi pakan ternak.\n\nSalah satu cara yang dapat dilakukan adalah fermentasi. Proses ini membantu meningkatkan kualitas jerami sehingga lebih mudah dimanfaatkan sebagai pakan. Pastikan bahan dan proses pengolahan dilakukan dengan benar agar hasilnya aman untuk ternak.", likeCount = 24),
            Article("article-2", "cat-sekam", "Sekam Padi untuk Media Tanam", "Sekam padi adalah kulit gabah yang terpisah saat proses penggilingan. Bahan ini dapat dimanfaatkan sebagai campuran media tanam.\n\nSekam membantu memperbaiki sirkulasi udara dan drainase pada media tanam. Sekam mentah maupun sekam bakar memiliki karakteristik yang berbeda, jadi penggunaannya perlu disesuaikan dengan jenis tanaman.", likeCount = 18),
            Article("article-3", "cat-tongkol", "Manfaat Tongkol Jagung Setelah Panen", "Tongkol jagung sering kali hanya dibuang atau dibakar setelah bijinya dipisahkan. Padahal, tongkol jagung dapat dimanfaatkan sebagai bahan kerajinan, bahan bakar biomassa, atau bahan olahan lain.\n\nPemanfaatan yang dipilih perlu disesuaikan dengan kondisi bahan, alat yang tersedia, dan kebutuhan di sekitar.", likeCount = 31),
            Article("article-4", "cat-lingkungan", "Mengurangi Pembakaran Limbah Pertanian", "Pembakaran limbah pertanian dapat menghasilkan asap dan mengurangi peluang pemanfaatan bahan tersebut. Sebelum membuang atau membakar limbah, coba pisahkan berdasarkan jenisnya.\n\nJerami, sekam, dan tongkol jagung memiliki potensi pemanfaatan yang berbeda. Bahan yang masih layak dapat disimpan atau ditawarkan kepada pihak yang membutuhkan.", likeCount = 15)
        )
    )
    override val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _wasteTypes = MutableStateFlow(
        listOf(
            WasteType("waste-jerami", "Jerami Padi", "kg", "Limbah batang dan daun padi"),
            WasteType("waste-sekam", "Sekam Padi", "kg", "Kulit gabah hasil penggilingan"),
            WasteType("waste-tongkol", "Tongkol Jagung", "kg", "Bagian tongkol setelah biji dipisahkan")
        )
    )
    override val wasteTypes: StateFlow<List<WasteType>> = _wasteTypes.asStateFlow()

    private val _priceEstimates = MutableStateFlow(
        listOf(
            WastePriceEstimate("estimate-jerami-demo", "waste-jerami", 700.0, "2026-11-25", "Data contoh untuk demonstrasi"),
            WastePriceEstimate("estimate-sekam-demo", "waste-sekam", 1000.0, "2026-11-25", "Data contoh untuk demonstrasi"),
            WastePriceEstimate("estimate-tongkol-demo", "waste-tongkol", 1200.0, "2026-11-25", "Data contoh untuk demonstrasi")
        )
    )
    override val priceEstimates: StateFlow<List<WastePriceEstimate>> = _priceEstimates.asStateFlow()

    override suspend fun refresh(): RemoteResult<Unit> = RemoteResult.Success(Unit)

    override suspend fun toggleArticleLike(articleId: String): RemoteResult<Unit> {
        _articles.value = _articles.value.map { article ->
            if (article.id == articleId) {
                val liked = !article.isLikedByCurrentUser
                article.copy(
                    isLikedByCurrentUser = liked,
                    likeCount = (article.likeCount + if (liked) 1 else -1).coerceAtLeast(0)
                )
            } else article
        }
        return RemoteResult.Success(Unit)
    }
}
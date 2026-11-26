package com.example.tanico.feature.education.data

import com.example.tanico.data.model.Article
import com.example.tanico.data.model.ArticleCategory
import com.example.tanico.data.model.WastePriceEstimate
import com.example.tanico.data.model.WasteType
import com.example.tanico.data.remote.RemoteResult
import kotlinx.coroutines.flow.StateFlow

interface EducationRepository {
    val categories: StateFlow<List<ArticleCategory>>
    val articles: StateFlow<List<Article>>
    val wasteTypes: StateFlow<List<WasteType>>
    val priceEstimates: StateFlow<List<WastePriceEstimate>>

    suspend fun refresh(): RemoteResult<Unit>
    suspend fun toggleArticleLike(articleId: String): RemoteResult<Unit>
}

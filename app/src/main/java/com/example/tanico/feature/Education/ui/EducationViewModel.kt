package com.example.tanico.feature.education.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tanico.data.model.Article
import com.example.tanico.data.model.ArticleCategory
import com.example.tanico.data.model.WastePriceEstimate
import com.example.tanico.data.model.WasteType
import com.example.tanico.data.remote.RemoteResult
import com.example.tanico.feature.education.data.EducationRepository
import com.example.tanico.feature.education.data.SupabaseEducationRepository
import com.example.tanico.feature.education.domain.EstimateWasteValueUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EducationUiState(
    val categories: List<ArticleCategory> = emptyList(),
    val articles: List<Article> = emptyList(),
    val wasteTypes: List<WasteType> = emptyList(),
    val priceEstimates: List<WastePriceEstimate> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredArticles: List<Article>
        get() = articles.filter { article ->
            val categoryMatch = selectedCategoryId == null || article.categoryId == selectedCategoryId
            val queryMatch = searchQuery.isBlank() ||
                    article.title.contains(searchQuery, true) ||
                    article.content.contains(searchQuery, true)
            article.status == "published" && categoryMatch && queryMatch
        }
}

class EducationViewModel(
    private val repository: EducationRepository = SupabaseEducationRepository(),
    private val estimateWasteValue: EstimateWasteValueUseCase = EstimateWasteValueUseCase()
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedCategoryId = MutableStateFlow<String?>(null)
    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    private val articleData = combine(repository.categories, repository.articles) { categories, articles ->
        categories to articles
    }
    private val calculatorData = combine(repository.wasteTypes, repository.priceEstimates) { types, estimates ->
        types to estimates
    }
    private val filters = combine(searchQuery, selectedCategoryId) { query, categoryId ->
        query to categoryId
    }
    private val loadingState = combine(isLoading, errorMessage) { loading, error ->
        loading to error
    }

    val uiState: StateFlow<EducationUiState> = combine(
        articleData,
        calculatorData,
        filters,
        loadingState
    ) { articles, calculator, filter, loading ->
        EducationUiState(
            categories = articles.first,
            articles = articles.second,
            wasteTypes = calculator.first,
            priceEstimates = calculator.second,
            searchQuery = filter.first,
            selectedCategoryId = filter.second,
            isLoading = loading.first,
            errorMessage = loading.second
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EducationUiState()
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            when (val result = repository.refresh()) {
                is RemoteResult.Success -> errorMessage.value = null
                is RemoteResult.Failure -> errorMessage.value = result.message
            }
            isLoading.value = false
        }
    }

    fun calculateEstimatedValue(quantity: Double, estimatedUnitPrice: Double): Double =
        estimateWasteValue(quantity, estimatedUnitPrice)

    fun onSearchChanged(value: String) {
        searchQuery.value = value
    }

    fun onCategorySelected(categoryId: String?) {
        selectedCategoryId.value = categoryId
    }

    fun onLikeClicked(articleId: String) {
        viewModelScope.launch {
            when (val result = repository.toggleArticleLike(articleId)) {
                is RemoteResult.Success -> errorMessage.value = null
                is RemoteResult.Failure -> errorMessage.value = result.message
            }
        }
    }
}

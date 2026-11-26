package com.example.tanico.feature.education.data

import com.example.tanico.data.model.Article
import com.example.tanico.data.model.ArticleCategory
import com.example.tanico.data.model.WastePriceEstimate
import com.example.tanico.data.model.WasteType
import com.example.tanico.data.remote.RemoteResult
import com.example.tanico.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseEducationRepository : EducationRepository {

    private val _categories = MutableStateFlow<List<ArticleCategory>>(emptyList())
    override val categories: StateFlow<List<ArticleCategory>> = _categories.asStateFlow()
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    override val articles: StateFlow<List<Article>> = _articles.asStateFlow()
    private val _wasteTypes = MutableStateFlow<List<WasteType>>(emptyList())
    override val wasteTypes: StateFlow<List<WasteType>> = _wasteTypes.asStateFlow()
    private val _priceEstimates = MutableStateFlow<List<WastePriceEstimate>>(emptyList())
    override val priceEstimates: StateFlow<List<WastePriceEstimate>> = _priceEstimates.asStateFlow()
    private val client
        get() = SupabaseClientProvider.client

    override suspend fun refresh(): RemoteResult<Unit> = try {
        if (!SupabaseClientProvider.isConfigured) {
            error("Supabase belum dikonfigurasi. Periksa URL dan publishable key di local.properties.")
        }

        val loadedCategories = client
            .from("article_categories")
            .select()
            .decodeList<ArticleCategory>()

        val loadedArticles = client
            .from("articles")
            .select {
                filter {
                    eq("status", "published")
                }
            }
            .decodeList<Article>()

        val loadedWasteTypes = client
            .from("waste_types")
            .select {
                filter {
                    eq("is_active", true)
                }
            }
            .decodeList<WasteType>()

        val loadedEstimates = client
            .from("waste_price_estimates")
            .select()
            .decodeList<WastePriceEstimate>()
            .sortedByDescending { it.effectiveAt }

        val likeRows = try {
            client.from("article_likes").select().decodeList<ArticleLikeRow>()
        } catch (_: Exception) {
            emptyList()
        }

        val currentUserId = client.auth.currentSessionOrNull()?.user?.id
        val likeCounts = likeRows.groupingBy { it.articleId }.eachCount()
        val likedArticleIds = likeRows
            .filter { it.userId == currentUserId && currentUserId != null }
            .map { it.articleId }
            .toSet()

        _categories.value = loadedCategories
        _articles.value = loadedArticles.map { article ->
            article.copy(
                likeCount = likeCounts[article.id] ?: 0,
                isLikedByCurrentUser = article.id in likedArticleIds
            )
        }
        _wasteTypes.value = loadedWasteTypes
        _priceEstimates.value = loadedEstimates

        RemoteResult.Success(Unit)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        RemoteResult.Failure(
            message = exception.message ?: "Gagal memuat data Education dari Supabase.",
            cause = exception
        )
    }

    override suspend fun toggleArticleLike(articleId: String): RemoteResult<Unit> = try {
        val userId = client.auth.currentSessionOrNull()?.user?.id
            ?: error("Silakan masuk ke akun TANICO untuk menyukai artikel.")

        val existingLike = client
            .from("article_likes")
            .select {
                filter {
                    eq("article_id", articleId)
                    eq("user_id", userId)
                }
            }
            .decodeList<ArticleLikeRow>()

        if (existingLike.isEmpty()) {
            client.from("article_likes").insert(
                ArticleLikeInsert(
                    articleId = articleId,
                    userId = userId
                )
            )
        } else {
            client.from("article_likes").delete {
                filter {
                    eq("article_id", articleId)
                    eq("user_id", userId)
                }
            }
        }

        refresh()
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        RemoteResult.Failure(
            message = exception.message ?: "Gagal memperbarui like artikel.",
            cause = exception
        )
    }

    @Serializable
    private data class ArticleLikeRow(
        val id: String,
        @SerialName("article_id") val articleId: String,
        @SerialName("user_id") val userId: String,
        @SerialName("created_at") val createdAt: String? = null
    )

    @Serializable
    private data class ArticleLikeInsert(
        @SerialName("article_id") val articleId: String,
        @SerialName("user_id") val userId: String
    )
}

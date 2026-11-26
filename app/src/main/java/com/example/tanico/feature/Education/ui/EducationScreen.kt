package com.example.tanico.feature.education.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tanico.data.model.Article
import com.example.tanico.data.model.WasteType
import com.example.tanico.ui.theme.ForestGreen
import com.example.tanico.ui.theme.FreshGreen
import com.example.tanico.ui.theme.MainText
import com.example.tanico.ui.theme.MutedText
import java.text.NumberFormat
import java.util.Locale

private enum class EducationPage { LIST, DETAIL, CALCULATOR }

@Composable
fun EducationFeatureScreen(
    modifier: Modifier = Modifier,
    viewModel: EducationViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var page by remember { mutableStateOf(EducationPage.LIST) }
    var selectedArticle by remember { mutableStateOf<Article?>(null) }

    when (page) {
        EducationPage.LIST -> EducationListScreen(
            state = state,
            onSearchChanged = viewModel::onSearchChanged,
            onCategorySelected = viewModel::onCategorySelected,
            onArticleClick = { selectedArticle = it; page = EducationPage.DETAIL },
            onLikeClick = viewModel::onLikeClicked,
            onCalculatorClick = { page = EducationPage.CALCULATOR }
        )
        EducationPage.DETAIL -> selectedArticle?.let { article ->
            ArticleDetailScreen(
                article = state.articles.firstOrNull { it.id == article.id } ?: article,
                categoryName = state.categories.firstOrNull { it.id == article.categoryId }?.name.orEmpty(),
                onBack = { page = EducationPage.LIST },
                onLike = { viewModel.onLikeClicked(article.id) }
            )
        }
        EducationPage.CALCULATOR -> CalculatorScreen(
            wasteTypes = state.wasteTypes,
            priceForWaste = { wasteId -> state.priceEstimates.firstOrNull { it.wasteTypeId == wasteId } },
            calculateEstimatedValue = viewModel::calculateEstimatedValue,
            onBack = { page = EducationPage.LIST }
        )
    }
}

@Composable
private fun EducationListScreen(
    state: EducationUiState,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onArticleClick: (Article) -> Unit,
    onLikeClick: (String) -> Unit,
    onCalculatorClick: () -> Unit
) {
    val popularArticles = state.articles
        .filter { it.status == "published" }
        .sortedByDescending { it.likeCount }
        .take(3)
    val showPopularArticles = state.selectedCategoryId == null && state.searchQuery.isBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.tanico.ui.theme.WarmCream)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 18.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EEE3)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Pembelajaran", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = MainText)
                    Spacer(Modifier.height(3.dp))
                    Text("Temukan ide baru dari sisa hasil panen", fontSize = 12.sp, color = MutedText)
                }
                Box(
                    Modifier.size(42.dp).background(Color.White.copy(alpha = .8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("✳", fontSize = 21.sp, color = ForestGreen) }
            }
        }
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 13.dp),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("Cari ide olahan jerami, sekam...") },
            leadingIcon = { Text("⌕", fontSize = 23.sp, color = MutedText) },
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = com.example.tanico.ui.theme.NaturalBeige,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, top = 15.dp, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreen)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 17.dp, top = 16.dp, bottom = 16.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(27.dp).background(Color.White.copy(alpha = .18f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) { Text("▦", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.width(7.dp))
                                Text("Kalkulator Limbah", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(7.dp))
                            Text("Limbah panen,\nbisa punya nilai!", color = Color.White, fontSize = 19.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text("Cari tahu perkiraan nilai dari sisa hasil panenmu.", color = Color.White.copy(alpha = .92f), fontSize = 11.sp, lineHeight = 16.sp)
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = onCalculatorClick,
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF5F2E9), contentColor = ForestGreen
                                ),
                                shape = RoundedCornerShape(11.dp)
                            ) { Text("Hitung sekarang  →", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                        }
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1520052203542-d3095f1b6cf0?auto=format&fit=crop&w=700&q=85",
                            contentDescription = "Petani bekerja di area persawahan",
                            modifier = Modifier.padding(start = 9.dp).size(width = 104.dp, height = 116.dp).clip(RoundedCornerShape(15.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text("Jelajahi kategori", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MainText)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = state.selectedCategoryId == null,
                            onClick = { onCategorySelected(null) },
                            label = { Text("Semua") }
                        )
                    }
                    items(state.categories, key = { it.id }) { category ->
                        FilterChip(
                            selected = state.selectedCategoryId == category.id,
                            onClick = { onCategorySelected(category.id) },
                            label = { Text(category.name) },
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = MainText
                            ),
                            border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = state.selectedCategoryId == category.id,
                                borderColor = com.example.tanico.ui.theme.NaturalBeige,
                                selectedBorderColor = ForestGreen
                            )
                        )
                    }
                }
            }

            if (showPopularArticles && popularArticles.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(18.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Artikel Terpopuler", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MainText)
                        Spacer(Modifier.weight(1f))
                        Text("Paling banyak disukai", fontSize = 10.sp, color = MutedText)
                    }
                    Spacer(Modifier.height(9.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(popularArticles, key = { "popular-${it.id}" }) { article ->
                            val category = state.categories.firstOrNull { it.id == article.categoryId }?.name.orEmpty()
                            PopularArticleCard(
                                article = article,
                                categoryName = category,
                                onClick = { onArticleClick(article) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (state.selectedCategoryId == null) "Semua Artikel" else "Artikel Pilihan",
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MainText
                    )
                    Spacer(Modifier.weight(1f))
                    Text("${state.filteredArticles.size} artikel", fontSize = 11.sp, color = MutedText)
                }
                Spacer(Modifier.height(10.dp))
            }

            state.errorMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        color = com.example.tanico.ui.theme.MutedText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            when {
                state.isLoading -> item {
                    Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(color = ForestGreen)
                    }
                }
                state.filteredArticles.isEmpty() -> item {
                    EmptyEducationState("Artikel tidak ditemukan", "Coba kata kunci atau kategori yang lain.")
                }
                else -> items(state.filteredArticles, key = { it.id }) { article ->
                    val category = state.categories.firstOrNull { it.id == article.categoryId }?.name.orEmpty()
                    ArticleCard(
                        article = article,
                        categoryName = category,
                        onClick = { onArticleClick(article) },
                        onLike = { onLikeClick(article.id) }
                    )
                    Spacer(Modifier.height(11.dp))
                }
            }
            item {
                Text(
                    "Konten artikel diambil dari database TANICO.",
                    fontSize = 10.sp, color = MutedText,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun PopularArticleCard(
    article: Article,
    categoryName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(245.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                categoryName.uppercase().ifBlank { "EDUKASI" },
                color = FreshGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                article.title,
                color = MainText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(9.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("♥ ${article.likeCount} suka", color = MutedText, fontSize = 10.sp)
                Spacer(Modifier.weight(1f))
                Text("Baca →", color = ForestGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ArticleCard(article: Article, categoryName: String, onClick: () -> Unit, onLike: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1520052203542-d3095f1b6cf0?auto=format&fit=crop&w=500&q=80",
                contentDescription = "Foto kegiatan pertanian",
                modifier = Modifier.width(104.dp).height(112.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(categoryName.uppercase().ifBlank { "EDUKASI" }, color = FreshGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(article.title, color = MainText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 3, overflow = TextOverflow.Ellipsis, lineHeight = 19.sp)
                Spacer(Modifier.height(5.dp))
                Text(article.content, color = MutedText, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 16.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onLike, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp, vertical = 0.dp)) {
                        Text(if (article.isLikedByCurrentUser) "♥ Disukai" else "♡ Suka", color = if (article.isLikedByCurrentUser) ForestGreen else MutedText, fontSize = 11.sp)
                    }
                    Text(article.likeCount.toString(), color = MutedText, fontSize = 10.sp)
                    Spacer(Modifier.weight(1f))
                    Text("Baca →", color = ForestGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ArticleDetailScreen(article: Article, categoryName: String, onBack: () -> Unit, onLike: () -> Unit) {
    Column(Modifier.fillMaxSize().background(com.example.tanico.ui.theme.WarmCream).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).background(Color.White, CircleShape).clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) { Text("‹", color = ForestGreen, fontSize = 30.sp) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Detail artikel", color = MainText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Baca dan pelajari lebih lanjut", color = MutedText, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        AsyncImage(
            model = "https://images.unsplash.com/photo-1520052203542-d3095f1b6cf0?auto=format&fit=crop&w=1100&q=85",
            contentDescription = "Foto pertanian untuk artikel",
            modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(18.dp))
        Text(categoryName.uppercase(), color = FreshGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Text(article.title, color = MainText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text(article.content, color = MainText, fontSize = 15.sp, lineHeight = 25.sp)
        Spacer(Modifier.height(18.dp))
        OutlinedButton(onClick = onLike) { Text(if (article.isLikedByCurrentUser) "♥ Artikel disukai" else "♡ Suka artikel") }
        Text("Konten artikel ditampilkan berdasarkan data artikel yang berstatus published.", color = MutedText, fontSize = 11.sp)
    }
}

@Composable
private fun CalculatorScreen(
    wasteTypes: List<WasteType>,
    priceForWaste: (String) -> com.example.tanico.data.model.WastePriceEstimate?,
    calculateEstimatedValue: (Double, Double) -> Double,
    onBack: () -> Unit
) {
    var selectedWasteId by remember(wasteTypes) { mutableStateOf(wasteTypes.firstOrNull()?.id) }
    var quantityText by remember { mutableStateOf("") }
    var hasCalculated by remember { mutableStateOf(false) }
    val selectedWaste = wasteTypes.firstOrNull { it.id == selectedWasteId }
    val estimate = selectedWaste?.let { priceForWaste(it.id) }
    val quantity = quantityText.replace(',', '.').toDoubleOrNull()
    val valid = quantity != null && quantity > 0 && estimate != null
    val result = if (valid) calculateEstimatedValue(quantity!!, estimate!!.estimatedPrice) else 0.0

    Column(Modifier.fillMaxSize().background(com.example.tanico.ui.theme.WarmCream).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).background(Color.White, CircleShape).clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) { Text("‹", color = ForestGreen, fontSize = 30.sp) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Kalkulator limbah", color = MainText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Hitung perkiraan nilai hasil panen", color = MutedText, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Kalkulator Cuan Limbah", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = MainText)
        Text("Perkirakan nilai ekonomi limbah hasil panen", fontSize = 13.sp, color = MutedText)
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text("Pilih jenis limbah", fontWeight = FontWeight.SemiBold, color = MainText)
                Spacer(Modifier.height(8.dp))
                wasteTypes.filter { it.isActive }.forEach { waste ->
                    val currentEstimate = priceForWaste(waste.id)
                    Row(
                        Modifier.fillMaxWidth().clickable { selectedWasteId = waste.id; hasCalculated = false }.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(waste.name, color = MainText, fontWeight = FontWeight.Medium)
                            Text(
                                currentEstimate?.let { "Rp ${formatRupiah(it.estimatedPrice)} / ${waste.unit}" } ?: "Harga belum tersedia",
                                color = MutedText,
                                fontSize = 12.sp
                            )
                        }
                        if (selectedWasteId == waste.id) Text("✓", color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Jumlah limbah (${selectedWaste?.unit ?: "unit"})", fontWeight = FontWeight.SemiBold, color = MainText)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it.filter { char -> char.isDigit() || char == '.' || char == ',' }; hasCalculated = false },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Contoh: 25") }
                )
                Spacer(Modifier.height(14.dp))
                Button(onClick = { hasCalculated = true }, modifier = Modifier.fillMaxWidth(), enabled = valid) { Text("Hitung Estimasi") }
            }
        }
        if (hasCalculated && valid && selectedWaste != null && estimate != null) {
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = FreshGreen.copy(alpha = 0.12f)), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Perkiraan nilai limbah", color = MutedText, fontSize = 13.sp)
                    Spacer(Modifier.height(5.dp))
                    Text("Rp ${formatRupiah(result)}", color = ForestGreen, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("$quantityText ${selectedWaste.unit} × Rp ${formatRupiah(estimate.estimatedPrice)}", color = MainText, fontSize = 12.sp)
                    Text("Acuan harga: ${estimate.effectiveAt}", color = MutedText, fontSize = 11.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("Catatan: hasil ini hanya estimasi, bukan jaminan harga jual atau pendapatan. Harga sebenarnya dapat berbeda tergantung kualitas, lokasi, dan kondisi pasar.", color = MutedText, fontSize = 12.sp, lineHeight = 18.sp)
        Text("Harga yang tampil mengikuti data estimasi di database TANICO.", color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun EmptyEducationState(title: String, message: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🌿", fontSize = 38.sp)
        Spacer(Modifier.height(10.dp))
        Text(title, color = MainText, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(message, color = MutedText, fontSize = 12.sp)
    }
}

private fun categoryEmoji(category: String): String = when (category.lowercase()) {
    "jerami" -> "🌾"
    "sekam" -> "🌱"
    "tongkol jagung" -> "🌽"
    else -> "♻️"
}

private fun formatRupiah(value: Double): String = NumberFormat.getNumberInstance(Locale("id", "ID"))
    .apply { maximumFractionDigits = 0; minimumFractionDigits = 0 }
    .format(value)

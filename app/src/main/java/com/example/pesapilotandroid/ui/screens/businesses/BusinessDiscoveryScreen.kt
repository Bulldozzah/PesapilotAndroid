package com.example.pesapilotandroid.ui.screens.businesses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.pesapilotandroid.data.model.BusinessCategory
import com.example.pesapilotandroid.data.model.BusinessTemplate
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDiscoveryScreen(
    navController: NavController,
    viewModel: BusinessDiscoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val categoryMap = remember(uiState.categories) {
        uiState.categories.associateBy { it.id }
    }

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Hero header ─────────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppPrimary)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Start Your Business Journey 🚀",
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Browse ${uiState.templates.size} business types across " +
                        "${uiState.categories.size} categories and get step-by-step guidance " +
                        "to launch your dream business",
                    fontFamily = FigtreeFamily,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.82f),
                    lineHeight = 18.sp
                )
            }
        }

        // ── Sticky search bar ────────────────────────────────────────────────
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        "Search business ideas…",
                        fontFamily = FigtreeFamily,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = AppMutedText)
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AppMutedText)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor    = AppAccentBlue,
                    unfocusedBorderColor  = AppBorder,
                    focusedContainerColor = AppCard,
                    unfocusedContainerColor = AppCard
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // ── Category chips ───────────────────────────────────────────────────
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick  = { viewModel.selectCategory(null) },
                        label    = { Text("All", fontFamily = FigtreeFamily) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppPrimary,
                            selectedLabelColor     = Color.White
                        )
                    )
                }
                items(uiState.categories) { cat ->
                    FilterChip(
                        selected = uiState.selectedCategory?.id == cat.id,
                        onClick  = { viewModel.selectCategory(cat) },
                        label    = {
                            Text(
                                text = "${cat.emoji.orEmpty()} ${cat.name}".trim(),
                                fontFamily = FigtreeFamily
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppPrimary,
                            selectedLabelColor     = Color.White
                        )
                    )
                }
            }
        }

        // ── Count label ──────────────────────────────────────────────────────
        item {
            Text(
                text = "Showing ${uiState.filteredTemplates.size} of ${uiState.templates.size}",
                fontFamily = FigtreeFamily,
                fontSize = 12.sp,
                color = AppMutedText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // ── Custom business banner ───────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.5.dp,
                        color = AppAccentBlue.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(AppAccentBlue.copy(alpha = 0.07f))
                    .clickable { /* TODO: navigate to custom business creation */ }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Default.Add,
                        contentDescription = null,
                        tint               = AppAccentBlue,
                        modifier           = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text       = "Create Custom Business",
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 14.sp,
                            color      = AppAccentBlue
                        )
                        Text(
                            text       = "Don't see your idea? Build your own roadmap",
                            fontFamily = FigtreeFamily,
                            fontSize   = 12.sp,
                            color      = AppMutedText
                        )
                    }
                }
            }
        }

        // ── 2-column card grid ───────────────────────────────────────────────
        if (uiState.filteredTemplates.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "No business ideas found",
                        fontFamily = FigtreeFamily,
                        color      = AppMutedText
                    )
                }
            }
        } else {
            items(uiState.filteredTemplates.chunked(2)) { pair ->
                Row(
                    modifier            = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { template ->
                        BusinessTemplateCard(
                            template  = template,
                            category  = categoryMap[template.categoryId],
                            onClick   = {
                                navController.navigate(NavRoute.BusinessTemplateDetail(template.id))
                            },
                            modifier  = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ─── Business Template Card ───────────────────────────────────────────────────

@Composable
private fun BusinessTemplateCard(
    template: BusinessTemplate,
    category: BusinessCategory?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val breakevenMonths = remember(template) {
        if (template.monthlyProfitMin > 0)
            (template.startupCostMin / template.monthlyProfitMin).toInt().coerceAtLeast(1)
        else
            template.timeToProfitMonths
    }

    Card(
        onClick   = onClick,
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = AppCard)
    ) {
        Column {
            // ── Image box ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                // Gradient fallback
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colorStops = arrayOf(
                                    0.0f to AppPrimary.copy(alpha = 0.7f),
                                    0.55f to Color(0xFF9C27B0).copy(alpha = 0.6f),
                                    1.0f to Color(0xFFE91E8C).copy(alpha = 0.6f)
                                ),
                                start = Offset(0f, 0f),
                                end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        )
                )

                // Cover photo
                if (!template.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model          = template.imageUrl,
                        contentDescription = template.name,
                        contentScale   = ContentScale.Crop,
                        modifier       = Modifier.fillMaxSize()
                    )
                }

                // Dark vignette overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent,
                                    0.5f to Color.Black.copy(alpha = 0.1f),
                                    1.0f to Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )

                // Difficulty badge — top-right
                DifficultyBadge(
                    difficulty = template.difficulty,
                    modifier   = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )

                // Category emoji — bottom-left
                val emoji = category?.emoji
                if (!emoji.isNullOrBlank()) {
                    Text(
                        text     = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    )
                }
            }

            // ── Text body ─────────────────────────────────────────────────
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text       = template.name,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    color      = AppText,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                if (!template.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text       = template.description,
                        fontFamily = FigtreeFamily,
                        fontSize   = 11.sp,
                        color      = AppMutedText,
                        maxLines   = 2,
                        overflow   = TextOverflow.Ellipsis,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Three financial stats
                StatRow(label = "💰 Startup", value = "KES ${fmtK(template.startupCostMin)}")
                StatRow(
                    label     = "📈 Profit/mo",
                    value     = "KES ${fmtK(template.monthlyProfitMin)}",
                    valueColor = AppGreenSuccess
                )
                StatRow(label = "⏱ Breakeven", value = "${breakevenMonths}mo")
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color = AppText
) {
    Row(
        modifier            = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment   = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            fontFamily = FigtreeFamily,
            fontSize   = 10.sp,
            color      = AppMutedText
        )
        Text(
            text       = value,
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 10.sp,
            color      = valueColor
        )
    }
}

// ─── Difficulty Badge ─────────────────────────────────────────────────────────

@Composable
private fun DifficultyBadge(difficulty: String, modifier: Modifier = Modifier) {
    val (bg, label) = when (difficulty.lowercase()) {
        "easy"   -> Color(0xFF28A87A) to "Beginner"
        "medium" -> Color(0xFFE0A020) to "Intermediate"
        "hard"   -> Color(0xFFCC4433) to "Advanced"
        else     -> AppAccentBlue     to difficulty.replaceFirstChar { it.uppercase() }
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = modifier
    ) {
        Text(
            text       = label,
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 9.sp,
            color      = Color.White,
            letterSpacing = 0.5.sp,
            modifier   = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun fmtK(amount: Double): String = when {
    amount >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", amount / 1_000_000)
    amount >= 1_000     -> String.format(java.util.Locale.US, "%.0fK", amount / 1_000)
    else                -> String.format(java.util.Locale.US, "%.0f",  amount)
}

package com.example.pesapilotandroid.ui.screens.businesses

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDetailScreen(
    businessId: String,
    navController: NavController,
    viewModel: BusinessDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(businessId) {
        viewModel.loadBusiness(businessId)
    }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = uiState.business?.name ?: "Business Details",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            uiState.business?.let { business ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Business Info Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = business.name,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                business.description?.let { desc ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    val status = if (business.startedAt != null) "Active" else "Planning"
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(status) },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    )
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("Budget: ${business.currency} ${business.budget}") },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.AttachMoney,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Roadmap Steps
                    item {
                        Text(
                            text = "Setup Roadmap",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    itemsIndexed(roadmapSteps) { index, step ->
                        val stepNumber = index + 1
                        // Progress is now tracked via step_progress table
                        // For now, show all steps as available
                        val isCompleted = false
                        val isCurrent = stepNumber == 1

                        RoadmapStepCard(
                            stepNumber = stepNumber,
                            title = step.title,
                            description = step.description,
                            isCompleted = isCompleted,
                            isCurrent = isCurrent,
                            showRegistrationLink = stepNumber == 2 && isCurrent,
                            countryAuthority = uiState.countryAuthority,
                            onOpenRegistration = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            onMarkComplete = {
                                viewModel.completeStep(stepNumber)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoadmapStepCard(
    stepNumber: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    showRegistrationLink: Boolean = false,
    countryAuthority: CountryAuthorityInfo? = null,
    onOpenRegistration: (String) -> Unit = {},
    onMarkComplete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                isCurrent -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when {
                        isCompleted -> MaterialTheme.colorScheme.primary
                        isCurrent -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = stepNumber.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isCurrent) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Registration Link for Step 2
            if (showRegistrationLink && countryAuthority != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = countryAuthority.countryName,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = countryAuthority.authorityName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        countryAuthority.website?.let { website ->
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { onOpenRegistration(website) }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Visit Registration Website")
                            }
                        }
                    }
                }
            }

            // Mark Complete Button
            if (isCurrent) {
                Spacer(modifier = Modifier.height(12.dp))
                PesaPilotButton(
                    text = "Mark as Complete",
                    onClick = onMarkComplete
                )
            }
        }
    }
}

data class RoadmapStep(
    val title: String,
    val description: String
)

val roadmapSteps = listOf(
    RoadmapStep("Business Idea Validation", "Validate your business idea and market research"),
    RoadmapStep("Licenses & Registration", "Register your business and obtain necessary licenses"),
    RoadmapStep("Setup Requirements", "Set up your business infrastructure and resources"),
    RoadmapStep("Funding", "Secure funding for your business"),
    RoadmapStep("Marketing", "Develop and execute your marketing strategy"),
    RoadmapStep("Operations", "Set up day-to-day operations"),
    RoadmapStep("Growth Tracking", "Monitor and grow your business")
)

data class CountryAuthorityInfo(
    val countryName: String,
    val authorityName: String,
    val website: String?
)

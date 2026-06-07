package com.example.pesapilotandroid.ui.screens.businesses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapsScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Business Roadmaps",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Your journey from idea to successful business",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(roadmapSteps) { index, step ->
                RoadmapGuideCard(
                    stepNumber = index + 1,
                    title = step.title,
                    description = step.description,
                    tips = getRoadmapTips(index + 1)
                )
            }
        }
    }
}

@Composable
private fun RoadmapGuideCard(
    stepNumber: Int,
    title: String,
    description: String,
    tips: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
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
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stepNumber.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
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
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tips & Guidance",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                tips.forEach { tip ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.LightbulbCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

private fun getRoadmapTips(step: Int): List<String> {
    return when (step) {
        1 -> listOf(
            "Research your target market thoroughly",
            "Identify your unique value proposition",
            "Talk to potential customers",
            "Analyze competitors in your space"
        )
        2 -> listOf(
            "Check local business registration requirements",
            "Obtain necessary permits and licenses",
            "Register for tax purposes",
            "Consider business structure (sole proprietor, LLC, etc.)"
        )
        3 -> listOf(
            "Set up your workspace or location",
            "Purchase necessary equipment",
            "Establish supplier relationships",
            "Set up business banking"
        )
        4 -> listOf(
            "Calculate your startup costs accurately",
            "Explore microfinance options",
            "Consider bootstrapping initially",
            "Prepare a solid business plan for investors"
        )
        5 -> listOf(
            "Build your brand identity",
            "Create social media presence",
            "Network with potential customers",
            "Develop a marketing budget"
        )
        6 -> listOf(
            "Establish daily routines and processes",
            "Set up inventory management",
            "Create customer service protocols",
            "Track expenses and revenue"
        )
        7 -> listOf(
            "Monitor key performance indicators",
            "Gather customer feedback",
            "Look for expansion opportunities",
            "Reinvest profits strategically"
        )
        else -> emptyList()
    }
}

package com.example.pesapilotandroid.ui.screens.funding

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.data.model.RequiredDocument
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApplicationScreen(
    microfinanceId: String,
    navController: NavController,
    viewModel: LoanApplicationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(microfinanceId) {
        viewModel.loadMicrofinance(microfinanceId)
    }

    LaunchedEffect(uiState.applicationReady) {
        if (uiState.applicationReady) {
            // Open WhatsApp or Email with document links
            val mfi = uiState.microfinance
            val message = buildString {
                append("Hello, I would like to apply for a loan.\n\n")
                append("My documents:\n")
                uiState.uploadedDocuments.forEach { (doc, url) ->
                    append("- ${RequiredDocument.entries.find { it.value == doc }?.displayName ?: doc}: $url\n")
                }
            }

            if (mfi?.whatsappNumber != null) {
                val intent = Intent(Intent.ACTION_VIEW, 
                    Uri.parse("https://wa.me/${mfi.whatsappNumber}?text=${Uri.encode(message)}"))
                context.startActivity(intent)
            } else if (mfi?.email != null) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(mfi.email))
                    putExtra(Intent.EXTRA_SUBJECT, "Loan Application")
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                context.startActivity(intent)
            }

            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Apply for Loan",
                onBackClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            uiState.microfinance?.let { mfi ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Applying to: ${mfi.name}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Upload the required documents below",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Required Documents",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(mfi.requiredDocuments) { docType ->
                        val docName = RequiredDocument.entries
                            .find { it.value == docType }?.displayName ?: docType
                        val isUploaded = uiState.uploadedDocuments.containsKey(docType)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUploaded)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isUploaded) 
                                        Icons.Default.CheckCircle 
                                    else 
                                        Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (isUploaded)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = docName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (!isUploaded) {
                                    OutlinedButton(
                                        onClick = {
                                            // In a real app, this would open file picker
                                            // For now, simulate upload
                                            viewModel.simulateUpload(docType)
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Upload,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Upload")
                                    }
                                } else {
                                    Text(
                                        text = "Uploaded",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val allUploaded = mfi.requiredDocuments.all { 
                            uiState.uploadedDocuments.containsKey(it) 
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PesaPilotButton(
                                text = "Send via WhatsApp",
                                onClick = { viewModel.submitApplication("whatsapp") },
                                enabled = allUploaded && mfi.whatsappNumber != null,
                                icon = Icons.Default.Chat
                            )

                            PesaPilotButton(
                                text = "Send via Email",
                                onClick = { viewModel.submitApplication("email") },
                                enabled = allUploaded && mfi.email != null,
                                icon = Icons.Default.Email
                            )
                        }

                        if (!allUploaded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Please upload all required documents to continue",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

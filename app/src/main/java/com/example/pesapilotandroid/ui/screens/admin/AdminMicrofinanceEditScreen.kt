package com.example.pesapilotandroid.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.data.model.RequiredDocument
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotTextField
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMicrofinanceEditScreen(
    microfinanceId: String?,
    navController: NavController,
    viewModel: AdminMicrofinanceEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(microfinanceId) {
        if (microfinanceId != null) {
            viewModel.loadMicrofinance(microfinanceId)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = if (microfinanceId == null) "Add Microfinance" else "Edit Microfinance",
                onBackClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    PesaPilotTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = "Name *"
                    )
                }

                item {
                    PesaPilotTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = "Description",
                        singleLine = false,
                        maxLines = 3
                    )
                }

                item {
                    PesaPilotTextField(
                        value = uiState.logoUrl,
                        onValueChange = { viewModel.updateLogoUrl(it) },
                        label = "Logo URL"
                    )
                }

                item {
                    Text(
                        text = "Loan Information",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PesaPilotTextField(
                            value = uiState.minLoanAmount,
                            onValueChange = { viewModel.updateMinLoanAmount(it) },
                            label = "Min Loan",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                        PesaPilotTextField(
                            value = uiState.maxLoanAmount,
                            onValueChange = { viewModel.updateMaxLoanAmount(it) },
                            label = "Max Loan",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PesaPilotTextField(
                            value = uiState.minInterestRate,
                            onValueChange = { viewModel.updateMinInterestRate(it) },
                            label = "Min Interest %",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                        PesaPilotTextField(
                            value = uiState.maxInterestRate,
                            onValueChange = { viewModel.updateMaxInterestRate(it) },
                            label = "Max Interest %",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Contact Information",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    PesaPilotTextField(
                        value = uiState.address,
                        onValueChange = { viewModel.updateAddress(it) },
                        label = "Address"
                    )
                }

                item {
                    PesaPilotTextField(
                        value = uiState.phoneNumber,
                        onValueChange = { viewModel.updatePhoneNumber(it) },
                        label = "Phone Number",
                        keyboardType = KeyboardType.Phone
                    )
                }

                item {
                    PesaPilotTextField(
                        value = uiState.whatsappNumber,
                        onValueChange = { viewModel.updateWhatsappNumber(it) },
                        label = "WhatsApp Number",
                        keyboardType = KeyboardType.Phone
                    )
                }

                item {
                    PesaPilotTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = "Email",
                        keyboardType = KeyboardType.Email
                    )
                }

                item {
                    PesaPilotTextField(
                        value = uiState.website,
                        onValueChange = { viewModel.updateWebsite(it) },
                        label = "Website"
                    )
                }

                item {
                    Text(
                        text = "Required Documents",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(RequiredDocument.entries) { doc ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.requiredDocuments.contains(doc.value),
                            onCheckedChange = { checked ->
                                viewModel.toggleDocument(doc.value, checked)
                            }
                        )
                        Text(
                            text = doc.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    PesaPilotButton(
                        text = if (microfinanceId == null) "Create" else "Save Changes",
                        onClick = { viewModel.save() },
                        isLoading = uiState.isSaving,
                        enabled = uiState.name.isNotBlank()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

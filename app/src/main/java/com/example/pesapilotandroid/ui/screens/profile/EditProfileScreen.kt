package com.example.pesapilotandroid.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotTextField
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar
import com.example.pesapilotandroid.ui.screens.auth.countries
import com.example.pesapilotandroid.ui.screens.auth.currencies

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
                title = "Edit Profile",
                onBackClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PesaPilotTextField(
                    value = uiState.fullName,
                    onValueChange = { viewModel.updateFullName(it) },
                    label = "Full Name",
                    leadingIcon = Icons.Default.Person
                )

                PesaPilotTextField(
                    value = uiState.phoneNumber,
                    onValueChange = { viewModel.updatePhoneNumber(it) },
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = Icons.Default.Phone
                )

                // Country Dropdown
                var countryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = countryExpanded,
                    onExpandedChange = { countryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uiState.country,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Country") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = countryExpanded,
                        onDismissRequest = { countryExpanded = false }
                    ) {
                        countries.forEach { country ->
                            DropdownMenuItem(
                                text = { Text(country.name) },
                                onClick = {
                                    viewModel.updateCountry(country.name, country.code)
                                    countryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Currency Dropdown
                var currencyExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = currencyExpanded,
                    onExpandedChange = { currencyExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uiState.currency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = currencyExpanded,
                        onDismissRequest = { currencyExpanded = false }
                    ) {
                        currencies.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text("${currency.code} - ${currency.name}") },
                                onClick = {
                                    viewModel.updateCurrency(currency.code)
                                    currencyExpanded = false
                                }
                            )
                        }
                    }
                }

                PesaPilotTextField(
                    value = uiState.businessName,
                    onValueChange = { viewModel.updateBusinessName(it) },
                    label = "Business Name (Optional)",
                    leadingIcon = Icons.Default.Business
                )

                Spacer(modifier = Modifier.height(16.dp))

                PesaPilotButton(
                    text = "Save Changes",
                    onClick = { viewModel.saveProfile() },
                    isLoading = uiState.isSaving,
                    enabled = uiState.fullName.isNotBlank()
                )
            }
        }
    }
}

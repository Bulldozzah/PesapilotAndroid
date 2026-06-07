package com.example.pesapilotandroid.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onSetupComplete()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Complete Your Profile",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = "Tell us a bit about yourself",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PesaPilotTextField(
                value = uiState.fullName,
                onValueChange = { viewModel.updateFullName(it) },
                label = "Full Name",
                leadingIcon = Icons.Default.Person
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PesaPilotTextField(
                value = uiState.phoneNumber,
                onValueChange = { viewModel.updatePhoneNumber(it) },
                label = "Phone Number",
                keyboardType = KeyboardType.Phone,
                leadingIcon = Icons.Default.Phone
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                                viewModel.updateCountry(country)
                                countryExpanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PesaPilotTextField(
                value = uiState.businessName,
                onValueChange = { viewModel.updateBusinessName(it) },
                label = "Business Name (Optional)",
                leadingIcon = Icons.Default.Business
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PesaPilotButton(
                text = "Complete Setup",
                onClick = { viewModel.saveProfile() },
                isLoading = uiState.isLoading,
                enabled = uiState.fullName.isNotBlank() && 
                         uiState.country.isNotBlank() && 
                         uiState.currency.isNotBlank()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class Country(
    val name: String,
    val code: String,
    val currency: String
)

data class Currency(
    val code: String,
    val name: String
)

val countries = listOf(
    Country("Zambia", "ZM", "ZMW"),
    Country("Kenya", "KE", "KES"),
    Country("Tanzania", "TZ", "TZS"),
    Country("Uganda", "UG", "UGX"),
    Country("Rwanda", "RW", "RWF"),
    Country("Malawi", "MW", "MWK"),
    Country("Zimbabwe", "ZW", "ZWL"),
    Country("Botswana", "BW", "BWP"),
    Country("South Africa", "ZA", "ZAR"),
    Country("Nigeria", "NG", "NGN"),
    Country("Ghana", "GH", "GHS"),
    Country("Ethiopia", "ET", "ETB"),
    Country("United States", "US", "USD"),
    Country("United Kingdom", "GB", "GBP")
)

val currencies = listOf(
    Currency("ZMW", "Zambian Kwacha"),
    Currency("KES", "Kenyan Shilling"),
    Currency("TZS", "Tanzanian Shilling"),
    Currency("UGX", "Ugandan Shilling"),
    Currency("RWF", "Rwandan Franc"),
    Currency("MWK", "Malawian Kwacha"),
    Currency("ZWL", "Zimbabwean Dollar"),
    Currency("BWP", "Botswana Pula"),
    Currency("ZAR", "South African Rand"),
    Currency("NGN", "Nigerian Naira"),
    Currency("GHS", "Ghanaian Cedi"),
    Currency("ETB", "Ethiopian Birr"),
    Currency("USD", "US Dollar"),
    Currency("GBP", "British Pound"),
    Currency("EUR", "Euro")
)

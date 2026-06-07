package com.example.pesapilotandroid.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotPasswordField
import com.example.pesapilotandroid.ui.components.PesaPilotTextField
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    val passwordsMatch = password == confirmPassword
    val isFormValid = fullName.isNotBlank() && 
                      email.isNotBlank() && 
                      password.length >= 6 && 
                      passwordsMatch

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Create Account",
                onBackClick = onNavigateToLogin
            )
        },
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
            Text(
                text = "Join PesaPilot",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = "Create an account to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PesaPilotTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
                leadingIcon = Icons.Default.Person
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PesaPilotTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Email
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PesaPilotPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isError = password.isNotEmpty() && password.length < 6,
                errorMessage = if (password.isNotEmpty() && password.length < 6) 
                    "Password must be at least 6 characters" else null
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PesaPilotPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                isError = confirmPassword.isNotEmpty() && !passwordsMatch,
                errorMessage = if (confirmPassword.isNotEmpty() && !passwordsMatch) 
                    "Passwords do not match" else null
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PesaPilotButton(
                text = "Create Account",
                onClick = {
                    viewModel.signUp(email, password, fullName, onSignUpSuccess)
                },
                isLoading = uiState.isLoading,
                enabled = isFormValid
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text("Sign In")
                }
            }
        }
    }
}

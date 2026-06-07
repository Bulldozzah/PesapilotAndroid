package com.example.pesapilotandroid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pesapilotandroid.ui.screens.auth.AuthViewModel

@Composable
fun TestLogin2() {
    // Mimic LoginScreen - Step 1: hiltViewModel
    val viewModel: AuthViewModel = hiltViewModel()
    
    // Mimic LoginScreen - Step 2: collectAsState calls
    val uiState by viewModel.uiState.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState(initial = false)
    
    // Mimic LoginScreen - Step 3: remember states
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = "TEST2 - VIEWMODEL OK", color = Color.White)
            Text(text = "auth=$isAuthenticated", color = Color.Yellow)
            Text(text = "email=$email", color = Color.Cyan)
        }
    }
}

package com.example.pesapilotandroid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pesapilotandroid.ui.screens.auth.AuthViewModel

@Composable
fun TestLogin() {
    // Test if hiltViewModel works here
    val viewModel: AuthViewModel = hiltViewModel()
    
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TEST LOGIN - VIEWMODEL OK",
            color = Color.White
        )
    }
}

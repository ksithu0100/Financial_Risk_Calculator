package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financialriskcalculator.viewmodel.FinancialViewModel

@Composable
fun SettingsScreen(
    viewModel: FinancialViewModel,
    onEditProfile: () -> Unit,
    onDeleteAccount: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        
        Button(onClick = onEditProfile, modifier = Modifier.fillMaxWidth()) {
            Text("Edit Profile")
        }
        
        Button(
            onClick = onDeleteAccount,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Delete Account")
        }
        
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

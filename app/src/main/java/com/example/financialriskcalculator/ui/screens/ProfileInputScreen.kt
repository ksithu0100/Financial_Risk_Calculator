package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financialriskcalculator.viewmodel.FinancialViewModel

@Composable
fun ProfileInputScreen(viewModel: FinancialViewModel, onNext: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }
    var savings by remember { mutableStateOf("") }
    var creditScore by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    
    var showWarningDialog by remember { mutableStateOf(false) }

    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("Financial Flag Detected") },
            text = { Text("🚨 Note: Your high savings relative to a low credit score is an unusual pattern that may be flagged as suspicious. Please ensure all data is accurate before proceeding.") },
            confirmButton = {
                Button(onClick = { 
                    showWarningDialog = false
                    onNext() 
                }) {
                    Text("Proceed Anyway")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWarningDialog = false }) {
                    Text("Review Data")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("User Profile Information", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = income, onValueChange = { income = it }, label = { Text("Monthly Income") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = savings, onValueChange = { savings = it }, label = { Text("Current Savings") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = creditScore, onValueChange = { creditScore = it }, label = { Text("Credit Score") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        OutlinedTextField(value = occupation, onValueChange = { occupation = it }, label = { Text("Occupation") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val s = savings.toDoubleOrNull() ?: 0.0
                val c = creditScore.toIntOrNull() ?: 0
                
                viewModel.updateBasicInfo(
                    name,
                    income.toDoubleOrNull() ?: 0.0,
                    s,
                    c,
                    occupation
                )
                
                if (s > 50000 && c < 550) {
                    showWarningDialog = true
                } else {
                    onNext()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue to Expenses")
        }
    }
}

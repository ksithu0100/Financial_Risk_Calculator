package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financialriskcalculator.viewmodel.FinancialViewModel

@Composable
fun ProfileInputScreen(viewModel: FinancialViewModel, onNext: () -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
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

    // Validation Helpers
    val filterName: (String) -> String = { input ->
        input.filter { it.isLetter() || it.isWhitespace() }
    }

    val filterDecimal: (String) -> String = { input ->
        val filtered = input.filter { it.isDigit() || it == '.' }
        val firstDotIndex = filtered.indexOf('.')
        if (firstDotIndex != -1) {
            val beforeDot = filtered.substring(0, firstDotIndex + 1)
            val afterDot = filtered.substring(firstDotIndex + 1).replace(".", "")
            beforeDot + afterDot
        } else {
            filtered
        }
    }

    val filterInteger: (String) -> String = { input ->
        input.filter { it.isDigit() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("User Profile Information", style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = filterName(it) },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = filterName(it) },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = age,
            onValueChange = { age = filterInteger(it) },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = income,
            onValueChange = { income = filterDecimal(it) },
            label = { Text("Monthly Income") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            prefix = { Text("$") }
        )
        
        OutlinedTextField(
            value = savings,
            onValueChange = { savings = filterDecimal(it) },
            label = { Text("Current Savings") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            prefix = { Text("$") }
        )
        
        OutlinedTextField(
            value = creditScore,
            onValueChange = { creditScore = filterInteger(it) },
            label = { Text("Credit Score") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        
        OutlinedTextField(
            value = occupation,
            onValueChange = { occupation = filterName(it) },
            label = { Text("Occupation") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val s = savings.toDoubleOrNull() ?: 0.0
                val c = creditScore.toIntOrNull() ?: 0
                val a = age.toIntOrNull() ?: 0
                
                viewModel.updateBasicInfo(
                    "$firstName $lastName".trim(),
                    income.toDoubleOrNull() ?: 0.0,
                    s,
                    c,
                    occupation
                )
                // Also set age in profile
                viewModel.userProfile.setAge(a)
                
                if (s > 50000 && c < 550) {
                    showWarningDialog = true
                } else {
                    onNext()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = firstName.isNotBlank() && lastName.isNotBlank() && age.isNotBlank() && income.isNotBlank() && savings.isNotBlank() && creditScore.isNotBlank()
        ) {
            Text("Continue to Expenses")
        }
    }
}

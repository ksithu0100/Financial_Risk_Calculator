package com.example.financialriskcalculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MonetaryDataScreen(
    profile: RiskProfile,
    onProfileChange: (RiskProfile) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Financial Details", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = if (profile.rent == 0.0) "" else profile.rent.toString(),
            onValueChange = { onProfileChange(profile.copy(rent = it.toDoubleOrNull() ?: 0.0)) },
            label = { Text("Rent") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = if (profile.carPayment == 0.0) "" else profile.carPayment.toString(),
            onValueChange = { onProfileChange(profile.copy(carPayment = it.toDoubleOrNull() ?: 0.0)) },
            label = { Text("Car Payment") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = if (profile.subscription == 0.0) "" else profile.subscription.toString(),
            onValueChange = { onProfileChange(profile.copy(subscription = it.toDoubleOrNull() ?: 0.0)) },
            label = { Text("Subscriptions") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = if (profile.insurance == 0.0) "" else profile.insurance.toString(),
            onValueChange = { onProfileChange(profile.copy(insurance = it.toDoubleOrNull() ?: 0.0)) },
            label = { Text("Insurance") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = if (profile.utilities == 0.0) "" else profile.utilities.toString(),
            onValueChange = { onProfileChange(profile.copy(utilities = it.toDoubleOrNull() ?: 0.0)) },
            label = { Text("Utilities") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = if (profile.savings == 0.0) "" else profile.savings.toString(),
            onValueChange = { onProfileChange(profile.copy(savings = it.toDoubleOrNull() ?: 0.0)) },
            label = { Text("Savings") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = if (profile.creditScore == 0) "" else profile.creditScore.toString(),
            onValueChange = { onProfileChange(profile.copy(creditScore = it.toIntOrNull() ?: 0)) },
            label = { Text("Credit Score") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }
}

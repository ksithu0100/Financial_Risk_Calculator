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
fun PersonalInfoScreen(
    profile: RiskProfile,
    onProfileChange: (RiskProfile) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Personal Information", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = profile.name,
            onValueChange = { onProfileChange(profile.copy(name = it)) },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = if (profile.age == 0) "" else profile.age.toString(),
            onValueChange = { onProfileChange(profile.copy(age = it.toIntOrNull() ?: 0)) },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = if (profile.monthlyIncome == 0.0) "" else profile.monthlyIncome.toString(),
            onValueChange = { onProfileChange(profile.copy(monthlyIncome = it.toDoubleOrNull() ?: 0.0)) },
            label = { Text("Monthly Income") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = profile.job,
            onValueChange = { onProfileChange(profile.copy(job = it)) },
            label = { Text("Job") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}

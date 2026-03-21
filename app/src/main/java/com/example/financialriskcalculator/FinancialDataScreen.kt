package com.example.financialriskcalculator

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun FinancialDataScreen(
    onSave: (RiskProfile) -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var profile by remember { mutableStateOf(RiskProfile()) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentStep) {
            1 -> PersonalInfoScreen(
                profile = profile,
                onProfileChange = { profile = it },
                onNext = { currentStep = 2 }
            )
            2 -> MonetaryDataScreen(
                profile = profile,
                onProfileChange = { profile = it },
                onSave = { onSave(profile) },
                onBack = { currentStep = 1 }
            )
        }
    }
}

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
import com.example.financialriskcalculator.models.FinancialDecision
import com.example.financialriskcalculator.viewmodel.FinancialViewModel

@Composable
fun DecisionInputScreen(viewModel: FinancialViewModel, onCalculate: () -> Unit) {
    var isLongTerm by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FinancialDecision.Category.HOUSING) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Evaluate Purchase", style = MaterialTheme.typography.headlineMedium)

        Text("Type of Decision", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !isLongTerm,
                onClick = { isLongTerm = false },
                label = { Text("Short-Term") }
            )
            FilterChip(
                selected = isLongTerm,
                onClick = { isLongTerm = true },
                label = { Text("Long-Term") }
            )
        }

        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name (e.g., Concert, Stocks, Dinner)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Purchase Cost") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            prefix = { Text("$") }
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { categoryExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Category: ${selectedCategory.displayName}")
            }
            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                FinancialDecision.Category.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.calculateRisk(
                    itemName,
                    amount.toDoubleOrNull() ?: 0.0,
                    selectedCategory,
                    isLongTerm
                )
                onCalculate()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = itemName.isNotBlank() && amount.isNotBlank()
        ) {
            Text("Calculate", style = MaterialTheme.typography.titleLarge)
        }
    }
}

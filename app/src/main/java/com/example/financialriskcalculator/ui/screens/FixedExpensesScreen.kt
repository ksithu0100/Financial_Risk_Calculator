package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financialriskcalculator.models.UserProfile
import com.example.financialriskcalculator.viewmodel.FinancialViewModel

data class ExpenseItem(val name: String, val amount: String)

@Composable
fun FixedExpensesScreen(
    viewModel: FinancialViewModel, 
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val userProfile = viewModel.userProfile
    val expenses = remember { 
        val list = mutableStateListOf<ExpenseItem>()
        
        // Add default if they don't exist, otherwise populate from profile
        val currentExpenses = userProfile.getFixedExpenses()
        
        if (currentExpenses.isEmpty()) {
            list.add(ExpenseItem("Rent", ""))
            list.add(ExpenseItem("Insurance", ""))
            list.add(ExpenseItem("Utilities", ""))
        } else {
            currentExpenses.forEach { (name, amount) ->
                list.add(ExpenseItem(name, if (amount == 0.0) "" else amount.toString()))
            }
        }
        list
    }
    
    var expanded by remember { mutableStateOf(false) }
    var selectedStrategy by remember { 
        mutableStateOf(userProfile.budgetStrategy ?: UserProfile.BudgetStrategy.STRATEGY_50_30_20) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Monthly Fixed Expenses", style = MaterialTheme.typography.headlineMedium)
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }

        expenses.forEachIndexed { index, expense ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = expense.amount,
                    onValueChange = { newValue ->
                        expenses[index] = expense.copy(amount = newValue)
                    },
                    label = { Text(expense.name) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("$") }
                )
                
                // Allow deleting only extra expenses
                if (expense.name != "Rent" && expense.name != "Insurance" && expense.name != "Utilities") {
                    IconButton(onClick = { expenses.removeAt(index) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Expense")
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add More Expenses")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                viewModel.availableExtraExpenses.forEach { extra ->
                    if (expenses.none { it.name == extra }) {
                        DropdownMenuItem(
                            text = { Text(extra) },
                            onClick = {
                                expenses.add(ExpenseItem(extra, ""))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Select Budget Strategy", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UserProfile.BudgetStrategy.values().forEach { strategy ->
                FilterChip(
                    selected = selectedStrategy == strategy,
                    onClick = { selectedStrategy = strategy },
                    label = { Text(strategy.displayName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.userProfile.getFixedExpenses().clear()
                expenses.forEach {
                    viewModel.addFixedExpense(it.name, it.amount.toDoubleOrNull() ?: 0.0)
                }
                viewModel.setBudgetStrategy(selectedStrategy)
                
                viewModel.saveProfileToDb()
                onNext()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save & Continue")
        }
    }
}

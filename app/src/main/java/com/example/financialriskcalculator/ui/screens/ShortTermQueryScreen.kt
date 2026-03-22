package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialriskcalculator.models.FinancialPlan
import com.example.financialriskcalculator.models.PlanExpenditure
import com.example.financialriskcalculator.models.UserProfile
import com.example.financialriskcalculator.viewmodel.FinancialViewModel
import java.time.LocalDate
import java.util.Locale

@Composable
fun ShortTermQueryScreen(viewModel: FinancialViewModel, onSave: () -> Unit, onBack: () -> Unit) {
    val plan = viewModel.activeStq
    var name by remember { mutableStateOf(plan?.name ?: "") }
    var expenditures by remember { mutableStateOf(plan?.expenditures ?: emptyList()) }
    
    var showMenu by remember { mutableStateOf(false) }
    var editingExpIndex by remember { mutableStateOf(-1) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var customExpName by remember { mutableStateOf("") }
    var customExpAmount by remember { mutableStateOf("") }

    var showAssessment by remember { mutableStateOf(false) }

    val userProfile: UserProfile = viewModel.userProfile
    val monthlyIncome: Double = userProfile.monthlyIncome
    val totalExpenditure: Double = expenditures.sumOf { it.amount }
    
    val needLimit = monthlyIncome * 0.5
    val wantLimit = monthlyIncome * 0.3
    val needExpenditure = expenditures.filter { it.name in listOf("Mortgage", "Rent", "Utilities", "Insurance") }.sumOf { it.amount }
    val wantExpenditure = totalExpenditure - needExpenditure

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Short Term Query", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Plan: ", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.weight(1f).height(56.dp),
                placeholder = { Text("<add name>") },
                shape = RoundedCornerShape(24.dp)
            )
        }

        Text("Monthly Income: $${String.format(Locale.US, "%,.0f", monthlyIncome)}")
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth().height(24.dp).border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
            color = Color(0xFFB4E197),
            trackColor = Color.Transparent
        )

        Text("Need Expenditure: $${needExpenditure.toInt()}/${needLimit.toInt()}")
        LinearProgressIndicator(
            progress = { if (needLimit > 0.0) (needExpenditure / needLimit).toFloat().coerceIn(0f, 1f) else 0f },
            modifier = Modifier.fillMaxWidth().height(24.dp).border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
            color = Color(0xFFB4E197),
            trackColor = Color.Transparent
        )

        Text("Want Expenditure: $${wantExpenditure.toInt()}/${wantLimit.toInt()}")
        Box(modifier = Modifier.fillMaxWidth().height(24.dp).border(1.dp, Color.Black, RoundedCornerShape(4.dp))) {
            val progress = if (wantLimit > 0.0) (wantExpenditure / wantLimit).toFloat().coerceIn(0f, 1f) else 0f
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(progress.coerceAtLeast(0.01f)).fillMaxHeight().background(Color(0xFFB4E197)))
                Box(modifier = Modifier.weight((1f - progress).coerceAtLeast(0.01f)).fillMaxHeight().background(Color(0xFFFFB4B4)))
            }
        }

        Text("Expenses:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { /* Already here */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFADD8E6))) {
                Text("Short Term", color = Color.Black)
            }
            OutlinedButton(onClick = { /* Switch to LTP */ }) {
                Text("Long Term", color = Color.Black)
            }
        }

        expenditures.forEachIndexed { index, exp ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier.weight(1f).border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Expenditure ${index + 1}: ${exp.name}")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Amount: $${exp.amount.toInt()}")
                            Text("Date: ${exp.date}")
                        }
                    }
                }
                IconButton(onClick = { 
                    editingExpIndex = index
                    customExpName = exp.name
                    customExpAmount = exp.amount.toString()
                    showCustomDialog = true
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Blue)
                }
                IconButton(onClick = { 
                    expenditures = expenditures.filterIndexed { i, _ -> i != index }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        TextButton(onClick = { showMenu = !showMenu }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(" Add expenditure", modifier = Modifier.padding(start = 4.dp))
            }
        }

        if (showMenu) {
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black, RoundedCornerShape(8.dp))) {
                listOf("Dinner", "Festival", "Shopping", "Subscriptions", "Custom").forEach { item ->
                    TextButton(
                        onClick = {
                            if (item == "Custom") {
                                editingExpIndex = -1
                                customExpName = ""
                                customExpAmount = ""
                                showCustomDialog = true
                            } else {
                                expenditures = expenditures + PlanExpenditure(name = item, amount = 0.0, date = LocalDate.now())
                                showMenu = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(item, color = Color.Black)
                    }
                    if (item != "Custom") HorizontalDivider(color = Color.Black)
                }
            }
        }

        Button(
            onClick = { showAssessment = true },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
        ) {
            Text("Assess Plan", color = Color.Red)
        }

        if (showAssessment) {
            AssessmentChatBox(
                title = "Short Term Query Assessment",
                totalCost = totalExpenditure,
                budget = monthlyIncome * 0.8,
                details = emptyList(),
                onDismiss = { showAssessment = false }
            )
        }
    }

    if (showCustomDialog) {
        CommonExpenditureDialog(
            isEdit = editingExpIndex >= 0,
            name = customExpName,
            amount = customExpAmount,
            onNameChange = { customExpName = it },
            onAmountChange = { customExpAmount = it },
            onSave = {
                val amount = customExpAmount.toDoubleOrNull() ?: 0.0
                if (editingExpIndex >= 0) {
                    val updated = expenditures.toMutableList()
                    updated[editingExpIndex] = updated[editingExpIndex].copy(name = customExpName, amount = amount)
                    expenditures = updated
                } else {
                    expenditures = expenditures + PlanExpenditure(name = customExpName, amount = amount, date = LocalDate.now())
                }
                showCustomDialog = false
                showMenu = false
            },
            onDismiss = { showCustomDialog = false }
        )
    }
}

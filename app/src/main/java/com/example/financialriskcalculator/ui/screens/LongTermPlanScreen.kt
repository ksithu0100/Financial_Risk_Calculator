package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialriskcalculator.models.ChangeType
import com.example.financialriskcalculator.models.FinancialPlan
import com.example.financialriskcalculator.models.PlanChange
import com.example.financialriskcalculator.models.PlanExpenditure
import com.example.financialriskcalculator.viewmodel.FinancialViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun LongTermPlanScreen(viewModel: FinancialViewModel, onSave: () -> Unit, onBack: () -> Unit) {
    val plan = viewModel.activeLtp
    
    var name by remember { mutableStateOf(plan?.name ?: "") }
    var goal by remember { mutableStateOf(plan?.goal ?: "") }
    var cost by remember { mutableStateOf(plan?.cost?.toString() ?: "") }
    var amountSaved by remember { mutableStateOf(plan?.amountSaved?.toString() ?: "") }
    var createDate by remember { mutableStateOf(plan?.createDate?.toString() ?: LocalDate.now().toString()) }
    var goalDate by remember { mutableStateOf(plan?.goalDate?.toString() ?: "") }
    var description by remember { mutableStateOf(plan?.description ?: "") }

    var changes by remember { mutableStateOf(plan?.changes ?: emptyList()) }
    var expenditures by remember { mutableStateOf(plan?.expenditures ?: emptyList()) }
    
    var editingChangeIndex by remember { mutableStateOf(-1) }
    var tempChangeAmount by remember { mutableStateOf("") }
    var tempChangeType by remember { mutableStateOf(ChangeType.WANT) }

    var showExpMenu by remember { mutableStateOf(false) }
    var editingExpIndex by remember { mutableStateOf(-1) }
    var showCustomExpDialog by remember { mutableStateOf(false) }
    var customExpName by remember { mutableStateOf("") }
    var customExpAmount by remember { mutableStateOf("") }

    var showFeasibilityWarning by remember { mutableStateOf(false) }
    var showAssessment by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Long Term Plan", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Plan Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("Goal") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Cost") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = amountSaved,
                onValueChange = { amountSaved = it },
                label = { Text("Amount Saved") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = createDate,
                onValueChange = { createDate = it },
                label = { Text("Create Date (YYYY-MM-DD)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = goalDate,
                onValueChange = { 
                    val oldDate = try { LocalDate.parse(goalDate) } catch(e: Exception) { null }
                    val newDate = try { LocalDate.parse(it) } catch(e: Exception) { null }
                    if (oldDate != null && newDate != null && newDate.isBefore(oldDate)) {
                        showFeasibilityWarning = true
                    }
                    goalDate = it 
                },
                label = { Text("Goal Date (YYYY-MM-DD)") },
                modifier = Modifier.weight(1f),
                placeholder = { Text("<insert date>") }
            )
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        HorizontalDivider()

        Text("Changes:", fontWeight = FontWeight.Bold)
        
        changes.forEachIndexed { index, change ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Change ${index + 1}: ", fontWeight = FontWeight.Bold)
                if (editingChangeIndex == index) {
                    OutlinedTextField(
                        value = tempChangeAmount,
                        onValueChange = { tempChangeAmount = it },
                        modifier = Modifier.width(100.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("<amount>") }
                    )
                    TextButton(onClick = { tempChangeType = if (tempChangeType == ChangeType.WANT) ChangeType.NEED else ChangeType.WANT }) {
                        Text(tempChangeType.name)
                    }
                    IconButton(onClick = {
                        val amount = tempChangeAmount.toDoubleOrNull() ?: 0.0
                        val updatedChanges = changes.toMutableList()
                        updatedChanges[index] = PlanChange(amount = amount, type = tempChangeType)
                        changes = updatedChanges
                        editingChangeIndex = -1
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save Change")
                    }
                } else {
                    Box(modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("$${change.amount}")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.border(1.dp, Color.Black, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(change.type.name)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        editingChangeIndex = index
                        tempChangeAmount = change.amount.toString()
                        tempChangeType = change.type
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Change", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = {
                        changes = changes.filterIndexed { i, _ -> i != index }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Change", modifier = Modifier.size(18.dp), tint = Color.Red)
                    }
                }
            }
        }

        if (editingChangeIndex == -2) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("New Change: ", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = tempChangeAmount,
                    onValueChange = { tempChangeAmount = it },
                    modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("<amount>") }
                )
                TextButton(onClick = { tempChangeType = if (tempChangeType == ChangeType.WANT) ChangeType.NEED else ChangeType.WANT }) {
                    Text(tempChangeType.name)
                }
                IconButton(onClick = {
                    val amount = tempChangeAmount.toDoubleOrNull() ?: 0.0
                    changes = changes + PlanChange(amount = amount, type = tempChangeType)
                    editingChangeIndex = -1
                }) {
                    Icon(Icons.Default.Save, contentDescription = "Save New Change")
                }
            }
        }

        TextButton(onClick = { 
            editingChangeIndex = -2
            tempChangeAmount = ""
            tempChangeType = ChangeType.WANT
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                Text(" Add change", modifier = Modifier.padding(start = 4.dp))
            }
        }

        HorizontalDivider()
        Text("Progress", fontWeight = FontWeight.Bold)
        Text("Current Split: ${viewModel.selectedSplitLabel}")
        
        LinearProgressIndicator(progress = { 0.5f }, modifier = Modifier.fillMaxWidth().height(12.dp), color = Color(0xFFB4E197))
        Text("Need Balance: $1000/1000 remaining", fontSize = 12.sp)
        
        LinearProgressIndicator(progress = { 0.3f }, modifier = Modifier.fillMaxWidth().height(12.dp), color = Color(0xFFB4E197))
        Text("Want Balance: $300/500 remaining", fontSize = 12.sp)

        HorizontalDivider()
        Text("Expenditures:", fontWeight = FontWeight.Bold)
        
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
                    showCustomExpDialog = true
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp), tint = Color.Blue)
                }
                IconButton(onClick = { 
                    expenditures = expenditures.filterIndexed { i, _ -> i != index }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp), tint = Color.Red)
                }
            }
        }

        TextButton(onClick = { showExpMenu = !showExpMenu }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                Text(" Add expenditure", modifier = Modifier.padding(start = 4.dp))
            }
        }

        if (showExpMenu) {
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black, RoundedCornerShape(8.dp))) {
                listOf("Mortgage", "Car Payment", "Insurance", "Utilities", "Custom").forEach { item ->
                    TextButton(
                        onClick = {
                            if (item == "Custom") {
                                editingExpIndex = -1
                                customExpName = ""
                                customExpAmount = ""
                                showCustomExpDialog = true
                            } else {
                                expenditures = expenditures + PlanExpenditure(name = item, amount = 0.0, date = LocalDate.now())
                                showExpMenu = false
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { showAssessment = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
            ) {
                Text("Assess Plan", color = Color.Red)
            }
        }

        if (showAssessment) {
            val totalCostNum = cost.toDoubleOrNull() ?: 0.0
            val saved = amountSaved.toDoubleOrNull() ?: 0.0
            val targetDate = try { LocalDate.parse(goalDate) } catch(e: Exception) { null }
            val monthsRemaining = if (targetDate != null) ChronoUnit.MONTHS.between(LocalDate.now(), targetDate).coerceAtLeast(1) else 12L
            val monthlyNeeded = (totalCostNum - saved) / monthsRemaining
            val monthlyIncomeNum = viewModel.userProfile.monthlyIncome
            val savingsCapacity = monthlyIncomeNum * 0.2
            
            AssessmentChatBox(
                title = "Long Term Plan Assessment",
                totalCost = monthlyNeeded,
                budget = savingsCapacity,
                details = listOf(
                    "Remaining to Save" to (totalCostNum - saved),
                    "Time Frame" to monthsRemaining.toDouble(),
                    "Monthly Goal" to monthlyNeeded,
                    "Monthly Capacity" to savingsCapacity
                ),
                onDismiss = { showAssessment = false }
            )
        }

        FloatingActionButton(
            onClick = {
                val newPlan = FinancialPlan.LongTermPlan(
                    id = plan?.id ?: 0,
                    name = name,
                    goal = goal,
                    cost = cost.toDoubleOrNull() ?: 0.0,
                    amountSaved = amountSaved.toDoubleOrNull() ?: 0.0,
                    createDate = try { LocalDate.parse(createDate) } catch(e: DateTimeParseException) { LocalDate.now() },
                    goalDate = try { LocalDate.parse(goalDate) } catch(e: DateTimeParseException) { null },
                    description = description,
                    changes = changes,
                    expenditures = expenditures
                )
                viewModel.savePlan(newPlan)
                onSave()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Save, contentDescription = "Save Plan")
        }
    }

    if (showCustomExpDialog) {
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
                showCustomExpDialog = false
                showExpMenu = false
            },
            onDismiss = { showCustomExpDialog = false }
        )
    }

    if (showFeasibilityWarning) {
        AlertDialog(
            onDismissRequest = { showFeasibilityWarning = false },
            title = { Text("Feasibility Alert") },
            text = { Text("Shortening the goal date means your monthly reductions must increase to stay feasible.") },
            confirmButton = {
                TextButton(onClick = { showFeasibilityWarning = false }) {
                    Text("OK")
                }
            }
        )
    }
}

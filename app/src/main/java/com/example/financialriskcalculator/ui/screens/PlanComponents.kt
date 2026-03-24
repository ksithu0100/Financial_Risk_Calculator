package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Locale

@Composable
fun AssessmentChatBox(
    title: String,
    totalCost: Double,
    budget: Double,
    details: List<Pair<String, Double>> = emptyList(),
    onDismiss: () -> Unit
) {
    val percentage = if (budget > 0.0) (totalCost / budget) * 100.0 else 100.0
    
    val (riskScale, recommendation, color) = when {
        percentage < 30.0 -> Triple(0, "Safe to proceed", Color(0xFF4CAF50))
        percentage <= 50.0 -> Triple(1, "Proceed with caution", Color(0xFFFBC02D))
        percentage <= 75.0 -> Triple(2, "High risk", Color(0xFFF57C00))
        else -> Triple(3, "Not recommended", Color(0xFFD32F2F))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(6.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            
            HorizontalDivider(color = Color.Black.copy(0.2f))
            
            Text("Risk Scale: $riskScale", fontWeight = FontWeight.Bold, color = color)
            Text("Recommendation: $recommendation", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            
            Spacer(modifier = Modifier.height(4.dp))
            Text("Calculations:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            details.forEach { (label, value) ->
                Text("$label: $${String.format(Locale.US, "%.2f", value)}")
            }
            
            if (details.isEmpty()) {
                Text("Total Cost: $${String.format(Locale.US, "%.2f", totalCost)}")
                Text("Budget Available: $${String.format(Locale.US, "%.2f", budget)}")
            }
            Text("Budget Usage: ${String.format(Locale.US, "%.1f", percentage)}%")
            
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color.Black.copy(0.1f))
            Text("Scale Reference:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Column {
                AssessmentScaleRow(0, "Safe (<30%)", percentage < 30.0)
                AssessmentScaleRow(1, "Caution (30-50%)", percentage in 30.0..50.0)
                AssessmentScaleRow(2, "High Risk (50-75%)", percentage > 50.0 && percentage <= 75.0)
                AssessmentScaleRow(3, "Critical (>75%)", percentage > 75.0)
            }
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
            ) {
                Text("Close", color = Color.Black)
            }
        }
    }
}

@Composable
fun AssessmentScaleRow(level: Int, text: String, isActive: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$level: $text", fontSize = 12.sp, color = if (isActive) Color.Black else Color.Gray, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal)
        if (isActive) {
            Text(" ← Current", fontSize = 10.sp, color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CommonExpenditureDialog(
    isEdit: Boolean,
    name: String,
    amount: String,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "Edit Expenditure" else "Add Custom Expenditure") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Name") })
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SplitSelectionDialog(onDismiss: () -> Unit, onSelect: (String, Int, Int, Int) -> Unit) {
    var customMode by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Budget Split", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                if (!customMode) {
                    Button(onClick = { onSelect("50/30/20", 50, 30, 20) }, modifier = Modifier.fillMaxWidth()) { Text("50/30/20") }
                    Button(onClick = { onSelect("70/20/10", 70, 20, 10) }, modifier = Modifier.fillMaxWidth()) { Text("70/20/10") }
                    OutlinedButton(onClick = { customMode = true }, modifier = Modifier.fillMaxWidth()) { Text("Custom") }
                } else {
                    var n by remember { mutableStateOf("0") }
                    var w by remember { mutableStateOf("0") }
                    var s by remember { mutableStateOf("0") }
                    val total = (n.toIntOrNull() ?: 0) + (w.toIntOrNull() ?: 0) + (s.toIntOrNull() ?: 0)
                    OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Needs %") })
                    OutlinedTextField(value = w, onValueChange = { w = it }, label = { Text("Wants %") })
                    OutlinedTextField(value = s, onValueChange = { s = it }, label = { Text("Savings %") })
                    if (total != 100) Text("Must sum to 100 (Current: $total)", color = Color.Red, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { customMode = false }) { Text("Back") }
                        Button(onClick = { onSelect("Custom ($n/$w/$s)", n.toInt(), w.toInt(), s.toInt()) }, enabled = total == 100) { Text("Save") }
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialProgressBar(label: String, subLabel: String, progress: Float, color: Color, backgroundColor: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "$label: $subLabel", fontSize = 14.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(backgroundColor, RoundedCornerShape(4.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f - progress)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .background(color, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
            )
        }
    }
}

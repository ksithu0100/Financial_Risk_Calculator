package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialriskcalculator.models.FinancialPlan
import com.example.financialriskcalculator.viewmodel.FinancialViewModel
import java.time.LocalDate

@Composable
fun LogbookScreen(viewModel: FinancialViewModel, onEditPlan: (FinancialPlan) -> Unit) {
    val plans by viewModel.plans.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Log Book", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text("Catalog", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(16.dp))

            if (plans.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No plans yet. Click + to add one.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(plans) { plan ->
                        PlanItem(
                            plan = plan,
                            onView = { 
                                viewModel.selectPlan(plan)
                                onEditPlan(plan)
                            },
                            onEdit = {
                                viewModel.selectPlan(plan)
                                onEditPlan(plan)
                            },
                            onDelete = { viewModel.deletePlan(plan) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Plan")
        }

        if (showCreateDialog) {
            CreatePlanDialog(
                onDismiss = { showCreateDialog = false },
                onAddLTP = {
                    val newPlan = FinancialPlan.LongTermPlan(
                        name = "",
                        goal = "",
                        cost = 0.0,
                        amountSaved = 0.0,
                        createDate = LocalDate.now(),
                        goalDate = null,
                        description = ""
                    )
                    viewModel.selectPlan(newPlan)
                    onEditPlan(newPlan)
                    showCreateDialog = false
                },
                onAddSTQ = {
                    val newPlan = FinancialPlan.ShortTermQuery(
                        name = ""
                    )
                    viewModel.selectPlan(newPlan)
                    onEditPlan(newPlan)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun PlanItem(plan: FinancialPlan, onView: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
        onClick = onView,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            // Background Progress for LTP (Diagonal Stripe)
            if (plan is FinancialPlan.LongTermPlan) {
                val progress = if (plan.cost > 0) (plan.amountSaved / plan.cost).toFloat() else 0f
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    clipRect(right = w * progress) {
                        drawRect(Color(0xFFB4E197).copy(0.4f))
                        val step = 10.dp.toPx()
                        for (x in -h.toInt()..(w + h).toInt() step step.toInt()) {
                            drawLine(Color.Black, Offset(x.toFloat(), 0f), Offset(x - h, h), 1.dp.toPx())
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = if(plan.name.isBlank()) "<add name>" else plan.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (plan is FinancialPlan.LongTermPlan) {
                        Text(text = "Goal: ${if(plan.goal.isBlank()) "<insert goal>" else plan.goal}", fontSize = 14.sp)
                        Text(text = "Saved: \$${plan.amountSaved.toInt()}/\$${plan.cost.toInt()}", fontSize = 12.sp)
                    } else {
                        Text(text = "Short-Term Query", fontSize = 14.sp)
                    }
                }
                Row {
                    IconButton(onClick = onView) {
                        Icon(Icons.Default.Visibility, contentDescription = "View")
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePlanDialog(onDismiss: () -> Unit, onAddLTP: () -> Unit, onAddSTQ: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Plan") },
        text = { Text("Choose the type of plan you want to create.") },
        confirmButton = {
            Button(onClick = onAddLTP) { Text("Long-Term Plan") }
        },
        dismissButton = {
            Button(onClick = onAddSTQ) { Text("Short-Term Query") }
        }
    )
}

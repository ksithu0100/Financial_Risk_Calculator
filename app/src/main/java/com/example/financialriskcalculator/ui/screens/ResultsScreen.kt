package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialriskcalculator.viewmodel.FinancialViewModel

@Composable
fun ResultsScreen(viewModel: FinancialViewModel, onReset: () -> Unit) {
    val result = viewModel.riskResult
    val decision = viewModel.currentDecision

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Risk Analysis Result", style = MaterialTheme.typography.headlineLarge)
        
        Spacer(modifier = Modifier.height(32.dp))

        if (result != null && decision != null) {
            val (riskLabel, color) = when (result.riskScale) {
                0 -> "LOW RISK" to Color(0xFF4CAF50)
                1 -> "MEDIUM RISK" to Color(0xFFFFC107)
                2 -> "HIGH RISK" to Color(0xFFFF9800)
                else -> "NOT RECOMMENDED" to Color(0xFFF44336)
            }

            Text(
                text = riskLabel,
                color = color,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Item: ${decision.itemName}", style = MaterialTheme.typography.titleMedium)
                    Text("Cost: $${decision.amount}", style = MaterialTheme.typography.titleMedium)
                    Text("Category: ${decision.category.displayName}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Recommendation: ${result.recommendation}", fontWeight = FontWeight.Bold)
                    Text("Insight: ${result.insight}")
                }
            }

            if (result.isShady) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336).copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🚨 FLAG: High savings but low credit score. This pattern is unusual and may be flagged for review.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Dashboard")
        }
    }
}

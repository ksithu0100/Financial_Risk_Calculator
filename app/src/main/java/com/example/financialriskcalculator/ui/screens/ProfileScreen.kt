package com.example.financialriskcalculator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.financialriskcalculator.R
import com.example.financialriskcalculator.viewmodel.FinancialViewModel
import java.util.*

@Composable
fun ProfileScreen(viewModel: FinancialViewModel) {
    val userProfile = viewModel.userProfile
    var isPrivacyVisible by remember { mutableStateOf(false) }
    var showSplitDialog by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Profile Header Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.LightGray
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.default_avatar_foreground),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Welcome ${userProfile.name ?: "User Name"}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Profession: ${userProfile.occupation ?: "xxxx"}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy Protected Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PrivacyStatItem(
                label = "Base Salary (\$/yr)",
                value = (userProfile.monthlyIncome * 12).toInt().toString(),
                isVisible = isPrivacyVisible,
                onToggle = { isPrivacyVisible = !isPrivacyVisible }
            )
            PrivacyStatItem(
                label = "Initial Savings (\$)",
                value = userProfile.totalSavings.toInt().toString(),
                isVisible = isPrivacyVisible,
                onToggle = { isPrivacyVisible = !isPrivacyVisible }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current Split Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Current Split: ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedCard(
                onClick = { showSplitDialog = true },
                modifier = Modifier.padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = viewModel.selectedSplitLabel)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Financial Visualization
        val calendar = Calendar.getInstance()
        val monthYear = "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${calendar.get(Calendar.YEAR)}"
        Text(text = monthYear, fontWeight = FontWeight.Bold)
        
        FinancialProgressBar(
            label = "Monthly Income",
            subLabel = "\$${userProfile.monthlyIncome.toInt()}",
            progress = 1.0f,
            color = Color(0xFFB4E197)
        )

        val needsAllocated = userProfile.monthlyIncome * viewModel.needsRatio
        val needsSpent = userProfile.totalFixedExpenses
        val needsRemaining = needsAllocated - needsSpent
        FinancialProgressBar(
            label = "Need Expenditure",
            subLabel = "\$${needsRemaining.toInt()}/\$${needsAllocated.toInt()}",
            progress = if (needsAllocated > 0.0) (needsSpent / needsAllocated).toFloat().coerceIn(0f, 1f) else 0f,
            color = Color(0xFFB4E197),
            backgroundColor = Color(0xFFFFB4B4),
            isSegmented = true
        )

        val wantsAllocated = userProfile.monthlyIncome * viewModel.wantsRatio
        FinancialProgressBar(
            label = "Want Expenditure",
            subLabel = "\$${wantsAllocated.toInt()}/\$${wantsAllocated.toInt()}",
            progress = 1.0f,
            color = Color(0xFFB4E197)
        )

        // Savings Bar
        val monthSavings = (userProfile.monthlyIncome * viewModel.savingsRatio).toInt()
        val totalSavingsDisplay = (userProfile.totalSavings + monthSavings).toInt()
        
        val savingsText = if (isPrivacyVisible) {
            "\$${totalSavingsDisplay}/\$${userProfile.totalSavings.toInt()} (+\$${monthSavings})"
        } else {
            "\$####/\$#### (+\$####)"
        }

        Text(
            text = "Savings : $savingsText",
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        SavingsBar(
            previousSavings = userProfile.totalSavings.toFloat(),
            currentSavings = monthSavings.toFloat(),
            isVisible = isPrivacyVisible,
            onToggle = { isPrivacyVisible = !isPrivacyVisible }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Future Implementation Section
        Text(text = "Display Plan:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Select Plan...")
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
    }

    if (showSplitDialog) {
        SplitSelectionDialog(
            onDismiss = { showSplitDialog = false },
            onSelect = { label, needs, wants, savings ->
                viewModel.updateSplit(label, needs, wants, savings)
                showSplitDialog = false
            }
        )
    }
}

@Composable
fun PrivacyStatItem(label: String, value: String, isVisible: Boolean, onToggle: () -> Unit) {
    Column {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = if (isVisible) value else "\$####", fontSize = 16.sp)
            IconButton(onClick = onToggle, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Visibility",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun FinancialProgressBar(
    label: String,
    subLabel: String,
    progress: Float,
    color: Color,
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.3f),
    isSegmented: Boolean = false
) {
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
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(if (isSegmented) Color(0xFFFFB4B4) else color, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun SavingsBar(previousSavings: Float, currentSavings: Float, isVisible: Boolean, onToggle: () -> Unit) {
    val total = previousSavings + currentSavings
    val prevRatio = if (total > 0f) previousSavings / total else 0.8f
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(30.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Crosshatch section (Previous Savings)
            clipRect(right = canvasWidth * prevRatio) {
                drawRect(color = Color(0xFFB4E197).copy(alpha = 0.5f))
                val spacing = 10.dp.toPx()
                var xPos = -canvasHeight
                while (xPos < canvasWidth) {
                    drawLine(
                        color = Color.Black,
                        start = Offset(xPos, 0f),
                        end = Offset(xPos + canvasHeight, canvasHeight),
                        strokeWidth = 1.dp.toPx()
                    )
                    xPos += spacing
                }
            }

            // Solid Green section (Current Month Savings)
            drawRect(
                color = if (isVisible) Color(0xFFB4E197) else Color.Gray.copy(alpha = 0.3f),
                topLeft = Offset(canvasWidth * prevRatio, 0f),
                size = Size(canvasWidth * (1f - prevRatio), canvasHeight)
            )
            
            // If not visible, we can also hatch the current month with a different pattern or just leave it gray
            if (!isVisible) {
                clipRect(left = canvasWidth * prevRatio) {
                    drawRect(color = Color.Gray.copy(alpha = 0.2f))
                }
            }
        }
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = null
            )
        }
    }
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
                    Button(onClick = { onSelect("50/30/20", 50, 30, 20) }, modifier = Modifier.fillMaxWidth()) {
                        Text("50/30/20")
                    }
                    Button(onClick = { onSelect("70/20/10", 70, 20, 10) }, modifier = Modifier.fillMaxWidth()) {
                        Text("70/20/10")
                    }
                    OutlinedButton(onClick = { customMode = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Custom")
                    }
                } else {
                    var needs by remember { mutableStateOf("0") }
                    var wants by remember { mutableStateOf("0") }
                    var savings by remember { mutableStateOf("0") }
                    
                    val n = needs.toIntOrNull() ?: 0
                    val w = wants.toIntOrNull() ?: 0
                    val s = savings.toIntOrNull() ?: 0
                    val total = n + w + s
                    
                    OutlinedTextField(value = needs, onValueChange = { needs = it }, label = { Text("Needs %") })
                    OutlinedTextField(value = wants, onValueChange = { wants = it }, label = { Text("Wants %") })
                    OutlinedTextField(value = savings, onValueChange = { savings = it }, label = { Text("Savings %") })
                    
                    if (total != 100) {
                        Text("Total must add to 100 (Current: $total)", color = Color.Red, fontSize = 12.sp)
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { customMode = false }) { Text("Back") }
                        Button(
                            onClick = { onSelect("Custom ($n/$w/$s)", n, w, s) },
                            enabled = total == 100
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

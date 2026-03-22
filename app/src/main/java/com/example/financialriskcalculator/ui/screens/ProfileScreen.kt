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
import com.example.financialriskcalculator.models.FinancialPlan
import com.example.financialriskcalculator.viewmodel.FinancialViewModel
import java.util.*

@Composable
fun ProfileScreen(viewModel: FinancialViewModel) {
    val userProfile = viewModel.userProfile
    val plans by viewModel.plans.collectAsState()
    var isPrivacyVisible by remember { mutableStateOf(false) }
    var showSplitDialog by remember { mutableStateOf(false) }
    var planExpanded by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 1. Header (Square photo + Welcome + Profession)
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
                    text = "Age: ${if (userProfile.age > 0) userProfile.age else "xx"}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Profession: ${userProfile.occupation ?: "xxxx"}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Privacy UI (Base Salary & Initial Savings with Eye Icon)
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

        // 3. Current Split
        Text(text = "Current Split:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        OutlinedCard(
            onClick = { showSplitDialog = true },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = viewModel.selectedSplitLabel)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Dynamic Progress Bars
        val calendar = Calendar.getInstance()
        val monthYear = "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${calendar.get(Calendar.YEAR)}"
        Text(text = monthYear, fontWeight = FontWeight.Bold)
        
        FinancialProgressBar(
            label = "Monthly Income",
            subLabel = "$${userProfile.monthlyIncome.toInt()}",
            progress = 1.0f,
            color = Color(0xFFB4E197),
            backgroundColor = Color.LightGray.copy(alpha = 0.2f)
        )

        val needsAllocated = userProfile.monthlyIncome * viewModel.needsRatio
        val needsSpent = userProfile.totalFixedExpenses
        FinancialProgressBar(
            label = "Need Expenditure",
            subLabel = "$${(needsAllocated - needsSpent).toInt()}/$${needsAllocated.toInt()}",
            progress = if (needsAllocated > 0) (needsSpent / needsAllocated).toFloat().coerceIn(0f, 1f) else 0f,
            color = Color(0xFFB4E197), // Green for remaining
            backgroundColor = Color(0xFFFFB4B4) // Red for spent
        )

        val wantsAllocated = userProfile.monthlyIncome * viewModel.wantsRatio
        FinancialProgressBar(
            label = "Want Expenditure",
            subLabel = "$${wantsAllocated.toInt()}/$${wantsAllocated.toInt()}",
            progress = 0f,
            color = Color(0xFFB4E197),
            backgroundColor = Color(0xFFFFB4B4)
        )

        // 5. Custom Savings Bar (3-part Stacked)
        val baseSavings = (userProfile.monthlyIncome * viewModel.savingsRatio).toFloat()
        val extraSavingsFromPlan = viewModel.activeLtp?.let { (it.cost / 12).toFloat() } ?: 0f 
        
        val totalCurrentSavings = baseSavings + extraSavingsFromPlan
        val savingsDisplayValue = if (isPrivacyVisible) {
            "$${(userProfile.totalSavings + totalCurrentSavings).toInt()}/$${userProfile.totalSavings.toInt()}"
        } else {
            "$####/####"
        }

        Text(text = "Savings : $savingsDisplayValue", fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        SavingsBar(
            previousSavings = userProfile.totalSavings.toFloat(),
            baseAllocation = baseSavings,
            additionalLtp = extraSavingsFromPlan,
            isVisible = isPrivacyVisible
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 6. Display Plan Dropdown
        Text(text = "Display Plan:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Box {
            OutlinedCard(
                onClick = { planExpanded = true },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = viewModel.activeLtp?.name ?: "Select Plan...")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
            DropdownMenu(expanded = planExpanded, onDismissRequest = { planExpanded = false }) {
                plans.forEach { plan ->
                    DropdownMenuItem(
                        text = { Text(plan.name) },
                        onClick = {
                            viewModel.selectPlan(plan)
                            planExpanded = false
                        }
                    )
                }
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
            Text(text = if (isVisible) value else "$####", fontSize = 16.sp)
            IconButton(onClick = onToggle, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
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
            // The "color" part represents the REMAINING budget (Green)
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

@Composable
fun SavingsBar(previousSavings: Float, baseAllocation: Float, additionalLtp: Float, isVisible: Boolean) {
    val total = previousSavings + baseAllocation + additionalLtp
    if (total <= 0) return

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
    ) {
        val w = size.width
        val h = size.height
        val p1 = (previousSavings / total) * w
        val p2 = (baseAllocation / total) * w
        val p3 = (additionalLtp / total) * w

        // 1. Crosshatch (Previous Savings)
        clipRect(right = p1) {
            drawRect(if (isVisible) Color(0xFFB4E197).copy(0.4f) else Color.LightGray)
            val step = 10.dp.toPx()
            for (x in -h.toInt()..w.toInt() step step.toInt()) {
                drawLine(Color.Black, Offset(x.toFloat(), 0f), Offset(x + h, h), 1.dp.toPx())
                drawLine(Color.Black, Offset(x.toFloat(), h), Offset(x + h, 0f), 1.dp.toPx())
            }
        }

        // 2. Diagonal Stripe (Base Percentage Allocation)
        clipRect(left = p1, right = p1 + p2) {
            drawRect(if (isVisible) Color(0xFFB4E197).copy(0.7f) else Color.Gray)
            val step = 10.dp.toPx()
            for (x in -h.toInt()..(w + h).toInt() step step.toInt()) {
                drawLine(Color.Black, Offset(x.toFloat(), 0f), Offset(x - h, h), 1.dp.toPx())
            }
        }

        // 3. Solid Green (Additional LTP Savings)
        drawRect(
            color = if (isVisible) Color(0xFF4CAF50) else Color.DarkGray,
            topLeft = Offset(p1 + p2, 0f),
            size = Size(p3, h)
        )
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

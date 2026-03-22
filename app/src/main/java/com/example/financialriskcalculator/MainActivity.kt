package com.example.financialriskcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialriskcalculator.ui.screens.*
import com.example.financialriskcalculator.ui.theme.FinancialRiskCalculatorTheme
import com.example.financialriskcalculator.viewmodel.FinancialViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemTheme) }
            
            FinancialRiskCalculatorTheme(darkTheme = isDarkMode) {
                AppNavigation(isDarkMode = isDarkMode, onThemeChange = { isDarkMode = it })
            }
        }
    }
}

enum class AppState {
    LOGIN, SIGNUP, PROFILE_INPUT, FIXED_EXPENSES, DECISION_INPUT, RESULTS, SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(isDarkMode: Boolean, onThemeChange: (Boolean) -> Unit) {
    var currentState by rememberSaveable { mutableStateOf(AppState.LOGIN) }
    var previousState by rememberSaveable { mutableStateOf(AppState.LOGIN) }
    val viewModel: FinancialViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentState != AppState.LOGIN && currentState != AppState.SIGNUP) {
                TopAppBar(
                    title = { Text("Financial Risk Calculator") },
                    actions = {
                        if (currentState != AppState.SETTINGS) {
                            IconButton(onClick = {
                                previousState = currentState
                                currentState = AppState.SETTINGS
                            }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    },
                    navigationIcon = {
                        if (currentState == AppState.SETTINGS) {
                            IconButton(onClick = { currentState = previousState }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentState) {
                AppState.LOGIN -> LoginScreen(
                    onLoginSuccess = { currentState = AppState.PROFILE_INPUT },
                    onCreateAccountClick = { currentState = AppState.SIGNUP }
                )
                AppState.SIGNUP -> SignupScreen(
                    onSignupSuccess = { currentState = AppState.LOGIN },
                    onBackToLogin = { currentState = AppState.LOGIN }
                )
                AppState.PROFILE_INPUT -> ProfileInputScreen(viewModel, onNext = {
                    currentState = AppState.FIXED_EXPENSES
                })
                AppState.FIXED_EXPENSES -> FixedExpensesScreen(viewModel, onNext = {
                    currentState = AppState.DECISION_INPUT
                })
                AppState.DECISION_INPUT -> DecisionInputScreen(viewModel, onCalculate = {
                    currentState = AppState.RESULTS
                })
                AppState.RESULTS -> ResultsScreen(viewModel, onReset = {
                    currentState = AppState.DECISION_INPUT
                })
                AppState.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    onThemeChange = onThemeChange
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: FinancialViewModel, isDarkMode: Boolean, onThemeChange: (Boolean) -> Unit) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var securityErrorMessage by remember { mutableStateOf<String?>(null) }
    var securitySuccessMessage by remember { mutableStateOf<String?>(null) }

    // User Profile fields
    var name by remember { mutableStateOf(viewModel.userProfile.name ?: "") }
    var monthlyIncome by remember { mutableStateOf(viewModel.userProfile.monthlyIncome.toString()) }
    var subscriptions by remember { mutableStateOf(viewModel.userProfile.fixedExpenses.getOrDefault("Streaming Subscriptions", 0.0).toString()) }
    var rent by remember { mutableStateOf(viewModel.userProfile.fixedExpenses.getOrDefault("Rent", 0.0).toString()) }
    var insurance by remember { mutableStateOf(viewModel.userProfile.fixedExpenses.getOrDefault("Insurance", 0.0).toString()) }
    
    var profileSuccessMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Appearance Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dark Mode")
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onThemeChange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Profile & Finances",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; profileSuccessMessage = null },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = monthlyIncome,
                    onValueChange = { monthlyIncome = it; profileSuccessMessage = null },
                    label = { Text("Monthly Income ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Fixed Expenses",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = rent,
                    onValueChange = { rent = it; profileSuccessMessage = null },
                    label = { Text("Rent ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = subscriptions,
                    onValueChange = { subscriptions = it; profileSuccessMessage = null },
                    label = { Text("Subscriptions ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = insurance,
                    onValueChange = { insurance = it; profileSuccessMessage = null },
                    label = { Text("Insurance ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                if (profileSuccessMessage != null) {
                    Text(
                        text = profileSuccessMessage!!,
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val incomeVal = monthlyIncome.toDoubleOrNull() ?: 0.0
                        val rentVal = rent.toDoubleOrNull() ?: 0.0
                        val subVal = subscriptions.toDoubleOrNull() ?: 0.0
                        val insVal = insurance.toDoubleOrNull() ?: 0.0
                        
                        viewModel.userProfile.name = name
                        viewModel.userProfile.monthlyIncome = incomeVal
                        viewModel.userProfile.addFixedExpense("Rent", rentVal)
                        viewModel.userProfile.addFixedExpense("Streaming Subscriptions", subVal)
                        viewModel.userProfile.addFixedExpense("Insurance", insVal)
                        
                        profileSuccessMessage = "Profile updated successfully!"
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Update Profile")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Security",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { 
                        newPassword = it
                        securityErrorMessage = null
                        securitySuccessMessage = null
                    },
                    label = { Text("New Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        securityErrorMessage = null
                        securitySuccessMessage = null
                    },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (securityErrorMessage != null) {
                    Text(
                        text = securityErrorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                if (securitySuccessMessage != null) {
                    Text(
                        text = securitySuccessMessage!!,
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val validationError = validatePassword(newPassword, confirmPassword)
                        if (validationError == null) {
                            // In a real app, update password in backend/database here
                            securitySuccessMessage = "Password updated successfully!"
                            newPassword = ""
                            confirmPassword = ""
                        } else {
                            securityErrorMessage = validationError
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Update Password")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onCreateAccountClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.mipmap.app_logo_foreground),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(0.9f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFA9C7EE),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LOGIN",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = onLoginSuccess,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2D5F8B)
                        )
                    ) {
                        Text("Sign In", color = Color.White)
                    }
                    
                    TextButton(onClick = onCreateAccountClick) {
                        Text("Create Account", color = Color(0xFF2D5F8B))
                    }
                }
            }
        }
    }
}

@Composable
fun SignupScreen(onSignupSuccess: () -> Unit, onBackToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.mipmap.app_logo_foreground),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .alpha(0.9f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFA9C7EE),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = "Toggle password visibility")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            errorMessage = null
                        },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            val validationError = validatePassword(password, confirmPassword)
                            if (validationError == null) {
                                onSignupSuccess()
                            } else {
                                errorMessage = validationError
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2D5F8B)
                        )
                    ) {
                        Text("Sign Up", color = Color.White)
                    }
                    
                    TextButton(onClick = onBackToLogin) {
                        Text("Already have an account? Login", color = Color(0xFF2D5F8B))
                    }
                }
            }
        }
    }
}

fun validatePassword(password: String, confirmPassword: String): String? {
    if (password != confirmPassword) return "Passwords do not match"
    if (password.length < 7) return "Password must be at least 7 characters long"
    if (!password.any { it.isUpperCase() }) return "Password must contain at least one uppercase letter"
    if (!password.any { it.isDigit() }) return "Password must contain at least one digit"
    if (!password.any { !it.isLetterOrDigit() }) return "Password must contain at least one special character"
    return null
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    FinancialRiskCalculatorTheme {
        LoginScreen(onLoginSuccess = {}, onCreateAccountClick = {})
    }
}

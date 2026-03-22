package com.example.financialriskcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
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
import com.example.financialriskcalculator.ui.theme.FinancialRiskCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialRiskCalculatorTheme {
                AppNavigation()
            }
        }
    }
}

enum class AuthScreen {
    LOGIN, SIGNUP, MAIN_APP
}

@Composable
fun AppNavigation() {
    var currentScreen by rememberSaveable { mutableStateOf(AuthScreen.LOGIN) }

    when (currentScreen) {
        AuthScreen.LOGIN -> LoginScreen(
            onLoginSuccess = { currentScreen = AuthScreen.MAIN_APP },
            onCreateAccountClick = { currentScreen = AuthScreen.SIGNUP }
        )
        AuthScreen.SIGNUP -> SignupScreen(
            onSignupSuccess = { currentScreen = AuthScreen.LOGIN },
            onBackToLogin = { currentScreen = AuthScreen.LOGIN }
        )
        AuthScreen.MAIN_APP -> FinancialRiskCalculatorApp()
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onCreateAccountClick: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)), // Background color #f0f0f0
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo at the top, centered, with 90% opacity
            Image(
                painter = painterResource(id = R.mipmap.app_logo_foreground),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(0.9f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login dialog box with color #a9c7ee
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFA9C7EE),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
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
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray
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
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray
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
            .background(Color(0xFFF0F0F0)),
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
                            unfocusedContainerColor = Color.White
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
                            unfocusedContainerColor = Color.White
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
                            unfocusedContainerColor = Color.White
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

@Composable
fun FinancialRiskCalculatorApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                when (currentDestination) {
                    AppDestinations.HOME -> Text("Welcome to Home Screen")
                    AppDestinations.FAVORITES -> Text("Your Favorites")
                    AppDestinations.PROFILE -> ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    FAVORITES("Favorites", R.drawable.ic_favorite),
    PROFILE("Profile", R.drawable.ic_account_box),
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    FinancialRiskCalculatorTheme {
        LoginScreen(onLoginSuccess = {}, onCreateAccountClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    FinancialRiskCalculatorTheme {
        SignupScreen(onSignupSuccess = {}, onBackToLogin = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    FinancialRiskCalculatorTheme {
        ProfileScreen()
    }
}

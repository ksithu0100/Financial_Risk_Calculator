package com.example.financialriskcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialriskcalculator.ui.screens.*
import com.example.financialriskcalculator.ui.theme.FinancialRiskCalculatorTheme
import com.example.financialriskcalculator.viewmodel.FinancialViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinancialRiskCalculatorTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

enum class AppState {
    LOGIN, SIGNUP, PROFILE_INPUT, FIXED_EXPENSES, DECISION_INPUT, RESULTS, PROFILE_MAIN, LOGBOOK, SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    var currentState by rememberSaveable { mutableStateOf(AppState.LOGIN) }
    val viewModel: FinancialViewModel = viewModel()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentState == AppState.PROFILE_MAIN || currentState == AppState.LOGBOOK || currentState == AppState.SETTINGS) {
                TopAppBar(
                    title = { Text(if (currentState == AppState.PROFILE_MAIN) "PROFILE" else "") },
                    actions = {
                        IconButton(onClick = { currentState = AppState.LOGBOOK }) {
                            Icon(Icons.Default.History, contentDescription = "Logbook")
                        }
                        IconButton(onClick = { currentState = AppState.PROFILE_MAIN }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                        }
                        IconButton(onClick = { currentState = AppState.SETTINGS }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentState) {
                AppState.LOGIN -> LoginScreen(
                    onLoginSuccess = {
                        scope.launch {
                            val user = viewModel.checkIfUserExists()
                            if (user != null) {
                                viewModel.loadProfileFromDb()
                                currentState = AppState.PROFILE_MAIN
                            } else {
                                currentState = AppState.PROFILE_INPUT
                            }
                        }
                    },
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
                    viewModel.saveProfileToDb()
                    currentState = AppState.PROFILE_MAIN
                })
                AppState.DECISION_INPUT -> DecisionInputScreen(viewModel, onCalculate = {
                    currentState = AppState.RESULTS
                })
                AppState.RESULTS -> ResultsScreen(viewModel, onReset = {
                    currentState = AppState.DECISION_INPUT
                })
                AppState.PROFILE_MAIN -> ProfileScreen(viewModel)
                AppState.LOGBOOK -> PlaceholderScreen("Logbook")
                AppState.SETTINGS -> PlaceholderScreen("Settings")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = name, style = MaterialTheme.typography.headlineMedium)
    }
}

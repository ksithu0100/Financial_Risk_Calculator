package com.example.financialriskcalculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.financialriskcalculator.logic.RiskCalculator
import com.example.financialriskcalculator.models.FinancialDecision
import com.example.financialriskcalculator.models.UserProfile

class FinancialViewModel : ViewModel() {
    var userProfile by mutableStateOf(UserProfile())
        private set

    var currentDecision by mutableStateOf<FinancialDecision?>(null)
        private set

    var riskResult by mutableStateOf<RiskCalculator.RiskResult?>(null)
        private set

    // Fixed expenses management
    val availableExtraExpenses = listOf(
        "Streaming Subscriptions",
        "Music Subscriptions",
        "Groceries",
        "Gym Membership",
        "Internet",
        "Phone Bill"
    )

    fun updateBasicInfo(name: String, income: Double, savings: Double, creditScore: Int, occupation: String) {
        userProfile.name = name
        userProfile.monthlyIncome = income
        userProfile.totalSavings = savings
        userProfile.creditScore = creditScore
        userProfile.occupation = occupation
    }

    fun addFixedExpense(name: String, amount: Double) {
        userProfile.addFixedExpense(name, amount)
    }

    fun setBudgetStrategy(strategy: UserProfile.BudgetStrategy) {
        userProfile.budgetStrategy = strategy
    }

    fun calculateRisk(itemName: String, amount: Double, category: FinancialDecision.Category, isLongTerm: Boolean) {
        val decision = FinancialDecision(itemName, amount, category, isLongTerm)
        currentDecision = decision
        riskResult = RiskCalculator.calculateRisk(userProfile, decision)
    }
}

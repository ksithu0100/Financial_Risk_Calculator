package com.example.financialriskcalculator.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialriskcalculator.db.AppDatabase
import com.example.financialriskcalculator.db.entities.ExpenseEntity
import com.example.financialriskcalculator.db.entities.UserEntity
import com.example.financialriskcalculator.logic.RiskCalculator
import com.example.financialriskcalculator.models.FinancialDecision
import com.example.financialriskcalculator.models.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FinancialViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    
    var userProfile by mutableStateOf(UserProfile())
        private set

    var currentDecision by mutableStateOf<FinancialDecision?>(null)
        private set

    var riskResult by mutableStateOf<RiskCalculator.RiskResult?>(null)
        private set

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

    // Database Persistence
    fun saveProfileToDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val names = (userProfile.name ?: "").split(" ")
            val firstName = names.getOrNull(0) ?: ""
            val lastName = if (names.size > 1) names.subList(1, names.size).joinToString(" ") else ""

            val userEntity = UserEntity().apply {
                this.firstName = firstName
                this.lastName = lastName
                this.monthlyIncome = userProfile.monthlyIncome
                this.totalSavings = userProfile.totalSavings
                this.creditScore = userProfile.creditScore
                this.occupation = userProfile.occupation
                this.budgetStrategy = userProfile.budgetStrategy?.displayName ?: ""
            }

            val userId = db.userDao().insertUser(userEntity).toInt()

            // Save expenses
            val expenseEntities = userProfile.fixedExpenses.map { (name, amount) ->
                ExpenseEntity().apply {
                    this.userId = userId
                    this.expenseName = name
                    this.amount = amount
                }
            }
            db.expenseDao().deleteExpensesForUser(userId)
            db.expenseDao().insertAll(expenseEntities)
        }
    }

    fun loadProfileFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val userEntity = db.userDao().getUser()
            if (userEntity != null) {
                withContext(Dispatchers.Main) {
                    userProfile.name = "${userEntity.firstName} ${userEntity.lastName}".trim()
                    userProfile.monthlyIncome = userEntity.monthlyIncome
                    userProfile.totalSavings = userEntity.totalSavings
                    userProfile.creditScore = userEntity.creditScore
                    userProfile.occupation = userEntity.occupation
                    // Strategy loading can be added here
                }
                
                val expenses = db.expenseDao().getExpensesForUser(userEntity.id)
                withContext(Dispatchers.Main) {
                    userProfile.fixedExpenses.clear()
                    expenses.forEach { 
                        userProfile.addFixedExpense(it.expenseName, it.amount)
                    }
                }
            }
        }
    }
}

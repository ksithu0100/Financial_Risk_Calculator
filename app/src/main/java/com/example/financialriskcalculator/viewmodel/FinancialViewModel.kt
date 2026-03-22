package com.example.financialriskcalculator.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
    
    var userProfile: UserProfile by mutableStateOf(UserProfile())
        private set

    var currentDecision by mutableStateOf<FinancialDecision?>(null)
        private set

    var riskResult by mutableStateOf<RiskCalculator.RiskResult?>(null)
        private set

    // Profile Screen State
    var selectedSplitLabel by mutableStateOf("50/30/20")
        private set
    var needsRatio by mutableDoubleStateOf(0.5)
        private set
    var wantsRatio by mutableDoubleStateOf(0.3)
        private set
    var savingsRatio by mutableDoubleStateOf(0.2)
        private set

    val availableExtraExpenses = listOf(
        "Streaming Subscriptions",
        "Music Subscriptions",
        "Groceries",
        "Gym Membership",
        "Internet",
        "Phone Bill"
    )

    fun updateSplit(label: String, needs: Int, wants: Int, savings: Int) {
        selectedSplitLabel = label
        needsRatio = needs / 100.0
        wantsRatio = wants / 100.0
        savingsRatio = savings / 100.0
    }

    fun updateBasicInfo(name: String, income: Double, savings: Double, creditScore: Int, occupation: String) {
        userProfile.setName(name)
        userProfile.setMonthlyIncome(income)
        userProfile.setTotalSavings(savings)
        userProfile.setCreditScore(creditScore)
        userProfile.setOccupation(occupation)
    }

    fun addFixedExpense(name: String, amount: Double) {
        userProfile.addFixedExpense(name, amount)
    }

    fun setBudgetStrategy(strategy: UserProfile.BudgetStrategy) {
        userProfile.setBudgetStrategy(strategy)
    }

    suspend fun checkIfUserExists(): UserEntity? {
        return withContext(Dispatchers.IO) {
            db.userDao().getUser()
        }
    }

    fun calculateRisk(itemName: String, amount: Double, category: FinancialDecision.Category, isLongTerm: Boolean) {
        val decision = FinancialDecision(itemName, amount, category, isLongTerm)
        currentDecision = decision
        riskResult = RiskCalculator.calculateRisk(userProfile, decision)
    }

    fun saveProfileToDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val names = (userProfile.getName() ?: "").split(" ")
            val firstName = names.getOrNull(0) ?: ""
            val lastName = if (names.size > 1) names.subList(1, names.size).joinToString(" ") else ""

            val userEntity = UserEntity().apply {
                this.firstName = firstName
                this.lastName = lastName
                this.monthlyIncome = userProfile.getMonthlyIncome()
                this.totalSavings = userProfile.getTotalSavings()
                this.creditScore = userProfile.getCreditScore()
                this.occupation = userProfile.getOccupation()
                this.budgetStrategy = selectedSplitLabel
            }

            val userId = db.userDao().insertUser(userEntity).toInt()

            // Save expenses
            val expenseEntities = userProfile.getFixedExpenses().entries.map { entry ->
                ExpenseEntity().apply {
                    this.userId = userId
                    this.expenseName = entry.key
                    this.amount = entry.value
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
                    userProfile.setName("${userEntity.firstName} ${userEntity.lastName}".trim())
                    userProfile.setMonthlyIncome(userEntity.monthlyIncome)
                    userProfile.setTotalSavings(userEntity.totalSavings)
                    userProfile.setCreditScore(userEntity.creditScore)
                    userProfile.setOccupation(userEntity.occupation)
                    selectedSplitLabel = userEntity.budgetStrategy ?: "50/30/20"
                    
                    if (selectedSplitLabel == "70/20/10") {
                        needsRatio = 0.7; wantsRatio = 0.2; savingsRatio = 0.1
                    } else if (selectedSplitLabel == "50/30/20") {
                        needsRatio = 0.5; wantsRatio = 0.3; savingsRatio = 0.2
                    }
                }
                
                val userId = userEntity.id
                val expenses = db.expenseDao().getExpensesForUser(userId)
                withContext(Dispatchers.Main) {
                    userProfile.getFixedExpenses().clear()
                    expenses.forEach { 
                        userProfile.addFixedExpense(it.expenseName, it.amount)
                    }
                }
            }
        }
    }
}

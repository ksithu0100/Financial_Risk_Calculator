package com.example.financialriskcalculator.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialriskcalculator.db.AppDatabase
import com.example.financialriskcalculator.db.entities.ExpenseEntity
import com.example.financialriskcalculator.db.entities.PlanEntity
import com.example.financialriskcalculator.db.entities.UserEntity
import com.example.financialriskcalculator.logic.RiskCalculator
import com.example.financialriskcalculator.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class FinancialViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    
    var currentUserEmail: String? by mutableStateOf(null)
        private set

    var userProfile: UserProfile by mutableStateOf(UserProfile())
        private set

    private val _plans = MutableStateFlow<List<FinancialPlan>>(emptyList())
    val plans: StateFlow<List<FinancialPlan>> = _plans.asStateFlow()

    var activeLtp by mutableStateOf<FinancialPlan.LongTermPlan?>(null)
        private set

    var activeStq by mutableStateOf<FinancialPlan.ShortTermQuery?>(null)
        private set

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
        
        val strategy = if (label == "70/20/10") UserProfile.BudgetStrategy.STRATEGY_70_20_10 
                      else UserProfile.BudgetStrategy.STRATEGY_50_30_20
        userProfile.setBudgetStrategy(strategy)
        userProfile = userProfile // Trigger recomposition
    }

    fun updateBasicInfo(name: String, age: Int, income: Double, savings: Double, creditScore: Int, occupation: String) {
        userProfile.setName(name)
        userProfile.setAge(age)
        userProfile.setMonthlyIncome(income)
        userProfile.setTotalSavings(savings)
        userProfile.setCreditScore(creditScore)
        userProfile.setOccupation(occupation)
        userProfile = userProfile // Trigger recomposition
    }

    fun addFixedExpense(name: String, amount: Double) {
        userProfile.addFixedExpense(name, amount)
        userProfile = userProfile // Trigger recomposition
    }

    fun setBudgetStrategy(strategy: UserProfile.BudgetStrategy) {
        userProfile.setBudgetStrategy(strategy)
        userProfile = userProfile // Trigger recomposition
    }

    suspend fun findUserByEmail(email: String): UserEntity? {
        val normalizedEmail = email.lowercase().trim()
        return withContext(Dispatchers.IO) {
            try {
                val user = db.userDao().getUserByEmail(normalizedEmail)
                Log.d("FinancialViewModel", "findUserByEmail: $normalizedEmail found: ${user != null}")
                user
            } catch (e: Exception) {
                Log.e("FinancialViewModel", "Error finding user", e)
                null
            }
        }
    }

    fun logout() {
        currentUserEmail = null
        userProfile = UserProfile()
        currentDecision = null
        riskResult = null
    }

    fun setCurrentUser(email: String) {
        currentUserEmail = email.lowercase().trim()
    }

    suspend fun signupUser(email: String, password: String): Boolean {
        val normalizedEmail = email.lowercase().trim()
        return withContext(Dispatchers.IO) {
            try {
                val existing = db.userDao().getUserByEmail(normalizedEmail)
                if (existing != null) return@withContext false
                
                val newUser = UserEntity().apply {
                    this.email = normalizedEmail
                    this.password = password
                }
                db.userDao().insertUser(newUser)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun checkIfUserExists(): UserEntity? {
        return withContext(Dispatchers.IO) { db.userDao().getUser() }
    }

    fun saveProfileToDb() {
        val email = currentUserEmail ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userEntity = db.userDao().getUserByEmail(email) ?: UserEntity().apply { this.email = email }
                
                val names = (userProfile.getName() ?: "").split(" ")
                val firstName = names.getOrNull(0) ?: ""
                val lastName = if (names.size > 1) names.subList(1, names.size).joinToString(" ") else ""

                userEntity.firstName = firstName
                userEntity.lastName = lastName
                userEntity.age = userProfile.getAge()
                userEntity.monthlyIncome = userProfile.getMonthlyIncome()
                userEntity.totalSavings = userProfile.getTotalSavings()
                userEntity.creditScore = userProfile.getCreditScore()
                userEntity.occupation = userProfile.getOccupation()
                userEntity.budgetStrategy = selectedSplitLabel

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
                
                loadProfileFromDb(email)
            } catch (e: Exception) {
                Log.e("FinancialViewModel", "Error saving profile", e)
            }
        }
    }

    fun loadProfileFromDb(email: String) {
        val normalizedEmail = email.lowercase().trim()
        currentUserEmail = normalizedEmail
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userEntity = db.userDao().getUserByEmail(normalizedEmail)
                if (userEntity != null) {
                    val userId = userEntity.id
                    val expenses = db.expenseDao().getExpensesForUser(userId)
                    
                    withContext(Dispatchers.Main) {
                        val updatedProfile = UserProfile()
                        updatedProfile.setName("${userEntity.firstName ?: ""} ${userEntity.lastName ?: ""}".trim())
                        updatedProfile.setAge(userEntity.age)
                        updatedProfile.setMonthlyIncome(userEntity.monthlyIncome)
                        updatedProfile.setTotalSavings(userEntity.totalSavings)
                        updatedProfile.setCreditScore(userEntity.creditScore)
                        updatedProfile.setOccupation(userEntity.occupation ?: "")
                        
                        expenses.forEach { 
                            updatedProfile.addFixedExpense(it.expenseName, it.amount)
                        }

                        selectedSplitLabel = userEntity.budgetStrategy ?: "50/30/20"
                        if (selectedSplitLabel == "70/20/10") {
                            needsRatio = 0.7; wantsRatio = 0.2; savingsRatio = 0.1
                        } else if (selectedSplitLabel == "50/30/20") {
                            needsRatio = 0.5; wantsRatio = 0.3; savingsRatio = 0.2
                        }
                        
                        userProfile = updatedProfile
                    }
                }
            } catch (e: Exception) {
                Log.e("FinancialViewModel", "Error loading profile", e)
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        val email = currentUserEmail ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    db.expenseDao().deleteExpensesForUser(user.id)
                    db.userDao().deleteUser(user)
                    withContext(Dispatchers.Main) {
                        logout()
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                Log.e("FinancialViewModel", "Error deleting account", e)
            }
        }
    }
}

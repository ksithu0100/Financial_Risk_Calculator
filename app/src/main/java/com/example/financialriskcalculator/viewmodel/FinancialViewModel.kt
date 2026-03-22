package com.example.financialriskcalculator.viewmodel

import android.app.Application
import androidx.compose.runtime.*
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

    // Properties for ResultsScreen
    var riskResult by mutableStateOf<RiskCalculator.RiskResult?>(null)
        private set
    var currentDecision by mutableStateOf<FinancialDecision?>(null)
        private set

    val availableExtraExpenses = listOf("Childcare", "Gym/Fitness", "Subscriptions", "Debt Repayment", "Hobbies")

    fun updateBasicInfo(name: String, income: Double, savings: Double, creditScore: Int, occupation: String, age: Int) {
        userProfile.name = name
        userProfile.monthlyIncome = income
        userProfile.totalSavings = savings
        userProfile.creditScore = creditScore
        userProfile.occupation = occupation
        userProfile.age = age
        // Trigger recomposition
        userProfile = userProfile
    }

    fun addFixedExpense(name: String, amount: Double) {
        userProfile.addFixedExpense(name, amount)
        userProfile = userProfile
    }

    fun setBudgetStrategy(strategy: UserProfile.BudgetStrategy) {
        userProfile.budgetStrategy = strategy
        this.selectedSplitLabel = strategy.displayName
        this.needsRatio = strategy.needsRatio
        this.wantsRatio = strategy.wantsRatio
        this.savingsRatio = strategy.savingsRatio
        userProfile = userProfile
    }

    fun updateSplit(label: String, needs: Int, wants: Int, savings: Int) {
        selectedSplitLabel = label
        needsRatio = needs / 100.0
        wantsRatio = wants / 100.0
        savingsRatio = savings / 100.0
        
        val strategy = if (label == "70/20/10") UserProfile.BudgetStrategy.STRATEGY_70_20_10 else UserProfile.BudgetStrategy.STRATEGY_50_30_20
        userProfile.budgetStrategy = strategy
        userProfile = userProfile
    }

    suspend fun checkIfUserExists(): UserEntity? {
        return withContext(Dispatchers.IO) { db.userDao().getUser() }
    }

    fun saveProfileToDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = UserEntity().apply {
                val fullName = userProfile.name ?: ""
                this.firstName = fullName.split(" ").getOrNull(0) ?: ""
                this.lastName = if (fullName.split(" ").size > 1) fullName.split(" ").drop(1).joinToString(" ") else ""
                this.monthlyIncome = userProfile.monthlyIncome
                this.totalSavings = userProfile.totalSavings
                this.creditScore = userProfile.creditScore
                this.occupation = userProfile.occupation
                this.budgetStrategy = selectedSplitLabel
            }
            val userId = db.userDao().insertUser(user).toInt()
            
            val expenseEntities = userProfile.fixedExpenses.entries.map { entry ->
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
            val userEntity = db.userDao().getUser() ?: return@launch
            withContext(Dispatchers.Main) {
                userProfile.name = "${userEntity.firstName} ${userEntity.lastName}".trim()
                userProfile.monthlyIncome = userEntity.monthlyIncome
                userProfile.totalSavings = userEntity.totalSavings
                userProfile.creditScore = userEntity.creditScore
                userProfile.occupation = userEntity.occupation
                selectedSplitLabel = userEntity.budgetStrategy ?: "50/30/20"
                
                val strategy = if (selectedSplitLabel == "70/20/10") {
                    needsRatio = 0.7; wantsRatio = 0.2; savingsRatio = 0.1
                    UserProfile.BudgetStrategy.STRATEGY_70_20_10
                } else {
                    needsRatio = 0.5; wantsRatio = 0.3; savingsRatio = 0.2
                    UserProfile.BudgetStrategy.STRATEGY_50_30_20
                }
                userProfile.budgetStrategy = strategy
                userProfile = userProfile
            }
            val userId = userEntity.id
            val expenses = db.expenseDao().getExpensesForUser(userId)
            withContext(Dispatchers.Main) {
                userProfile.fixedExpenses.clear()
                expenses.forEach { userProfile.addFixedExpense(it.expenseName, it.amount) }
                userProfile = userProfile
            }
            loadPlans(userId)
        }
    }

    private fun loadPlans(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val entities = db.planDao().getPlansForUser(userId)
            val mapped = entities.map { entity ->
                if (entity.type == "LTP") {
                    FinancialPlan.LongTermPlan(
                        id = entity.id,
                        name = entity.planName ?: "",
                        goal = entity.goal ?: "",
                        cost = entity.cost,
                        amountSaved = entity.amountSaved,
                        createDate = try { LocalDate.parse(entity.createDate) } catch(e: Exception) { LocalDate.now() },
                        goalDate = if (entity.goalDate != null && entity.goalDate != "null") try { LocalDate.parse(entity.goalDate) } catch(e: Exception) { null } else null,
                        description = entity.description ?: "",
                        changes = emptyList(),
                        expenditures = emptyList()
                    )
                } else {
                    FinancialPlan.ShortTermQuery(id = entity.id, name = entity.planName ?: "", expenditures = emptyList())
                }
            }
            _plans.value = mapped
        }
    }

    fun selectPlan(plan: FinancialPlan) {
        if (plan is FinancialPlan.LongTermPlan) {
            activeLtp = plan
        } else if (plan is FinancialPlan.ShortTermQuery) {
            activeStq = plan
        }
    }

    fun calculateRisk(itemName: String, amount: Double, category: FinancialDecision.Category, isLongTerm: Boolean) {
        val decision = FinancialDecision(itemName, amount, category, isLongTerm)
        currentDecision = decision
        
        // Ensure strategy is set before calculation
        val strategy = if (selectedSplitLabel == "70/20/10") UserProfile.BudgetStrategy.STRATEGY_70_20_10 else UserProfile.BudgetStrategy.STRATEGY_50_30_20
        userProfile.budgetStrategy = strategy
        userProfile = userProfile
        
        riskResult = RiskCalculator.calculateRisk(userProfile, decision)
    }

    fun calculateRisk(queryCost: Double): RiskCalculator.RiskResult {
        val decision = FinancialDecision("Temporary", queryCost, FinancialDecision.Category.MISC, false)
        return RiskCalculator.calculateRisk(userProfile, decision)
    }

    fun savePlan(plan: FinancialPlan) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = db.userDao().getUser() ?: return@launch
            val entity = PlanEntity().apply {
                if (plan.id != 0) {
                    this.id = plan.id
                }
                this.userId = user.id
                this.planName = plan.name
                if (plan is FinancialPlan.LongTermPlan) {
                    this.type = "LTP"
                    this.goal = plan.goal
                    this.cost = plan.cost
                    this.amountSaved = plan.amountSaved
                    this.createDate = plan.createDate.toString()
                    this.goalDate = plan.goalDate?.toString() ?: "null"
                    this.description = plan.description
                } else {
                    this.type = "STQ"
                }
            }
            db.planDao().insertPlan(entity)
            loadPlans(user.id)
        }
    }

    fun deletePlan(plan: FinancialPlan) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = db.userDao().getUser() ?: return@launch
            val entity = db.planDao().getPlanById(plan.id)
            if (entity != null) {
                db.planDao().deletePlan(entity)
                loadPlans(user.id)
            }
        }
    }
}

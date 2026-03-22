package com.example.financialriskcalculator.models

import java.time.LocalDate

sealed class FinancialPlan {
    abstract val id: Int
    abstract val name: String
    abstract val expenditures: List<PlanExpenditure>

    data class LongTermPlan(
        override val id: Int = 0,
        override val name: String,
        val goal: String,
        val cost: Double,
        val amountSaved: Double,
        val createDate: LocalDate,
        val goalDate: LocalDate? = null,
        val description: String,
        val changes: List<PlanChange> = emptyList(),
        override val expenditures: List<PlanExpenditure> = emptyList()
    ) : FinancialPlan()

    data class ShortTermQuery(
        override val id: Int = 0,
        override val name: String,
        override val expenditures: List<PlanExpenditure> = emptyList()
    ) : FinancialPlan()
}

data class PlanChange(
    val id: Int = 0,
    val amount: Double,
    val type: ChangeType // WANT or NEED
)

enum class ChangeType { WANT, NEED }

data class PlanExpenditure(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val date: LocalDate
)

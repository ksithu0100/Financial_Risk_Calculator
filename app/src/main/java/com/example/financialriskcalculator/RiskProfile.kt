package com.example.financialriskcalculator

data class RiskProfile(
    val name: String = "",
    val monthlyIncome: Double = 0.0,
    val age: Int = 0,
    val job: String = "",
    val rent: Double = 0.0,
    val carPayment: Double = 0.0,
    val subscription: Double = 0.0,
    val insurance: Double = 0.0,
    val utilities: Double = 0.0,
    val savings: Double = 0.0,
    val creditScore: Int = 0
)

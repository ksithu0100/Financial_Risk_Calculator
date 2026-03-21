package com.example.financialriskcalculator.models;

import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    private String name;
    private double monthlyIncome;
    private int age;
    private String occupation;
    private double totalSavings;
    private int creditScore;
    private BudgetStrategy budgetStrategy;
    private final Map<String, Double> fixedExpenses = new HashMap<>();
    private final Map<FinancialDecision.Category, Double> currentMonthSpending = new HashMap<>();

    public enum BudgetStrategy {
        STRATEGY_50_30_20("50/30/20", 0.50, 0.30, 0.20),
        STRATEGY_70_20_10("70/20/10", 0.70, 0.20, 0.10);

        public final String displayName;
        public final double needsRatio;
        public final double wantsRatio;
        public final double savingsRatio;

        BudgetStrategy(String displayName, double needs, double wants, double savings) {
            this.displayName = displayName;
            this.needsRatio = needs;
            this.wantsRatio = wants;
            this.savingsRatio = savings;
        }

        public double getRatioForCategory(FinancialDecision.Category category) {
            switch (category) {
                case HOUSING:
                case UTILITIES:
                case TRANSPORTATION:
                case GROCERIES:
                case HEALTHCARE:
                case INSURANCE:
                case INTERNET_PHONE:
                    return needsRatio;
                case DINING_OUT:
                case ENTERTAINMENT:
                case SHOPPING:
                case TRAVEL:
                case SUBSCRIPTIONS:
                case GYM_FITNESS:
                case HOBBIES:
                case GIFTS:
                case MISC:
                    return wantsRatio;
                case STOCKS_ASSETS:
                case EMERGENCY_FUND:
                case RETIREMENT:
                case DEBT_REPAYMENT:
                case EDUCATION:
                    return savingsRatio;
                default:
                    return 0;
            }
        }
    }

    public UserProfile() {
        for (FinancialDecision.Category cat : FinancialDecision.Category.values()) {
            currentMonthSpending.put(cat, 0.0);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public double getTotalSavings() { return totalSavings; }
    public void setTotalSavings(double totalSavings) { this.totalSavings = totalSavings; }
    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }
    public BudgetStrategy getBudgetStrategy() { return budgetStrategy; }
    public void setBudgetStrategy(BudgetStrategy budgetStrategy) { this.budgetStrategy = budgetStrategy; }
    
    public Map<String, Double> getFixedExpenses() { return fixedExpenses; }
    public void addFixedExpense(String name, double amount) { fixedExpenses.put(name, amount); }

    public double getTotalFixedExpenses() {
        double total = 0;
        for (Double amount : fixedExpenses.values()) {
            if (amount != null) total += amount;
        }
        return total;
    }

    public double getDisposableIncome() {
        return monthlyIncome - getTotalFixedExpenses();
    }

    public double getCurrentMonthSpending(FinancialDecision.Category category) {
        Double spending = currentMonthSpending.get(category);
        return spending != null ? spending : 0.0;
    }

    public void setSpending(FinancialDecision.Category category, double amount) {
        currentMonthSpending.put(category, amount);
    }

    public boolean isShady() {
        return totalSavings > 50000 && creditScore < 550;
    }
}

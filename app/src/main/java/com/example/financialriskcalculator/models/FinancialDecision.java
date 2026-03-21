package com.example.financialriskcalculator.models;

public class FinancialDecision {
    public enum Category {
        // Needs (Necessities)
        HOUSING("Housing (Rent/Mortgage)"),
        UTILITIES("Utilities & Bills"),
        TRANSPORTATION("Transportation (Car/Gas/Public Transit)"),
        GROCERIES("Groceries & Home Essentials"),
        HEALTHCARE("Healthcare & Medical"),
        INSURANCE("Insurance Payments"),
        INTERNET_PHONE("Internet & Phone Bill"),
        
        // Wants (Lifestyle/Discretionary)
        DINING_OUT("Dining Out & Takeout"),
        ENTERTAINMENT("Entertainment (Movies, Events, Concerts)"),
        SHOPPING("Shopping (Clothes, Accessories)"),
        TRAVEL("Travel & Vacations"),
        SUBSCRIPTIONS("Streaming & Digital Services"),
        GYM_FITNESS("Gym & Fitness"),
        HOBBIES("Hobbies & Leisure"),
        GIFTS("Gifts & Donations"),
        ELECTRONICS("Electronics & Gadgets"),
        
        // Savings & Investments
        STOCKS_ASSETS("Stocks, Crypto & Assets"),
        EMERGENCY_FUND("Emergency Fund"),
        RETIREMENT("Retirement (401k/IRA)"),
        DEBT_REPAYMENT("Debt Repayment (Loans/Credit)"),
        EDUCATION("Education & Self-Improvement"),
        
        // Misc
        MISC("Miscellaneous");

        public final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }
    }

    private String itemName;
    private double amount;
    private Category category;
    private boolean isLongTerm;

    public FinancialDecision(String itemName, double amount, Category category, boolean isLongTerm) {
        this.itemName = itemName;
        this.amount = amount;
        this.category = category;
        this.isLongTerm = isLongTerm;
    }

    public String getItemName() { return itemName; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public boolean isLongTerm() { return isLongTerm; }
}

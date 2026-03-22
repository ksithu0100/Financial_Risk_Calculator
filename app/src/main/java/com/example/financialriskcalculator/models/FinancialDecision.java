package com.example.financialriskcalculator.models;

public class FinancialDecision {
    public enum Category {
        // Needs (Essentials)
        HOUSING("Housing (Rent/Mortgage)"),
        UTILITIES("Utilities (Electric/Water/Heat)"),
        TRANSPORTATION("Transportation (Car/Gas/Public Transit)"),
        GROCERIES("Groceries & Food Staples"),
        HEALTHCARE("Healthcare & Medical"),
        INSURANCE("Insurance Premiums"),
        INTERNET_PHONE("Internet & Phone Plan"),
        CHILDCARE("Childcare / Schooling"),
        MAINTENANCE("Maintenance (Car/Home)"),
        
        // Wants (Lifestyle)
        DINING_OUT("Dining Out / Coffee / Takeout"),
        ENTERTAINMENT("Movies / Concerts / Events"),
        SHOPPING("Shopping (Clothes/Accessories)"),
        TRAVEL("Travel / Vacations / Hotels"),
        SUBSCRIPTIONS("Streaming (Netflix/Spotify)"),
        GYM_FITNESS("Gym / Fitness Classes"),
        HOBBIES("Hobbies (Gaming/Crafts)"),
        HOME_DECOR("Home Decor & Furniture"),
        ELECTRONICS("Electronics (Phone/Laptop)"),
        PERSONAL_CARE("Personal Care (Hair/Skin)"),
        PET_EXPENSES("Pet Supplies / Vet"),
        
        // Savings & Debt (Financial Future)
        STOCKS_INVESTMENTS("Stocks & Brokerage"),
        EMERGENCY_FUND("Emergency Fund"),
        RETIREMENT("Retirement (401k/IRA)"),
        DEBT_REPAYMENT("Debt Repayment (Loans/Credit)"),
        CRYPTO("Cryptocurrency"),
        PROFESSIONAL_DUES("Professional Dues / Education"),
        
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

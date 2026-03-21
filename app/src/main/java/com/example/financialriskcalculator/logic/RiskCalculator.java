package com.example.financialriskcalculator.logic;

import com.example.financialriskcalculator.models.FinancialDecision;
import com.example.financialriskcalculator.models.UserProfile;
import java.util.Locale;

public class RiskCalculator {

    public static class RiskResult {
        public final int riskScale;
        public final String recommendation;
        public final String insight;
        public final boolean isShady;

        public RiskResult(int riskScale, String recommendation, String insight, boolean isShady) {
            this.riskScale = riskScale;
            this.recommendation = recommendation;
            this.insight = insight;
            this.isShady = isShady;
        }
    }

    public static RiskResult calculateRisk(UserProfile profile, FinancialDecision decision) {
        double disposableIncome = profile.getDisposableIncome();
        double ratio = profile.getBudgetStrategy().getRatioForCategory(decision.getCategory());
        double allocatedBucket = disposableIncome * ratio;
        double currentSpending = profile.getCurrentMonthSpending(decision.getCategory());
        double remainingBudget = allocatedBucket - currentSpending;

        double cost = decision.getAmount();
        int riskScale;
        String recommendation;
        String insight;

        if (cost > remainingBudget) {
            riskScale = 3;
            recommendation = "Not recommended";
            insight = String.format(Locale.US, "This purchase ($%.2f) exceeds your remaining budget ($%.2f) for this category.", cost, remainingBudget);
        } else {
            double percent = (cost / remainingBudget) * 100;
            if (percent < 30) {
                riskScale = 0;
                recommendation = "Safe to proceed";
            } else if (percent <= 50) {
                riskScale = 1;
                recommendation = "Proceed with caution";
            } else if (percent <= 75) {
                riskScale = 2;
                recommendation = "High risk";
            } else {
                riskScale = 3;
                recommendation = "Not recommended";
            }
            insight = String.format(Locale.US, "This purchase uses $%.2f out of your $%.2f remaining %s budget (%.1f%%).", 
                cost, remainingBudget, decision.getCategory().displayName, percent);
        }

        return new RiskResult(riskScale, recommendation, insight, profile.isShady());
    }
}

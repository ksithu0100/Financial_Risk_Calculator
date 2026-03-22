package com.example.financialriskcalculator.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String firstName;
    public String lastName;
    public int age;              // Must be > 0
    public double monthlyIncome; // Must be >= 0
    public double totalSavings;  // Must be >= 0
    public int creditScore;      // Must be >= 0
    public String occupation;
    public String budgetStrategy; // e.g., "50/30/20"
}

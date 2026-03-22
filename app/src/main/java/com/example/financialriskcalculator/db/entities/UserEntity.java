package com.example.financialriskcalculator.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String email = "";
    
    @NonNull
    public String password = "";
    
    @NonNull
    public String firstName = "";
    
    @NonNull
    public String lastName = "";
    
    public int age = 0;
    
    public double monthlyIncome = 0.0;
    
    public double totalSavings = 0.0;
    
    public int creditScore = 0;
    
    @NonNull
    public String occupation = "";
    
    @NonNull
    public String budgetStrategy = "50/30/20";
}

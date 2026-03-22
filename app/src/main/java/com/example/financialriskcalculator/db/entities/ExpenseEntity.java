package com.example.financialriskcalculator.db.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "fixed_expenses",
    foreignKeys = @ForeignKey(
        entity = UserEntity.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
    )
)
public class ExpenseEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String expenseName;
    public double amount; // Must be >= 0
}

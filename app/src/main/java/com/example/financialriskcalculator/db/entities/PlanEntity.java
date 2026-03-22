package com.example.financialriskcalculator.db.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "plans",
    foreignKeys = @ForeignKey(
        entity = UserEntity.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
    )
)
public class PlanEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String planName; // e.g., "LT Plan 1"
    public String goal;     // e.g., "Vacation to Hawaii"
    public double cost;     // Must be >= 0
    public String goalDate;
    public String description;
}

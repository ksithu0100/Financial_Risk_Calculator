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
    public String planName;
    public String type; // "LTP" or "STQ"
    
    // LTP specific
    public String goal;
    public double cost;
    public double amountSaved;
    public String createDate;
    public String goalDate;
    public String description;

    // Split for this plan
    public double needsRatio;
    public double wantsRatio;
    public double savingsRatio;

    // Serialized lists for persistence
    public String changesJson;
    public String expendituresJson;
}

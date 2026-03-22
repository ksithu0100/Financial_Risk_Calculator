package com.example.financialriskcalculator.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.financialriskcalculator.db.entities.PlanEntity;
import java.util.List;

@Dao
public interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPlan(PlanEntity plan);

    @Update
    void updatePlan(PlanEntity plan);

    @Delete
    void deletePlan(PlanEntity plan);

    @Query("SELECT * FROM plans WHERE userId = :userId")
    List<PlanEntity> getPlansForUser(int userId);

    @Query("SELECT * FROM plans WHERE id = :planId")
    PlanEntity getPlanById(int planId);
}

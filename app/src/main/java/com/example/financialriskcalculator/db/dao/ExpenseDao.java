package com.example.financialriskcalculator.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.financialriskcalculator.db.entities.ExpenseEntity;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insertAll(List<ExpenseEntity> expenses);

    @Query("SELECT * FROM fixed_expenses WHERE userId = :userId")
    List<ExpenseEntity> getExpensesForUser(int userId);

    @Query("DELETE FROM fixed_expenses WHERE userId = :userId")
    void deleteExpensesForUser(int userId);
}

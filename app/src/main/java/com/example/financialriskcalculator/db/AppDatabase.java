package com.example.financialriskcalculator.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.financialriskcalculator.db.dao.UserDao;
import com.example.financialriskcalculator.db.dao.ExpenseDao;
import com.example.financialriskcalculator.db.entities.UserEntity;
import com.example.financialriskcalculator.db.entities.ExpenseEntity;
import com.example.financialriskcalculator.db.entities.PlanEntity;

@Database(entities = {UserEntity.class, ExpenseEntity.class, PlanEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ExpenseDao expenseDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "financial_risk_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

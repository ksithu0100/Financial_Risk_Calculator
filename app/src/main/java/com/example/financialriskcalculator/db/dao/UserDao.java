package com.example.financialriskcalculator.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.financialriskcalculator.db.entities.UserEntity;
import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity user);

    @Query("SELECT * FROM users LIMIT 1")
    UserEntity getUser();

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();
}

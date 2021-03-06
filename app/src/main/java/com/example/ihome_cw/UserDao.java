package com.example.ihome_cw;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

  @Query("DELETE FROM users WHERE email LIKE :email")
  void deleteByEmail(String email);

  @Query("SELECT * FROM users")
  List<User> getAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertUser(User user);
}

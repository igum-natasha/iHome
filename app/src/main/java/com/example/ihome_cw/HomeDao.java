package com.example.ihome_cw;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HomeDao {

  @Query("DELETE FROM home WHERE userEmail LIKE :email")
  void deleteByEmail(String email);

  @Query("SELECT * FROM home")
  List<Home> getAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertHome(Home home);
}

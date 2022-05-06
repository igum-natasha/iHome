package com.example.ihome_cw;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SceneDao {

  @Query("DELETE FROM scenes WHERE userEmail LIKE :email")
  void deleteByEmail(String email);

  @Query("DELETE FROM scenes WHERE SceneId LIKE :id")
  void deleteById(String id);

  @Query("SELECT * FROM scenes WHERE SceneId LIKE :id")
  Scene selectById(String id);

  @Query("SELECT * FROM scenes")
  List<Scene> getAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertScene(Scene scene);
}

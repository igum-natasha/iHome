package com.example.ihome_cw;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeviceDao {

  @Query("DELETE FROM devices WHERE userEmail LIKE :email")
  void deleteByEmail(String email);

  @Query("SELECT * FROM devices")
  List<Device> getAll();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertDevice(Device device);
}

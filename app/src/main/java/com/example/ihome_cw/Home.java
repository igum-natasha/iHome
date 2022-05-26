package com.example.ihome_cw;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity(tableName = "home",
        foreignKeys = {@ForeignKey(entity = User.class,
        parentColumns = "email",
        childColumns = "userEmail",
        onDelete = ForeignKey.CASCADE)})
public class Home {
  @PrimaryKey @NonNull public String userEmail;

  @NonNull
  @ColumnInfo(name = "homeName")
  public String homeName;

  @NonNull
  @ColumnInfo(name = "roomList")
  @TypeConverters({Converter.class})
  public List<String> roomList;

  @NonNull
  public String getUserEmail() {
    return userEmail;
  }

  @NonNull
  public String getHomeName() {
    return homeName;
  }

  @NonNull
  @TypeConverters({Converter.class})
  public List<String> getRoomList() {
    return roomList;
  }

  public void setUserEmail(@NonNull String email) {
    this.userEmail = email;
  }

  public void setHomeName(@NonNull String homeName) {
    this.homeName = homeName;
  }

  @TypeConverters({Converter.class})
  public void setRoomList(@NonNull List<String> roomList) {
    this.roomList = roomList;
  }
}

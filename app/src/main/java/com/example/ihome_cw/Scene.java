package com.example.ihome_cw;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "scenes",
    foreignKeys = {
      @ForeignKey(
          entity = User.class,
          parentColumns = "email",
          childColumns = "userEmail",
          onDelete = ForeignKey.CASCADE)
    })
public class Scene {
  @NonNull public String userEmail;

  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "SceneId")
  public String sceneId;

  @NonNull
  @ColumnInfo(name = "DeviceId")
  public String deviceId;

  @NonNull
  @ColumnInfo(name = "SceneName")
  public String sceneName;

  @NonNull
  @ColumnInfo(name = "Repeat")
  public String repeat;

  @NonNull
  @ColumnInfo(name = "Time")
  public String time;

  @NonNull
  @ColumnInfo(name = "Conditions")
  public String condition;

  @ColumnInfo(name = "Image")
  public int image;

  @NonNull
  public String getUserEmail() {
    return userEmail;
  }

  @NonNull
  public String getCondition() {
    return condition;
  }

  @NonNull
  public String getRepeat() {
    return repeat;
  }

  @NonNull
  public String getSceneId() {
    return sceneId;
  }

  @NonNull
  public String getSceneName() {
    return sceneName;
  }

  @NonNull
  public String getTime() {
    return time;
  }

  public int getImage() {
    return image;
  }

  public void setDeviceId(@NonNull String deviceId) {
    this.deviceId = deviceId;
  }

  public void setCondition(@NonNull String condition) {
    this.condition = condition;
  }

  public void setRepeat(@NonNull String repeat) {
    this.repeat = repeat;
  }

  public void setSceneId(@NonNull String sceneId) {
    this.sceneId = sceneId;
  }

  public void setSceneName(@NonNull String sceneName) {
    this.sceneName = sceneName;
  }

  public void setTime(@NonNull String time) {
    this.time = time;
  }

  @NonNull
  public String getDeviceId() {
    return deviceId;
  }

  public void setImage(int image) {
    this.image = image;
  }

  public void setUserEmail(@NonNull String userEmail) {
    this.userEmail = userEmail;
  }
}

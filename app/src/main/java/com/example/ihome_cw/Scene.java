package com.example.ihome_cw;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scenes")
public class Scene {
    @NonNull
    public String userEmail;

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

    public void setUserEmail(@NonNull String userEmail) {
        this.userEmail = userEmail;
    }
}

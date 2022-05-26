package com.example.ihome_cw;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "devices",
        foreignKeys = {@ForeignKey(entity = User.class,
        parentColumns = "email",
        childColumns = "userEmail",
        onDelete = ForeignKey.CASCADE)})
public class Device {
  @NonNull public String userEmail;

  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "DeviceId")
  public String deviceId;

  @NonNull
  @ColumnInfo(name = "DeviceName")
  public String deviceName;

  @NonNull
  @ColumnInfo(name = "ProductId")
  public String productId;

  @NonNull
  @ColumnInfo(name = "Category")
  public String category;

  @ColumnInfo(name = "Image")
  public int image;

  @NonNull
  public String getUserEmail() {
    return userEmail;
  }

  @NonNull
  public String getCategory() {
    return category;
  }

  @NonNull
  public String getDeviceId() {
    return deviceId;
  }

  @NonNull
  public String getDeviceName() {
    return deviceName;
  }

  @NonNull
  public String getProductId() {
    return productId;
  }

  public int getImage() {
    return image;
  }

  public void setUserEmail(@NonNull String email) {
    this.userEmail = email;
  }

  public void setCategory(@NonNull String category) {
    this.category = category;
  }

  public void setDeviceId(@NonNull String deviceId) {
    this.deviceId = deviceId;
  }

  public void setDeviceName(@NonNull String deviceName) {
    this.deviceName = deviceName;
  }

  public void setProductId(@NonNull String productId) {
    this.productId = productId;
  }

  public void setImage(int image) {
    this.image = image;
  }
}

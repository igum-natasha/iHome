package com.example.ihome_cw;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
  @PrimaryKey @NonNull public String email;

  @NonNull
  @ColumnInfo(name = "password")
  public String password;

  @NonNull
  @ColumnInfo(name = "country_code")
  public String countryCode;

  @ColumnInfo(name = "first_name")
  public String firstName;

  @ColumnInfo(name = "last_name")
  public String lastName;

  @NonNull
  public String getEmail() {
    return email;
  }

  @NonNull
  public String getCountryCode() {
    return countryCode;
  }

  @NonNull
  public String getPassword() {
    return password;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setEmail(@NonNull String email) {
    this.email = email;
  }

  public void setPassword(@NonNull String password) {
    this.password = password;
  }

  public void setCountryCode(@NonNull String country_code) {
    this.countryCode = country_code;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @NonNull
  @Override
  public String toString() {
    return "ID: " + email + " FIRSTNAME:" + firstName + " LASTNAME: " + lastName;
  }
}

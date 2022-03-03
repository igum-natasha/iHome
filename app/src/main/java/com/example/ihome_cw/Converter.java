package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class Converter {

  @SuppressLint("NewApi")
  @RequiresApi(api = Build.VERSION_CODES.N)
  @TypeConverter
  public String fromHobbies(List<String> hobbies) {
    return String.join(",", hobbies);
  }

  @TypeConverter
  public List<String> toHobbies(String data) {
    return Arrays.asList(data.split(","));
  }
}

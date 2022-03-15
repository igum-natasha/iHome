package com.example.ihome_cw;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {User.class, Home.class, Device.class},
    version = 8,
    exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
  private static AppDatabase db;

  public abstract UserDao userDao();

  public abstract HomeDao homeDao();

  public abstract DeviceDao deviceDao();

  public static AppDatabase build(Context context) {
    if (db == null) {
      db =
          Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database")
              .fallbackToDestructiveMigration()
              .allowMainThreadQueries()
              .build();
    }
    return db;
  }
}

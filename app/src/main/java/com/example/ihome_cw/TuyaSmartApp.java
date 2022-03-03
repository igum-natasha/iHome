package com.example.ihome_cw;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.INeedLoginListener;

import java.util.ArrayList;
import java.util.List;

public class TuyaSmartApp extends Application {
  private List<User> usersList;

  @Override
  public void onCreate() {
    super.onCreate();
    TuyaHomeSdk.setDebugMode(true);
    TuyaHomeSdk.init(this);

    AppDatabase db = AppDatabase.build(this.getApplicationContext());
    usersList = new ArrayList<>();
    usersList = db.userDao().getAll();
    //    Home home2 = db.homeDao().getAll().get(0);
    //    Toast.makeText(TuyaSmartApp.this, home2.getHomeName(), Toast.LENGTH_LONG)
    //            .show();
    if (usersList.size() == 0) {
      TuyaHomeSdk.setOnNeedLoginListener(
          new INeedLoginListener() {
            @Override
            public void onNeedLogin(Context context) {
              startActivity(new Intent(TuyaSmartApp.this, PreMainActivity.class));
            }
          });
    } else {
      Bundle bundle = new Bundle();
      bundle.putString("Email", usersList.get(0).email);
      bundle.putString("Password", usersList.get(0).password);
      bundle.putString("CountryCode", usersList.get(0).countryCode);
      Intent intent = new Intent(TuyaSmartApp.this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtras(bundle);
      startActivity(intent);
    }
  }
}

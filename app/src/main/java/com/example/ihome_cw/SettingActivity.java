package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuya.smart.android.user.api.ILogoutCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {


  LinearLayout translate, deleteAccount, logOut;
  ImageButton btnBack;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);

    initViews();
    btnBack.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                finish();
              }
            });

    translate.setOnClickListener(new View.OnClickListener() {
      @SuppressLint("NewApi")
      @Override
      public void onClick(View view) {
        String lng = Locale.getDefault().getLanguage();
        String new_lng;
        if ("en".equals(lng) && getResources().getString(R.string.profile).equals("Profile")) {
          new_lng = "ru";
        } else {
          new_lng = "en";
        }
        Locale locale = new Locale(new_lng);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
        Toast.makeText(
                SettingActivity.this, getResources().getString(R.string.lng_change), Toast.LENGTH_LONG)
                .show();
      }
    });

    deleteAccount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        TuyaHomeSdk.getUserInstance().cancelAccount (new IResultCallback() {
          @Override
          public void onError (String code, String error) {
            Toast.makeText(
                    SettingActivity.this, getResources().getString(R.string.delete_ac_fail) + " " + error, Toast.LENGTH_LONG)
                    .show();
          }
          @Override
          public void onSuccess () {
            Toast.makeText(
                    SettingActivity.this, getResources().getString(R.string.delete_ac_suc), Toast.LENGTH_LONG)
                    .show();
            AppDatabase db = AppDatabase.build(getApplicationContext());
            db.userDao().deleteByEmail(HomeActivity.getEmail());
          }
        });
        startActivity(new Intent(SettingActivity.this, PreMainActivity.class));
      }
    });

    logOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        TuyaHomeSdk.getUserInstance().logout (new ILogoutCallback() {
          @Override
          public void onSuccess () {
            Toast.makeText(
                    SettingActivity.this, getResources().getString(R.string.logout_suc), Toast.LENGTH_LONG)
                    .show();
            startActivity(new Intent(SettingActivity.this, PreMainActivity.class));
          }

          @Override
          public void onError (String errorCode, String errorMsg) {
            Toast.makeText(
                    SettingActivity.this, getResources().getString(R.string.logout_fail) + " " + errorMsg, Toast.LENGTH_LONG)
                    .show();
          }
        });
      }
    });
    BottomNavigationView nav_view = findViewById(R.id.bottom_navigatin_view);

    nav_view.setSelectedItemId(R.id.account);
    nav_view.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.home:
                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
              case R.id.control:
                startActivity(new Intent(SettingActivity.this, TaskAdditionActivity.class));
                overridePendingTransition(0, 0);
                return true;
              case R.id.account:
                startActivity(new Intent(SettingActivity.this, AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
          }
        });



  }

  private void initViews() {
    translate = findViewById(R.id.translate);
    deleteAccount = findViewById(R.id.deleteAccount);
    logOut = findViewById(R.id.logout);
    btnBack = findViewById(R.id.left_icon);
  }
}

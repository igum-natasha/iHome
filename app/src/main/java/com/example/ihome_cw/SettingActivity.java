package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);

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
                startActivity(new Intent(SettingActivity.this, TaskActivity.class));
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
}

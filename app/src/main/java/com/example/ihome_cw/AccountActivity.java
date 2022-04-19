package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {
  ImageButton btnSetting, btnBack;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account);
    initViews();
    btnSetting.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(AccountActivity.this, SettingActivity.class);
            startActivity(intent);
          }
        });
    btnBack.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            finish();
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
                startActivity(new Intent(AccountActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
              case R.id.control:
                startActivity(new Intent(AccountActivity.this, TaskActivity.class));
                overridePendingTransition(0, 0);
                return true;
              case R.id.account:
                return true;
            }
            return false;
          }
        });
  }

  private void initViews() {
    btnSetting = findViewById(R.id.setting_icon);
    btnBack = findViewById(R.id.left_icon);
  }
}

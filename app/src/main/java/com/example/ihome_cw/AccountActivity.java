package com.example.ihome_cw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountActivity extends AppCompatActivity {
  ImageButton btnSetting, btnBack;
  TextView tvName, tvEmail;
  LinearLayout timeZone, messageCenter, questions;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account);
    initViews();
    tvEmail.setText(HomeActivity.getEmail());
    String mail = HomeActivity.getEmail();
    String regex = "^([A-Za-z0-9+_.-]+)(@.+$)";
    Pattern pattern = Pattern.compile(regex);

    Matcher matcher = pattern.matcher(mail);
    if (matcher.find()) {
      tvName.setText(matcher.group(1));
    }

    timeZone.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(AccountActivity.this, SettingActivity.class); // ?
            startActivity(intent);
          }
        });
    messageCenter.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent browserIntent =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://support.tuya.com/en/help"));
            startActivity(browserIntent);
          }
        });
    questions.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent browserIntent =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tuya.com/contact"));
            startActivity(browserIntent);
          }
        });
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
                startActivity(new Intent(AccountActivity.this, TaskAdditionActivity.class));
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
    tvName = findViewById(R.id.userName);
    tvEmail = findViewById(R.id.userEmail);

    timeZone = findViewById(R.id.timeZone);
    messageCenter = findViewById(R.id.messageCenter);
    questions = findViewById(R.id.questions);
  }
}

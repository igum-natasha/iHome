package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountActivity extends AppCompatActivity {
  ImageButton btnSetting, btnBack;
  TextView tvName, tvEmail;
  LinearLayout aboutUser, messageCenter, questions;
  Dialog infoDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account);
    initViews();
    defineInfoDialog();
    tvEmail.setText(HomeActivity.getEmail());
    String mail = HomeActivity.getEmail();
    String regex = "^([A-Za-z0-9+_.-]+)(@.+$)";
    Pattern pattern = Pattern.compile(regex);

    Matcher matcher = pattern.matcher(mail);
    if (matcher.find()) {
      tvName.setText(matcher.group(1));
    }

    aboutUser.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            infoDialog.show();
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

  private void defineInfoDialog() {
    infoDialog = new Dialog(AccountActivity.this);
    infoDialog.setContentView(R.layout.info_user_dialog);
    infoDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    infoDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    infoDialog.setCancelable(false);
    TextView tvEmail, tvCity, tvTime;
    tvEmail = infoDialog.findViewById(R.id.tvEmail);
    tvCity = infoDialog.findViewById(R.id.tvCityName);
    tvTime = infoDialog.findViewById(R.id.tvTimeZone);
    tvEmail.setText(HomeActivity.getEmail());
    tvCity.setText(HomeActivity.getCity().getCity());
    Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
    tvTime.setText(formatForDateNow.format(date));
    Button ok = infoDialog.findViewById(R.id.btn_okay);
    ok.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            infoDialog.dismiss();
          }
        });
  }

  private void initViews() {
    btnSetting = findViewById(R.id.setting_icon);
    btnBack = findViewById(R.id.left_icon);
    tvName = findViewById(R.id.userName);
    tvEmail = findViewById(R.id.userEmail);

    aboutUser = findViewById(R.id.aboutUser);
    messageCenter = findViewById(R.id.messageCenter);
    questions = findViewById(R.id.questions);
  }
}

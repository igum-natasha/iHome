package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TaskActivity extends AppCompatActivity {
  Button btnWeather, btnLocation, btnShedule;
  String devId, devName, prodId, category;
  LinearLayout btnHome, btnControl, btnAccount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task);

    Bundle bundle = getIntent().getExtras();

    initViews();

    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
    }
    //    btnHome.setOnClickListener(
    //        new View.OnClickListener() {
    //          @Override
    //          public void onClick(View view) {
    //            Bundle bundle = new Bundle();
    //            bundle.putString("Email", HomeActivity.getEmail());
    //            bundle.putString("WifiLogin", HomeActivity.getSsid());
    //            bundle.putString("WifiPassword", HomeActivity.getPassword());
    //            Intent intent = new Intent(TaskActivity.this, HomeActivity.class);
    //            intent.putExtras(bundle);
    //            startActivity(intent);
    //          }
    //        });
    //    btnControl.setOnClickListener(
    //        new View.OnClickListener() {
    //          @Override
    //          public void onClick(View view) {
    //            Intent intent = new Intent(TaskActivity.this, TaskActivity.class);
    //            startActivity(intent);
    //          }
    //        });
    //    btnAccount.setOnClickListener(
    //        new View.OnClickListener() {
    //          @Override
    //          public void onClick(View view) {
    //            Intent intent = new Intent(TaskActivity.this, AccountActivity.class);
    //            startActivity(intent);
    //          }
    //        });

    BottomNavigationView nav_view = findViewById(R.id.bottom_navigatin_view);

    nav_view.setSelectedItemId(R.id.control);
    nav_view.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.home:
                startActivity(new Intent(TaskActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
              case R.id.control:
                return true;
              case R.id.account:
                startActivity(new Intent(TaskActivity.this, AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
          }
        });
    btnWeather.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(TaskActivity.this, TaskWeatherAdditionActivity.class);
            start(intent);
          }
        });

    btnLocation.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(TaskActivity.this, TaskLocationAdditionActivity.class);
            start(intent);
          }
        });

    btnShedule.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(TaskActivity.this, TaskAdditionActivity.class);
            start(intent);
          }
        });
  }

  private void start(Intent intent) {
    Bundle bundle = new Bundle();
    bundle.putString("DeviceId", devId);
    bundle.putString("DeviceName", devName);
    bundle.putString("ProductId", prodId);
    bundle.putString("Category", category);
    intent.putExtras(bundle);
    startActivity(intent);
  }

  private void initViews() {
    btnShedule = findViewById(R.id.btnSheduledTask);
    btnLocation = findViewById(R.id.btnLocationTask);
    btnWeather = findViewById(R.id.btnWeatherTask);
  }
}

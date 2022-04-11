package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

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
  btnHome.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Bundle bundle = new Bundle();
          bundle.putString("Email", HomeActivity.getEmail());
          bundle.putString("WifiLogin", HomeActivity.getSsid());
          bundle.putString("WifiPassword", HomeActivity.getPassword());
          Intent intent = new Intent(TaskActivity.this, HomeActivity.class);
          intent.putExtras(bundle);
          startActivity(intent);
      }
  });
  btnControl.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Intent intent = new Intent(TaskActivity.this, TaskActivity.class);
          startActivity(intent);
      }
  });
  btnAccount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Intent intent = new Intent(TaskActivity.this, AccountActivity.class);
          startActivity(intent);
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

      btnAccount = findViewById(R.id.btnAccount);
      btnControl = findViewById(R.id.btnControl);
      btnHome = findViewById(R.id.btnHome);
  }
}

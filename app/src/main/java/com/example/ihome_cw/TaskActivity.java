package com.example.ihome_cw;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskActivity extends AppCompatActivity {
  LinearLayout btnWeather, btnLocation, btnShedule;
  String devId, devName, prodId, category;
  ImageButton btnAdd;
  CircleImageView btnAccount;
  Dialog addDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task);

    Bundle bundle = getIntent().getExtras();

    initViews();
    defineAddDialog();
    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
    }
    btnAdd.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.show();
          }
        });
    btnAccount.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(TaskActivity.this, AccountActivity.class);
            startActivity(intent);
          }
        });
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
                  startActivity(new Intent(TaskActivity.this, TaskAdditionActivity.class));
                  overridePendingTransition(0, 0);
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
            Intent intent = new Intent(TaskActivity.this, SimpleTaskActivity.class);
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

  private void defineAddDialog() {
    addDialog = new Dialog(TaskActivity.this);
    addDialog.setContentView(R.layout.add_dialog);
    addDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    addDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    addDialog.getWindow().setGravity(Gravity.CENTER);
    addDialog.setCancelable(false);

    LinearLayout addDevice = addDialog.findViewById(R.id.btnAddNewDevice);
    addDevice.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.dismiss();
            Intent intent = new Intent(TaskActivity.this, HomeActivity.class); // ?
            startActivity(intent);
          }
        });
    LinearLayout addTask = addDialog.findViewById(R.id.btnAddNewTask);
    addTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.dismiss();
            Intent intent = new Intent(TaskActivity.this, TaskActivity.class); // ?
            startActivity(intent);
          }
        });
  }

  private void initViews() {
    btnAdd = findViewById(R.id.plus_icon);
    btnAccount = findViewById(R.id.avatar_icon);

    btnShedule = findViewById(R.id.btnSheduledTask);
    btnLocation = findViewById(R.id.btnLocationTask);
    btnWeather = findViewById(R.id.btnWeatherTask);
  }
}

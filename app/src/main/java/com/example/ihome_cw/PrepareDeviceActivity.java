package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrepareDeviceActivity extends AppCompatActivity {

  String devId, devName, prodId, category;
  String repeatList = "";
  String time;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_prepare_device);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
    }
    addSimpleRec(String.format("Rec: ON %10s at 08:00", devName.substring(0, 6)), true, devId, "08:00");
    addSimpleRec(String.format("Rec: OFF %10s at 21:00", devName.substring(0, 6)), false, devId, "21:00");
    Intent intent = new Intent(PrepareDeviceActivity.this, HomeActivity.class);
    startActivity(intent);
  }

  public void addSimpleRec(String name, boolean on, String devId, String t) {
    List<SceneTask> tasks = new ArrayList<>();
    List<SceneCondition> conditions = new ArrayList<>();
    Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
    repeatList = "1111111";
    time = t;
    AddTask ad = new AddTask(time, repeatList, devId);
    tasks.add(ad.createSimpleTask(on));
    String correctDate = formatForDateNow.format(date);
    conditions.add(ad.createTimeSceneCond(name, correctDate));
    ad.addTask(name, tasks, conditions, PrepareDeviceActivity.this, on);
  }
}

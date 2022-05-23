package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.TimerRule;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PrepareDeviceActivity extends AppCompatActivity {

  HashMap<String, Object> taskMap = new HashMap<>();
  List<SceneTask> tasks = new ArrayList<>();
  List<SceneCondition> conditions = new ArrayList<>();
  String devId, devName, prodId, category;
  String repeatList = "";
  String time;
  ImageView loading;

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
    addSimpleRec(String.format("Rec: ON %s at 08:00", devName), true, devId, "08:00");
    addSimpleRec(String.format("Rec: OFF %s at 21:00", devName), false, devId, "21:00");
    Intent intent = new Intent(PrepareDeviceActivity.this, HomeActivity.class);
    startActivity(intent);
  }

  public void addSimpleRec(String name, boolean on, String devId, String t) {
    List<SceneTask> tasks = new ArrayList<>();
    List<SceneCondition> conditions = new ArrayList<>();
    String Name = name;
    taskMap.put("1", on);
    SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap);
    tasks.add(task);
    Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
    repeatList = "1111111";
    time = t;
    TimerRule timerRule = TimerRule.newInstance(repeatList, time, formatForDateNow.format(date));
    SceneCondition condition =
        SceneCondition.createTimerCondition(
            "Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday",
            Name,
            "timer",
            timerRule);
    conditions.add(condition);
    addTask(Name, tasks, conditions);
  }

  public void addTask(
      String Name,
      List<SceneTask> tasks,
      List<SceneCondition> conditions) {
    TuyaHomeSdk.getSceneManagerInstance()
        .createScene(
            HomeActivity.getHomeId(),
            Name, // The name of the scene.
            false,
            "", // Indicates whether the scene is displayed on the homepage.
            conditions, // The conditions.
            tasks, // The tasks.
            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
            new ITuyaResultCallback<SceneBean>() {
              @Override
              public void onSuccess(SceneBean sceneBean) {
                sceneBean.setEnabled(false);
                Toast.makeText(PrepareDeviceActivity.this, "successful!", Toast.LENGTH_LONG).show();
                addScene(sceneBean.getId(), Name, time, repeatList, String.valueOf(true));
              }

              @Override
              public void onError(String errorCode, String errorMessage) {
                Toast.makeText(
                        PrepareDeviceActivity.this,
                        "fail with error: " + errorMessage,
                        Toast.LENGTH_LONG)
                    .show();
              }
            });
  }

  private int getResourceId(String image) {
    Resources resources = getApplicationContext().getResources();
    return resources.getIdentifier(image, "drawable", getApplicationContext().getPackageName());
  }

  private void addScene(String id, String name, String time, String repeat, String cond) {
    List<String> images = Arrays.asList("bolt", "bolt2");
    Random rand = new Random();
    String randomElement = images.get(rand.nextInt(images.size()));
    int resId = getResourceId(randomElement);
    AppDatabase db = AppDatabase.build(getApplicationContext());
    Scene scene = new Scene();
    scene.setUserEmail(HomeActivity.getEmail());
    scene.setDeviceId(devId);
    scene.setSceneId(id);
    scene.setSceneName(name);
    scene.setTime(time);
    scene.setRepeat(repeat);
    scene.setCondition(cond);
    scene.setImage(resId);
    db.sceneDao().insertScene(scene);
  }
}

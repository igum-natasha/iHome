package com.example.ihome_cw;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.TimerRule;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.ValueRule;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class AddTask {
  private String devId;
  private String info;
  private String time;
  private String repeatList;

  AddTask(String time, String repeatList, String devId) {
    this.time = time;
    this.devId = devId;
    this.repeatList = repeatList;
  }

  public SceneTask createSimpleTask(boolean state) {
    HashMap<String, Object> taskMap = new HashMap<>();
    taskMap.put("1", state);
    return TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap);
  }

  public SceneCondition createTimeSceneCond(String Name, String date) {
    TimerRule timerRule =
        TimerRule.newInstance(TimeZone.getDefault().getID(), repeatList, time, date);
    return SceneCondition.createTimerCondition(
        "Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday", Name, "timer", timerRule);
  }

  public SceneCondition createWeatherSceneCond(int temp, String condition) {
    ValueRule tempRule = ValueRule.newInstance("temp", condition, temp);
    return SceneCondition.createWeatherCondition(HomeActivity.getCity(), "temp", tempRule);
  }

  public void addTask(
      String Name,
      List<SceneTask> tasks,
      List<SceneCondition> conditions,
      Context context,
      boolean state) {
    TuyaHomeSdk.getSceneManagerInstance()
        .createScene(
            HomeActivity.getHomeId(),
            Name, // The name of the scene.
            true,
            "", // Indicates whether the scene is displayed on the homepage.
            conditions, // The conditions.
            tasks, // The tasks.
            SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
            new ITuyaResultCallback<SceneBean>() {
              @Override
              public void onSuccess(SceneBean sceneBean) {
                Toast.makeText(
                        context,
                        context.getResources().getString(R.string.task_add_suc),
                        Toast.LENGTH_LONG)
                    .show();
                info =
                    ((state)
                        ? context.getResources().getString(R.string.on)
                        : context.getResources().getString(R.string.off));
                addScene(sceneBean.getId(), Name, context);
              }

              @Override
              public void onError(String errorCode, String errorMessage) {
                Toast.makeText(
                        context,
                        context.getResources().getString(R.string.task_add_fail),
                        Toast.LENGTH_LONG)
                    .show();
              }
            });
  }

  private int getResourceId(String image, Context context) {
    Resources resources = context.getApplicationContext().getResources();
    return resources.getIdentifier(
        image, "drawable", context.getApplicationContext().getPackageName());
  }

  public void addScene(String id, String name, Context context) {
    List<String> images =
        Arrays.asList(
            "bomb",
            "brain",
            "bullseye",
            "cake",
            "controller",
            "cookie",
            "emoticon",
            "flower",
            "flower2",
            "food",
            "football",
            "fruit",
            "gift",
            "party");
    Random rand = new Random();
    String randomElement = images.get(rand.nextInt(images.size()));
    int resId = getResourceId(randomElement, context);
    AppDatabase db = AppDatabase.build(context.getApplicationContext());
    Scene scene = new Scene();
    scene.setUserEmail(HomeActivity.getEmail());
    scene.setDeviceId(devId);
    scene.setSceneId(id);
    scene.setSceneName(name);
    scene.setTime(time);
    scene.setRepeat(repeatList);
    scene.setCondition(info);
    scene.setImage(resId);
    db.sceneDao().insertScene(scene);
  }
}

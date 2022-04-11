package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.PreCondition;
import com.tuya.smart.home.sdk.bean.scene.PreConditionExpr;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.TimerRule;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class SimpleTaskActivity extends AppCompatActivity {

  private EditText etTime, etDate, etName;
  private Button btnAddTask;
  String devId, devName, prodId, category;
  private List<SceneTask> tasks = new ArrayList<>();
  private List<SceneCondition> conditions = new ArrayList<>();
  LinearLayout btnHome, btnControl, btnAccount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_task);

    initViews();

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
    }
    btnHome.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", HomeActivity.getEmail());
            bundle.putString("WifiLogin", HomeActivity.getSsid());
            bundle.putString("WifiPassword", HomeActivity.getPassword());
            Intent intent = new Intent(SimpleTaskActivity.this, HomeActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
    btnControl.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(SimpleTaskActivity.this, TaskActivity.class);
            startActivity(intent);
          }
        });
    btnAccount.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(SimpleTaskActivity.this, AccountActivity.class);
            startActivity(intent);
          }
        });
    btnAddTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String Date = etDate.getText().toString();
            String Time = etTime.getText().toString();
            String Name = etName.getText().toString();
            HashMap taskMap = new HashMap();
            taskMap.put("1", true);
            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap);
            tasks.add(task);
            TimerRule timerRule = TimerRule.newInstance("1111111", Time, Date);
            SceneCondition condition =
                SceneCondition.createTimerCondition(
                    "Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday",
                    "Scheduled for weekday",
                    "timer",
                    timerRule);
            conditions.add(condition);

            PreCondition preCondition = new PreCondition();
            PreConditionExpr expr = new PreConditionExpr();
            expr.setStart("00:00");
            expr.setEnd("23:59");
            expr.setTimeInterval(PreCondition.TIMEINTERVAL_ALLDAY);
            preCondition.setCondType(PreCondition.TYPE_TIME_CHECK);
            expr.setTimeZoneId(TimeZone.getDefault().getID());
            preCondition.setExpr(expr);
            List<PreCondition> preConditions = new ArrayList<>();
            preConditions.add(preCondition);

            TuyaHomeSdk.getSceneManagerInstance()
                .createScene(
                    HomeActivity.getHomeId(),
                    Name, // The name of the scene.
                    false,
                    "", // Indicates whether the scene is displayed on the homepage.
                    conditions, // The conditions.
                    tasks, // The tasks.
                    preConditions, // The effective period. This parameter is optional.
                    SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                    new ITuyaResultCallback<SceneBean>() {
                      @Override
                      public void onSuccess(SceneBean sceneBean) {
                        Toast.makeText(SimpleTaskActivity.this, "successful!", Toast.LENGTH_LONG)
                            .show();
                        Bundle bundle = new Bundle();
                        bundle.putString("DeviceId", devId);
                        bundle.putString("DeviceName", devName);
                        bundle.putString("ProductId", prodId);
                        bundle.putString("Category", category);
                        Intent intent =
                            new Intent(SimpleTaskActivity.this, TaskAdditionActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                      }

                      @Override
                      public void onError(String errorCode, String errorMessage) {}
                    });
          }
        });
  }

  private void initViews() {
    etDate = findViewById(R.id.etDate);
    etTime = findViewById(R.id.etTime);
    btnAddTask = findViewById(R.id.btnAddTask);
    etName = findViewById(R.id.etName);

    btnAccount = findViewById(R.id.btnAccount);
    btnControl = findViewById(R.id.btnControl);
    btnHome = findViewById(R.id.btnHome);
  }
}

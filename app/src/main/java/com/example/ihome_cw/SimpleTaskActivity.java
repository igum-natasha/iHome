package com.example.ihome_cw;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SimpleTaskActivity extends AppCompatActivity {

  private EditText etTime, etDate, etName;
  private Button btnAddTask;
  String devId, devName, prodId, category;
  private List<SceneTask> tasks = new ArrayList<>();
  private List<SceneCondition> conditions = new ArrayList<>();
  ImageButton btnAdd;
  CircleImageView btnAccount;
  Dialog addDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_task);

    initViews();
    defineAddDialog();
    Bundle bundle = getIntent().getExtras();
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
            Intent intent = new Intent(SimpleTaskActivity.this, AccountActivity.class);
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
                Bundle bundle = new Bundle();
                bundle.putString("Email", HomeActivity.getEmail());
                bundle.putString("WifiLogin", HomeActivity.getSsid());
                bundle.putString("WifiPassword", HomeActivity.getPassword());
                Intent intent = new Intent(SimpleTaskActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
              case R.id.control:
                startActivity(new Intent(SimpleTaskActivity.this, TaskActivity.class));
                overridePendingTransition(0, 0);
                return true;
              case R.id.account:
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
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
            expr.setEnd(Time);
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
                          sceneBean.setEnabled(true);
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

  private void defineAddDialog() {
    addDialog = new Dialog(SimpleTaskActivity.this);
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
            Intent intent = new Intent(SimpleTaskActivity.this, HomeActivity.class); // ?
            startActivity(intent);
          }
        });
    LinearLayout addTask = addDialog.findViewById(R.id.btnAddNewTask);
    addTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.dismiss();
            Intent intent = new Intent(SimpleTaskActivity.this, TaskActivity.class); // ?
            startActivity(intent);
          }
        });
  }

  private void initViews() {
    btnAdd = findViewById(R.id.plus_icon);
    btnAccount = findViewById(R.id.avatar_icon);

    etDate = findViewById(R.id.etDate);
    etTime = findViewById(R.id.etTime);
    btnAddTask = findViewById(R.id.btnAddTask);
    etName = findViewById(R.id.etName);
  }
}

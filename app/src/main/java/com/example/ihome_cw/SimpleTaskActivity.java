package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class SimpleTaskActivity extends AppCompatActivity {

  private EditText etName;
  private Button btnAddTask, btnSetTime, btnSetRepeat;
  String devId, devName, prodId, category, time;
  private List<SceneTask> tasks = new ArrayList<>();
  private List<SceneCondition> conditions = new ArrayList<>();
  ImageButton btnAdd;
  CircleImageView btnAccount;
  Dialog addDialog, repeatDialog;
  int hour, minute;
  //    List<String> resultRepeat = new ArrayList<>();
  Map<String, Integer> resultRepeat = new HashMap<String, Integer>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_task);

    initViews();
    defineAddDialog();
    defineRepeatDialog();
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
    btnSetRepeat.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            repeatDialog.show();
          }
        });
    btnAddTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String Name = etName.getText().toString();
            HashMap taskMap = new HashMap();
            taskMap.put("1", true);
            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap);
            tasks.add(task);
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
            List<String> week =
                Arrays.asList(
                    "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
            String repeatList = "";
            for (String key : week) {
              repeatList += resultRepeat.get(key);
            }
            TimerRule timerRule =
                TimerRule.newInstance(repeatList, time, formatForDateNow.format(date));
            SceneCondition condition =
                SceneCondition.createTimerCondition(
                    "Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday",
                    Name,
                    "timer",
                    timerRule);
            conditions.add(condition);
            PreCondition preCondition = new PreCondition();
            PreConditionExpr expr = new PreConditionExpr();
            //            expr.setStart(Time);
            //            expr.setEnd("23:59");
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

    btnSetTime = findViewById(R.id.btnSetTime);
    btnSetRepeat = findViewById(R.id.btnSetRepeat);
    btnAddTask = findViewById(R.id.btnAddTask);
    etName = findViewById(R.id.etName);
  }

  public void popTimePicker(View view) {
    TimePickerDialog.OnTimeSetListener onTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
          @SuppressLint("DefaultLocale")
          @Override
          public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;
            time = String.format("%02d:%02d", hour, minute);
            btnSetTime.setText(time);
          }
        };
    int style = AlertDialog.THEME_DEVICE_DEFAULT_DARK;
    TimePickerDialog timePickerDialog =
        new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);
    timePickerDialog.setTitle("Select Time");
    timePickerDialog.show();
  }

  private void defineRepeatDialog() {
    repeatDialog = new Dialog(SimpleTaskActivity.this);
    repeatDialog.setContentView(R.layout.repeat_dialog);
    repeatDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    repeatDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    repeatDialog.getWindow().setGravity(Gravity.CENTER);
    repeatDialog.setCancelable(false);
    repeatDialog.setTitle("Select time for repeat");

    CheckBox mon, tue, wed, thu, fri, sat, sun;
    Button submit, cancel;
    cancel = repeatDialog.findViewById(R.id.btnCancel);
    submit = repeatDialog.findViewById(R.id.btnSubmit);
    mon = repeatDialog.findViewById(R.id.cbMon);
    tue = repeatDialog.findViewById(R.id.cbTue);
    wed = repeatDialog.findViewById(R.id.cbWed);
    thu = repeatDialog.findViewById(R.id.cbThur);
    fri = repeatDialog.findViewById(R.id.cbFr);
    sat = repeatDialog.findViewById(R.id.cbSat);
    sun = repeatDialog.findViewById(R.id.cbSun);
    List<CheckBox> checkBoxes = Arrays.asList(sun, mon, tue, wed, thu, fri, sat);

    cancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            repeatDialog.dismiss();
          }
        });
    submit.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            for (CheckBox ch : checkBoxes) {
              resultRepeat.put(String.valueOf(ch.getText()), 0);
              if (ch.isChecked()) {
                resultRepeat.put(String.valueOf(ch.getText()), 1);
              }
            }
            repeatDialog.dismiss();
          }
        });
  }
}

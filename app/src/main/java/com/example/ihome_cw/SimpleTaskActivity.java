package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Random;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class SimpleTaskActivity extends AppCompatActivity {

  private EditText etName;
  private Button btnAddTask, btnSetTime, btnSetRepeat, btnDevice;
  String devId, devName, prodId, category, time;
  private List<SceneTask> tasks = new ArrayList<>();
  private List<SceneCondition> conditions = new ArrayList<>();
  ImageButton btnAdd;
  CircleImageView btnAccount;
  private List<Device> devices;
  private RecyclerView rv;
  Dialog addDialog, repeatDialog, deviceDialog;
  int hour, minute, pos;
  String repeatList = "";
  //    List<String> resultRepeat = new ArrayList<>();
  Map<String, Integer> resultRepeat = new HashMap<String, Integer>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_task);

    initViews();
    defineAddDialog();
    defineRepeatDialog();
    defineDeviceDialog();
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
      if (devName != null) {
        btnDevice.setText("Device: " + devName);
        btnDevice.setEnabled(false);
      }
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
                startActivity(new Intent(SimpleTaskActivity.this, TaskAdditionActivity.class));
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
    btnDevice.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            deviceDialog.show();
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
            HashMap<String, Object> taskMap = new HashMap<>();
            taskMap.put("1", true);
            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap);
            tasks.add(task);
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
            List<String> week =
                Arrays.asList(
                    "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

            for (String key : week) {
              repeatList += resultRepeat.get(key);
            }
            String task_time = String.valueOf(btnSetTime.getText());
            TimerRule timerRule =
                TimerRule.newInstance(repeatList, task_time, formatForDateNow.format(date));
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
            addTask(Name, preConditions);
          }

          private void addTask(String Name, List<PreCondition> preConditions) {
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
                        addScene(sceneBean.getId(), Name, time, repeatList, String.valueOf(true));
                        Intent intent =
                            new Intent(SimpleTaskActivity.this, TaskAdditionActivity.class);
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
    btnDevice = findViewById(R.id.btnChooseDevice);
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
            btnSetTime.setText("Time: " + time);
            btnSetTime.setEnabled(false);
          }
        };
    TimePickerDialog timePickerDialog =
        new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
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
    List<String> repeates = new ArrayList<>();
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
                repeates.add(String.valueOf(ch.getText()));
              }
            }
            repeatDialog.dismiss();
            btnSetRepeat.setText("Repeat: " + Arrays.toString(repeates.toArray()));
            btnSetRepeat.setEnabled(false);
          }
        });
  }

  private int getResourceId(String image) {
    Resources resources = getApplicationContext().getResources();
    return resources.getIdentifier(image, "drawable", getApplicationContext().getPackageName());
  }

  private void defineDeviceDialog() {
    deviceDialog = new Dialog(SimpleTaskActivity.this);
    deviceDialog.setContentView(R.layout.device_dialog);
    deviceDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    deviceDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    deviceDialog.getWindow().setGravity(Gravity.CENTER);
    deviceDialog.setCancelable(false);
    deviceDialog.setTitle("Select device");
    showDevices();
  }

  private void initializeData() {
    AppDatabase db = AppDatabase.build(getApplicationContext());
    devices = db.deviceDao().getAll();
  }

  private void initializeAdapter() {
    RVAdapter adapter = new RVAdapter(devices);
    rv.setAdapter(adapter);
    adapter.setOnItemClickListener(
        new RVAdapter.ClickListener() {
          @Override
          public void onItemClick(int position, View v) {
            pos = position;
            deviceDialog.dismiss();
            btnDevice.setText("Device: " + devices.get(position).getDeviceName());
            devId = devices.get(position).getDeviceId();
            devName = devices.get(position).getDeviceName();
            prodId = devices.get(position).getProductId();
            category = devices.get(position).getCategory();
            btnDevice.setEnabled(false);
          }

          @Override
          public void onItemLongClick(int position, View v) {}
        });
  }

  private void addScene(String id, String name, String time, String repeat, String cond) {
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

  private void showDevices() {
    rv = deviceDialog.findViewById(R.id.rvDevice);

    LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    rv.setLayoutManager(llm);
    rv.setHasFixedSize(true);

    initializeData();
    initializeAdapter();
  }
}

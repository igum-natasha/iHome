package com.example.ihome_cw;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskWeatherAdditionActivity extends AppCompatActivity {
  String devId, devName, prodId, category;
  String condition;
  Button btnLess, btnMore, btnEqual;
  Switch swOn;
  Button btnAddWtask, btnDevice;
  EditText etName;
  ImageButton btnAdd;
  CircleImageView btnAccount;
  SeekBar seekBar;
  TextView tvProgress;
  Dialog addDialog, devDialog;

  private List<SceneTask> tasks = new ArrayList<>();
  private List<SceneCondition> conditions = new ArrayList<>();
  public String temp;
  private List<Device> devices;
  private RecyclerView rv;
  private boolean state = false;
  String repeatList = "1111111";
  String time;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_weather_addition);

    Bundle bundle = getIntent().getExtras();

    initViews();
    defineAddDialog();
    defineDeviceDialog();
    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
      if (devName != null) {
        btnDevice.setText(getResources().getString(R.string.device) + ": " + devName);
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
            Intent intent = new Intent(TaskWeatherAdditionActivity.this, AccountActivity.class);
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
                Intent intent = new Intent(TaskWeatherAdditionActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
              case R.id.control:
                startActivity(
                    new Intent(TaskWeatherAdditionActivity.this, TaskAdditionActivity.class));
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
    MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleGroup);
    toggleGroup.addOnButtonCheckedListener(
        new MaterialButtonToggleGroup.OnButtonCheckedListener() {
          @Override
          public void onButtonChecked(
              MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
            switch (checkedId) {
              case R.id.button1:
                condition = String.valueOf(btnLess.getText());
                break;
              case R.id.button2:
                condition = "" + btnEqual.getText() + btnEqual.getText();
                break;
              case R.id.button3:
                condition = String.valueOf(btnMore.getText());
                break;
            }
          }
        });
    btnDevice.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            devDialog.show();
          }
        });
    seekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            tvProgress.setText(progress + " °C");
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    swOn.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            state = b;
          }
        });
    btnAddWtask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String name = String.valueOf(etName.getText());
            temp = tvProgress.getText().toString();
            temp = temp.substring(0, temp.indexOf(" "));
            time =
                String.format(
                    "%s %s %s°C", getResources().getString(R.string.cond_desc), condition, temp);
            AddTask ad = new AddTask(time, repeatList, devId);
            tasks.add(ad.createSimpleTask(state));
            conditions.add(ad.createWeatherSceneCond(Integer.parseInt(temp), condition));

            ad.addTask(name, tasks, conditions, TaskWeatherAdditionActivity.this, state);
            Intent intent =
                new Intent(TaskWeatherAdditionActivity.this, TaskAdditionActivity.class);
            startActivity(intent);
          }
        });
  }

  private void defineAddDialog() {
    addDialog = new Dialog(TaskWeatherAdditionActivity.this);
    addDialog.setContentView(R.layout.add_dialog);
    addDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    addDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    addDialog.getWindow().setGravity(Gravity.CENTER);
    addDialog.setCancelable(false);

    LinearLayout addDevice = addDialog.findViewById(R.id.btnAddNewDevice);
    addDevice.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.dismiss();
            Intent intent = new Intent(TaskWeatherAdditionActivity.this, HomeActivity.class);
            startActivity(intent);
          }
        });
    LinearLayout addTask = addDialog.findViewById(R.id.btnAddNewTask);
    addTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.dismiss();
            Intent intent = new Intent(TaskWeatherAdditionActivity.this, TaskActivity.class);
            startActivity(intent);
          }
        });
  }

  private void initViews() {
    btnAdd = findViewById(R.id.plus_icon);
    btnAccount = findViewById(R.id.avatar_icon);
    btnLess = findViewById(R.id.button1);
    btnMore = findViewById(R.id.button3);
    btnEqual = findViewById(R.id.button2);
    etName = findViewById(R.id.etName);
    btnDevice = findViewById(R.id.btnChooseDev);
    swOn = findViewById(R.id.switchOn);
    swOn.setChecked(state);
    btnAddWtask = findViewById(R.id.btnAddWtask);
    seekBar = findViewById(R.id.seekBar);
    tvProgress = findViewById(R.id.tvProgress);
  }

  private void defineDeviceDialog() {
    devDialog = new Dialog(TaskWeatherAdditionActivity.this);
    devDialog.setContentView(R.layout.device_dialog);
    devDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    devDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    devDialog.getWindow().setGravity(Gravity.CENTER);
    //    devDialog.setCancelable(false);
    devDialog.setTitle(getResources().getString(R.string.device));
    showDevices();
  }

  private void initializeData() {
    AppDatabase db = AppDatabase.build(getApplicationContext());
    devices = db.deviceDao().getAll();
      if (devices.isEmpty()) {
          devDialog.dismiss();
          Toast.makeText(
                  TaskWeatherAdditionActivity.this,
                  getResources().getString(R.string.no_dev_found),
                  Toast.LENGTH_LONG)
                  .show();
          Intent intent = new Intent(TaskWeatherAdditionActivity.this, TaskActivity.class);
          startActivity(intent);
      }
  }

  private void initializeAdapter() {
    RVAdapter adapter = new RVAdapter(devices);
    rv.setAdapter(adapter);
    adapter.setOnItemClickListener(
        new RVAdapter.ClickListener() {
          @Override
          public void onItemClick(int position, View v) {
            devDialog.dismiss();
            btnDevice.setText(
                getResources().getString(R.string.device)
                    + ": "
                    + devices.get(position).getDeviceName());
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

  private void showDevices() {
    rv = devDialog.findViewById(R.id.rvDevice);

    LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    rv.setLayoutManager(llm);
    rv.setHasFixedSize(true);

    initializeData();
    initializeAdapter();
  }
}

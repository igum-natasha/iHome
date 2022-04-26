package com.example.ihome_cw;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.PlaceFacadeBean;
import com.tuya.smart.home.sdk.bean.scene.PreCondition;
import com.tuya.smart.home.sdk.bean.scene.PreConditionExpr;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.ValueRule;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskWeatherAdditionActivity extends AppCompatActivity {
  String devId, devName, prodId, category;
  String condition;
  Button btnLess, btnMore, btnEqual;
  Button btnAddWtask;
  LocationManager locationManager;
  EditText etName;
  ImageButton btnAdd;
  CircleImageView btnAccount;
  SeekBar seekBar;
  TextView tvProgress;
  Dialog addDialog;

  private List<SceneTask> tasks = new ArrayList<>();
  private List<SceneCondition> conditions = new ArrayList<>();
  public ValueRule tempRule;
  public SceneCondition sceneCondition;
  public String temp;
  public PlaceFacadeBean placeFacadeBean = new PlaceFacadeBean();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_weather_addition);

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
                startActivity(new Intent(TaskWeatherAdditionActivity.this, TaskActivity.class));
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
                condition = String.valueOf(btnEqual.getText());
                break;
              case R.id.button3:
                condition = String.valueOf(btnMore.getText());
                break;
            }
          }
        });
    seekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            tvProgress.setText(String.valueOf(progress + " °C"));
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {}

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    btnAddWtask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Location bestLocation = getLocation();
            String name = String.valueOf(etName.getText());
            temp = tvProgress.getText().toString();
            temp = temp.substring(0, temp.indexOf(" "));
            ;
            Toast.makeText(
                    TaskWeatherAdditionActivity.this, name + temp + condition, Toast.LENGTH_LONG)
                .show();
            TuyaHomeSdk.getSceneManagerInstance()
                .getCityByLatLng(
                    String.valueOf(bestLocation.getLongitude()),
                    String.valueOf(bestLocation.getLatitude()),
                    new ITuyaResultCallback<PlaceFacadeBean>() {
                      @Override
                      public void onSuccess(PlaceFacadeBean result) {
                        placeFacadeBean = result;
                      }

                      @Override
                      public void onError(String errorCode, String errorMessage) {}
                    });
            tempRule = ValueRule.newInstance("temp", condition, Integer.parseInt(temp));
            sceneCondition =
                SceneCondition.createWeatherCondition(placeFacadeBean, "temp", tempRule);
            HashMap taskMap = new HashMap();
            taskMap.put("1", true);
            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(devId, taskMap);
            tasks.add(task);
            conditions.add(sceneCondition);

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
                    name, // The name of the scene.
                    false,
                    "", // Indicates whether the scene is displayed on the homepage.
                    conditions, // The conditions.
                    tasks, // The tasks.
                    preConditions, // The effective period. This parameter is optional.
                    SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                    new ITuyaResultCallback<SceneBean>() {
                      @Override
                      public void onSuccess(SceneBean sceneBean) {
                        Toast.makeText(
                                TaskWeatherAdditionActivity.this, "successful!", Toast.LENGTH_LONG)
                            .show();
                        Bundle bundle = new Bundle();
                        bundle.putString("DeviceId", devId);
                        bundle.putString("DeviceName", devName);
                        bundle.putString("ProductId", prodId);
                        bundle.putString("Category", category);
                        Intent intent =
                            new Intent(TaskWeatherAdditionActivity.this, TaskActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                      }

                      @Override
                      public void onError(String errorCode, String errorMessage) {
                        Toast.makeText(TaskWeatherAdditionActivity.this, "fail!", Toast.LENGTH_LONG)
                            .show();
                      }
                    });
          }
        });
  }

  public Location getLocation() {
    Location bestLocation = null;
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(
          this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
      return bestLocation;
    } else {
      List<String> providers = locationManager.getProviders(true);
      for (String provider : providers) {
        Location l = locationManager.getLastKnownLocation(provider);
        if (l == null) {
          continue;
        }
        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
          // Found best last known location: %s", l);
          bestLocation = l;
        }
      }
      if (bestLocation == null) {
        Toast.makeText(TaskWeatherAdditionActivity.this, "Error! Turn on GPS", Toast.LENGTH_LONG)
            .show();
      }
    }
    return bestLocation;
  }

  private void defineAddDialog() {
    addDialog = new Dialog(TaskWeatherAdditionActivity.this);
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
            Intent intent = new Intent(TaskWeatherAdditionActivity.this, HomeActivity.class); // ?
            startActivity(intent);
          }
        });
    LinearLayout addTask = addDialog.findViewById(R.id.btnAddNewTask);
    addTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.dismiss();
            Intent intent = new Intent(TaskWeatherAdditionActivity.this, TaskActivity.class); // ?
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
    btnAddWtask = findViewById(R.id.btnAddWtask);
    seekBar = findViewById(R.id.seekBar);
    tvProgress = findViewById(R.id.tvProgress);
  }
}

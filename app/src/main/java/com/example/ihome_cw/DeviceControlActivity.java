package com.example.ihome_cw;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuya.smart.centralcontrol.TuyaLightDevice;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.centralcontrol.api.ILightListener;
import com.tuya.smart.sdk.centralcontrol.api.ITuyaLightDevice;
import com.tuya.smart.sdk.centralcontrol.api.bean.LightDataPoint;
import com.tuya.smart.sdk.centralcontrol.api.constants.LightMode;
import com.tuya.smart.sdk.centralcontrol.api.constants.LightScene;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeviceControlActivity extends AppCompatActivity {

  private TextView tvDeviceName, labelScene, labelWorkMode;
  private SeekBar sbBrightness;
  private Switch swStatus;
  private Spinner spWorkMode, spScene;
  private Button btnAddTask;
  String devId, devName, prodId, category;
  public static final String STHEME_DPID_101 = "1";
  ImageButton btnAdd;
  CircleImageView btnAccount;
  Dialog addDialog, statusDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_control);

    Bundle bundle = getIntent().getExtras();

    initViews();
    defineAddDialog();
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
            Intent intent = new Intent(DeviceControlActivity.this, AccountActivity.class);
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
                Intent intent = new Intent(DeviceControlActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
              case R.id.control:
                startActivity(new Intent(DeviceControlActivity.this, TaskAdditionActivity.class));
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
    String[] scenes = new String[] {"Goodnight", "Casual", "Read", "Work"};
    String[] workModes = new String[] {"Scene", "White", "Color"};

    ArrayAdapter<String> sceneAdapter =
        new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, scenes);
    ArrayAdapter<String> workModeAdapter =
        new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, workModes);

    spScene.setAdapter(sceneAdapter);
    spWorkMode.setAdapter(workModeAdapter);

    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
      tvDeviceName.setText(devName);
    }

    btnAddTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("DeviceId", devId);
            bundle.putString("DeviceName", devName);
            bundle.putString("ProductId", prodId);
            bundle.putString("Category", category);
            Intent intent = new Intent(DeviceControlActivity.this, TaskActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });

    if (category.equals("dj")) {
      ITuyaLightDevice controlDevice = new TuyaLightDevice(devId);

      controlDevice.registerLightListener(
          new ILightListener() {
            @Override
            public void onDpUpdate(LightDataPoint lightDataPoint) {}

            @Override
            public void onRemoved() {}

            @Override
            public void onStatusChanged(boolean b) {
              swStatus.setChecked(b);
            }

            @Override
            public void onNetworkStatusChanged(boolean b) {}

            @Override
            public void onDevInfoUpdate() {}
          });
      swStatus.setOnCheckedChangeListener(
          new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              controlDevice.powerSwitch(
                  b,
                  new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                      defineStatusDialog(devName);
                      statusDialog.show();
                    }

                    @Override
                    public void onSuccess() {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              "Light change successful!",
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
            }
          });
      sbBrightness.setOnSeekBarChangeListener(
          new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
              controlDevice.brightness(
                  i,
                  new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              "Light brightness change failed!",
                              Toast.LENGTH_LONG)
                          .show();
                    }

                    @Override
                    public void onSuccess() {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              "Light brightness change successful!",
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
          });

      spWorkMode.setOnItemSelectedListener(
          new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

              LightMode selectedLightMode = LightMode.MODE_WHITE;
              String selectedWorkMode = workModeAdapter.getItem(i);

              switch (selectedWorkMode) {
                case "Scene":
                  selectedLightMode = LightMode.MODE_SCENE;
                  break;
                case "White":
                  selectedLightMode = LightMode.MODE_WHITE;
                  break;
                case "Color":
                  selectedLightMode = LightMode.MODE_COLOUR;
                  break;
              }

              controlDevice.workMode(
                  selectedLightMode,
                  new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              "Work mode change failed!",
                              Toast.LENGTH_LONG)
                          .show();
                    }

                    @Override
                    public void onSuccess() {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              "Work mode change successful!",
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
          });

      spScene.setOnItemSelectedListener(
          new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

              LightScene selectedLightScene = LightScene.SCENE_CASUAL;
              String selectedScene = sceneAdapter.getItem(i);

              switch (selectedScene) {
                case "Goodnight":
                  selectedLightScene = LightScene.SCENE_GOODNIGHT;
                  break;
                case "Casual":
                  selectedLightScene = LightScene.SCENE_CASUAL;
                  break;
                case "Read":
                  selectedLightScene = LightScene.SCENE_READ;
                  break;
                case "Work":
                  selectedLightScene = LightScene.SCENE_WORK;
                  break;
              }

              controlDevice.scene(
                  selectedLightScene,
                  new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                      Toast.makeText(
                              DeviceControlActivity.this, "Scene change failed!", Toast.LENGTH_LONG)
                          .show();
                    }

                    @Override
                    public void onSuccess() {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              "Scene change successful!",
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
          });
    } else {
      ITuyaDevice conDevice = TuyaHomeSdk.newDeviceInstance(devId);
      sbBrightness.setVisibility(View.INVISIBLE);
      spWorkMode.setVisibility(View.INVISIBLE);
      spScene.setVisibility(View.INVISIBLE);
      labelScene.setVisibility(View.INVISIBLE);
      labelWorkMode.setVisibility(View.INVISIBLE);
      conDevice.registerDevListener(
          new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {}

            @Override
            public void onRemoved(String devId) {}

            @Override
            public void onStatusChanged(String devId, boolean online) {
              swStatus.setChecked(online);
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {}

            @Override
            public void onDevInfoUpdate(String devId) {}
          });

      swStatus.setOnCheckedChangeListener(
          new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              HashMap<String, Object> hashMap = new HashMap<>();
              hashMap.put(STHEME_DPID_101, b);
              conDevice.publishDps(
                  JSONObject.toJSONString(hashMap),
                  new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                      defineStatusDialog(devName);
                      statusDialog.show();
                    }

                    @Override
                    public void onSuccess() {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              "Socket status change successful!",
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
            }
          });
    }
    swStatus.setChecked(true);
  }

  private void defineAddDialog() {
    addDialog = new Dialog(DeviceControlActivity.this);
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
            Intent intent = new Intent(DeviceControlActivity.this, HomeActivity.class); // ?
            startActivity(intent);
          }
        });
    LinearLayout addTask = addDialog.findViewById(R.id.btnAddNewTask);
    addTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.dismiss();
            Intent intent = new Intent(DeviceControlActivity.this, TaskActivity.class); // ?
            startActivity(intent);
          }
        });
  }

  private void defineStatusDialog(String name) {
    statusDialog = new Dialog(DeviceControlActivity.this);
    statusDialog.setContentView(R.layout.status_dev_dialog);
    statusDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    statusDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    statusDialog.setCancelable(false);

    TextView info = statusDialog.findViewById(R.id.info);
    info.setText(String.format("Please check %s device connection and try again!", name));
    Button ok = statusDialog.findViewById(R.id.btn_retry);
    ok.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            statusDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("DeviceId", devId);
            bundle.putString("DeviceName", devName);
            bundle.putString("ProductId", prodId);
            bundle.putString("Category", category);
            Intent intent = new Intent(DeviceControlActivity.this, DeviceControlActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
    Button cancel = statusDialog.findViewById(R.id.btn_сancel);
    cancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            statusDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("Email", HomeActivity.getEmail());
            bundle.putString("WifiLogin", HomeActivity.getSsid());
            bundle.putString("WifiPassword", HomeActivity.getPassword());
            Intent intent = new Intent(DeviceControlActivity.this, HomeActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
  }

  private void initViews() {
    btnAdd = findViewById(R.id.plus_icon);
    btnAccount = findViewById(R.id.avatar_icon);
    tvDeviceName = findViewById(R.id.tvDeviceControlName);
    sbBrightness = findViewById(R.id.sbBrightness);
    swStatus = findViewById(R.id.swStatus);
    spScene = findViewById(R.id.spScene);
    spWorkMode = findViewById(R.id.spWorkMode);
    btnAddTask = findViewById(R.id.btnAddTask);
    labelScene = findViewById(R.id.labelScene);
    labelWorkMode = findViewById(R.id.labelWorkMode);
  }
}

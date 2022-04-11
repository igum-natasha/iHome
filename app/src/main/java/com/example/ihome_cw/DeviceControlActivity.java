package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
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

public class DeviceControlActivity extends AppCompatActivity {

  private TextView tvDeviceName, labelScene, labelWorkMode;
  private SeekBar sbBrightness;
  private Switch swStatus;
  private Spinner spWorkMode, spScene;
  private Button btnAddTask;
  String devId, devName, prodId, category;
    LinearLayout btnHome, btnControl, btnAccount;
  public static final String STHEME_DPID_101 = "1";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_control);

    Bundle bundle = getIntent().getExtras();

    initViews();
      btnHome.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Bundle bundle = new Bundle();
              bundle.putString("Email", HomeActivity.getEmail());
              bundle.putString("WifiLogin", HomeActivity.getSsid());
              bundle.putString("WifiPassword", HomeActivity.getPassword());
              Intent intent = new Intent(DeviceControlActivity.this, HomeActivity.class);
              intent.putExtras(bundle);
              startActivity(intent);
          }
      });
      btnControl.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(DeviceControlActivity.this, TaskActivity.class);
              startActivity(intent);
          }
      });
      btnAccount.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(DeviceControlActivity.this, AccountActivity.class);
              startActivity(intent);
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
            public void onStatusChanged(boolean b) {}

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
                      Toast.makeText(
                              DeviceControlActivity.this, "Light change failed!", Toast.LENGTH_LONG)
                          .show();
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
            public void onDpUpdate(String devId, String dpStr) {
              Toast.makeText(DeviceControlActivity.this, "Device state updated!", Toast.LENGTH_LONG)
                  .show();
            }

            @Override
            public void onRemoved(String devId) {}

            @Override
            public void onStatusChanged(String devId, boolean online) {}

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
                      Toast.makeText(
                              DeviceControlActivity.this,
                              "Socket change failed!",
                              Toast.LENGTH_LONG)
                          .show();
                    }

                    @Override
                    public void onSuccess() {}
                  });
            }
          });
    }
  }

  private void initViews() {
    tvDeviceName = findViewById(R.id.tvDeviceControlName);
    sbBrightness = findViewById(R.id.sbBrightness);
    swStatus = findViewById(R.id.swStatus);
    spScene = findViewById(R.id.spScene);
    spWorkMode = findViewById(R.id.spWorkMode);
    btnAddTask = findViewById(R.id.btnAddTask);
    labelScene = findViewById(R.id.labelScene);
    labelWorkMode = findViewById(R.id.labelWorkMode);

      btnAccount = findViewById(R.id.btnAccount);
      btnControl = findViewById(R.id.btnControl);
      btnHome = findViewById(R.id.btnHome);
  }
}

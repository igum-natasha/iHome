package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.akaita.android.circularseekbar.CircularSeekBar;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeviceControlActivity extends AppCompatActivity {

  private TextView tvDeviceName, labelScene, labelWorkMode;
  private CircularSeekBar sbBrightness;
  private Switch swStatus;
  private Spinner spWorkMode, spScene;
  private Button btnAddTask;
  String devId, devName, prodId, category;
  public static final String STHEME_DPID_101 = "1";
  ImageButton btnAdd;
  ImageView backImg;
  CircleImageView btnAccount;
  Dialog addDialog, statusDialog;
  boolean pressed;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_control);

    Bundle bundle = getIntent().getExtras();

    initViews();
    setBackImg();
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

            @SuppressLint("NewApi")
            @Override
            public void onStatusChanged(boolean b) {
              if (b) {
                sbBrightness.setInnerCircleColor(
                    getApplicationContext().getColor(R.color.primary_600));
                sbBrightness.setProgressText("ON");
                sbBrightness.setProgressTextColor(
                    getApplicationContext().getColor(R.color.base_50));
              } else {
                sbBrightness.setInnerCircleColor(
                    getApplicationContext().getColor(R.color.base_300));
                sbBrightness.setProgressText("OFF");
                sbBrightness.setProgressTextColor(
                    getApplicationContext().getColor(R.color.base_900));
              }
            }

            @Override
            public void onNetworkStatusChanged(boolean b) {}

            @Override
            public void onDevInfoUpdate() {}
          });

      sbBrightness.setOnCenterClickedListener(
          new CircularSeekBar.OnCenterClickedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCenterClicked(CircularSeekBar seekBar, float progress) {
              boolean state;
              if (sbBrightness.getInnerCircleColor()
                  == getApplicationContext().getColor(R.color.base_300)) {
                state = true;
                sbBrightness.setInnerCircleColor(
                    getApplicationContext().getColor(R.color.primary_600));
                sbBrightness.setProgressText(getResources().getString(R.string.on));
                sbBrightness.setProgressTextColor(
                    getApplicationContext().getColor(R.color.base_50));
              } else {
                state = false;
                sbBrightness.setInnerCircleColor(
                    getApplicationContext().getColor(R.color.base_300));
                sbBrightness.setProgressText(getResources().getString(R.string.off));
                sbBrightness.setProgressTextColor(
                    getApplicationContext().getColor(R.color.base_900));
              }
              controlDevice.powerSwitch(
                  state,
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
                              getResources().getString(R.string.light_success),
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
            }
          });
      sbBrightness.setOnCircularSeekBarChangeListener(
          new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(
                CircularSeekBar seekBar, float progress, boolean fromUser) {
              controlDevice.brightness(
                  (int) progress,
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
                              getResources().getString(R.string.bright_success),
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {}
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
                      defineStatusDialog(devName);
                      statusDialog.show();
                    }

                    @Override
                    public void onSuccess() {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              getResources().getString(R.string.work_mode_success),
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
                      defineStatusDialog(devName);
                      statusDialog.show();
                    }

                    @Override
                    public void onSuccess() {
                      Toast.makeText(
                              DeviceControlActivity.this,
                              getResources().getString(R.string.scene_success),
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

            @SuppressLint("NewApi")
            @Override
            public void onStatusChanged(String devId, boolean online) {
              if (online) {
                sbBrightness.setInnerCircleColor(
                    getApplicationContext().getColor(R.color.primary_600));
                sbBrightness.setProgressText(getResources().getString(R.string.on));
                sbBrightness.setProgressTextColor(
                    getApplicationContext().getColor(R.color.base_50));
              } else {
                sbBrightness.setInnerCircleColor(
                    getApplicationContext().getColor(R.color.base_300));
                sbBrightness.setProgressText(getResources().getString(R.string.off));
                sbBrightness.setProgressTextColor(
                    getApplicationContext().getColor(R.color.base_900));
              }
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {}

            @Override
            public void onDevInfoUpdate(String devId) {}
          });

      sbBrightness.setOnCenterClickedListener(
          new CircularSeekBar.OnCenterClickedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCenterClicked(CircularSeekBar seekBar, float progress) {
              boolean state;
              if (sbBrightness.getInnerCircleColor()
                  == getApplicationContext().getColor(R.color.base_300)) {
                state = true;
                sbBrightness.setInnerCircleColor(
                    getApplicationContext().getColor(R.color.primary_600));
                sbBrightness.setProgressText(getResources().getString(R.string.on));
                sbBrightness.setProgressTextColor(
                    getApplicationContext().getColor(R.color.base_50));
              } else {
                state = false;
                sbBrightness.setInnerCircleColor(
                    getApplicationContext().getColor(R.color.base_300));
                sbBrightness.setProgressText(getResources().getString(R.string.off));
                sbBrightness.setProgressTextColor(
                    getApplicationContext().getColor(R.color.base_900));
              }
              HashMap<String, Object> hashMap = new HashMap<>();
              hashMap.put(STHEME_DPID_101, state);
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
                              getResources().getString(R.string.status_suc),
                              Toast.LENGTH_LONG)
                          .show();
                    }
                  });
            }
          });
    }
    sbBrightness.setClickable(true);
  }

  private void setBackImg() {
    List<String> images = Arrays.asList("img1", "img2", "img3", "img4", "img5", "img6");
    Random rand = new Random();
    String randomElement = images.get(rand.nextInt(images.size()));
    Resources resources = getApplicationContext().getResources();
    int resId =
        resources.getIdentifier(
            randomElement, "drawable", getApplicationContext().getPackageName());
    backImg.setBackgroundResource(resId);
  }

  private void defineAddDialog() {
    addDialog = new Dialog(DeviceControlActivity.this);
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
    info.setText(
        String.format(
            "%s %s %s",
            getResources().getString(R.string.status_desc_p1),
            name,
            getResources().getString(R.string.status_desc_p2)));
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
    backImg = findViewById(R.id.backImg);
    btnAdd = findViewById(R.id.plus_icon);
    btnAccount = findViewById(R.id.avatar_icon);
    tvDeviceName = findViewById(R.id.tvDeviceControlName);
    sbBrightness = findViewById(R.id.sbBrightness);
    spScene = findViewById(R.id.spScene);
    spWorkMode = findViewById(R.id.spWorkMode);
    btnAddTask = findViewById(R.id.btnAddTask);
    labelScene = findViewById(R.id.labelScene);
    labelWorkMode = findViewById(R.id.labelWorkMode);
  }
}

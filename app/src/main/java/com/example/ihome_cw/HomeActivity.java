package com.example.ihome_cw;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorEZStepCode;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

  private CardView cvDevice;
  private Button btnSearch;
  private TextView tvDeviceName, tvDeviceId, tvProductId;

  ArrayList<String> roomList;
  String[] rooms = {"kitchen", "bedroom", "study"};
  String homeName = "MyHome";
  private static final String TAG = "TuyaSmartHome";

  private String ssid;
  private String password;
  private HomeBean currentHomeBean;
  private DeviceBean currentDeviceBean;
  ITuyaActivator tuyaActivator;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    Bundle bundle = getIntent().getExtras();

    if (bundle != null) {
      ssid = bundle.getString("WifiLogin");
      password = bundle.getString("WifiPassword");
    }

    initViews();

    cvDevice.setClickable(false);
    cvDevice.setBackgroundColor(Color.LTGRAY);

    roomList = new ArrayList<>();
    roomList.addAll(Arrays.asList(rooms));

    createHome(homeName, roomList);

    btnSearch.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String currentText = btnSearch.getText().toString();

            if (tuyaActivator == null) {
              Toast.makeText(HomeActivity.this, "Wifi config in progress", Toast.LENGTH_LONG)
                  .show();
            } else {
              if (currentText.equalsIgnoreCase("Search Devices")) {
                tuyaActivator.start();
                btnSearch.setText("Stop Search");
              } else {
                btnSearch.setText("Search Devices");
                tuyaActivator.stop();
              }
            }
          }
        });

    cvDevice.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("DeviceId", currentDeviceBean.devId);
            bundle.putString("DeviceName", currentDeviceBean.name);
            bundle.putString("ProductId", currentDeviceBean.productId);
            Intent intent = new Intent(HomeActivity.this, DeviceControlActivity.class);
//            Intent intent = new Intent(HomeActivity.this, SocketControlActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
  }

  private void createHome(String homeName, List<String> roomList) {

    TuyaHomeSdk.getHomeManagerInstance()
        .createHome(
            homeName,
            0,
            0,
            "",
            roomList,
            new ITuyaHomeResultCallback() {
              @Override
              public void onSuccess(HomeBean homeBean) {
                currentHomeBean = homeBean;
                Toast.makeText(HomeActivity.this, "Home creation successful!", Toast.LENGTH_LONG)
                    .show();
                getRegistrationToken();
              }

              @Override
              public void onError(String s, String s1) {
                Log.d(TAG, "Home creation failed with error: " + s1);
                Toast.makeText(HomeActivity.this, "Home creation failed!", Toast.LENGTH_LONG)
                    .show();
              }
            });
  }

  private void searchDevices(String token) {

    tuyaActivator =
        TuyaHomeSdk.getActivatorInstance()
            .newMultiActivator(
                new ActivatorBuilder()
                    .setSsid(ssid)
                    .setPassword(password)
                    .setContext(this)
                    .setActivatorModel(ActivatorModelEnum.TY_EZ)
                    .setTimeOut(1000)
                    .setToken(token)
                    .setListener(
                        new ITuyaSmartActivatorListener() {
                          @Override
                          public void onError(String s, String s1) {
                            Toast.makeText(
                                    HomeActivity.this,
                                    "Devices detection failed!",
                                    Toast.LENGTH_LONG)
                                .show();
                            startActivity(new Intent(HomeActivity.this, WifiLoginActivity.class));
                          }

                          @Override
                          public void onActiveSuccess(DeviceBean deviceBean) {
                            Toast.makeText(
                                    HomeActivity.this,
                                    "Devices detection successful!",
                                    Toast.LENGTH_LONG)
                                .show();
                            currentDeviceBean = deviceBean;
                            cvDevice.setClickable(true);
                            cvDevice.setBackgroundColor(Color.WHITE);
                            tvDeviceId.setText("Device ID: " + currentDeviceBean.devId);
                            tvDeviceName.setText("Device Name: " + currentDeviceBean.name);
                            tvProductId.setText("Product ID: " + currentDeviceBean.productId);
                            btnSearch.setText("Search Devices");
                            tuyaActivator.stop();
                          }

                          @Override
                          public void onStep(String s, Object o) {
                            switch (s) {
                              case ActivatorEZStepCode.DEVICE_BIND_SUCCESS:
                                Toast.makeText(
                                        HomeActivity.this,
                                        "Devices bind successful!",
                                        Toast.LENGTH_LONG)
                                    .show();
                                break;
                              case ActivatorEZStepCode.DEVICE_FIND:
                                Toast.makeText(
                                        HomeActivity.this, "New device found!", Toast.LENGTH_LONG)
                                    .show();
                                break;
                            }
                          }
                        }));
  }

  private void getRegistrationToken() {
    long homeId = currentHomeBean.getHomeId();
    TuyaHomeSdk.getActivatorInstance()
        .getActivatorToken(
            homeId,
            new ITuyaActivatorGetToken() {
              @Override
              public void onSuccess(String s) {
                searchDevices(s);
              }

              @Override
              public void onFailure(String s, String s1) {}
            });
  }

  private void initViews() {
    cvDevice = findViewById(R.id.cvDevice);
    btnSearch = findViewById(R.id.btnSearch);
    tvDeviceName = findViewById(R.id.tvDeviceName);
    tvDeviceId = findViewById(R.id.tvDeviceId);
    tvProductId = findViewById(R.id.tvProductId);
  }
}

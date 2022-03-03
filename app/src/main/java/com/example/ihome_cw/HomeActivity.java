package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
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

  private String ssid, email;
  private String password;
  private HomeBean currentHomeBean;
  private long homeId;
  private DeviceBean currentDeviceBean;

  private List<Home> homesList;
  ITuyaActivator tuyaActivator;
  private List<Device> devices;
  private RecyclerView rv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    Bundle bundle = getIntent().getExtras();

    if (bundle != null) {
      email = bundle.getString("Email");
      ssid = bundle.getString("WifiLogin");
      password = bundle.getString("WifiPassword");
    }
    initViews();
    createHome();

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

  }
  private void initializeData(){
      TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(new ITuyaHomeResultCallback() {
          @Override
          public void onSuccess(HomeBean bean) {
              if (bean.getDeviceList().size() > 0) {
                  Device dev = new Device();

                  dev.setDeviceId(bean.getDeviceList().get(0).getDevId());
                  dev.setProductId(bean.getDeviceList().get(0).getProductId());
                  dev.setDeviceName(bean.getDeviceList().get(0).getName());
                  dev.setUserEmail(email);
                  devices.add(dev);

              }
          }
          @Override
          public void onError(String errorCode, String errorMsg) {
              Toast.makeText(HomeActivity.this, "Not found devices", Toast.LENGTH_LONG)
                      .show();
          }
      });
      AppDatabase db = AppDatabase.build(getApplicationContext());
      devices = db.deviceDao().getAll();
  }

  private void initializeAdapter(){
      RVAdapter adapter = new RVAdapter(devices);
      rv.setAdapter(adapter);
      adapter.setOnItemClickListener(new RVAdapter.ClickListener() {
          @Override
          public void onItemClick(int position, View v) {
            Bundle bundle = new Bundle();
            bundle.putString("DeviceId", devices.get(position).getDeviceId());
            bundle.putString("DeviceName", devices.get(position).getDeviceName());
            bundle.putString("ProductId", devices.get(position).getProductId());
            Intent intent = new Intent(HomeActivity.this, DeviceControlActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }

          @Override
          public void onItemLongClick(int position, View v) {
              Log.d(TAG, "onItemLongClick pos = " + position);
          }
      });
  }
  private void showDevices() {
      rv = findViewById(R.id.rv);

      LinearLayoutManager llm = new LinearLayoutManager(this);
      rv.setLayoutManager(llm);
      rv.setHasFixedSize(true);

      initializeData();
      initializeAdapter();

  }

  private void addDevice(DeviceBean bean) {
      AppDatabase db = AppDatabase.build(this.getApplicationContext());
      Device device = new Device();
      device.setUserEmail(email);
      device.setDeviceId(bean.devId);
      device.setDeviceName(bean.name);
      device.setProductId(bean.productId);
      db.deviceDao().insertDevice(device);
  }
  private void createHome() {
    roomList = new ArrayList<>();
    roomList.addAll(Arrays.asList(rooms));

    AppDatabase db = AppDatabase.build(this.getApplicationContext());

    homesList = db.homeDao().getAll();
    if (homesList.isEmpty()) {
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
                                addHome();
                                getRegistrationToken();
                            }

                            @Override
                            public void onError(String s, String s1) {
                                Log.d(TAG, "Home creation failed with error: " + s1);
                                Toast.makeText(HomeActivity.this, "Home creation failed!", Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
    } else {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                Toast.makeText(HomeActivity.this, "Home found successful!", Toast.LENGTH_LONG).show();
                currentHomeBean = homeBeans.get(0);
                getRegistrationToken();
                showDevices();
            }

            @Override
            public void onError(String errorCode, String error) {
                Log.d(TAG, "Home finding failed with error: " + error);
                Toast.makeText(HomeActivity.this, "Home finding failed!", Toast.LENGTH_LONG)
                        .show();
            }
        });

    }
    btnSearch.setVisibility(View.VISIBLE);
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
                            tuyaActivator.stop();
                            addDevice(currentDeviceBean);
                            showDevices();
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

  private void addHome() {
      AppDatabase db = AppDatabase.build(getApplicationContext());
      Home home = new Home();
      home.setUserEmail(email);
      home.setHomeName(homeName);
      home.setRoomList(roomList);
      db.homeDao().insertHome(home);
  }
  private void getRegistrationToken() {
    homeId = currentHomeBean.getHomeId();
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
    btnSearch = findViewById(R.id.btnSearch);
    rv = findViewById(R.id.rv);
  }

}

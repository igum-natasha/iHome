package com.example.ihome_cw;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.PlaceFacadeBean;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorEZStepCode;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

  private CardView cvDevice;
  private Toolbar mToolbar;
  private Button btnSearch;
  private ImageButton btnAdd;
  private CircleImageView btnAvatar;
  private TextView tvWeather, tvWeatherTemp, tvWeatherHumidity;

  String API = "a489972de36b54432056bbefac20242b";
  ImageView tvImage;
  WeatherAPI.ApiInterface api;
  LocationManager locationManager;

  ArrayList<String> roomList;
  String[] rooms = {"kitchen", "bedroom", "study"};
  String homeName = "MyHome";
  private static final String TAG = "TuyaSmartHome";

  public static String ssid, email;
  public static String password;
  private HomeBean currentHomeBean;
  public static long homeId;
  private DeviceBean currentDeviceBean;

  private List<Home> homesList;
  ITuyaActivator tuyaActivator;
  private List<Device> devices;
  private RecyclerView rv;
  Dialog locationDialog, addDialog;

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
    defineLocationDialog();
    defineAddDialog();
    btnAdd.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              addDialog.show();
          }
        });
    btnAvatar.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
            startActivity(intent);
          }
        });

    BottomNavigationView nav_view = findViewById(R.id.bottom_navigatin_view);

    nav_view.setSelectedItemId(R.id.home);
    nav_view.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.home:
                return true;
              case R.id.control:
                Intent intent = new Intent(HomeActivity.this, TaskActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
              case R.id.account:
                startActivity(new Intent(HomeActivity.this, AccountActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
          }
        });
    api = WeatherAPI.getClient().create(WeatherAPI.ApiInterface.class);
    getWeather();
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
        Toast.makeText(HomeActivity.this, "Error! Turn on GPS", Toast.LENGTH_LONG).show();
      }
    }
    return bestLocation;
  }

  public void getWeather() {
    Location bestLocation = getLocation();
    if (bestLocation == null) {
      locationDialog.show();
    } else {

      String key = WeatherAPI.KEY;
      // get weather for today

      Call<WeatherDay> callToday =
          api.getToday(bestLocation.getLatitude(), bestLocation.getLongitude(), "metric", key);
      callToday.enqueue(
          new Callback<WeatherDay>() {
            @Override
            public void onResponse(Call<WeatherDay> call, Response<WeatherDay> response) {
              WeatherDay data = response.body();
              if (response.isSuccessful()) {
                //                            tvTemp.setText(data.getTempInteger());
                TuyaHomeSdk.getSceneManagerInstance()
                    .getCityByLatLng(
                        String.valueOf(bestLocation.getLongitude()),
                        String.valueOf(bestLocation.getLatitude()),
                        new ITuyaResultCallback<PlaceFacadeBean>() {
                          @Override
                          public void onSuccess(PlaceFacadeBean result) {
                            tvWeather.setText(result.getCity());
                          }

                          @Override
                          public void onError(String errorCode, String errorMessage) {}
                        });

                tvImage.setImageResource(R.drawable.cloud_sun);
                tvWeatherTemp.setText(data.getTempWithDegree());
                tvWeatherHumidity.setText(data.getHumidity());
              }
            }

            @Override
            public void onFailure(Call<WeatherDay> call, Throwable t) {
              Toast.makeText(HomeActivity.this, "Weather failed!" + t, Toast.LENGTH_LONG).show();
            }
          });
    }
  }

  private void initializeData() {
    TuyaHomeSdk.newHomeInstance(homeId)
        .getHomeDetail(
            new ITuyaHomeResultCallback() {
              @Override
              public void onSuccess(HomeBean bean) {
                if (bean.getDeviceList().size() > 0) {
                  List<DeviceBean> devArr = bean.getDeviceList();
                  for (int i = 0; i < devArr.size(); i++) {
                    Device dev = new Device();
                    dev.setDeviceId(devArr.get(i).getDevId());
                    dev.setProductId(devArr.get(i).getProductId());
                    dev.setDeviceName(devArr.get(i).getName());
                    dev.setUserEmail(email);
                    dev.setCategory(devArr.get(i).getDeviceCategory());
                    devices.add(dev);
                  }
                }
              }

              @Override
              public void onError(String errorCode, String errorMsg) {
                Toast.makeText(HomeActivity.this, "Not found devices", Toast.LENGTH_LONG).show();
              }
            });
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
            Bundle bundle = new Bundle();
            bundle.putString("DeviceId", devices.get(position).getDeviceId());
            bundle.putString("DeviceName", devices.get(position).getDeviceName());
            bundle.putString("ProductId", devices.get(position).getProductId());
            bundle.putString("Category", devices.get(position).getCategory());
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
    device.setDeviceId(bean.getDevId());
    device.setDeviceName(bean.getName());
    device.setProductId(bean.getProductId());
    device.setCategory(bean.getDeviceCategory());
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
      TuyaHomeSdk.getHomeManagerInstance()
          .queryHomeList(
              new ITuyaGetHomeListCallback() {
                @Override
                public void onSuccess(List<HomeBean> homeBeans) {
                  Toast.makeText(HomeActivity.this, "Home found successful!", Toast.LENGTH_LONG)
                      .show();
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
    btnAvatar = findViewById(R.id.avatar_icon);
    btnAdd = findViewById(R.id.plus_icon);

    btnSearch = findViewById(R.id.btnSearch);
    rv = findViewById(R.id.rv);
    tvWeather = findViewById(R.id.tvWeather);
    tvWeatherTemp = findViewById(R.id.tvWeatherTemp);
    tvWeatherHumidity = findViewById(R.id.tvWeatherHumidity);
    tvImage = findViewById(R.id.ivIcon);
  }

  private void defineLocationDialog() {
    locationDialog = new Dialog(HomeActivity.this);
    locationDialog.setContentView(R.layout.location_dialog);
    locationDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    locationDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    locationDialog.setCancelable(false);

    Button ok = locationDialog.findViewById(R.id.btn_okay);
    ok.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            locationDialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("Email", email);
            bundle.putString("WifiLogin", ssid);
            bundle.putString("WifiPassword", password);
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
  }
    private void defineAddDialog() {
        addDialog = new Dialog(HomeActivity.this);
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
                        Intent intent = new Intent(HomeActivity.this, HomeActivity.class); //?
                        startActivity(intent);
                    }
                });
        LinearLayout addTask = addDialog.findViewById(R.id.btnAddNewTask);
        addTask.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addDialog.dismiss();
                        Intent intent = new Intent(HomeActivity.this, TaskActivity.class); //?
                        startActivity(intent);
                    }
                });
    }
  public static long getHomeId() {
    return homeId;
  }

  public static String getPassword() {
    return password;
  }

  public static String getSsid() {
    return ssid;
  }

  public static String getEmail() {
    return email;
  }
}

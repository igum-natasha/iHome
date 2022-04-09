package com.example.ihome_cw;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskWeatherAdditionActivity extends AppCompatActivity {
  String devId, devName, prodId, category;
  String condititon;
  ImageButton btnLess, btnMore, btnEqual;
  Button btnAddWtask;
  EditText etWeather;
  TextView tvTemp;
  String API = "a489972de36b54432056bbefac20242b";
  ImageView tvImage;
  WeatherAPI.ApiInterface api;
  LocationManager locationManager;


  private List<SceneTask> tasks = new ArrayList<>();
  private List<SceneBean> scenes = new ArrayList<>();
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

    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
    }

    api = WeatherAPI.getClient().create(WeatherAPI.ApiInterface.class);

    btnLess.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            condititon = "<";
          }
        });

    btnMore.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            condititon = ">";
          }
        });

    btnEqual.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            condititon = "=";
          }
        });

    btnAddWtask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Location bestLocation = getLocation();
              temp = etWeather.getText().toString();
              TuyaHomeSdk.getSceneManagerInstance().getCityByLatLng(String.valueOf(bestLocation.getLongitude()),
                      String.valueOf(bestLocation.getLatitude()),
                      new ITuyaResultCallback<PlaceFacadeBean>() {
                          @Override
                          public void onSuccess(PlaceFacadeBean result) {
                              placeFacadeBean = result;
                          }

                          @Override
                          public void onError(String errorCode, String errorMessage) {
                          }
                      });
              tempRule = ValueRule.newInstance(
                      "temp",
                      condititon,
                      Integer.parseInt(temp)
              );
              sceneCondition = SceneCondition.createWeatherCondition(
                      placeFacadeBean,
                      "temp",
                      tempRule
              );
              HashMap taskMap = new HashMap();
              taskMap.put("1", true);
              SceneTask task =
                      TuyaHomeSdk.getSceneManagerInstance().createDpTask(
                              devId,
                              taskMap
                      );
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
                              "Test", // The name of the scene.
                              false,
                              "", // Indicates whether the scene is displayed on the homepage.
                              conditions, // The conditions.
                              tasks, // The tasks.
                              preConditions, // The effective period. This parameter is optional.
                              SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                              new ITuyaResultCallback<SceneBean>() {
                                  @Override
                                  public void onSuccess(SceneBean sceneBean) {
                                      Toast.makeText(TaskWeatherAdditionActivity.this, "successful!", Toast.LENGTH_LONG)
                                              .show();
                                      Bundle bundle = new Bundle();
                                      bundle.putString("DeviceId", devId);
                                      bundle.putString("DeviceName", devName);
                                      bundle.putString("ProductId", prodId);
                                      bundle.putString("Category", category);
                                      Intent intent = new Intent(TaskWeatherAdditionActivity.this, TaskActivity.class);
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
//            String ref_temp = etWeather.getText().toString();
//            getWeather();
//            temp = tvTemp.getText().toString();
//            if (!temp.isEmpty()) {
//              if (condititon.equals(">") && Integer.parseInt(ref_temp) > Integer.parseInt(temp)) {
//                ITuyaDevice controlDevice = TuyaHomeSdk.newDeviceInstance(devId);
//                HashMap<String, Object> hashMap = new HashMap<>();
//                hashMap.put(STHEME_DPID_101, true);
//                controlDevice.publishDps(
//                    JSONObject.toJSONString(hashMap),
//                    new IResultCallback() {
//                      @Override
//                      public void onError(String code, String error) {
//                        Toast.makeText(
//                                TaskWeatherAdditionActivity.this,
//                                "Socket change failed!",
//                                Toast.LENGTH_LONG)
//                            .show();
//                      }
//
//                      @Override
//                      public void onSuccess() {}
//                    });
//              }
//            }
//          }
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
  public void getWeather() {
    Location bestLocation = getLocation();

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
              tvTemp.setText(data.getTempInteger());
              //                    Toast.makeText(
              //                            TaskWeatherAdditionActivity.this,
              //                            "Weather successful!",
              //                            Toast.LENGTH_LONG)
              //                            .show();
            }
          }

          @Override
          public void onFailure(Call<WeatherDay> call, Throwable t) {
            Toast.makeText(
                    TaskWeatherAdditionActivity.this, "Weather failed!" + t, Toast.LENGTH_LONG)
                .show();
          }
        });
  }

  private void initViews() {
    btnLess = findViewById(R.id.btnLess);
    btnMore = findViewById(R.id.btnMore);
    btnEqual = findViewById(R.id.btnEqual);
    btnAddWtask = findViewById(R.id.btnAddWtask);
    tvTemp = findViewById(R.id.tvTemp);
    etWeather = findViewById(R.id.etTemp);
  }
}

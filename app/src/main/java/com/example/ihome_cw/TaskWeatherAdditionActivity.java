package com.example.ihome_cw;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSONObject;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;

import java.util.HashMap;
import java.util.List;

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
    public static final String STHEME_DPID_101 = "1";
    String TAG = "WEATHER";
    ImageView tvImage;
    LinearLayout llForecast;
    WeatherAPI.ApiInterface api;
    LocationManager locationManager;
    public String temp;

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

        btnLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                condititon = "<";
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                condititon = ">";
            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                condititon = "=";
            }
        });

        btnAddWtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ref_temp = etWeather.getText().toString();
                getWeather();
                temp = tvTemp.getText().toString();
                if (!temp.isEmpty()) {
                    Toast.makeText(
                            TaskWeatherAdditionActivity.this,
                            ref_temp+ temp,
                            Toast.LENGTH_LONG)
                            .show();
                    if (condititon.equals(">") && Integer.parseInt(ref_temp) > Integer.parseInt(temp)) {
                        ITuyaDevice controlDevice = TuyaHomeSdk.newDeviceInstance(devId);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(STHEME_DPID_101, true);
                        controlDevice.publishDps(
                                JSONObject.toJSONString(hashMap),
                                new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        Toast.makeText(
                                                TaskWeatherAdditionActivity.this,
                                                "Socket change failed!",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }

                                    @Override
                                    public void onSuccess() {
                                    }
                                });
                    }
                }
            }
        });

    }

    public void getWeather() {
        Location bestLocation = null;
        String key = WeatherAPI.KEY;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
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
                Toast.makeText(
                        TaskWeatherAdditionActivity.this,
                        "Error! Turn on GPS",
                        Toast.LENGTH_LONG)
                        .show();
            }

        }

        // get weather for today

        Call<WeatherDay> callToday = api.getToday(bestLocation.getLatitude(), bestLocation.getLongitude(), "metric", key);
        callToday.enqueue(new Callback<WeatherDay>() {
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
                        TaskWeatherAdditionActivity.this,
                        "Weather failed!" + t,
                        Toast.LENGTH_LONG)
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

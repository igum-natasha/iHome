package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

public class SceneInfoActivity extends AppCompatActivity {
  private String sceneId;
  public static long homeId;
  private TextView tvSceneName, tvSceneDate, tvSceneTime, tvSceneEnable;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scene_info);
    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      sceneId = bundle.getString("SceneId");
      homeId = HomeActivity.getHomeId();
    }

    initViews();
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
                Intent intent = new Intent(SceneInfoActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
              case R.id.control:
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
    TuyaHomeSdk.getSceneManagerInstance()
        .getSceneDetail(
            homeId,
            sceneId,
            new ITuyaResultCallback<SceneBean>() {
              @Override
              public void onSuccess(SceneBean result) {
                Toast.makeText(
                        SceneInfoActivity.this,
                        result.getActions().get(0).getExecutorProperty().toString(),
                        Toast.LENGTH_LONG)
                    .show();
                tvSceneName.setText(result.getName());
                //
                // tvSceneDate.setText(result.getConditions().get(0).getExprDisplay());
                //                tvSceneTime.setText(result.getConditions().get(0).);
                tvSceneEnable.setText(String.valueOf(result.isEnabled()));
              }

              @Override
              public void onError(String errorCode, String errorMessage) {}
            });
  }

  private void initViews() {
    tvSceneName = findViewById(R.id.tvSceneName);
    tvSceneDate = findViewById(R.id.tvSceneDate);
    tvSceneTime = findViewById(R.id.tvSceneTime);
    tvSceneEnable = findViewById(R.id.tvSceneEnable);
  }
}

package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

public class SceneInfoActivity extends AppCompatActivity {
  private String sceneId;
  public static long homeId;
  private TextView tvSceneName, tvSceneDate, tvSceneTime, tvSceneEnable;
  LinearLayout btnHome, btnControl, btnAccount;

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
    btnHome.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", HomeActivity.getEmail());
            bundle.putString("WifiLogin", HomeActivity.getSsid());
            bundle.putString("WifiPassword", HomeActivity.getPassword());
            Intent intent = new Intent(SceneInfoActivity.this, HomeActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
    btnControl.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(SceneInfoActivity.this, TaskActivity.class);
            startActivity(intent);
          }
        });
    btnAccount.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(SceneInfoActivity.this, AccountActivity.class);
            startActivity(intent);
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

    btnAccount = findViewById(R.id.btnAccount);
    btnControl = findViewById(R.id.btnControl);
    btnHome = findViewById(R.id.btnHome);
  }
}

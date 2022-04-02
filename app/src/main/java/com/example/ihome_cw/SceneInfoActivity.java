package com.example.ihome_cw;

import android.os.Bundle;
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
        TuyaHomeSdk.getSceneManagerInstance().getSceneDetail(homeId, sceneId, new ITuyaResultCallback<SceneBean>() {
            @Override
            public void onSuccess(SceneBean result) {
                Toast.makeText(SceneInfoActivity.this, result.getActions().get(0).getExecutorProperty().toString(), Toast.LENGTH_LONG)
                        .show();
                tvSceneName.setText(result.getName());
//                tvSceneDate.setText(result.getConditions().get(0).getExprDisplay());
//                tvSceneTime.setText(result.getConditions().get(0).);
                tvSceneEnable.setText(String.valueOf(result.isEnabled()));


            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });
    }
    private void initViews() {
        tvSceneName = findViewById(R.id.tvSceneName);
        tvSceneDate = findViewById(R.id.tvSceneDate);
        tvSceneTime = findViewById(R.id.tvSceneTime);
        tvSceneEnable = findViewById(R.id.tvSceneEnable);
    }
}
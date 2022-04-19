package com.example.ihome_cw;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import de.hdodenhof.circleimageview.CircleImageView;

public class SceneInfoActivity extends AppCompatActivity {
  private String sceneId;
  public static long homeId;
  private TextView tvSceneName, tvSceneDate, tvSceneTime, tvSceneEnable;
  ImageButton btnAdd;
  CircleImageView btnAccount;
  Dialog addDialog;

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
    defineAddDialog();
    btnAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        addDialog.show();
      }
    });
    btnAccount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(SceneInfoActivity.this, AccountActivity.class);
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
                Intent intent = new Intent(SceneInfoActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
              case R.id.control:
                startActivity(new Intent(SceneInfoActivity.this, TaskActivity.class));
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
  private void defineAddDialog() {
    addDialog = new Dialog(SceneInfoActivity.this);
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
                Intent intent = new Intent(SceneInfoActivity.this, HomeActivity.class); //?
                startActivity(intent);
              }
            });
    LinearLayout addTask = addDialog.findViewById(R.id.btnAddNewTask);
    addTask.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                addDialog.dismiss();
                Intent intent = new Intent(SceneInfoActivity.this, TaskActivity.class); //?
                startActivity(intent);
              }
            });
  }
  private void initViews() {
    btnAdd = findViewById(R.id.plus_icon);
    btnAccount = findViewById(R.id.avatar_icon);

    tvSceneName = findViewById(R.id.tvSceneName);
    tvSceneDate = findViewById(R.id.tvSceneDate);
    tvSceneTime = findViewById(R.id.tvSceneTime);
    tvSceneEnable = findViewById(R.id.tvSceneEnable);
  }
}

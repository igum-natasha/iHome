package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import java.util.ArrayList;
import java.util.List;

public class TaskAdditionActivity extends AppCompatActivity {

  String devId, devName, prodId, category;

  private Button btnAdd;
  private List<SceneBean> scenes = new ArrayList<>();
  private RecyclerView rv_tasks;
  LinearLayout btnHome, btnControl, btnAccount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_addition);

    Bundle bundle = getIntent().getExtras();

    initViews();
    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
    }
    btnHome.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", HomeActivity.getEmail());
            bundle.putString("WifiLogin", HomeActivity.getSsid());
            bundle.putString("WifiPassword", HomeActivity.getPassword());
            Intent intent = new Intent(TaskAdditionActivity.this, HomeActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
    btnControl.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(TaskAdditionActivity.this, TaskActivity.class);
            startActivity(intent);
          }
        });
    btnAccount.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(TaskAdditionActivity.this, AccountActivity.class);
            startActivity(intent);
          }
        });
    showTasks();

    btnAdd.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString("DeviceId", devId);
            bundle.putString("DeviceName", devName);
            bundle.putString("ProductId", prodId);
            bundle.putString("Category", category);
            Intent intent = new Intent(TaskAdditionActivity.this, SimpleTaskActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }
        });
  }

  private void initializeData() {
    TuyaHomeSdk.getSceneManagerInstance()
        .getSceneList(
            HomeActivity.getHomeId(),
            new ITuyaResultCallback<List<SceneBean>>() {
              @Override
              public void onSuccess(List<SceneBean> result) {
                scenes = result;
              }

              @Override
              public void onError(String errorCode, String errorMessage) {}
            });
  }

  private void initializeAdapter() {
    RVAdapterTasks adapter = new RVAdapterTasks(scenes);
    rv_tasks.setAdapter(adapter);
    adapter.setOnItemClickListener(
        new RVAdapterTasks.ClickListener() {
          @Override
          public void onItemClick(int position, View v) {
            Bundle bundle = new Bundle();
            bundle.putString("SceneId", scenes.get(position).getId());
            Intent intent = new Intent(TaskAdditionActivity.this, SceneInfoActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
          }

          @Override
          public void onItemLongClick(int position, View v) {}
        });
  }

  private void showTasks() {
    rv_tasks = findViewById(R.id.rv_tasks);

    LinearLayoutManager llm = new LinearLayoutManager(this);
    rv_tasks.setLayoutManager(llm);
    rv_tasks.setHasFixedSize(true);

    initializeData();
    initializeAdapter();
  }

  private void initViews() {
    btnAdd = findViewById(R.id.btnAdd);

    btnAccount = findViewById(R.id.btnAccount);
    btnControl = findViewById(R.id.btnControl);
    btnHome = findViewById(R.id.btnHome);
  }
}

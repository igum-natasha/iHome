package com.example.ihome_cw;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdditionActivity extends AppCompatActivity {

  String devId, devName, prodId, category;

  private Button btnAddTask;
  private static List<SceneBean> scenes = new ArrayList<>();
  private RecyclerView rv_tasks;
  ImageButton btnAdd;
  CircleImageView btnAccount;
  Dialog addDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_addition);

    Bundle bundle = getIntent().getExtras();
    defineAddDialog();
    initViews();
    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
    }

    btnAdd.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.show();
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
                Intent intent = new Intent(TaskAdditionActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
              case R.id.control:
                startActivity(new Intent(TaskAdditionActivity.this, TaskActivity.class));
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
    showTasks();

    btnAddTask.setOnClickListener(
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
                scenes.addAll(result);
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

  private void defineAddDialog() {
    addDialog = new Dialog(TaskAdditionActivity.this);
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
            Intent intent = new Intent(TaskAdditionActivity.this, HomeActivity.class); // ?
            startActivity(intent);
          }
        });
    LinearLayout addTask = addDialog.findViewById(R.id.btnAddNewTask);
    addTask.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            addDialog.dismiss();
            Intent intent = new Intent(TaskAdditionActivity.this, TaskActivity.class); // ?
            startActivity(intent);
          }
        });
  }

  private void initViews() {

    btnAdd = findViewById(R.id.plus_icon);
    btnAccount = findViewById(R.id.avatar_icon);

    btnAddTask = findViewById(R.id.btnAdd);
  }
}

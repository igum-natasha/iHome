package com.example.ihome_cw;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdditionActivity extends AppCompatActivity {

  String devId, devName, prodId, category;

  private Button btnAddTask;
  private static List<Scene> scenes = new ArrayList<>();
  private static List<Device> devices = new ArrayList<>();
  private static List<Scene> rec = new ArrayList<>();
  private RecyclerView rv_tasks, rv_rec;
  ImageButton btnAdd;
  TextView tvInfoRec, tvInfoTask;
  CircleImageView btnAccount;
  Dialog addDialog, sceneDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_addition);

    Bundle bundle = getIntent().getExtras();
    defineAddDialog();
    initViews();
    tvInfoTask.setVisibility(View.INVISIBLE);
    tvInfoRec.setVisibility(View.INVISIBLE);
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
    showRec();
  }

  private void initializeData() {
    AppDatabase db = AppDatabase.build(getApplicationContext());
    scenes = db.sceneDao().getWithoutRec();
    if (scenes.isEmpty()) {
      tvInfoTask.setVisibility(View.VISIBLE);
    }
  }

  private void initializeAdapter() {
    RVAdapterTasks adapter = new RVAdapterTasks(scenes);
    rv_tasks.setAdapter(adapter);
    adapter.setOnItemClickListener(
        new RVAdapterTasks.ClickListener() {
          @Override
          public void onItemClick(int position, View v) {
            defineSceneDialog(scenes.get(position).getSceneId());
            sceneDialog.show();
          }

          @Override
          public void onItemLongClick(int position, View v) {}
        });
  }

  private void showTasks() {
    rv_tasks = findViewById(R.id.rv_tasks);

    GridLayoutManager llm = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
    rv_tasks.setLayoutManager(llm);
    //    rv_tasks.setHasFixedSize(true);

    initializeData();
    initializeAdapter();
  }

  private void initializeRec() {
    AppDatabase db = AppDatabase.build(getApplicationContext());
    rec = db.sceneDao().getRec();
    if (rec.isEmpty()) {
      tvInfoRec.setVisibility(View.VISIBLE);
    }
  }

  private void initializeAdapterRec() {
    RVAdapterRecommend adapter = new RVAdapterRecommend(rec);
    rv_rec.setAdapter(adapter);
    adapter.setOnItemClickListener(
        new RVAdapterRecommend.ClickListener() {
          @Override
          public void onItemClick(int position, View v) {
            defineSceneDialog(rec.get(position).getSceneId());
            sceneDialog.show();
          }

          @Override
          public void onItemLongClick(int position, View v) {}
        });
  }

  private void showRec() {
    rv_rec = findViewById(R.id.rv_rec);

    LinearLayoutManager llm = new LinearLayoutManager(this);
    rv_rec.setLayoutManager(llm);

    initializeRec();
    initializeAdapterRec();
  }

  private void defineAddDialog() {
    addDialog = new Dialog(TaskAdditionActivity.this);
    addDialog.setContentView(R.layout.add_dialog);
    addDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    addDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

  @SuppressLint("NewApi")
  private void defineSceneDialog(String sceneId) {
    sceneDialog = new Dialog(TaskAdditionActivity.this);
    sceneDialog.setContentView(R.layout.scene_dialog);
    sceneDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_dialog));
    sceneDialog
        .getWindow()
        .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    sceneDialog.getWindow().setGravity(Gravity.CENTER);
    sceneDialog.setCancelable(false);
    TextView tvName = sceneDialog.findViewById(R.id.tvSceneName);
    TextView tvDevName = sceneDialog.findViewById(R.id.tvDeviceName);
    TextView tvRepeat = sceneDialog.findViewById(R.id.tvSceneRepeat);
    TextView tvTime = sceneDialog.findViewById(R.id.tvSceneTime);
    TextView tvCondition = sceneDialog.findViewById(R.id.tvSceneCondition);
    AppDatabase db = AppDatabase.build(getApplicationContext());
    Scene sc = db.sceneDao().selectById(sceneId);
    Device dev = db.deviceDao().selectById(sc.getDeviceId());
    tvName.setText(sc.getSceneName());
    tvDevName.setText(dev.getDeviceName());
    String repeat = sc.getRepeat();
    List<String> week = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
    String res = "";
    char[] list = repeat.toCharArray();
    for (int i = 0; i < list.length; i++) {
      if (list[i] == '1') {
        res = res + week.get(i) + " ";
      }
    }
    tvRepeat.setText(res);
    tvTime.setText(sc.getTime());
    tvCondition.setText(sc.getCondition());
    Button cancel = sceneDialog.findViewById(R.id.btnCancel);
    cancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sceneDialog.dismiss();
          }
        });
    Button delete = sceneDialog.findViewById(R.id.btnDelete);
    delete.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            sceneDialog.dismiss();
            TuyaHomeSdk.newSceneInstance(sceneId)
                .deleteScene(
                    new IResultCallback() {
                      @Override
                      public void onSuccess() {
                        Toast.makeText(
                                TaskAdditionActivity.this,
                                "Delete Scene Success",
                                Toast.LENGTH_LONG)
                            .show();
                      }

                      @Override
                      public void onError(String errorCode, String errorMessage) {}
                    });
            db.sceneDao().deleteById(sceneId);
            Intent intent = new Intent(TaskAdditionActivity.this, TaskAdditionActivity.class);
            startActivity(intent);
          }
        });
  }

  private void initViews() {

    btnAdd = findViewById(R.id.plus_icon);
    btnAccount = findViewById(R.id.avatar_icon);
    tvInfoRec = findViewById(R.id.tvInfoRec);
    tvInfoTask = findViewById(R.id.tvInfoTasks);
  }
}

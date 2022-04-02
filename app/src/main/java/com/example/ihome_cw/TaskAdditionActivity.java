package com.example.ihome_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.PreCondition;
import com.tuya.smart.home.sdk.bean.scene.PreConditionExpr;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.TimerRule;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class TaskAdditionActivity extends AppCompatActivity {

  String devId, devName, prodId, category;
  private EditText etTime, etDate, etName;
  private Button btnAdd;
  private List<SceneTask> tasks = new ArrayList<>();
  ;
  private List<SceneBean> scenes = new ArrayList<>();
  ;
  private List<SceneCondition> conditions = new ArrayList<>();
  private RecyclerView rv_tasks;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_addition);

    Bundle bundle = getIntent().getExtras();

    //        initViews();
    btnAdd = findViewById(R.id.btnAdd);
    if (bundle != null) {
      devId = bundle.getString("DeviceId");
      devName = bundle.getString("DeviceName");
      prodId = bundle.getString("ProductId");
      category = bundle.getString("Category");
    }
    showTasks();

    btnAdd.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            //                String Date = etDate.getText().toString();
            //                String Time = etTime.getText().toString();
            //                String Name = etName.getText().toString();
            JSONObject dps = new JSONObject();
            try {
              dps.put("1", true);
            } catch (JSONException e) {
              e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject();
            try {
              jsonObject.put("time", "00:00"); // Time);
              jsonObject.put("dps", dps);
            } catch (JSONException e) {
              e.printStackTrace();
            }
            SceneTask task =
                TuyaHomeSdk.getSceneManagerInstance()
                    .createDelayTask(
                        2, // The number of minutes.
                        2 // The number of seconds.
                        );
            tasks.add(task);
            TimerRule timerRule =
                TimerRule.newInstance("Europe/Moscow", "1111111", "18:17", "20220402");
            SceneCondition condition =
                SceneCondition.createTimerCondition(
                    "Monday, Tuesday, Wednesday, Thursday, Friday",
                    "Scheduled for weekday",
                    "timer",
                    timerRule);
            conditions.add(condition);

            PreCondition preCondition = new PreCondition();
            PreConditionExpr expr = new PreConditionExpr();
            expr.setCityName("Moscow");
            expr.setStart("00:00");
            expr.setEnd("23:59");
            expr.setLoops("1111111");
            expr.setTimeInterval(PreCondition.TIMEINTERVAL_ALLDAY);
            preCondition.setCondType(PreCondition.TYPE_TIME_CHECK);
            expr.setTimeZoneId(TimeZone.getDefault().getID());
            preCondition.setExpr(expr);
            List<PreCondition> preConditions = new ArrayList<>();
            preConditions.add(preCondition);

            TuyaHomeSdk.getSceneManagerInstance()
                .createScene(
                    HomeActivity.getHomeId(),
                    "Morning", // The name of the scene.
                    false,
                    "", // Indicates whether the scene is displayed on the homepage.
                    conditions, // The conditions.
                    tasks, // The tasks.
                    preConditions, // The effective period. This parameter is optional.
                    SceneBean.MATCH_TYPE_AND, // The type of trigger conditions to match.
                    new ITuyaResultCallback<SceneBean>() {
                      @Override
                      public void onSuccess(SceneBean sceneBean) {
                        Toast.makeText(TaskAdditionActivity.this, "successful!", Toast.LENGTH_LONG)
                            .show();
                        showTasks();
                      }

                      @Override
                      public void onError(String errorCode, String errorMessage) {}
                    });
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
                //                Toast.makeText(TaskAdditionActivity.this, scenes.size(),
                // Toast.LENGTH_LONG)
                //                        .show();
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
  //    private void initViews() {
  //        etDate = findViewById(R.id.etDate);
  //        etTime = findViewById(R.id.etTime);
  //        btnAdd = findViewById(R.id.btnAdd);
  //        etName = findViewById(R.id.etName);
  //    }
}

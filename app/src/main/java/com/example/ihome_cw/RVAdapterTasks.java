package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.List;

public class RVAdapterTasks extends RecyclerView.Adapter<RVAdapterTasks.TaskViewHolder> {
  private static ClickListener clickListener;

  public static class TaskViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener, View.OnLongClickListener {

    CardView cv;
    TextView taskName;
    ImageView image;
    Switch sw;

    TaskViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);
      itemView.setOnLongClickListener(this);
      cv = itemView.findViewById(R.id.cv_tasks);
      taskName = itemView.findViewById(R.id.task_name);
      image = itemView.findViewById(R.id.task_photo);
      sw = itemView.findViewById(R.id.sw);
    }

    @Override
    public void onClick(View v) {
      clickListener.onItemClick(getAdapterPosition(), v);
    }

    @Override
    public boolean onLongClick(View v) {
      clickListener.onItemLongClick(getAdapterPosition(), v);
      return false;
    }
  }

  List<Scene> tasks;

  RVAdapterTasks(List<Scene> tasks) {
    this.tasks = tasks;
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }

  @Override
  public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v =
        LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tasks, viewGroup, false);
    TaskViewHolder task = new TaskViewHolder(v);
    return task;
  }

  @Override
  public void onBindViewHolder(TaskViewHolder taskViewHolder, @SuppressLint("RecyclerView") int i) {
    taskViewHolder.taskName.setText(tasks.get(i).getSceneName());
    taskViewHolder.image.setBackgroundResource(tasks.get(i).getImage());
    TuyaHomeSdk.getSceneManagerInstance().getSceneDetail(HomeActivity.getHomeId(), tasks.get(i).sceneId, new ITuyaResultCallback<SceneBean>() {
      @Override
      public void onSuccess(SceneBean result) {
        if (result.isEnabled()) {
          taskViewHolder.sw.setChecked(result.isEnabled());
        }
      }

      @Override
      public void onError(String errorCode, String errorMessage) {

      }
    });
    taskViewHolder.sw.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String id = tasks.get(i).sceneId;
            if (b) {
              TuyaHomeSdk.newSceneInstance(id)
                  .enableScene(
                      id,
                      new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {}
                        @SuppressLint("NewApi")
                        @Override
                        public void onSuccess() {
                          taskViewHolder.cv.setBackgroundColor(compoundButton.getContext().getColor(R.color.primary_50));
                        }
                      });
            } else {
              TuyaHomeSdk.newSceneInstance(id)
                  .disableScene(
                      id,
                      new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {}

                        @SuppressLint("NewApi")
                        @Override
                        public void onSuccess() {
                          taskViewHolder.cv.setBackgroundColor(compoundButton.getContext().getColor(R.color.base_50));
                        }
                      });
            }
          }
        });
  }

  @Override
  public int getItemCount() {
    return tasks.size();
  }

  public void setOnItemClickListener(ClickListener clickListener) {
    RVAdapterTasks.clickListener = clickListener;
  }

  public interface ClickListener {
    void onItemClick(int position, View v);

    void onItemLongClick(int position, View v);
  }
}

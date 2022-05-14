package com.example.ihome_cw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.List;

public class RVAdapterRecommend extends RecyclerView.Adapter<RVAdapterRecommend.TaskViewHolder> {
  private static RVAdapterRecommend.ClickListener clickListener;

  public static class TaskViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener, View.OnLongClickListener {

    CardView cv;
    TextView taskName;
    ImageView image;
    ImageButton plus;

    TaskViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);
      itemView.setOnLongClickListener(this);
      cv = itemView.findViewById(R.id.cv_rec);
      taskName = itemView.findViewById(R.id.name);
      image = itemView.findViewById(R.id.icon);
      plus = itemView.findViewById(R.id.plus);
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

  RVAdapterRecommend(List<Scene> tasks) {
    this.tasks = tasks;
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }

  @Override
  public RVAdapterRecommend.TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v =
        LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rec, viewGroup, false);
    RVAdapterRecommend.TaskViewHolder task = new RVAdapterRecommend.TaskViewHolder(v);
    return task;
  }

  @Override
  public void onBindViewHolder(
      RVAdapterRecommend.TaskViewHolder taskViewHolder, @SuppressLint("RecyclerView") int i) {
    taskViewHolder.taskName.setText(tasks.get(i).getSceneName());
    taskViewHolder.image.setBackgroundResource(tasks.get(i).getImage());
    taskViewHolder.plus.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String id = tasks.get(i).sceneId;
            TuyaHomeSdk.newSceneInstance(id)
                .enableScene(
                    id,
                    new IResultCallback() {
                      @Override
                      public void onError(String code, String error) {}

                      @Override
                      public void onSuccess() {
                        AppDatabase db = AppDatabase.build(view.getContext());
                        Scene scene = db.sceneDao().selectById(id);
                        String oldName = scene.getSceneName();
                        scene.setSceneName(oldName.substring(5));
                        db.sceneDao().insertScene(scene);
                        Intent intent =
                            new Intent(
                                taskViewHolder.plus.getContext(), TaskAdditionActivity.class); // ?
                        taskViewHolder.plus.getContext().startActivity(intent);
                      }
                    });
          }
        });
  }

  @Override
  public int getItemCount() {
    return tasks.size();
  }

  public void setOnItemClickListener(RVAdapterRecommend.ClickListener clickListener) {
    RVAdapterRecommend.clickListener = clickListener;
  }

  public interface ClickListener {
    void onItemClick(int position, View v);

    void onItemLongClick(int position, View v);
  }
}

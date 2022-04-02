package com.example.ihome_cw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.home.sdk.bean.scene.SceneBean;

import java.util.List;

public class RVAdapterTasks extends RecyclerView.Adapter<RVAdapterTasks.TaskViewHolder> {
    private static ClickListener clickListener;

    public static class TaskViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        CardView cv;
        TextView taskName;
        TextView deviceId;

        TaskViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            cv = itemView.findViewById(R.id.cv_tasks);
            taskName = itemView.findViewById(R.id.task_name);
            deviceId = itemView.findViewById(R.id.device_id_tasks);
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

    List<SceneBean> tasks;

    RVAdapterTasks(List<SceneBean> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tasks, viewGroup, false);
        TaskViewHolder task = new TaskViewHolder(v);
        return task;
    }

    @Override
    public void onBindViewHolder(TaskViewHolder taskViewHolder, int i) {
        taskViewHolder.taskName.setText(tasks.get(i).getName());
        taskViewHolder.deviceId.setText(tasks.get(i).getId());
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
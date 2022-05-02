package com.example.ihome_cw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DeviceViewHolder> {
  private static ClickListener clickListener;

  public static class DeviceViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener, View.OnLongClickListener {

    CardView cv;
    TextView deviceName;
    ImageView devicePhoto;

    DeviceViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);
      itemView.setOnLongClickListener(this);
      cv = itemView.findViewById(R.id.cv);
      deviceName = itemView.findViewById(R.id.deviceName);
      devicePhoto = itemView.findViewById(R.id.devicePhoto);
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

  List<Device> devices;

  RVAdapter(List<Device> devices) {
    this.devices = devices;
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }

  @Override
  public DeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
    DeviceViewHolder dev = new DeviceViewHolder(v);
    return dev;
  }

  @Override
  public void onBindViewHolder(DeviceViewHolder deviceViewHolder, int i) {
    deviceViewHolder.deviceName.setText(devices.get(i).getDeviceName());
    deviceViewHolder.devicePhoto.setBackgroundResource(R.drawable.lightbulb);
  }

  @Override
  public int getItemCount() {
    return devices.size();
  }

  public void setOnItemClickListener(ClickListener clickListener) {
    RVAdapter.clickListener = clickListener;
  }

  public interface ClickListener {
    void onItemClick(int position, View v);

    void onItemLongClick(int position, View v);
  }
}

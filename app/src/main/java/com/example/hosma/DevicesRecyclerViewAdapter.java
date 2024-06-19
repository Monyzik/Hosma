package com.example.hosma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder> {

    public interface OnItemSwitchListener {
        void OnItemSwitch(Device device, boolean isChecked);
    }
    private ArrayList <Device> devices;

    OnItemSwitchListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView deviceImage;
        private final SwitchMaterial deviceSwitcher;

        private final TextView deviceNameTextView;

        public ViewHolder(View view) {
            super(view);

            deviceImage = view.findViewById(R.id.device_image);
            deviceSwitcher = view.findViewById(R.id.device_switch_material_button);
            deviceNameTextView = view.findViewById(R.id.device_name);

        }

        public void bind(final Device device, final OnItemSwitchListener listener) {
            deviceSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.OnItemSwitch(device, isChecked);
                    if (isChecked) {
                        deviceImage.setImageResource(R.drawable.baseline_lightbulb_yellow);
                    } else {
                        deviceImage.setImageResource(R.drawable.baseline_lightbulb);
                    }
                }
            });
        }

        public TextView getDeviceNameTextView() {
            return deviceNameTextView;
        }

        public ImageView getDeviceImage() {
            return deviceImage;
        }

        public SwitchMaterial getDeviceSwitcher() {
            return deviceSwitcher;
        }
    }

    public DevicesRecyclerViewAdapter(ArrayList <Device> newDevices, OnItemSwitchListener listener) {
        devices = newDevices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.devise_item_for_devises_recycler_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.getDeviceImage().setImageResource(R.drawable.baseline_lightbulb);
        holder.getDeviceNameTextView().setText(device.getName());

        holder.bind(device, listener);

//        holder.getDeviceSwitcher().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    holder.getDeviceImage().setImageResource(R.drawable.baseline_lightbulb_yellow);
//                } else {
//                    holder.getDeviceImage().setImageResource(R.drawable.baseline_lightbulb);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}

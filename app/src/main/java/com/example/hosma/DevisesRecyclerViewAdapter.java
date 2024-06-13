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

public class DevisesRecyclerViewAdapter extends RecyclerView.Adapter<DevisesRecyclerViewAdapter.ViewHolder> {
    private ArrayList <Devise> devises;

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

    public DevisesRecyclerViewAdapter(ArrayList <Devise> newDevices) {
        devises = newDevices;
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
        Devise devise = devises.get(position);
        holder.getDeviceImage().setImageResource(R.drawable.baseline_lightbulb);
        holder.getDeviceNameTextView().setText(devise.getName());

        holder.getDeviceSwitcher().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.getDeviceImage().setImageResource(R.drawable.baseline_lightbulb_yellow);
                } else {
                    holder.getDeviceImage().setImageResource(R.drawable.baseline_lightbulb);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return devises.size();
    }
}

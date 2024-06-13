package com.example.hosma;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DevisesRecyclerViewAdapter extends RecyclerView.Adapter<DevisesRecyclerViewAdapter.ViewHolder> {
    private ArrayList <Devise> devises;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView deviceImage;

        public ViewHolder(View view) {
            super(view);

            deviceImage = view.findViewById(R.id.device_image);
        }

        public ImageView getDeviceImage() {
            return deviceImage;
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
        holder.getDeviceImage().setImageResource(R.drawable.baseline_lightbulb);
    }

    @Override
    public int getItemCount() {
        return devises.size();
    }
}

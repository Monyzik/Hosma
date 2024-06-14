package com.example.hosma;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class SearchArduinoAdapter extends RecyclerView.Adapter<SearchArduinoAdapter.ViewHolder> {

    ArrayList<BluetoothDevice> arrayList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceNameTextView;

        public ViewHolder(View view) {
            super(view);
            deviceNameTextView = view.findViewById(R.id.bluetooth_device_name);

        }

        public TextView getDeviceNameTextView() {
            return deviceNameTextView;
        }

    }
    public SearchArduinoAdapter(ArrayList<BluetoothDevice> newArrayList) {
        arrayList = newArrayList;
    }

    @NonNull
    @Override
    public SearchArduinoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_for_bluetooth_devices, parent, false);
        return new SearchArduinoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchArduinoAdapter.ViewHolder holder, int position) {
        try {
            holder.getDeviceNameTextView().setText(arrayList.get(holder.getAdapterPosition()).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setCancelable(true);
                    builder.setTitle(arrayList.get(holder.getAdapterPosition()).getName());
                    builder.setMessage("Mac adress " + arrayList.get(holder.getAdapterPosition()).getAddress());
                    builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        } catch (SecurityException e) {
            Log.e("e", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}

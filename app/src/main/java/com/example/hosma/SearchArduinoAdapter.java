package com.example.hosma;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

public class SearchArduinoAdapter extends RecyclerView.Adapter<SearchArduinoAdapter.ViewHolder> {

    ArrayList<BluetoothDevice> arrayList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceNameTextView;
        private final TextView isconnectedTextView;

        public ViewHolder(View view) {
            super(view);
            deviceNameTextView = view.findViewById(R.id.bluetooth_device_name);
            isconnectedTextView = view.findViewById(R.id.is_connected);

        }

        public TextView getDeviceNameTextView() {
            return deviceNameTextView;
        }

        public TextView getIsconnectedTextView() {
            return isconnectedTextView;
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
            if (isConnected(arrayList.get(holder.getAdapterPosition()))) {
                holder.getIsconnectedTextView().setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setCancelable(true);
                        builder.setTitle(arrayList.get(holder.getAdapterPosition()).getName());
                        builder.setMessage("Mac adress " + arrayList.get(holder.getAdapterPosition()).getAddress());
                        builder.setPositiveButton("Disconnect", new DialogInterface.OnClickListener() {
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
            }
            else {
                holder.getIsconnectedTextView().setVisibility(View.INVISIBLE);
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
                                //крутой код
//                                arrayList.get(holder.getAdapterPosition()).createBond();
//
//                                ParcelUuid[] uuids = arrayList.get(holder.getAdapterPosition()).getUuids();
//                                try {
//                                    BluetoothSocket socket = arrayList.get(holder.getAdapterPosition()).createRfcommSocketToServiceRecord(uuids[0].getUuid());
//                                    socket.connect();
//                                } catch (IOException e) {
//                                    throw new RuntimeException(e);
//                                }


                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //крутой код
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("e", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


}

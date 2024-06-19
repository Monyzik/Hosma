package com.example.hosma;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice item);
    }

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothSocket socket;

    OnItemClickListener listener;

    ArrayList<BluetoothDevice> arrayList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceNameTextView;
        private final TextView isconnectedTextView;

        public ViewHolder(View view) {
            super(view);
            deviceNameTextView = view.findViewById(R.id.bluetooth_device_name);
            isconnectedTextView = view.findViewById(R.id.is_connected);

        }

        public void bind(final BluetoothDevice item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public TextView getDeviceNameTextView() {
            return deviceNameTextView;
        }

        public TextView getIsconnectedTextView() {
            return isconnectedTextView;
        }
    }
    public SearchArduinoAdapter(ArrayList<BluetoothDevice> newArrayList, OnItemClickListener listener) {
        this.arrayList = newArrayList;
        this.listener = listener;
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
        holder.bind(arrayList.get(position), listener);
        try {
            BluetoothDevice device = arrayList.get(holder.getAdapterPosition());
            holder.getDeviceNameTextView().setText(device.getName());
            if (isConnected(device)) {
                holder.getIsconnectedTextView().setVisibility(View.VISIBLE);
            } else {
                holder.getIsconnectedTextView().setVisibility(View.INVISIBLE);
            }
        } catch (SecurityException e) {
            Log.e("E", e.getMessage());
        }
//        try {
//            holder.getDeviceNameTextView().setText(arrayList.get(holder.getAdapterPosition()).getName());
//            if (isConnected(arrayList.get(holder.getAdapterPosition()))) {
//                holder.getIsconnectedTextView().setVisibility(View.VISIBLE);
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                        builder.setCancelable(true);
//                        builder.setTitle(arrayList.get(holder.getAdapterPosition()).getName());
//                        builder.setMessage("Mac adress " + arrayList.get(holder.getAdapterPosition()).getAddress());
//                        builder.setPositiveButton("Disconnect", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.show();
//                    }
//                });
//            }
//            else {
//                holder.getIsconnectedTextView().setVisibility(View.INVISIBLE);
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                        builder.setCancelable(true);
//                        builder.setTitle(arrayList.get(holder.getAdapterPosition()).getName());
//                        builder.setMessage("Mac adress " + arrayList.get(holder.getAdapterPosition()).getAddress());
//                        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                BluetoothDevice device = arrayList.get(holder.getAdapterPosition());
//                                //крутой код
//                                device.createBond();
//                                System.out.println(device.getAddress());
//                                try {
//                                    socket = device.createRfcommSocketToServiceRecord(MY_UUID);
//                                } catch (IOException e) {
//                                    throw new RuntimeException(e);
//                                }
//                                // Establish the connection.  This will block until it connects.
//                                Log.d("11", "...Соединяемся...");
//                                try {
//                                    socket.connect();
//                                    Log.d("11", "...Соединение установлено и готово к передачи данных...");
//                                    Intent i = new Intent(v.getContext(), MainActivity.class);
//
//                                } catch (IOException e) {
//                                    try {
//                                        socket.close();
//                                    } catch (IOException e2) {
//                                        Log.e("111", e2.getMessage());
//                                    }
//                                }
//                            }
//                        });
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //крутой код
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.show();
//                    }
//                });
//            }
//        } catch (SecurityException e) {
//            Log.e("e", e.getMessage());
//        }

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

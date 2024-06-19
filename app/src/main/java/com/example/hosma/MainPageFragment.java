package com.example.hosma;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class MainPageFragment extends Fragment implements ServiceConnection, SerialListener {

    private static final int REQUEST_CONNECT_DEVICE = 1;

    private enum Connected { False, Pending, True }

    Connected connected = Connected.False;

    BluetoothArduinoService service;


    boolean initialStart = true;


    FloatingActionButton addDeviceButton;
    RecyclerView devisesRecyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;

    BluetoothArduinoService bluetoothArduinoService;
    ArrayList<Device> devices = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;

    FloatingActionButton search_arduino_btn;

    OutputStream outputStream;

    Bundle arguments;

    String deviceAddress;



    public MainPageFragment() {
        super(R.layout.main_page_fragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deviceAddress = getArguments().getString("device");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addDeviceButton = view.findViewById(R.id.add_new_device_button);
        search_arduino_btn = view.findViewById(R.id.search_ard_btn);

        devisesRecyclerView = view.findViewById(R.id.devises_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(devices, new DevicesRecyclerViewAdapter.OnItemSwitchListener() {
            @Override
            public void OnItemSwitch(Device device, boolean isChecked) {
                if (isChecked) {
                    send("1");
                } else {
                    send("2");
                }
            }
        });
        devisesRecyclerView.setLayoutManager(layoutManager);
        devisesRecyclerView.setAdapter(devicesRecyclerViewAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devices.add(new Device(1231, "Cats..."));
                devicesRecyclerViewAdapter.notifyItemInserted(devices.size() - 1);
            }
        });

        search_arduino_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getContext(), SearchArduino.class);
//                startActivityForResult(i, REQUEST_CONNECT_DEVICE);
                getParentFragmentManager().beginTransaction().hide(MainPageFragment.this).commit();
                getParentFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_view, SearchArduino.class, null)
                        .commit();

            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        requireActivity().bindService(new Intent(getActivity(), BluetoothArduinoService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try {
            requireActivity().unbindService(this);
        } catch(Exception ignored) {}
        super.onDetach();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (service != null) {
            service.attach(this);
        } else {
            requireActivity().startService(new Intent(requireActivity(), BluetoothArduinoService.class));
        }
    }

    @Override
    public void onStop() {
        if (service != null && !requireActivity().isChangingConfigurations()) {
            service.detach();
        }
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (service != null && initialStart) {
            initialStart = false;
            requireActivity().runOnUiThread(this::connect);
        }
    }

    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("connecting...");
            connected = Connected.Pending;
            BluetoothArduinoSerialSocket socket = new BluetoothArduinoSerialSocket(requireActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    private void send(String message) {
        if(connected != Connected.True) {
            Toast.makeText(requireActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            service.write(message.getBytes());
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(ArrayDeque<byte[]> datas) {
    }

    private void status(String str) {
        Toast.makeText(service, str, Toast.LENGTH_SHORT).show();
    }

    private void showNotificationSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", requireActivity().getPackageName());
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Arrays.equals(permissions, new String[]{Manifest.permission.POST_NOTIFICATIONS}) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !service.areNotificationsEnabled())
            showNotificationSettings();
    }

    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        ArrayDeque<byte[]> datas = new ArrayDeque<>();
        datas.add(data);
        receive(datas);
    }

    public void onSerialRead(ArrayDeque<byte[]> datas) {
        receive(datas);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((BluetoothArduinoService.SerialBinder)binder).getService();
        service.attach(this);
        if (initialStart && isResumed()) {
            initialStart = false;
            requireActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }




}

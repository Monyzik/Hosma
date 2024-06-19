package com.example.hosma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ServiceConnection, SerialListener {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_CONNECT_DEVICE = 1;

    private enum Connected { False, Pending, True }

    Connected connected = Connected.False;

    boolean initialStart = true;

    FloatingActionButton addDeviceButton;

    BluetoothArduinoService service;
    String deviceAddress;

    RecyclerView devisesRecyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;

    BluetoothArduinoService bluetoothArduinoService;

    ArrayList<Device> devices = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;

    Button search_arduino_btn;

    OutputStream outputStream;

    Bundle arguments;

    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addDeviceButton = findViewById(R.id.add_new_device_button);
        search_arduino_btn = findViewById(R.id.search_ard_btn);

        devisesRecyclerView = findViewById(R.id.devises_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(devices);
        devisesRecyclerView.setLayoutManager(layoutManager);
        devisesRecyclerView.setAdapter(devicesRecyclerViewAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.bindService(new Intent(this, BluetoothArduinoService.class), this, Context.BIND_AUTO_CREATE);



        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devices.add(new Device(1231, "Cats..."));
                devicesRecyclerViewAdapter.notifyItemInserted(devices.size() - 1);
                send("1");
            }
        });

        search_arduino_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchArduino.class);
                startActivityForResult(i, REQUEST_CONNECT_DEVICE);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (service != null) {
            service.attach(this);
        } else {
            this.startService(new Intent(this, BluetoothArduinoService.class));
        }
    }

    @Override
    protected void onStop() {
        if (service != null && !this.isChangingConfigurations()) {
            service.detach();
        }
        super.onStop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (service != null && initialStart) {
            initialStart = false;
            this.runOnUiThread(this::connect);
        }
    }

    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("connecting...");
            connected = Connected.Pending;
            BluetoothArduinoSerialSocket socket = new BluetoothArduinoSerialSocket(this.getApplicationContext(), device);
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
            Toast.makeText(
                    this, "not connected", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("android.provider.extra.APP_PACKAGE", MainActivity.this.getPackageName());
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        switch (requestCode) {
            case 2793:
                Toast.makeText(MainActivity.this, "Bluetooth is ON", Toast.LENGTH_SHORT).show();
                break;
            case REQUEST_CONNECT_DEVICE:
                deviceAddress = data.getStringExtra(SearchArduino.EXTRA_DEVICE_ADDRESS);
                this.runOnUiThread(this::connect);
                System.out.println("conected");
                break;
            case RESULT_CANCELED:
                Toast.makeText(MainActivity.this, ":(", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((BluetoothArduinoService.SerialBinder)binder).getService();
        service.attach(this);
        if (initialStart) {
            initialStart = false;
            this.runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }
//
//    private void connectDevice(Intent data) {
//        Bundle extras = data.getExtras();
//        if (extras == null) return;
//        String address = extras.getString(SearchArduino.EXTRA_DEVICE_ADDRESS);
//        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
//        try {
//            bluetoothArduinoService = new BluetoothArduinoService(this, mHandler);
//            System.out.println(device.getName());
//        } catch (SecurityException e) {
//            Log.e("Security exeption", e.getMessage());
//        }
//        bluetoothArduinoService.connect(device);
//        sendMessage("1");
//
//    }

//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothArduinoService.STATE_CONNECTED:
//                            break;
//                        case BluetoothArduinoService.STATE_CONNECTING:
//                            break;
//                        case BluetoothArduinoService.STATE_LISTEN:
//                        case BluetoothArduinoService.STATE_NONE:
//                            break;
//                    }
//                    break;
//                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    break;
//                case Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    String mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    if (null != MainActivity.this) {
//                        Toast.makeText(MainActivity.this, "Connected to "
//                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    if (null != MainActivity.this) {
//                        Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//            }
//        }
//    };

//    private void sendMessage(String message) {
//        if (bluetoothArduinoService.getState() != BluetoothArduinoService.STATE_CONNECTED) {
//            return;
//        }
//        if (message.length() > 0) {
//            byte[] bytes = message.getBytes();
//            bluetoothArduinoService.write(bytes);
//        }
//    }
//
//    public static boolean isConnected(BluetoothDevice device) {
//        try {
//            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
//            boolean connected = (boolean) m.invoke(device, (Object[]) null);
//            return connected;
//        } catch (Exception e) {
//            throw new IllegalStateException(e);
//        }
//    }



}
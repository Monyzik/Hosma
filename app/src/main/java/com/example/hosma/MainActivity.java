package com.example.hosma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
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
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_CONNECT_DEVICE = 1;

    private enum Connected { False, Pending, True }

    Connected connected = Connected.False;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        MainPageFragment mainPageFragment = new MainPageFragment(new MainActivityInterface() {
//            @Override
//            public ServiceConnection getServiceConnection() {
//                return (ServiceConnection) MainActivity.this;
//            }
//
//            @Override
//            public void sendFunction(String message) {
//                send(message);
//            }
//        });
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view, MainPageFragment.class, null)
                .commit();


    }
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getFragments().size();

        if (count == 1) {
            super.onBackPressed();
        }
        else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            getSupportFragmentManager().beginTransaction().show(fragments.get(fragments.size() - 2)).commit();
            getSupportFragmentManager().beginTransaction().remove(fragments.get(fragments.size() - 1)).commit();
        }

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
                System.out.println("conected");
                break;
            case RESULT_CANCELED:
                Toast.makeText(MainActivity.this, ":(", Toast.LENGTH_SHORT).show();
        }

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
package com.example.hosma;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchArduino extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    Button refresh_btn;
    Button back_btn;
    RecyclerView availableRecyclerView;
    SearchArduinoAdapter availableAdapter;
    ArrayList<BluetoothDevice> availableArrayList = new ArrayList<>();

    Activity activity;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = "";
                try {
                    deviceName = device.getName();
                } catch (SecurityException e) {
                    Log.e("e", e.getMessage());
                }
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null) availableArrayList.add(device);
//                adapter.notifyItemInserted(arrayList.size() - 1);
                availableAdapter.notifyDataSetChanged();
                System.out.println(deviceName);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_arduino);

        activity = this;

        refresh_btn = findViewById(R.id.refresh_btn);
        back_btn = findViewById(R.id.back_btn);


        availableRecyclerView = findViewById(R.id.rv_bluetooth_devices);
        availableAdapter = new SearchArduinoAdapter(availableArrayList);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothManager = getSystemService(BluetoothManager.class);



        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Ваше устройство не поддерживает Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2793);
        }



        availableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        availableRecyclerView.setAdapter(availableAdapter);

        searchDevices();


        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDevices();
            }
        });


    }

    public void searchDevices() {
        availableArrayList.clear();
        availableAdapter.notifyDataSetChanged();
        requestBluetoothPermissions();
        try {
            bluetoothAdapter.startDiscovery();
        } catch (SecurityException e) {
            Log.e("e", e.getMessage());
        }
//        try {
//            for (BluetoothDevice device: bluetoothAdapter.getBondedDevices()) {
//                connectedArrayList.add(device);
//            }
//            connectedAdapter.notifyDataSetChanged();
//        } catch (SecurityException e) {
//            Log.e("e", e.getMessage());
//        }

    }




    public void requestBluetoothPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if ((this.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                    || (this.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        android.Manifest.permission.BLUETOOTH_SCAN)) {

                    // display permission rationale in snackbar message
                } else {
                    Log.w(getClass().getName(), "requestBluetoothPermissions() BLUETOOTH_SCAN AND BLUETOOTH_CONNECT permissions needed => requesting them...");

                    this.requestPermissions(new String[]{
                            android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT
                    }, 111);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}

package com.example.hosma;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchArduino extends Fragment {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;

    SwipeRefreshLayout swipeRefreshLayout;

    ImageView back_btn;
    RecyclerView availableRecyclerView;
    SearchArduinoAdapter availableAdapter;
    ArrayList<BluetoothDevice> availableArrayList = new ArrayList<>();
    MainActivity.MainActivityInterface mainActivityInterface;

    Activity activity;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("started");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = "";
                try {
                    deviceName = device.getName();
                } catch (SecurityException e) {
                    Log.e("e", e.getMessage());
                }
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null && !availableArrayList.contains(device)) {
                    availableArrayList.add(device);
                    availableAdapter.notifyItemInserted(availableArrayList.size() - 1);
                }
//                System.out.println(deviceName);
            }
        }
    };


    public SearchArduino() {
        super(R.layout.search_arduino);
    }
    public SearchArduino(MainActivity.MainActivityInterface mainActivityInterface) {
        super(R.layout.search_arduino);
        this.mainActivityInterface = mainActivityInterface;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();

        back_btn = view.findViewById(R.id.back_btn);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        availableRecyclerView = view.findViewById(R.id.rv_bluetooth_devices);
        availableAdapter = new SearchArduinoAdapter(availableArrayList, mDeviceClickListener);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothManager = activity.getSystemService(BluetoothManager.class);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(receiver, filter);


        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, "Ваше устройство не поддерживает Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2793);
        }


        availableRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        availableRecyclerView.setAdapter(availableAdapter);

        searchDevices();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getParentFragmentManager().beginTransaction()
                        .remove(SearchArduino.this).commit();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchDevices();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private SearchArduinoAdapter.OnItemClickListener mDeviceClickListener = new SearchArduinoAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BluetoothDevice item) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle(item.getName());
                builder.setMessage("Mac adress " + item.getAddress());
                builder.setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_DEVICE_ADDRESS, item.getAddress());
//                        setResult(Activity.RESULT_OK, intent);
//                        finish();
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            } catch (SecurityException e) {
                Log.e("Security exception", e.getMessage());
            }

        }
    };

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

                    final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(activity, "Turn on your location to find bluetooth devices", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                        if ((getActivity().checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                                || (getActivity().checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                                    android.Manifest.permission.BLUETOOTH_SCAN)) {

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
                public void onDestroy() {
                    super.onDestroy();
                    if (bluetoothAdapter != null) {
                        try {
                            bluetoothAdapter.cancelDiscovery();
                        }catch (SecurityException e) {
                            Log.e("SecurityException", e.getMessage());
                        }
                    }
                        mainActivityInterface.unregisterReceiver(receiver);
                }




}

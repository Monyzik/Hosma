package com.example.hosma;

import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.OutputStream;
import java.util.ArrayList;

public class MainPageFragment extends Fragment {
    private static final int REQUEST_CONNECT_DEVICE = 1;

    FloatingActionButton addDeviceButton;
    RecyclerView devisesRecyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;

    BluetoothArduinoService bluetoothArduinoService;
    ArrayList<Device> devices = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;

    FloatingActionButton search_arduino_btn;

    OutputStream outputStream;
    MainActivity.MainActivityInterface mainPageInterface;

    Bundle arguments;



    public MainPageFragment() {
        super(R.layout.main_page_fragment);
    }
    public MainPageFragment(MainActivity.MainActivityInterface mainPageInterface) {
        super(R.layout.main_page_fragment);
        this.mainPageInterface = mainPageInterface;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addDeviceButton = view.findViewById(R.id.add_new_device_button);
        search_arduino_btn = view.findViewById(R.id.search_ard_btn);

        devisesRecyclerView = view.findViewById(R.id.devises_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(devices);
        devisesRecyclerView.setLayoutManager(layoutManager);
        devisesRecyclerView.setAdapter(devicesRecyclerViewAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        getContext().bindService(new Intent(getContext(), BluetoothArduinoService.class), mainPageInterface.getServiceConnection(), Context.BIND_AUTO_CREATE);



        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devices.add(new Device(1231, "Cats..."));
                devicesRecyclerViewAdapter.notifyItemInserted(devices.size() - 1);
                mainPageInterface.sendFunction("1");
            }
        });

        search_arduino_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getContext(), SearchArduino.class);
//                startActivityForResult(i, REQUEST_CONNECT_DEVICE);
                SearchArduino searchArduino = new SearchArduino(mainPageInterface);
                getParentFragmentManager().beginTransaction().hide(MainPageFragment.this).commit();
                getParentFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_view, searchArduino, null)
                        .commit();

            }
        });
    }




}

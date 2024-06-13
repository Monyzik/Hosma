package com.example.hosma;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addDeviceButton;

    RecyclerView devisesRecyclerView;
    DevisesRecyclerViewAdapter devisesRecyclerViewAdapter;

    ArrayList <Devise> devises = new ArrayList<>();

    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothManager = getSystemService(BluetoothManager.class);

        addDeviceButton = findViewById(R.id.add_new_device_button);

        devisesRecyclerView = findViewById(R.id.devises_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        devisesRecyclerViewAdapter = new DevisesRecyclerViewAdapter(devises);
        devisesRecyclerView.setLayoutManager(layoutManager);
        devisesRecyclerView.setAdapter(devisesRecyclerViewAdapter);


        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 2);
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

        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devises.add(new Devise(1231, "Cats..."));
                devisesRecyclerViewAdapter.notifyItemInserted(devises.size() - 1);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2793){
            Toast.makeText(MainActivity.this, "Bluetooth is ON", Toast.LENGTH_SHORT).show();
        } else
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this, ":(", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.hosma;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addDeviceButton;

    RecyclerView devisesRecyclerView;
    DevisesRecyclerViewAdapter devisesRecyclerViewAdapter;

    ArrayList<Devise> devises = new ArrayList<>();

    Button search_arduino_btn;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        addDeviceButton = findViewById(R.id.add_new_device_button);
        search_arduino_btn = findViewById(R.id.search_ard_btn);

        devisesRecyclerView = findViewById(R.id.devises_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        devisesRecyclerViewAdapter = new DevisesRecyclerViewAdapter(devises);
        devisesRecyclerView.setLayoutManager(layoutManager);
        devisesRecyclerView.setAdapter(devisesRecyclerViewAdapter);



        addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devises.add(new Devise(1231, "Cats..."));
                devisesRecyclerViewAdapter.notifyItemInserted(devises.size() - 1);
            }
        });

        search_arduino_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SearchArduino.class);
                startActivity(i);
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
package com.example.hosma;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addDeviceButton;

    RecyclerView devisesRecyclerView;
    DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;

    ArrayList<Device> devices = new ArrayList<>();

    Button search_arduino_btn;






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
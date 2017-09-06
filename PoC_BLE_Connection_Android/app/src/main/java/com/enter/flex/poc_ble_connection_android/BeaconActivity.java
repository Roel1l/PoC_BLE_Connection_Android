package com.enter.flex.poc_ble_connection_android;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BeaconActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        Intent intent = getIntent();
        BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("device");

        String i = device.getName();
        


    }
}

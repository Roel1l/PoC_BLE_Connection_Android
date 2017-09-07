package com.enter.flex.poc_ble_connection_android;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BeaconActivity extends AppCompatActivity {
    public static final String TAGS = "Beacon";
    private BluetoothGatt mBluetoothGatt;
    private Button connectionButton;
    private TextView connectionTextView;
    private TextView idTextView;
    private TextView nameTextView;
    private BluetoothDevice device;
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    private ListView servicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        device = intent.getParcelableExtra("device");

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);

        mBluetoothGatt.connect();

        connectionButton = (Button) findViewById(R.id.connectButton);
        connectionTextView = (TextView) findViewById(R.id.connectionTextView);
        idTextView = (TextView) findViewById(R.id.idTextView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);

        listItems = new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        servicesListView = (ListView)findViewById(R.id.servicesList);

        servicesListView.setAdapter(adapter);

        idTextView.setText(device.getAddress());
        if (device.getName() != null) {
            nameTextView.setText(device.getName());
            getSupportActionBar().setTitle(device.getName());
        }
        else{
            nameTextView.setText("N/A");
            getSupportActionBar().setTitle("N/A");
        }

        connectionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(connectionButton.getText().equals("Disconnect")) {
                   mBluetoothGatt.disconnect();
                   listItems.clear();
               }
               else{
                   mBluetoothGatt.connect();
                   connectionTextView.setText("Trying to connect...");
               }
            }
        });

    }

    private void connected(){
        connectionTextView.setText("Connected");
        connectionButton.setText("Disconnect");
    }

    private void disconnected(){
        connectionTextView.setText("Disconnected");
        connectionButton.setText("Connect");
    }

    private void showServices(){
        List<BluetoothGattService> services = mBluetoothGatt.getServices();

        for (BluetoothGattService s : services) {
            listItems.add(s.getUuid().toString());
            adapter.notifyDataSetChanged();
        }

    }

    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i(TAGS, "Connected to GATT server.");
                        Log.i(TAGS, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                connected();
                            }
                        };
                        mainHandler.post(myRunnable);

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.i(TAGS, "Disconnected from GATT server.");
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                disconnected();
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.i(TAGS, "Services discovered");
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run()
                            {
                                showServices();
                            }
                        };
                        mainHandler.post(myRunnable);
                    } else {
                        Log.w(TAGS, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.i(TAGS, "Characteristics read success");
                    }
                }

            };
}

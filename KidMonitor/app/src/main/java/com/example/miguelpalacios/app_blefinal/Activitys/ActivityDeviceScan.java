/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.miguelpalacios.app_blefinal.Activitys;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.miguelpalacios.app_blefinal.Adapters.AdapterDeviceScan;
import com.example.miguelpalacios.app_blefinal.Dialogs.DialogConectarGuardar;
import com.example.miguelpalacios.app_blefinal.Dialogs.DialogProgress;
import com.example.miguelpalacios.app_blefinal.R;

import java.util.UUID;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class ActivityDeviceScan extends AppCompatActivity {
    private static final String TAG = ActivityDeviceScan.class.getSimpleName();
    private AdapterDeviceScan mAdapterDeviceScan;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static final long SCAN_PERIOD = 10000;

    private ListView lisviewDispositivos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_scan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT < 19)
        {
            FrameLayout mFrameLayoutToolBar = (FrameLayout)findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.titulo_Device_scan);

        mHandler = new Handler();

        lisviewDispositivos = (ListView) findViewById(R.id.listViewListas);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void buscarDispositivosClick(View view)
    {
        mAdapterDeviceScan.clear();
        mAdapterDeviceScan.notifyDataSetChanged();
        scanLeDevice(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        mAdapterDeviceScan = new AdapterDeviceScan(this);
        lisviewDispositivos.setAdapter(mAdapterDeviceScan);
        scanLeDevice(true);
        lisviewDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = mAdapterDeviceScan.getDevice(position);
                if (device == null) return;
                Intent intent = new Intent(getApplicationContext(), ActivityDeviceControl.class);
                /*intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.getAddress());*/

                if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                new DialogConectarGuardar(ActivityDeviceScan.this, intent, device).crearDialogConectarGuardar();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mAdapterDeviceScan.clear();
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                    mProgressDialog.cancel();
                }
            }, SCAN_PERIOD);
            mProgressDialog = new DialogProgress(this, R.string.progressDialog_buscando).generarProgressDialog();
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mProgressDialog.cancel();
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapterDeviceScan.addDevice(device);
                    mAdapterDeviceScan.notifyDataSetChanged();
                }
            });
        }
    };
}
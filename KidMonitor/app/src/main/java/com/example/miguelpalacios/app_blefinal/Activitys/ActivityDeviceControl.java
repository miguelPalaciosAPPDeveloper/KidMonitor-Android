package com.example.miguelpalacios.app_blefinal.Activitys;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.miguelpalacios.app_blefinal.Clases.ClaseControl;
import com.example.miguelpalacios.app_blefinal.R;
import com.example.miguelpalacios.app_blefinal.Clases.ClaseSampleGattAttributes;
import com.example.miguelpalacios.app_blefinal.Services.ServiceBluetoothLe;
import com.example.miguelpalacios.app_blefinal.Services.ServiceMediaPlayer;
import com.example.miguelpalacios.app_blefinal.Services.ServiceNotification;
import com.example.miguelpalacios.app_blefinal.Services.ServiceMonitoreo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class ActivityDeviceControl extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = ActivityDeviceControl.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final int OCHO_METROS = 8;
    public static final int DOCE_METROS = 12;
    private TextView mConnectionState, mDBM;
    private Button buttonConexion;
    private RadioButton mRadioButtonCorto, mRadioButtonLargo;
    private ToggleButton toggleButtonComenzarRSSI;
    private String mDeviceName, mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private ServiceBluetoothLe mServiceBluetoothLe;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;

    private ClaseControl mClaseControl;

    public final static UUID HM_RX_TX = UUID.fromString(ClaseSampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mServiceBluetoothLe = ((ServiceBluetoothLe.LocalBinder) service).getService();
            if (!mServiceBluetoothLe.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mServiceBluetoothLe.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBluetoothLe = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ServiceBluetoothLe.ACTION_GATT_CONNECTED.equals(action)) {
                mClaseControl.setConnected(true);
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (ServiceBluetoothLe.ACTION_GATT_DISCONNECTED.equals(action)) {
                mClaseControl.setConnected(false);
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            } else if (ServiceBluetoothLe.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mServiceBluetoothLe.getSupportedGattServices());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT < 19)
        {
            FrameLayout mFrameLayoutToolBar = (FrameLayout)findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        getSupportActionBar().setTitle("Monitoreo");

        ((TextView) findViewById(R.id.device_address)).setText(mDeviceName);
        mConnectionState = (TextView) findViewById(R.id.connection_state);

        mDBM = (TextView)findViewById(R.id.textViewDBM);

        mRadioButtonCorto = (RadioButton)findViewById(R.id.radioButtonCortaDistancia);
        mRadioButtonLargo = (RadioButton)findViewById(R.id.radioButtonLargaDistancia);

        mRadioButtonCorto.setOnClickListener(this);
        mRadioButtonLargo.setOnClickListener(this);

        buttonConexion = (Button)findViewById(R.id.buttonConectarControl);
        buttonConexion.setOnClickListener(this);

        mClaseControl = new ClaseControl(this);
        stopService(new Intent(getApplicationContext(), ServiceNotification.class));

        Intent gattServiceIntent = new Intent(this, ServiceBluetoothLe.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        toggleButtonComenzarRSSI = (ToggleButton)findViewById(R.id.toggleButtonRSSI);
        toggleButtonComenzarRSSI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRadioButtonCorto.isChecked() || mRadioButtonLargo.isChecked()) {
                    if (toggleButtonComenzarRSSI.isChecked()) {
                        if (mClaseControl.getConnected()) {
                            Toast.makeText(ActivityDeviceControl.this, "RSSI iniciado", Toast.LENGTH_SHORT).show();
                            mClaseControl.setEstado(true);
                            hiloTextView();
                            startService(new Intent(getApplicationContext(), ServiceMonitoreo.class));
                        } else {
                            toggleButtonComenzarRSSI.setChecked(false);
                            Toast.makeText(ActivityDeviceControl.this, "No hay conexión", Toast.LENGTH_SHORT).show();
                        }
                    } else
                    {
                        if (mClaseControl.getConnected()) {
                            mClaseControl.setEstado(false);
                        }
                        else {Toast.makeText(ActivityDeviceControl.this, "No hay conexión", Toast.LENGTH_SHORT).show();}
                        stopService(new Intent(getApplicationContext(), ServiceMonitoreo.class));
                    }
                } else{
                    toggleButtonComenzarRSSI.setChecked(false);
                    Toast.makeText(ActivityDeviceControl.this, "Elija una distancia", Toast.LENGTH_SHORT).show();}
            }
        });
    }

    public void hiloTextView()
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mClaseControl.getEstado() && mClaseControl.getConnected()) {
                    //Log.d(TAG, "hiloTextview");
                    mDBM.post(new Runnable() {
                        @Override
                        public void run() {
                            mDBM.setText((int) mClaseControl.getPromedio() + "dbm");
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }
        }).start();
    }
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.radioButtonCortaDistancia:
                mRadioButtonLargo.setChecked(false);
                mRadioButtonCorto.setChecked(true);
                mClaseControl.setLimite(OCHO_METROS);
                break;
            case R.id.radioButtonLargaDistancia:
                mRadioButtonLargo.setChecked(true);
                mRadioButtonCorto.setChecked(false);
                mClaseControl.setLimite(DOCE_METROS);
                break;
            case R.id.buttonConectarControl:
                if(mClaseControl.getConnected())
                {
                    mServiceBluetoothLe.disconnect();
                    mClaseControl.setEstado(false);
                    toggleButtonComenzarRSSI.setChecked(false);
                }
                else {mServiceBluetoothLe.connect(mDeviceAddress);}
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mServiceBluetoothLe != null) {
            final boolean result = mServiceBluetoothLe.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestar");
        if(mClaseControl.getConnected())
        {
            mConnectionState.setText(R.string.connected);
            buttonConexion.setText(R.string.txt_boton_DesconectarConect);
            Log.d(TAG, "onVerdadero");
        }
        else
        {
            mConnectionState.setText(R.string.disconnected);
            buttonConexion.setText(R.string.txt_boton_ConectarConect);
            toggleButtonComenzarRSSI.setChecked(false);
            Log.d(TAG, "onfalso");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mServiceBluetoothLe = null;

        stopService(new Intent(getApplicationContext(), ServiceMediaPlayer.class));
        stopService(new Intent(getApplicationContext(), ServiceNotification.class));
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
        finish();
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "update");
                if (!mClaseControl.getConnected()) {
                    buttonConexion.setText(R.string.txt_boton_ConectarConect);
                } else {
                    buttonConexion.setText(R.string.txt_boton_DesconectarConect);
                    toggleButtonComenzarRSSI.setChecked(false);
                }
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, ClaseSampleGattAttributes.lookup(uuid, unknownServiceString));

            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            characteristicTX = gattService.getCharacteristic(ServiceBluetoothLe.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(ServiceBluetoothLe.UUID_HM_RX_TX);
        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ServiceBluetoothLe.ACTION_GATT_CONNECTED);
        intentFilter.addAction(ServiceBluetoothLe.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ServiceBluetoothLe.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ServiceBluetoothLe.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}

package com.example.miguelpalacios.app_blefinal.Activitys;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.miguelpalacios.app_blefinal.Admins.AdminDataBase;
import com.example.miguelpalacios.app_blefinal.Clases.ClaseSharedPreferences;
import com.example.miguelpalacios.app_blefinal.Dialogs.DialogOpcionesNFC;
import com.example.miguelpalacios.app_blefinal.R;


public class ActivityPrincipal extends AppCompatActivity {
    private static final String TAG = ActivityPrincipal.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLED_BT = 1;
    private static int mGuardados;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        if(Build.VERSION.SDK_INT < 19)
        {
            FrameLayout mFrameLayoutToolBar = (FrameLayout)findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    public void buscarNuevosClick(View view)
    {
        Intent intent = new Intent(this, ActivityDeviceScan.class);
        startActivity(intent);
    }

    public void buscarActualesClick(View view) {
        ClaseSharedPreferences claseSharedPreferences = new ClaseSharedPreferences(this);
        mGuardados = claseSharedPreferences.getGuardados();
        if(mGuardados > 0) {
            Intent intent = new Intent(this, ActivityDeviceSaved.class);
            intent.putExtra(ActivityDeviceSaved.EXTRA_DISPOSITIVOS, mGuardados);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, R.string.no_hay_dispositivos, Toast.LENGTH_LONG).show();
        }
    }


    public  void opcionesNFCClick(View view)
    {
        if(mNfcAdapter == null)
        {
            Toast.makeText(this, R.string.sin_tecnologia_nfc, Toast.LENGTH_SHORT).show();
        }
        else
        {
            new DialogOpcionesNFC(this).crearDialogOpcionesNFC();
        }
    }

    public void activarBluetooth()
    {
        if(!mBluetoothAdapter.isEnabled())
        {
            if(!mBluetoothAdapter.isEnabled())
            {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLED_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_ENABLED_BT && resultCode == Activity.RESULT_CANCELED)
        {
            Toast.makeText(this, R.string.bluetooth_no_Activado, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activarBluetooth();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.disable();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_acerca:
                break;
            case R.id.action_salir:
                onBackPressed();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

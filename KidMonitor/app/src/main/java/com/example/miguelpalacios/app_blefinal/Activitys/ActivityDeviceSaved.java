package com.example.miguelpalacios.app_blefinal.Activitys;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.miguelpalacios.app_blefinal.Adapters.AdapterDeviceSaved;
import com.example.miguelpalacios.app_blefinal.Admins.AdminDataBase;
import com.example.miguelpalacios.app_blefinal.Dialogs.DialogOpciones;
import com.example.miguelpalacios.app_blefinal.R;


public class ActivityDeviceSaved extends AppCompatActivity{
    private static final String TAG = ActivityDeviceSaved.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private AdapterDeviceSaved mAdapterDeviceSaved;
    private AdminDataBase mAdminDataBase;
    private static int mGuardados;
    private Dialog mDialog;
    public static final String EXTRA_DISPOSITIVOS = "DISPOSITIVOS";
    public ListView lisviewDispositivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_saved);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT < 19)
        {
            FrameLayout mFrameLayoutToolBar = (FrameLayout)findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.titulo_device_saved);

        final Intent intent = getIntent();
        mGuardados = intent.getIntExtra(EXTRA_DISPOSITIVOS, 0);
        if(mGuardados == 0)
        {
            finish();
        }

        lisviewDispositivos = (ListView) findViewById(R.id.listViewListas);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mAdminDataBase = new AdminDataBase();
        mAdminDataBase.AdminDataBase(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_ayuda:
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        reiniciarActivity(this);
    }

    public void reiniciarActivity(Activity activity)
    {
        Intent intent = getIntent();
        activity.finish();
        activity.startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        mAdapterDeviceSaved = new AdapterDeviceSaved(this);
        lisviewDispositivos.setAdapter(mAdapterDeviceSaved);
        obtenerDatos();

        lisviewDispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nombreDispositivo = mAdapterDeviceSaved.getNombre(position);
                String claveDispositivo = mAdapterDeviceSaved.getClave(position);

                Intent intent = getIntent();
                new DialogOpciones(ActivityDeviceSaved.this,ActivityDeviceSaved.this,intent, nombreDispositivo, claveDispositivo).crearDialogOpciones();
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        mAdapterDeviceSaved.clear();
        lisviewDispositivos.setAdapter(null);
        mAdapterDeviceSaved = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void obtenerDatos()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mAdapterDeviceSaved.setClaves(mAdminDataBase.consultaClavesDataBase());
                    for(int i = 0;i < mAdapterDeviceSaved.getCountClave();i++)
                    {
                        Log.e(TAG, "for: " + i);
                        String clave = mAdapterDeviceSaved.getClave(i);
                        mAdapterDeviceSaved.addNombres(mAdminDataBase.consultaNombreDataBase(clave));
                    }
                }
                catch (Exception Error)
                {
                    Log.e(TAG, "" + Error);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "fin pintado");
                    }
                });
            }

        }).start();
    }
}

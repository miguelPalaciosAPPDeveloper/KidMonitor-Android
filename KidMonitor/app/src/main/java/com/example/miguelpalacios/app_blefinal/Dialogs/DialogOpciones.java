package com.example.miguelpalacios.app_blefinal.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miguelpalacios.app_blefinal.Activitys.ActivityDeviceControl;
import com.example.miguelpalacios.app_blefinal.Activitys.ActivityDeviceSaved;
import com.example.miguelpalacios.app_blefinal.Adapters.AdapterDeviceSaved;
import com.example.miguelpalacios.app_blefinal.Admins.AdminDataBase;
import com.example.miguelpalacios.app_blefinal.Clases.ClaseSharedPreferences;
import com.example.miguelpalacios.app_blefinal.R;

/**
 * Created by miguelpalacios on 24/08/15.
 */
public class DialogOpciones {
    private static final String TAG = DialogOpciones.class.getSimpleName();
    private Context mContext;
    private Intent mIntent;
    private Dialog opcionesDialog;
    private ProgressDialog progressDialog;
    private Activity mActivity;
    private ClaseSharedPreferences mClaseSharedPreferences;
    private AdminDataBase mAdminDataBase;
    private AdapterDeviceSaved mAdapterDeviceSaved;
    private BluetoothAdapter mBluetoothAdapter;
    final boolean[] mCambio = new boolean[1];
    private static final int RESTA = -1;
    private String mTitulo;
    private String mClave;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 4000;
    private boolean mEncontrado;

    public DialogOpciones(Activity activity, Context context,Intent intent, String titulo, String clave)
    {
        mContext = context;
        mTitulo = titulo;
        mClave = clave;
        mIntent = intent;
        mActivity = activity;
        Inicio();
    }

    private void Inicio()
    {
        mClaseSharedPreferences = new ClaseSharedPreferences(mContext);
        mAdminDataBase = new AdminDataBase();
        mAdminDataBase.AdminDataBase(mContext);
        mAdapterDeviceSaved = new AdapterDeviceSaved(mContext);
        mHandler = new Handler();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public Dialog crearDialogOpciones()
    {
        opcionesDialog = new Dialog(mContext, R.style.Theme_Dialog_Translucent);
        opcionesDialog.show();
        opcionesDialog.setCancelable(false);
        opciones();

        return opcionesDialog;

    }

    private void opciones()
    {
        opcionesDialog.setContentView(R.layout.dialog_opciones_dispositivos);

        TextView textViewTitulo = (TextView) opcionesDialog.findViewById(R.id.textViewTitulo);
        textViewTitulo.setText(mTitulo);

        ((ImageButton)opcionesDialog.findViewById(R.id.imageButtonSalir)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionesDialog.dismiss();
            }
        });

        ((ImageButton)opcionesDialog.findViewById(R.id.imageButtonConectar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarDispositivo();
            }
        });

        ((ImageButton)opcionesDialog.findViewById(R.id.imageButtonConectar)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, R.string.conectar_dispositivo, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ((ImageButton)opcionesDialog.findViewById(R.id.imageButtonEditar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarNombre();
            }
        });

        ((ImageButton)opcionesDialog.findViewById(R.id.imageButtonEditar)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, R.string.editar_nombre, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ((ImageButton)opcionesDialog.findViewById(R.id.imageButtonBorrar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarDispositivo();
            }
        });
        ((ImageButton)opcionesDialog.findViewById(R.id.imageButtonBorrar)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, R.string.borrar_dispositivo, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void editarNombre()
    {
        opcionesDialog.setContentView(R.layout.dialog_cambiar_nombre);

        final EditText editTextNombre = (EditText) opcionesDialog.findViewById(R.id.editTextnNombre);

        ((Button)opcionesDialog.findViewById(R.id.buttonAceptarEdit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextNombre.getText().toString().isEmpty())
                {
                    opcionesDialog.dismiss();
                }
                else
                {
                    mCambio[0] = mAdminDataBase.modificarNombreDataBase(mClave, editTextNombre.getText().toString());

                    if(mCambio[0])
                    {
                        Toast.makeText(mContext, R.string.nombre_cambiado, Toast.LENGTH_SHORT).show();
                        opcionesDialog.dismiss();
                        mAdapterDeviceSaved.clear();
                        mActivity.finish();
                        mContext.startActivity(mIntent);
                        mActivity.overridePendingTransition(0, 0);
                    }
                    else
                    {
                        Toast.makeText(mContext, R.string.nombre_no_cambiado, Toast.LENGTH_SHORT).show();
                        opcionesDialog.dismiss();
                    }
                }
            }
        });

        ((Button)opcionesDialog.findViewById(R.id.buttonCancelarEdit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionesDialog.dismiss();
            }
        });
    }

    private void borrarDispositivo()
    {
        opcionesDialog.setContentView(R.layout.dialog_borrar_dispositivo);

        ((Button)opcionesDialog.findViewById(R.id.buttonAceptarBorrar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean mBorrado = mAdminDataBase.bajaDataBase(mClave);
                if(mBorrado)
                {
                    Toast.makeText(mContext, R.string.dispositivo_borrado, Toast.LENGTH_SHORT).show();
                    mClaseSharedPreferences.savePreferences(RESTA);
                    mAdapterDeviceSaved.clear();
                    mActivity.finish();
                    int mGuardados = mClaseSharedPreferences.getGuardados();
                    mIntent.putExtra(ActivityDeviceSaved.EXTRA_DISPOSITIVOS, mGuardados);
                    mContext.startActivity(mIntent);
                    mActivity.overridePendingTransition(0, 0);
                    opcionesDialog.dismiss();
                }
                else
                {
                    Toast.makeText(mContext, R.string.dispositivo_no_borrado, Toast.LENGTH_SHORT).show();
                    opcionesDialog.dismiss();
                }
            }
        });

        ((Button)opcionesDialog.findViewById(R.id.buttonCancelarBorrar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionesDialog.dismiss();
            }
        });
    }

    private void conectarDispositivo()
    {
        opcionesDialog.setContentView(R.layout.dialog_conectar_dispositivo);
        ((Button) opcionesDialog.findViewById(R.id.buttonConectarConectar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               scanLeDevice(true);
                progressDialog = new DialogProgress(mContext, R.string.progress_dialog_conectado).generarProgressDialog();
                opcionesDialog.dismiss();
            }
        });

        ((Button)opcionesDialog.findViewById(R.id.buttonCancelarConectar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionesDialog.dismiss();
            }
        });
    }

    private void conectar(String mac)
    {
        Intent intent = new Intent(mContext, ActivityDeviceControl.class);
        intent.putExtra(ActivityDeviceControl.EXTRAS_DEVICE_NAME, mTitulo);
        intent.putExtra(ActivityDeviceControl.EXTRAS_DEVICE_ADDRESS, mac);
        mContext.startActivity(intent);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "device: " + device.getAddress());
                            String MAC = mAdminDataBase.consultaMACDataBase(mClave);
                            if(device.getAddress().equals(MAC))
                            {
                                mEncontrado = true;
                                scanLeDevice(false);
                                conectar(MAC);
                            }
                        }
                    });
                }
            };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if(!mEncontrado) {
                        progressDialog.cancel();
                        Toast.makeText(mContext, R.string.dialog_fuera_alcance, Toast.LENGTH_SHORT).show();
                    }
                }
            }, SCAN_PERIOD);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            progressDialog.cancel();
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
}

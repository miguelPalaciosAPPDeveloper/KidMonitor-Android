package com.example.miguelpalacios.app_blefinal.Dialogs;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.miguelpalacios.app_blefinal.Admins.AdminDataBase;
import com.example.miguelpalacios.app_blefinal.Clases.ClaseSharedPreferences;
import com.example.miguelpalacios.app_blefinal.R;

/**
 * Created by miguelpalacios on 24/08/15.
 */
public class DialogConectarGuardar {
    private static final String TAG = DialogConectarGuardar.class.getSimpleName();
    private Context mContext;
    private Intent mIntent;
    private Dialog conectarGuardarDialog;
    private static final int SUMA = 1;
    private static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    private static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mClave;
    final boolean[] mGuardarDispositivo = new boolean[1];
    private AdminDataBase mAdminDataBase;
    private ClaseSharedPreferences mClaseSharedPreferences;
    private BluetoothDevice mDevice;

    public DialogConectarGuardar(Context context, Intent intent, BluetoothDevice device)
    {
        mContext = context;
        mIntent = intent;
        mDevice = device;
        Inicio(mDevice.getAddress());
    }

    public void Inicio(String MAC)
    {
        mAdminDataBase = new AdminDataBase();
        mAdminDataBase.AdminDataBase(mContext);
        mClaseSharedPreferences = new ClaseSharedPreferences(mContext);
        mClave = mClaseSharedPreferences.obtenerClave(MAC);
    }

    public Dialog crearDialogConectarGuardar()
    {
        conectarGuardarDialog = new Dialog(mContext, R.style.Theme_Dialog_Translucent);
        conectarGuardarDialog.show();
        conectarGuardarDialog.setCancelable(false);
        conectarGuardarDialog.setContentView(R.layout.dialog_conexion_guardar);
        final EditText EditTextClaveMAC = (EditText) conectarGuardarDialog.findViewById(R.id.editTextClaveMAC);

        ((CheckBox) conectarGuardarDialog.findViewById(R.id.checkBoxGuardar)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mGuardarDispositivo[0] = true;
                } else {
                    mGuardarDispositivo[0] = false;
                }
            }
        });

        ((CheckBox) conectarGuardarDialog.findViewById(R.id.checkBoxMostrar)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    EditTextClaveMAC.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    EditTextClaveMAC.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                int lengthText = EditTextClaveMAC.getText().length();
                EditTextClaveMAC.setSelection(lengthText, lengthText);

            }
        });

        ((Button) conectarGuardarDialog.findViewById(R.id.buttonConectar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EditTextClaveMAC.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, R.string.txt_toast_clave_incorreta, Toast.LENGTH_SHORT).show();
                }else {
                    if (EditTextClaveMAC.getText().toString().equals(mClave)) {
                        boolean verificacion = mAdminDataBase.verificacionDispositivoGuardado(mDevice.getAddress());
                        String nombreDispositivo = "";
                        if (mGuardarDispositivo[0]) {
                            if (verificacion){
                                Toast.makeText(mContext, R.string.dialog_dispositivo_verificado, Toast.LENGTH_SHORT).show();
                                conectarGuardarDialog.dismiss();
                            }else{
                                boolean DatosCargados = mAdminDataBase.altaDataBase(EditTextClaveMAC.getText().toString(), mDevice.getName(), mDevice.getAddress());
                                if (DatosCargados) {
                                    mClaseSharedPreferences.savePreferences(SUMA);
                                    Toast.makeText(mContext, R.string.txt_toast_dispositivo_guardado, Toast.LENGTH_SHORT).show();
                                    nombreDispositivo = mDevice.getName();
                                } else {
                                    Toast.makeText(mContext, R.string.txt_toast_dispositivo_no_guardado, Toast.LENGTH_SHORT).show();
                                }

                                mIntent.putExtra(EXTRAS_DEVICE_NAME, nombreDispositivo);
                                mIntent.putExtra(EXTRAS_DEVICE_ADDRESS, mDevice.getAddress());
                                mContext.startActivity(mIntent);
                                conectarGuardarDialog.dismiss();
                            }
                        } else {
                            if (verificacion){
                                nombreDispositivo = mAdminDataBase.consultaNombreDataBase(mClave);
                            }
                            mIntent.putExtra(EXTRAS_DEVICE_NAME, nombreDispositivo);
                            mIntent.putExtra(EXTRAS_DEVICE_ADDRESS, mDevice.getAddress());
                            mContext.startActivity(mIntent);
                            conectarGuardarDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(mContext, R.string.txt_toast_clave_incorreta, Toast.LENGTH_SHORT).show();
                        conectarGuardarDialog.dismiss();
                    }
                }
            }
        });

        ((Button) conectarGuardarDialog.findViewById(R.id.buttonCancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectarGuardarDialog.dismiss();
            }
        });

        return conectarGuardarDialog;
    }
}

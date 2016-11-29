package com.example.miguelpalacios.app_blefinal.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by X1 on 11/08/15.
 */
public class ClaseSharedPreferences {
    private static final String TAG = ClaseSharedPreferences.class.getSimpleName();
    private static final String DEVICESAVE = "deviceSave";
    private static final String SAVED = "saved";
    private static int mGuardados;
    private Context mContext;

    public int getGuardados()
    {
        return mGuardados;
    }
    public ClaseSharedPreferences(Context context)
    {
        mContext = context;

        SharedPreferences preferences = mContext.getSharedPreferences(DEVICESAVE, Context.MODE_PRIVATE);
        mGuardados = preferences.getInt(SAVED, 0);
        Log.e(TAG, "Guardados: " + mGuardados);
    }

    public String obtenerClave(String direccion)
    {
        String[] direccionMAC = direccion.split(":");
        String mClave = "";
        for(int i = 0; i < direccionMAC.length; i++)
        {
            mClave += direccionMAC[i];
        }
        Log.i(TAG, "Clave: " + mClave);

        return mClave;
    }

    public void savePreferences(int operacion)
    {
        mGuardados = mGuardados + operacion;
        SharedPreferences preferences = mContext.getSharedPreferences(DEVICESAVE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SAVED, mGuardados);
        editor.commit();
    }
}

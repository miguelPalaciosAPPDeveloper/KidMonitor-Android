package com.example.miguelpalacios.app_blefinal.Admins;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by miguelpalacios on 10/05/15.
 */
public class AdminDataBase {
    private static final String TAG = AdminDataBase.class.getSimpleName();
    private static final String DATABASE = "control";
    private static final String TABLE = "dispositivos";
    private static int mBorrado = 1;
    private static int mActualizado = 1;
    private AdminSQLiteOpenHelper mAdmin;
    private SQLiteDatabase mDatabase;

    public void AdminDataBase(Context context)
    {
        mAdmin = new AdminSQLiteOpenHelper(context, DATABASE, null, 1);
    }

    public boolean verificacionDispositivoGuardado(String mac){
        //boolean verificacion;
        try{
            mDatabase = mAdmin.getWritableDatabase();
            Cursor mDato = mDatabase.rawQuery("select nombre from " + TABLE + " where mac='" + mac + "'", null);
            mDato.moveToFirst();
            mDato.getString(0);
            mDatabase.close();
            return true;
        }
        catch (Exception Error){
            return false;
        }
    }
    public boolean altaDataBase(String claveMAC, String nombre, String mac)
    {
        try {
            mDatabase = mAdmin.getWritableDatabase();
            ContentValues mRegistro = new ContentValues();
            mRegistro.put("clave", claveMAC);
            mRegistro.put("nombre", nombre);
            mRegistro.put("mac", mac);
            mDatabase.insert(TABLE, null, mRegistro);
            mDatabase.close();

           return true;
        }
        catch (Exception Error) {
            return false;
        }
    }

    public String consultaNombreDataBase(String clave)
    {
        try
        {
            mDatabase = mAdmin.getWritableDatabase();
            Cursor mDato = mDatabase.rawQuery("select nombre from " + TABLE + " where clave='" + clave + "'", null);
            mDato.moveToFirst();
            String mNombre = mDato.getString(0);
            mDatabase.close();

            return mNombre;
        }
        catch (Exception Error){return null;}
    }

    public ArrayList consultaClavesDataBase()
    {
        try {

            mDatabase = mAdmin.getWritableDatabase();
            Cursor mFilas = mDatabase.rawQuery("select clave from " + TABLE, null);
            ArrayList<String> mDatos = new ArrayList<String>();
            mDatos.clear();
            if (mFilas.moveToFirst()) {
                mDatos.add(mFilas.getString(0));
                while (mFilas.moveToNext()) {
                    mDatos.add(mFilas.getString(0));
                }
            } else {
                mDatos = null;
                mDatabase.close();
            }

            return mDatos;
        } catch (Exception Error){return null;}
    }

    public String consultaMACDataBase(String clave)
    {
        try
        {
            mDatabase = mAdmin.getWritableDatabase();
            Cursor mDato = mDatabase.rawQuery("select mac from " + TABLE + " where clave='" + clave + "'", null);
            mDato.moveToFirst();
            String mMAC = mDato.getString(0);
            mDatabase.close();

            return mMAC;
        }
        catch (Exception Error){return null;}
    }
    public boolean bajaDataBase(String clave)
    {
        mDatabase = mAdmin.getWritableDatabase();
        int mCantidad = mDatabase.delete(TABLE, "clave='" + clave + "'", null);
        mDatabase.close();

        if(mCantidad == mBorrado)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean modificarNombreDataBase(String clave, String nombre)
    {
        try {
            mDatabase = mAdmin.getWritableDatabase();
            ContentValues mRegistro = new ContentValues();
            mRegistro.put("nombre", nombre);
            int mCantidad = mDatabase.update(TABLE, mRegistro, "clave='" + clave + "'", null);
            mDatabase.close();
            if (mCantidad == mActualizado) {
                return true;
            } else {
                return false;
            }
        }
        catch (Exception Error)
        {
            return false;
        }
    }
}

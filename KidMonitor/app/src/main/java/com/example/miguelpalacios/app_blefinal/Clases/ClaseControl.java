package com.example.miguelpalacios.app_blefinal.Clases;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.miguelpalacios.app_blefinal.Services.ServiceMediaPlayer;
import com.example.miguelpalacios.app_blefinal.Services.ServiceMonitoreo;
import com.example.miguelpalacios.app_blefinal.Services.ServiceNotification;

public class ClaseControl {
    private static final String TAG = ClaseControl.class.getCanonicalName();
    private static boolean mEstado;
    private static double mPromedio;
    private static int mLimite;
    public static int[] pRSSI = new int[1];
    private static boolean mConnected = false;
    private static Context mContext;

    public ClaseControl(Context context) {mContext = context;}

    public void setEstado(boolean estado) {mEstado = estado;}
    public boolean getEstado(){return mEstado;}

    public double getPromedio(){return mPromedio;}

    public void setLimite(int limite){mLimite = limite;}
    public int getLimite(){return  mLimite;}

    public void setConnected(boolean connected){mConnected = connected;}
    public boolean getConnected(){return mConnected;}


    public void Promedio(){
        final int mMuestreo = 20;
        final int[] mEntrada = new int[mMuestreo];
        final int[] mSumatoria = {0};

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 1; i < mMuestreo; i++)
                {
                    mEntrada[i] = pRSSI[0];
                    mSumatoria[0] += mEntrada[i];
                    Retardo();
                }
                mPromedio = mSumatoria[0] /(mMuestreo);
            }
        }).start();

    }

    public void Retardo()
    {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void activarServices()
    {
        if(mEstado && !mConnected)
        {
            mContext.stopService(new Intent(mContext, ServiceMonitoreo.class));
            mContext.startService(new Intent(mContext, ServiceNotification.class));
            mContext.startService(new Intent(mContext, ServiceMediaPlayer.class));
        }
        else if(!mEstado && mConnected)
        {
            mContext.stopService(new Intent(mContext, ServiceNotification.class));
            mContext.stopService(new Intent(mContext, ServiceMediaPlayer.class));
            Log.e(TAG, "stopServiceMedia");
        }
    }
}

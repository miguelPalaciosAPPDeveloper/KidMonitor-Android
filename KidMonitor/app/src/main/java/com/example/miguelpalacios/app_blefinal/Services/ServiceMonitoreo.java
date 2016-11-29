package com.example.miguelpalacios.app_blefinal.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.miguelpalacios.app_blefinal.Clases.ClaseControl;

/**
 * Created by miguelpalacios on 01/09/15.
 */
public class ServiceMonitoreo extends Service {
    private static final String TAG = ServiceMonitoreo.class.getSimpleName();
    private static boolean mNotificacion = false;
    private static double mConstante = 1e-8;
    private static double mDistanciaM;
    private ClaseControl mClaseControl;

    @Override
    public void onCreate() {
        mClaseControl = new ClaseControl(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mClaseControl.getEstado() && mClaseControl.getConnected()) {
                    mClaseControl.Promedio();
                    mDistanciaM = Math.sqrt((mConstante)/(Math.pow(10.0,(mClaseControl.getPromedio()/10))));
                    Log.e(TAG, "Distancia: " + mDistanciaM);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(mDistanciaM >  mClaseControl.getLimite())
                    {
                        if(!mNotificacion) {
                            startService(new Intent(getApplicationContext(), ServiceMediaPlayer.class));
                            startService(new Intent(getApplicationContext(), ServiceNotification.class));
                            stopService(new Intent(getApplicationContext(), ServiceNotification.class));
                            mNotificacion = true;
                        }
                    }
                    else{
                        mNotificacion = false;
                        stopService(new Intent(getApplicationContext(), ServiceMediaPlayer.class));
                        stopService(new Intent(getApplicationContext(), ServiceNotification.class));
                    }
                }
                Log.e(TAG, "Fin de hiloService");
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

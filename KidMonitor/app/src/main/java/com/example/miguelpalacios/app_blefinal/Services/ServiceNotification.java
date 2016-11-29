package com.example.miguelpalacios.app_blefinal.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.miguelpalacios.app_blefinal.Activitys.ActivityDeviceControl;
import com.example.miguelpalacios.app_blefinal.R;

public class ServiceNotification extends Service {
    private final static String TAG = ServiceNotification.class.getSimpleName();
    private static int NOTIF_ALERTA_ID = 1010;
    public ServiceNotification() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent notIntent = new Intent(getApplicationContext(), ActivityDeviceControl.class);
                //Intent notIntent = new Intent(getApplicationContext(), BeginActivity.class);

                PendingIntent contIntent = PendingIntent.getActivity(getApplicationContext(), 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setLargeIcon((((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap()))
                        .setContentTitle("APP_BLE")
                        .setContentText("Alerta, dispositivo fuera del limite o desconectado.")
                        .setTicker("Alerta!")
                        .setContentIntent(contIntent)
                        .setVibrate(new long[]{100, 250, 100, 500})
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSound(defaultSound)
                        .setLights(Color.BLUE, 1, 0);

                mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());

                Log.e(TAG, "Sigue");
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

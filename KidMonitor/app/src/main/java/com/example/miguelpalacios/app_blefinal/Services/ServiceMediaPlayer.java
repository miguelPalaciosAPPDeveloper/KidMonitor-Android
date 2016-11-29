package com.example.miguelpalacios.app_blefinal.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.example.miguelpalacios.app_blefinal.Clases.ClaseControl;

public class ServiceMediaPlayer extends Service {
    private static final String TAG = ServiceMediaPlayer.class.getSimpleName();
    private Uri mAlerta = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrar;
    private static boolean mReproducir = false;
    private ClaseControl mClaseControl = new ClaseControl(this);
    public ServiceMediaPlayer() {
    }

    @Override
    public void onCreate() {
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), mAlerta);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mMediaPlayer.isPlaying()) {
            mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
            mReproducir = true;
            hiloDesconexion();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        mReproducir = false;
        if (mMediaPlayer.isPlaying() && mClaseControl.getConnected()) {
            mMediaPlayer.setLooping(false);
            mMediaPlayer.stop();
            Log.e(TAG, "Stop 2");
        }
        super.onDestroy();
    }

    public void hiloDesconexion()
    {
        mVibrar = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mReproducir)
                {
                    Log.e(TAG, "hiloDesconexion");
                    mVibrar.vibrate(2000);
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

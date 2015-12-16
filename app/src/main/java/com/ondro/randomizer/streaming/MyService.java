package com.ondro.randomizer.streaming;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Ondro on 06-Dec-15.
 */
public class MyService extends Service  {

    private boolean isRunning;
    private Context context;
    private Thread myThread;
    private BufferedWriter bw;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        try{
            bw = new BufferedWriter(new FileWriter(new File(
                    context.getExternalFilesDir(null), "hopa.txt"), true));
        }
        catch(IOException e){
            e.printStackTrace();
            Toast.makeText(context, "Can't open BufferedWriter", Toast.LENGTH_SHORT).show();
        }

        this.myThread = new Thread(myTask);
        Log.e("MyService", "Created");
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            try{
                bw.append("hoplacupla\n");
                bw.flush();
            }
            catch(IOException e){
                e.printStackTrace();
                Toast.makeText(context, "Can't write to BufferedWriter", Toast.LENGTH_SHORT).show();
            }
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        try{
            bw.flush();
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
            Toast.makeText(context, "Can't close BufferedWriter", Toast.LENGTH_SHORT).show();
        }
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.myThread.start();
        }
        return START_STICKY;
    }
}

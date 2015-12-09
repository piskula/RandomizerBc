package com.ondro.randomizer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ondro on 06-Dec-15.
 */
public class MyService extends Service{

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
                    context.getExternalFilesDir(null), "hop.txt"), true));
        }
        catch(IOException e){
            e.printStackTrace();
            Toast.makeText(context, "Can't open BufferedWriter", Toast.LENGTH_SHORT).show();
        }
        this.myThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            try{
                bw.append(dateFormat.format(cal.getTime()));
                bw.append("." + cal.get(Calendar.MILLISECOND));
                bw.append("\n");
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

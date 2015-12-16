package com.ondro.randomizer.streaming;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.app.Service;

/**
 * Created by Ondro on 09-Dec-15.
 */
public class RepeatingService extends IntentService {
    public RepeatingService(){
        super("RepeatingService");
    }

    public final String TAG = "RepeatingService";

    private Context context;
    private Thread myThread;
    private boolean isRunning;
    //private AlarmManager alarmManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.myThread = new Thread(myTask);
        //alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "Created");
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            Log.d(TAG, "Thread Started");

            int i = 0;
            while(i < 20){
                Intent alarm = new Intent(context, MyService.class);
                startService(alarm);
                try{
                    Thread.sleep(1000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                //i++;
            }

            /*Intent alarm = new Intent(context, MyService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, alarm, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 1000, pendingIntent);*/

            //stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
        //stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.myThread.start();
        }
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent){
        AlarmReceiver.completeWakefulIntent(intent);
    }
}

package com.ondro.randomizer.streaming;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ondro.randomizer.R;

import java.util.Calendar;

/**
 * Created by Ondro on 08-Dec-15.
 */
public class BackgroundSetUpActivity extends AppCompatActivity implements OnClickListener{
    public final String TAG = "BackgroundSetUpActivity";
    private static int REQUEST_CODE = 39003;

    private Button btnService;
    private Switch switchBackground;
    private Context mContext;
    private AlarmManager alarmManager;
    private boolean isCheckedService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_activity);
        mContext = this;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        btnService = (Button) findViewById(R.id.btnService);
        switchBackground = (Switch) findViewById(R.id.switch_background);
        btnService.setOnClickListener(this);
        switchBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isCheckedService = true;
                }else{
                    isCheckedService = false;
                }

            }
        });

        if(isCheckedService)
            btnService.setText(getButtonTextService());
        else
            btnService.setText(getButtonTextBroadcast());
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnService:
                Intent alarm;
                if(isCheckedService) {
                    alarm = new Intent(mContext, MyService.class);
                    if (!isStreamingRunningService(alarm)) {
                        ///DRUHA MOZNOST
                        Log.d(TAG, "AlarmReceiver Intent Created");
                        //Tu funguje len getService() !!!!!!!!!!!!!!!!!!!!!!!
                        PendingIntent pendingIntent = PendingIntent.getService(mContext, REQUEST_CODE, alarm, 0);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 1000, pendingIntent);
                        Log.d(TAG, "AlarmReceiver Intent will start in 5 seconds!..");
                    } else {
                        PendingIntent pi = PendingIntent.getService(mContext, REQUEST_CODE, alarm, 0);
                        pi.cancel();
                        REQUEST_CODE++;
                    }
                    btnService.setText(getButtonTextService());
                }
                else {
                    alarm = new Intent(mContext, AlarmReceiver.class);
                    if (!isStreamingRunningBroadcast(alarm)) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.add(Calendar.SECOND, 5);

                        ///PRVA MOZNOST
                        Log.d(TAG, "AlarmReceiver Intent Created");
                        //TU funguje len getBroadcast() !!!!!!!!!!!!!!!!!!!!!!!
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, alarm, 0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Log.d(TAG, "AlarmReceiver Intent will start in 5 seconds!..");
                    } else {
                        PendingIntent pi = PendingIntent.getBroadcast(mContext, REQUEST_CODE, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
                        pi.cancel();
                        REQUEST_CODE++;
                    }
                    btnService.setText(getButtonTextBroadcast());
                }
                break;
        }
    }

    private boolean isStreamingRunningService(Intent alarm){
        return PendingIntent.getService(mContext, REQUEST_CODE, alarm, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private String getButtonTextService(){
        Intent alarm = new Intent(mContext, MyService.class);

        if(isStreamingRunningService(alarm))
            return getResources().getString(R.string.btn_running);
        else
            return getResources().getString(R.string.btn_stopped);
    }

    private boolean isStreamingRunningBroadcast(Intent alarm){
        return PendingIntent.getBroadcast(mContext, REQUEST_CODE, alarm, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private String getButtonTextBroadcast(){
        Intent alarm = new Intent(mContext, AlarmReceiver.class);

        if(isStreamingRunningBroadcast(alarm))
            return getResources().getString(R.string.btn_running);
        else
            return getResources().getString(R.string.btn_stopped);
    }
}

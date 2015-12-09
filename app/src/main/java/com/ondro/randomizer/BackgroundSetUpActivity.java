package com.ondro.randomizer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Ondro on 08-Dec-15.
 */
public class BackgroundSetUpActivity extends AppCompatActivity implements OnClickListener{
    //public static final int PENDING_INTENT_REQUEST_CODE = 39003;
    //public static final String SAVED_REQUEST_CODE = "rmgktoegt";
    private static int REQUEST_CODE = 39003;

    private Button btnService;
    private Context mContext;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_activity);
        mContext = this;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        btnService = (Button) findViewById(R.id.btnService);
        btnService.setOnClickListener(this);
        btnService.setText(getButtonText());
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnService:
                Intent alarm = new Intent(mContext, AlarmReceiver.class);
                if(!isStreamingRunning(alarm)) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, alarm, 0);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000, pendingIntent);
                }
                else{
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, alarm, 0);
                    alarmManager.cancel(pendingIntent);
                    REQUEST_CODE++;
                }
                btnService.setText(getButtonText());
                break;
        }
    }

    private boolean isStreamingRunning(Intent alarm){
        return PendingIntent.getBroadcast(mContext, REQUEST_CODE, alarm, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private String getButtonText(){
        Intent alarm = new Intent(mContext, AlarmReceiver.class);

        if(isStreamingRunning(alarm))
            return getResources().getString(R.string.btn_running);
        else
            return getResources().getString(R.string.btn_stopped);
    }
}

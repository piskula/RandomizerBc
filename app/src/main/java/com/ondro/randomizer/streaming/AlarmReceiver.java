package com.ondro.randomizer.streaming;

import android.support.v4.content.WakefulBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Ondro on 06-Dec-15.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    public final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, RepeatingService.class);
        Log.d(TAG, "Intent -> going to startWakefulService");
        startWakefulService(context, background);
        //context.startService(background);
    }
}

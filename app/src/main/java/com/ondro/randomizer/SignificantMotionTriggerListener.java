package com.ondro.randomizer;

import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Ondro on 25-Nov-15.
 */
public class SignificantMotionTriggerListener extends TriggerEventListener {
    private TextView mTextView;

    public SignificantMotionTriggerListener(TextView textView) {
        mTextView = textView;
    }

    @Override
    public void onTrigger(TriggerEvent event) {
        if (event.values[0] == 1) {
            Calendar cal = Calendar.getInstance();
            StringBuilder str = new StringBuilder();

            str.append(cal.get(Calendar.HOUR));
            str.append(":");
            str.append(cal.get(Calendar.MINUTE));
            str.append(":");
            str.append(cal.get(Calendar.SECOND));
            str.append(".");
            str.append(cal.get(Calendar.MILLISECOND));

            mTextView.append("Motion CAPTURED at " + str.toString() +  "\n");
            mTextView.append("Significant Motion DISABLED" + "\n");
            // Sensor is auto disabled.
        }
    }
}

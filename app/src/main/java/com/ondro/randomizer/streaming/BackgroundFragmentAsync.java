package com.ondro.randomizer.streaming;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ondro.randomizer.mainfragments.BaseSensorFragment;
import com.ondro.randomizer.MainActivity;
import com.ondro.randomizer.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ondro on 02-Dec-15.
 */
public class BackgroundFragmentAsync extends BaseSensorFragment implements OnClickListener, SensorEventListener {
    public static final int ID_MSG = 1;
    public static final String STREAMING_STOP = "No running stream";
    public static final String OPEN_BACKGROUND_FRAGMENT = "obf";

    private static View rootView;
    private Activity context;

    private NotificationManager mNotifyManager;

    private Button btnStart;
    private Button btnCancel;
    private ProgressBar progressBar;
    private TextView tvFile;
    private TextView tvPer;
    private Spinner spinnerTime;
    private Spinner spinnerFreq;

    private static MyBackgroundTask objMyTask;

    private Integer[] times = new Integer[]{1,3,5,10,15,20,30,45,60,90,120};
    private Integer[] freq = new Integer[]{1,2,5,10,20,30,60,120};

    private float accTextView01;
    private float accTextView02;
    private float accTextView03;
    private float linAccTextView01;
    private float linAccTextView02;
    private float linAccTextView03;
    private float rotVecTextView01;
    private float rotVecTextView02;
    private float rotVecTextView03;
    private float rotVecTextView04;
    private float rotVecTextView05;
    private float gravityTextView01;
    private float gravityTextView02;
    private float gravityTextView03;
    private float gyroscopeTextView01;
    private float gyroscopeTextView02;
    private float gyroscopeTextView03;
    private float lightTextView01;
    private float proximityTextView01;
    private float magneticTextView01;
    private float magneticTextView02;
    private float magneticTextView03;
    private float orientationTextView01;
    private float orientationTextView02;
    private float orientationTextView03;
    private float pressureTextView;
    private float ambientTemperatureTextView;
    private float microphone01TextView;
    private float microphone02TextView;
    private float batteryTextView01;
    private float batteryTextView02;
    private float batteryTextView03;
    private float batteryTextView04;
    private float batteryTextView05;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.background_fragment_async, container, false);

            btnStart = (Button) rootView.findViewById(R.id.btnstart);
            btnCancel = (Button) rootView.findViewById(R.id.btncancel);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
            tvFile = (TextView) rootView.findViewById(R.id.tv1);
            tvPer = (TextView) rootView.findViewById(R.id.tvper);
            spinnerTime = (Spinner) rootView.findViewById(R.id.spinner_time);
            spinnerFreq = (Spinner) rootView.findViewById(R.id.spinner_interval);

            if(objMyTask == null || objMyTask.isCancelled() || objMyTask.getStatus().equals(AsyncTask.Status.FINISHED)){
                btnStart.setEnabled(true);
                btnCancel.setEnabled(false);
            }
            else{
                btnStart.setEnabled(false);
                btnCancel.setEnabled(true);
            }

            btnStart.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            tvPer.setText(STREAMING_STOP);
            tvFile.setText("output file name");
            spinnerTime.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, times));
            spinnerFreq.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, freq));
        }

        if(context == null)
            context = getActivity();
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        return rootView;
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                accTextView01 = event.values[0];
                accTextView02 = event.values[1];
                accTextView03 = event.values[2];
                refreshOrientation();
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                linAccTextView01 = event.values[0];
                linAccTextView02 = event.values[1];
                linAccTextView03 = event.values[2];
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                rotVecTextView01 = event.values[0];
                rotVecTextView02 = event.values[1];
                rotVecTextView03 = event.values[2];
                rotVecTextView04 = event.values[3];
                rotVecTextView05 = event.values[4];
                break;
            case Sensor.TYPE_GRAVITY:
                gravityTextView01 = event.values[0];
                gravityTextView02 = event.values[1];
                gravityTextView03 = event.values[2];
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeTextView01 = event.values[0];
                gyroscopeTextView02 = event.values[1];
                gyroscopeTextView03 = event.values[2];
                break;
            case Sensor.TYPE_LIGHT:
                lightTextView01 = event.values[0];
                break;
            case Sensor.TYPE_PROXIMITY:
                proximityTextView01 = event.values[0];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                magneticTextView01 = event.values[0];
                magneticTextView02 = event.values[1];
                magneticTextView03 = event.values[2];
                refreshOrientation();
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                ambientTemperatureTextView = event.values[0];
                break;
            case Sensor.TYPE_PRESSURE:
                pressureTextView = event.values[0];
                break;
        }
    }

    private void refreshOrientation(){
        if(mGravity != null && mGeomagnetic != null){
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                orientationTextView01 = orientation[0];// * 180 / Math.PI);
                orientationTextView02 = orientation[1];// * 180 / Math.PI);
                orientationTextView03 = orientation[2];// * 180 / Math.PI);
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onResume() {
        super.onResume();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mySensorManager.registerListener(this, mySensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLinearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGravity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(this, mySensorAmbientTemperature, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
    }

    public void onClick(View src){
        switch(src.getId()){
            case R.id.btnstart:
                if(objMyTask == null || objMyTask.isCancelled() || objMyTask.getStatus().equals(AsyncTask.Status.FINISHED)){
                    String fileTitle = getTitle();
                    BufferedWriter bw;
                    try{
                        bw = new BufferedWriter(new FileWriter(new File(
                                context.getExternalFilesDir(null), fileTitle), true));
                    }
                    catch(IOException e){
                        e.printStackTrace();
                        Toast.makeText(context, "Can't open BufferedWriter", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    tvFile.setText("saving to: " + fileTitle);
                    objMyTask = new MyBackgroundTask();
                    objMyTask.execute(bw);
                    btnStart.setEnabled(false);
                    btnCancel.setEnabled(true);
                }
                break;
            case R.id.btncancel:
                if(!objMyTask.getStatus().equals(AsyncTask.Status.RUNNING)){
                    Toast.makeText(context, "Nothing running", Toast.LENGTH_SHORT).show();
                    break;
                }
                objMyTask.cancel(true);
                btnStart.setEnabled(true);
                btnCancel.setEnabled(false);
                break;
        }
    }

    private class MyBackgroundTask extends AsyncTask<BufferedWriter, Integer, Void> {
        private Builder mBuilder;
        int freq =  Integer.parseInt(spinnerFreq.getSelectedItem().toString());
        int time =  Integer.parseInt(spinnerTime.getSelectedItem().toString()) * 60;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mBuilder = new Builder(context);
            mBuilder.setContentTitle("Streaming")
                    .setContentText("..in progress")
                    .setSmallIcon(R.drawable.ic_drawer);
            mBuilder.setProgress(100, 0, false);

            Intent resultIntent = new Intent(context, MainActivity.class);
            resultIntent.setAction(OPEN_BACKGROUND_FRAGMENT);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            mNotifyManager.notify(ID_MSG, mBuilder.build());
        }

        @Override
        protected Void doInBackground(BufferedWriter... params) {

            for(int i = 0; i < (time / freq); i++){
                if(isCancelled()){
                    break;
                }
                else{
                    try{
                        params[0].append(getLine());
                        params[0].flush();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Can't write to BufferedWriter", Toast.LENGTH_SHORT).show();
                    }
                    publishProgress(i * 100 / (time / freq));

                    try {
                        Thread.sleep(freq * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mBuilder.setProgress(100, values[0], false);
            int minutes = (time * (100 - values[0])) / (100 * 60);
            int seconds = (time * (100 - values[0])) / 100 % 60;
            mBuilder.setContentText("Estimating " + minutes + " minutes, " + seconds + " seconds");
            mNotifyManager.notify(ID_MSG, mBuilder.build());

            progressBar.setProgress(values[0]);
            tvPer.setText(values[0] + " %");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mBuilder.setContentText("Streaming complete!");
            // Removes the progress bar
            mBuilder.setProgress(0, 0, false);
            mBuilder.setAutoCancel(true);
            tvPer.setText("100 %");
            mNotifyManager.notify(ID_MSG, mBuilder.build());
            btnStart.setEnabled(true);
            btnCancel.setEnabled(false);
        }
    }

    private String getTitle(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        return dateFormat.format(cal.getTime()) + ".txt";
    }

    private String getLine(){
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        sb.append(dateFormat.format(cal.getTime()));
        sb.append(".");
        sb.append(cal.get(Calendar.MILLISECOND));
        sb.append("|acc:");
        sb.append(accTextView01);
        sb.append(",");
        sb.append(accTextView02);
        sb.append(",");
        sb.append(accTextView03);
        sb.append("|magn:");
        sb.append(magneticTextView01);
        sb.append(",");
        sb.append(magneticTextView02);
        sb.append(",");
        sb.append(magneticTextView03);
        sb.append("|orient:");
        sb.append(orientationTextView01);
        sb.append(",");
        sb.append(orientationTextView02);
        sb.append(",");
        sb.append(orientationTextView03);
        sb.append("|gyro:");
        sb.append(gyroscopeTextView01);
        sb.append(",");
        sb.append(gyroscopeTextView02);
        sb.append(",");
        sb.append(gyroscopeTextView03);
        sb.append("|light:");
        sb.append(lightTextView01);
        sb.append("|proximity:");
        sb.append(proximityTextView01);
        sb.append("|gravity:");
        sb.append(gravityTextView01);
        sb.append(",");
        sb.append(gravityTextView02);
        sb.append(",");
        sb.append(gravityTextView03);
        sb.append("|linacc:");
        sb.append(linAccTextView01);
        sb.append(",");
        sb.append(linAccTextView02);
        sb.append(",");
        sb.append(linAccTextView03);
        sb.append("|rotvec:");
        sb.append(rotVecTextView01);
        sb.append(",");
        sb.append(rotVecTextView02);
        sb.append(",");
        sb.append(rotVecTextView03);
        sb.append(",");
        sb.append(rotVecTextView04);
        sb.append(",");
        sb.append(rotVecTextView05);
        sb.append("|temperature:");
        sb.append(ambientTemperatureTextView);
        sb.append("|pressure:");
        sb.append(pressureTextView);
        sb.append("|\r\n");

        return sb.toString();
    }
}

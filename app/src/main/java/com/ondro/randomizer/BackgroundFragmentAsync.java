package com.ondro.randomizer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ondro on 02-Dec-15.
 */
public class BackgroundFragmentAsync extends Fragment implements OnClickListener {
    public static final int ID_MSG = 1;
    public static final String STREAMING_STOP = "No running stream";
    public static final String OPEN_BACKGROUND_FRAGMENT = "obf";

    private static View rootView;
    private Activity context;

    private NotificationManager mNotifyManager;

    private Button btnStart;
    private Button btnCancel;
    private Button btnCreate;
    private ProgressBar progressBar;
    private TextView tvFile;
    private TextView tvPer;
    private Spinner spinnerTime;
    private Spinner spinnerFreq;

    private static MyBackgroundTask objMyTask;

    private Integer[] times = new Integer[]{5,10,15,20,30,45,60,90,120};
    private Integer[] freq = new Integer[]{1,2,5,10,20,30,60,120};

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.background_fragment_async, container, false);

            btnStart = (Button) rootView.findViewById(R.id.btnstart);
            btnCancel = (Button) rootView.findViewById(R.id.btncancel);
            btnCreate = (Button) rootView.findViewById(R.id.btncreate);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
            tvFile = (TextView) rootView.findViewById(R.id.tv1);
            tvPer = (TextView) rootView.findViewById(R.id.tvper);
            spinnerTime = (Spinner) rootView.findViewById(R.id.spinner_time);
            spinnerFreq = (Spinner) rootView.findViewById(R.id.spinner_interval);

            btnStart.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnCreate.setOnClickListener(this);
            tvPer.setText(STREAMING_STOP);
            spinnerTime.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, times));
            spinnerFreq.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, freq));
        }

        if(context == null)
            context = getActivity();
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        return rootView;
    }

    ////////
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
    }
    ///////

    public void onClick(View src){
        switch(src.getId()){
            case R.id.btnstart:
                if(objMyTask == null){
                    Toast.makeText(context, "First create some Task", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(objMyTask.isCancelled()){
                    Toast.makeText(context, "Must Create New One", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(objMyTask.getStatus().equals(AsyncTask.Status.RUNNING)){
                    Toast.makeText(context, "Still Running", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(objMyTask.getStatus().equals(AsyncTask.Status.FINISHED)){
                    Toast.makeText(context, "Done! Create New One", Toast.LENGTH_SHORT).show();
                    break;
                }

                String fileTitle = getTitle();
                BufferedWriter bw;
                try{
                    bw = new BufferedWriter(new FileWriter(new File(
                            getActivity().getExternalFilesDir(null), fileTitle), true));
                }
                catch(IOException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Can't open BufferedWriter", Toast.LENGTH_SHORT).show();
                    break;
                }
                tvFile.setText("saving to: " + fileTitle);
                objMyTask.execute(bw);
               break;
            case R.id.btncancel:
                if(!objMyTask.getStatus().equals(AsyncTask.Status.RUNNING)){
                    Toast.makeText(context, "Nothing running", Toast.LENGTH_SHORT).show();
                    break;
                }
                objMyTask.cancel(true);
                break;
            case R.id.btncreate:
                if(objMyTask == null || objMyTask.isCancelled()
                        || objMyTask.getStatus().equals(AsyncTask.Status.FINISHED))
                    objMyTask = new MyBackgroundTask();
                break;
        }
    }

    private class MyBackgroundTask extends AsyncTask<BufferedWriter, Integer, Void> {
        private Builder mBuilder;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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
                        Calendar cal = Calendar.getInstance();

                        params[0].append(dateFormat.format(cal.getTime()));
                        params[0].append(".");
                        params[0].append(cal.get(Calendar.MILLISECOND) + "\n");
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

            mBuilder.setContentText("Streaming complete");
            // Removes the progress bar
            mBuilder.setProgress(0, 0, false);
            mNotifyManager.notify(ID_MSG, mBuilder.build());
        }
    }

    private String getTitle(){
        StringBuilder sb = new StringBuilder("output_");

        Calendar cal = Calendar.getInstance();
        sb.append(cal.get(Calendar.YEAR));
        sb.append("_");
        sb.append(cal.get(Calendar.MONTH));
        sb.append("_");
        sb.append(cal.get(Calendar.DAY_OF_MONTH));
        sb.append("-");
        sb.append(cal.get(Calendar.HOUR));
        sb.append(":");
        sb.append(cal.get(Calendar.MINUTE));

        return sb.toString();
    }
}

package com.ondro.randomizer.mainfragments;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ondro.randomizer.R;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Ondro on 27-Oct-15.
 */
public class CameraActivity extends Activity {
    private static final String TAG = "CameraActivity";
    private static final int NUMBER_OF_PHOTOS = 60;
    private static final long PAUSE_BETWEEN_PHOTOS = 50; //in milliseconds

    private Camera mCamera = null;

    private CameraView mCameraView = null;

    private static int pictureCounter = 0;
    private static Thread myCameras[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        try{
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            myCameras = new Thread[NUMBER_OF_PHOTOS];
            for(int i = 0; i < myCameras.length; i++){
                myCameras[i] = new Thread(myStreamingBackgroundTask);
                myCameras[i].start();
                try{
                    Thread.sleep(PAUSE_BETWEEN_PHOTOS);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                    return false;
                }
                Log.d(TAG, "jedu " + i);
            }
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT);
        }
        return true;
    }

    private String getFileTitle(int count){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        return dateFormat.format(cal.getTime()) + "." + cal.getTimeInMillis() + "." + count + ".png";
    }

    private Runnable myStreamingBackgroundTask = new Runnable() {
        @Override
        public void run() {
            takeScreenshot(pictureCounter++);
        }
    };

    private void takeScreenshot(int count) {
        try{
            Process sh = Runtime.getRuntime().exec("su", null,null);
            OutputStream os = sh.getOutputStream();
            Log.d(TAG, getExternalFilesDir(null).toString());
            os.write(("/system/bin/screencap -p " + getExternalFilesDir(null) + "/" + getFileTitle(count)).getBytes("ASCII"));
            os.flush();
            os.close();
            sh.waitFor();
        }
        catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}

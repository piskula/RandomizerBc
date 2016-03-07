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
public class CameraActivity extends Activity {//implements OnClickListener {
    private static final String TAG = "CameraActivity";

    private Camera mCamera = null;

    private CameraView mCameraView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotationvector_layout);

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
            takeScreenshot();
            Toast.makeText(this, "OK, Captured!", Toast.LENGTH_SHORT);
        }
        return true;
    }

    private String getFileTitle(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        return dateFormat.format(cal.getTime()) + ".png";
    }

    private void takeScreenshot() {
        try{
            Process sh = Runtime.getRuntime().exec("su", null,null);
            OutputStream os = sh.getOutputStream();
            os.write(("/system/bin/screencap -p " + getExternalFilesDir(null) + "/" + getFileTitle()).getBytes("ASCII"));
            os.flush();
            os.close();
            sh.waitFor();
        }
        catch (IOException e_io){
            e_io.printStackTrace();
        }
        catch(InterruptedException e_in){
            e_in.printStackTrace();
        }
    }
}

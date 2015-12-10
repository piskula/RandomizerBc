package com.ondro.randomizer.mainfragments;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ondro.randomizer.ListSensorsAdapter;
import com.ondro.randomizer.R;

import java.util.List;

/**
 * Created by Ondro on 20-Nov-15.
 */
public class AvailableSensorFragment  extends ListFragment {
    private View rootView;
    private Bundle args;

    //private TextView hopa;
    private SensorManager mySensorManager;
    private ListSensorsAdapter mAdapter;
    private List<Sensor> listSensors;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.available_sensor_list, container, false);

        mySensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        args = getArguments();

        listSensors = mySensorManager.getSensorList(Sensor.TYPE_ALL);
        mAdapter = new ListSensorsAdapter(getActivity(), listSensors);
        setListAdapter(mAdapter);

        return rootView;
    }
}

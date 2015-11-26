package com.ondro.randomizer;

import android.hardware.Sensor;

import java.util.Comparator;

/**
 * Created by Ondro on 24-Nov-15.
 */
public class CustomSensorComparator implements Comparator<Sensor> {
    @Override
    public int compare(Sensor s1, Sensor s2){
        return s1.getType() - s2.getType();
    }
}

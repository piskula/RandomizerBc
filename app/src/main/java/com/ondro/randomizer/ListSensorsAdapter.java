package com.ondro.randomizer;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ondro on 24-Nov-15.
 */
public class ListSensorsAdapter extends BaseAdapter {
    private List<Sensor> mItems;
    private LayoutInflater mInflater;

    public ListSensorsAdapter(Context context, List<Sensor> sensors){
        this.mItems = new ArrayList<>();
        boolean isThere = false;
        for (Sensor current : sensors) {
            isThere = false;
            for (Sensor alreadyIn: mItems) {
                if(current.getType() == alreadyIn.getType()){
                    isThere = true;
                    break;
                }
            }
            if(!isThere){
                mItems.add(current);
            }
        }
        Collections.sort(this.mItems, new CustomSensorComparator());
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0;
    }

    @Override
    public Sensor getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getType() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        ViewHolder holder;

        if(v == null){
            v = mInflater.inflate(R.layout.item_sensor, parent, false);
            holder = new ViewHolder();

            holder.titleTextView = (TextView) v.findViewById(R.id.item_sensor_title);
            holder.subtitleTextView = (TextView) v.findViewById(R.id.item_sensor_subtitle);

            v.setTag(holder);
        }
        else{
            holder = (ViewHolder) v.getTag();
        }

        Sensor currentItem = getItem(position);
        if(currentItem != null){
            switch (currentItem.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    holder.titleTextView.setText("Accelerometer");
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    holder.titleTextView.setText("Linear Accelerometer");
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    holder.titleTextView.setText("Rotation Vector");
                    break;
                case Sensor.TYPE_GRAVITY:
                    holder.titleTextView.setText("Gravity");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    holder.titleTextView.setText("Gyroscope");
                    break;
                case Sensor.TYPE_LIGHT:
                    holder.titleTextView.setText("Light");
                    break;
                case Sensor.TYPE_PROXIMITY:
                    holder.titleTextView.setText("Proximity");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    holder.titleTextView.setText("Magnetic Field (Compass)");
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    holder.titleTextView.setText("Ambient Temperature");
                    break;
                case Sensor.TYPE_PRESSURE:
                    holder.titleTextView.setText("Ambient Pressure");
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    holder.titleTextView.setText("Rotation Vector (GAME)");
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    holder.titleTextView.setText("Rotation Vector (GeoMagnetic)");
                    break;
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    holder.titleTextView.setText("Gyroscope Uncalibrated");
                    break;
                case Sensor.TYPE_HEART_RATE:
                    holder.titleTextView.setText("Heart Rate");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    holder.titleTextView.setText("Magnetic Field (Compass) Uncalibrated");
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    holder.titleTextView.setText("Relative Humidity");
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    holder.titleTextView.setText("Significant Motion");
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    holder.titleTextView.setText("Step Counter");
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    holder.titleTextView.setText("Step Detector");
                    break;
                case 65539:
                    holder.titleTextView.setText("User Profile");
                    break;
                default:
                    if(Build.VERSION.SDK_INT >= 20){
                        holder.titleTextView.setText(currentItem.getStringType());
                    }
                    else{
                        holder.titleTextView.setText(currentItem.getName());
                    }
            }
            if(Build.VERSION.SDK_INT >= 21){
                holder.subtitleTextView.setText(currentItem.getType() + " || " + currentItem.toString()
                        + currentItem.getReportingMode());
            }
            else{
                holder.subtitleTextView.setText(currentItem.getType() + " || " + currentItem.toString());
            }
        }

        return v;
    }

    public List<Sensor> getItems() {
        return mItems;
    }

    class ViewHolder {
        TextView titleTextView;
        TextView subtitleTextView;
    }
}

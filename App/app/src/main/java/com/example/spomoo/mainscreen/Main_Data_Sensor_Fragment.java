package com.example.spomoo.mainscreen;

/*
 * Main_Data_Sensor_Fragment of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentMainDataSensorBinding;
import com.example.spomoo.sensorrecording.AccelerometerData;
import com.example.spomoo.sensorrecording.RotationData;
import com.example.spomoo.sensorrecording.StepsData;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

/*
 * Contains diagram for accelerometer and rotation data
 * Contains text view for steps count
 */
public class Main_Data_Sensor_Fragment extends Fragment {

    private FragmentMainDataSensorBinding binding;
    private LocalDatabaseManager db;    //local SQL database
    private SharedPrefManager sharedPrefManager;    //contains inputted date
    private ArrayList<AccelerometerData> accelData;  //contains accelerometer data of inputted date
    private ArrayList<RotationData> rotationData;  //contains rotation data of inputted date
    private StepsData stepsData;  //contains steps data of inputted date
    private MaterialTextView stepsValueText;    //text for steps
    private BarChart accelBarChart; //barchart of accelerometer data
    private BarChart rotationBarChart;  //barchart of rotation sensor data
    private Context context;    //cache context to fix errors
    private LayoutInflater inflater;    //cache LayoutInflater

    //colors for dynamic coloring
    int color1;
    int color2;
    int color3;
    int color4;
    int colorText;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainDataSensorBinding.inflate(inflater, container, false);

        //cache components
        db = new LocalDatabaseManager(getContext());
        if(getContext() != null) context = getContext();
        if(inflater != null) this.inflater = inflater;
        sharedPrefManager = SharedPrefManager.getInstance(context);
        stepsValueText = binding.mainDataSensorStepsValuetext;
        accelBarChart = binding.mainDataSensorAccelerometerBarchart;
        rotationBarChart = binding.mainDataSensorRotationBarchart;

        //set bar chart invisible at start
        accelBarChart.setVisibility(View.GONE);
        rotationBarChart.setVisibility(View.GONE);

        //get colors for dynamic coloring
        color1 = context.getColor(R.color.md_theme_dark_primary);
        color2 = context.getColor(R.color.md_theme_dark_tertiary);
        color3 = context.getColor(R.color.md_theme_dark_secondary);
        color4 = context.getColor(R.color.md_theme_dark_error);
        TypedValue typedValue = new TypedValue();;
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
        colorText = typedValue.data;

        //create listview containing items based on inputted date of Main_Data_Fragment
        createUI();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //creates the UI based on the inputted date of Main_Data_Fragment which is received via broadcast
    private void createUI(){
        //create UI from saved date
        createUIHelper(sharedPrefManager.getString(SharedPrefManager.KEY_DATA_DATE));

        //setup broadcast receiver to receive inputted date
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsBroadcast.BROADCAST_DATA_DATE_INPUT);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                createUIHelper(intent.getStringExtra(ConstantsBroadcast.BROADCAST_DATA_DATE_INPUT_VALUE));
            }
        };
        getContext().registerReceiver(broadcastReceiver, intentFilter);
    }

    //setup bar charts and text view based on inputted date
    private void createUIHelper(String date){
        //if no date has been inputted, do nothing
        if(date.isEmpty())
            return;

        //get data of inputted date
        stepsData = db.readStepsDataFromDate(date);
        accelData = db.readAccelerometerDataFromDate(date);
        rotationData = db.readRotationDataFromDate(date);

        //if data for bar charts is not available, set the corresponding bar chart invisible else create diagrams
        if(accelData.size() == 0) accelBarChart.setVisibility(View.GONE);
        else createAccelerometerDiagram();
        if(rotationData.size() == 0) rotationBarChart.setVisibility(View.GONE);
        else createRotationDiagram();
        //set steps value
        if(stepsData != null) stepsValueText.setText(" " + String.valueOf(stepsData.getSteps()));
        else stepsValueText.setText(" ");
    }

    private void createAccelerometerDiagram(){
        //create datasets and set their colors
        BarDataSet xAxisDataSet = new BarDataSet(getAccelDataXAxis(), context.getString(R.string.main_data_sensor_legend_xaxis));
        xAxisDataSet.setColor(color1);
        xAxisDataSet.setValueTextColor(colorText);
        BarDataSet yAxisDataSet = new BarDataSet(getAccelDataYAxis(), context.getString(R.string.main_data_sensor_legend_yaxis));
        yAxisDataSet.setColor(color2);
        yAxisDataSet.setValueTextColor(colorText);
        BarDataSet zAxisDataSet = new BarDataSet(getAccelDataZAxis(), context.getString(R.string.main_data_sensor_zaxis));
        zAxisDataSet.setColor(color3);
        zAxisDataSet.setValueTextColor(colorText);
        BarDataSet accelerationDataSet =new BarDataSet(getAccelDataAcceleration(), context.getString(R.string.main_data_sensor_accelerometer_legend_acceleration));
        accelerationDataSet.setColor(color4);
        accelerationDataSet.setValueTextColor(colorText);

        //load datasets in barchart
        BarData barData = new BarData(xAxisDataSet, yAxisDataSet, zAxisDataSet, accelerationDataSet);
        barData.setBarWidth(0.15f);
        accelBarChart.setData(barData);

        //modify x-Axis to show the times
        XAxis xAxis = accelBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(value < accelData.size()) return accelData.get((int) value).getTime();
                else return "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(colorText);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(2f);

        //modify the barchart
        float barSpace = 0.1f;
        float groupSpace = 0.5f;
        accelBarChart.groupBars(0, groupSpace, barSpace);
        accelBarChart.getDescription().setEnabled(false);
        accelBarChart.getLegend().setTextColor(colorText);
        accelBarChart.getAxisRight().setEnabled(false);
        accelBarChart.getAxisLeft().setTextColor(colorText);
        accelBarChart.getAxisLeft().setAxisLineWidth(2f);
        accelBarChart.animate();
        accelBarChart.setVisibility(View.VISIBLE);
        accelBarChart.invalidate();
    }
    //returns the xAxis values of accelerometer
    private ArrayList<BarEntry> getAccelDataXAxis(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < accelData.size(); i++){
            barEntries.add(new BarEntry(index, (float) accelData.get(i).getxAxis()));
            index += 1;
        }
        return  barEntries;
    }
    //returns the yAxis values of accelerometer
    private ArrayList<BarEntry> getAccelDataYAxis(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < accelData.size(); i++){
            barEntries.add(new BarEntry(index, (float) accelData.get(i).getyAxis()));
            index += 1;
        }
        return  barEntries;
    }
    //returns the zAxis values of accelerometer
    private ArrayList<BarEntry> getAccelDataZAxis(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < accelData.size(); i++){
            barEntries.add(new BarEntry(index, (float) accelData.get(i).getzAxis()));
            index += 1;
        }
        return  barEntries;
    }
    //returns the acceleration values
    private ArrayList<BarEntry> getAccelDataAcceleration(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < accelData.size(); i++){
            barEntries.add(new BarEntry(index, (float) accelData.get(i).getAcceleration()));
            index += 1;
        }
        return  barEntries;
    }

    private void createRotationDiagram(){
        //create datasets and set their colors
        BarDataSet xAxisDataSet = new BarDataSet(getRotationDataXAxis(), context.getString(R.string.main_data_sensor_legend_xaxis));
        xAxisDataSet.setColor(color1);
        xAxisDataSet.setValueTextColor(colorText);
        BarDataSet yAxisDataSet = new BarDataSet(getRotationDataYAxis(), context.getString(R.string.main_data_sensor_legend_yaxis));
        yAxisDataSet.setColor(color2);
        yAxisDataSet.setValueTextColor(colorText);
        BarDataSet zAxisDataSet = new BarDataSet(getRotationDataZAxis(), context.getString(R.string.main_data_sensor_zaxis));
        zAxisDataSet.setColor(color3);
        zAxisDataSet.setValueTextColor(colorText);

        //load datasets in barchart
        BarData barData = new BarData(xAxisDataSet, yAxisDataSet, zAxisDataSet);
        barData.setBarWidth(0.15f);
        rotationBarChart.setData(barData);

        //modify x-Axis to show the times
        XAxis xAxis = rotationBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(value < rotationData.size()) return rotationData.get((int) value).getTime();
                else return "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(colorText);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(2f);

        //modify the barchart
        float barSpace = 0.1f;
        float groupSpace = 0.5f;
        rotationBarChart.groupBars(0, groupSpace, barSpace);
        rotationBarChart.getDescription().setEnabled(false);
        rotationBarChart.getLegend().setTextColor(colorText);
        rotationBarChart.getAxisRight().setEnabled(false);
        rotationBarChart.getAxisLeft().setTextColor(colorText);
        rotationBarChart.getAxisLeft().setAxisLineWidth(2f);
        rotationBarChart.getAxisLeft().setAxisMinimum(0f);
        rotationBarChart.animate();
        rotationBarChart.setVisibility(View.VISIBLE);
        rotationBarChart.invalidate();
    }
    //returns the xAxis values of accelerometer
    private ArrayList<BarEntry> getRotationDataXAxis(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < rotationData.size(); i++){
            barEntries.add(new BarEntry(index, (float) rotationData.get(i).getxRotation()));
            index += 1;
        }
        return  barEntries;
    }
    //returns the yAxis values of accelerometer
    private ArrayList<BarEntry> getRotationDataYAxis(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < rotationData.size(); i++){
            barEntries.add(new BarEntry(index, (float) rotationData.get(i).getyRotation()));
            index += 1;
        }
        return  barEntries;
    }
    //returns the zAxis values of accelerometer
    private ArrayList<BarEntry> getRotationDataZAxis(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < rotationData.size(); i++){
            barEntries.add(new BarEntry(index, (float) rotationData.get(i).getzRotation()));
            index += 1;
        }
        return  barEntries;
    }

    @Override
    public void onResume() {
        super.onResume();

        //re-create UI after reload
        createUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
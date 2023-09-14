package com.example.spomoo.mainscreen;

/*
 * Main_Data_Week_Fragment of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
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
import com.example.spomoo.databinding.FragmentMainDataWeekBinding;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Contains diagram for sport duration, steps and mood of week
 */
public class Main_Data_Week_Fragment extends Fragment {

    private FragmentMainDataWeekBinding binding;    //layout binding
    private LocalDatabaseManager db;    //local SQL database
    private SharedPrefManager sharedPrefManager;    //contains inputted date
    private Context context;    //cache context to fix errors

    private List<String> xValues;   //x values for charts
    private ArrayList<Integer> sportDurationData;  //contains sport duration of week
    private ArrayList<Integer> stepsData;  //contains steps of week
    private ArrayList<Integer> moodData;  //contains mood of week
    private BarChart sportDurationChart; //barchart of sport duration
    private BarChart stepsChart;  //barchart of steps data
    private BarChart moodChart;  //barchart of mood data

    //colors for dynamic coloring
    int color1;
    int colorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainDataWeekBinding.inflate(inflater, container, false);

        //cache components
        db = new LocalDatabaseManager(getContext());
        if(getContext() != null) context = getContext();
        sharedPrefManager = SharedPrefManager.getInstance(context);
        sportDurationChart = binding.mainDataWeekDurationBarchart;
        stepsChart = binding.mainDataWeekStepsBarchart;
        moodChart = binding.mainDataWeekMoodBarchart;

        //get colors for dynamic coloring
        color1 = context.getColor(R.color.md_theme_dark_primary);
        TypedValue typedValue = new TypedValue();;
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
        colorText = typedValue.data;

        //get x values
        xValues = Arrays.asList(getString(R.string.main_data_week_monday), getString(R.string.main_data_week_tuesday), getString(R.string.main_data_week_wednesday),
                getString(R.string.main_data_week_thursday), getString(R.string.main_data_week_friday), getString(R.string.main_data_week_saturday), getString(R.string.main_data_week_sunday));

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

    //setup bar charts based on inputted date
    private void createUIHelper(String date){
        //if no date has been inputted make charts invisible
        if(date.isEmpty()){
            sportDurationChart.setVisibility(View.INVISIBLE);
            stepsChart.setVisibility(View.INVISIBLE);
            moodChart.setVisibility(View.INVISIBLE);
            return;
        }

        //make charts visible
        sportDurationChart.setVisibility(View.VISIBLE);
        stepsChart.setVisibility(View.VISIBLE);
        moodChart.setVisibility(View.VISIBLE);

        //get data of week
        sportDurationData = db.getSportDurationOfWeek(date);
        stepsData = db.getStepsOfWeek(date);
        moodData = db.getMoodOfWeek(date);

        //create diagrams
        createDiagram(sportDurationData, sportDurationChart, "duration");
        createDiagram(stepsData, stepsChart, "steps");
        createDiagram(moodData, moodChart, "mood");
    }

    private void createDiagram(ArrayList<Integer> data, BarChart chart, String label){
        //create datasets and set their colors
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int index = 0;
        for(int i = 0; i < data.size(); i++){
            if(data.get(i) < 0) barEntries.add(new BarEntry(index, 0));
            else barEntries.add(new BarEntry(index, data.get(i)));
            index += 1;
        }
        BarDataSet dataSet = new BarDataSet(barEntries, label);
        dataSet.setColor(color1);

        //load datasets in barchart
        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        //modify x-Axis to show days
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(colorText);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(2f);

        //modify the barchart
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextColor(colorText);
        chart.getAxisLeft().setAxisLineWidth(2f);
        chart.getAxisLeft().setAxisMinimum(0f);
        if(label.equals("mood")) chart.getAxisLeft().setAxisMaximum(100f);
        chart.animate();
        chart.setVisibility(View.VISIBLE);
        chart.invalidate();
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
package com.example.spomoo.mainscreen;

/*
 * Main_Data_Sports_Fragment of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentMainDataSportsBinding;
import com.example.spomoo.recordsport.SportData;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

/*
 * Contains a listview which contains items defined in layout listview_item_sportdata
 * By holding long on a item it can be deleted after confirmation in a dialog
 */
public class Main_Data_Sports_Fragment extends Fragment {

    private FragmentMainDataSportsBinding binding;  //layout binding
    private LocalDatabaseManager db;    //local SQL database
    private SharedPrefManager sharedPrefManager;    //contains inputted date
    private ListView listView;  //listview holding items
    private MaterialTextView nodataText;    //textview to show if no data is available
    SportsAdapter sportsAdapter;    //adapter for listview items
    private ArrayList<SportData> data;  //contains sport data of inputted date
    private Context context;    //cache context to fix errors
    private LayoutInflater inflater;    //cache LayoutInflater

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainDataSportsBinding.inflate(inflater, container, false);

        //cache components
        db = new LocalDatabaseManager(getContext());
        listView = binding.mainDataSportsListview;
        if(getContext() != null) context = getContext();
        if(inflater != null) this.inflater = inflater;
        sharedPrefManager = SharedPrefManager.getInstance(context);
        nodataText = binding.mainDataSportNodataText;

        //set nodataText invisible at start
        nodataText.setVisibility(View.GONE);

        //create listview containing items based on inputted date of Main_Data_Fragment
        createList();

        //when long clicking an item, show dialog to ask if selected data should be deleted
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            //show dialog to ask for deletion of data
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.main_data_dialog_delete_heading)
                    .setMessage(R.string.main_data_dialog_delete_content)
                    .setCancelable(true)
                    .setIcon(R.drawable.baseline_delete_24)
                    .setNegativeButton(R.string.main_data_dialog_delete_no, (dialog, which) -> {
                    })
                    .setPositiveButton(R.string.main_data_dialog_delete_yes, (dialog, which) -> {
                        //delete data
                        db.deleteSportData(String.valueOf(data.get(position).getSportId()));
                        data.remove(position);
                        sportsAdapter.remove(sportsAdapter.getItem(position));
                        sportsAdapter.notifyDataSetChanged();
                    })
                    .show();
            return true;
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //creates the listview items based on the inputted date of Main_Data_Fragment which is received via broadcast
    private void createList(){
        //create listview from saved date
        createListHelper(sharedPrefManager.getString(SharedPrefManager.KEY_DATA_DATE));

        //setup broadcast receiver to receive inputted date
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsBroadcast.BROADCAST_DATA_DATE_INPUT);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                createListHelper(intent.getStringExtra(ConstantsBroadcast.BROADCAST_DATA_DATE_INPUT_VALUE));
            }
        };
        getContext().registerReceiver(broadcastReceiver, intentFilter);
    }

    //setup listview based on inputted date
    private void createListHelper(String date){
        //if no date has been inputted, do nothing
        if(date.isEmpty())
            return;

        //get data of inputted date
        data = db.readSportDataFromDate(date);
        //if no data available
        if(data.size() == 0){
            nodataText.setText(context.getString(R.string.main_data_no_data));
            nodataText.setVisibility(View.VISIBLE);
            sportsAdapter = new SportsAdapter(context, data, new ArrayList<String>(), inflater);
            listView.setAdapter(sportsAdapter);
            return;
        }

        //set nodataText invisible
        nodataText.setText("");
        nodataText.setVisibility(View.GONE);

        //sort data by the start time
        data.sort((o1, o2) -> o1.getStart().compareTo(o2.getStart()));

        //create listview items
        ArrayList<String> dummy = new ArrayList<>();
        for (int ind = 0; ind < data.size(); ind++)
            dummy.add("");
        sportsAdapter = new SportsAdapter(context, data, dummy, inflater);
        listView.setAdapter(sportsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        //re-create listview after reload
        createList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //custom adapter for listview items
    class SportsAdapter extends ArrayAdapter<String> {

        LayoutInflater layoutInflater;
        ArrayList<SportData> sports;

        SportsAdapter(Context context, ArrayList<SportData> sports, ArrayList<String> test, LayoutInflater layoutInflater) {
            super(context, R.layout.listview_item_sportdata, R.id.listview_sport_title_text, test);
            this.layoutInflater = layoutInflater;
            this.sports = sports;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //get all UI elements
            View row = this.layoutInflater.inflate(R.layout.listview_item_sportdata, parent, false);
            MaterialTextView title = row.findViewById(R.id.listview_sport_title_text);
            MaterialTextView start = row.findViewById(R.id.listview_sport_start_text);
            MaterialTextView duration = row.findViewById(R.id.listview_sport_duration_text);
            MaterialTextView intensity = row.findViewById(R.id.listview_sport_intensity_text);

            //load texts
            title.setText(sports.get(position).getType());
            start.setText(context.getString(R.string.save_past_sport_start) + ": " + sports.get(position).getStart());
            duration.setText(context.getString(R.string.save_past_sport_duration) + ": " + sports.get(position).getDuration());
            if(sports.get(position).getIntensity() == -1) intensity.setVisibility(View.GONE);
            else intensity.setText(getString(R.string.sport_intensity_data) + ": " + sports.get(position).getIntensity());

            return row;
        }
    }
}
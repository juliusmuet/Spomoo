package com.example.spomoo.mainscreen;

/*
 * Main_Data_Fragment of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentMainDataBinding;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Contains a tab layout and a viewpager to show the three child fragments Main_Data_Sports_Fragment, Main_Data_Questionnaire_Fragment, Main_Data_Sensors_Fragment
 * The adapter SectionsPagerAdapter contains the three child fragments
 * Contains a TextInputEditText which opens a Date Picker on click to select a date which is sent to the child fragments via broadcast
 */
public class Main_Data_Fragment extends Fragment {

    private FragmentMainDataBinding binding;    //layout binding

    private SharedPrefManager sharedPrefManager;    //store inputted date

    private boolean datePickerAdded = false;    //only one date picker can be shown at a time

    TextInputEditText dateInput;    //cache dateInput

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(getContext());

        //setup swipe
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getChildFragmentManager());
        ViewPager mViewPager = binding.mainDataViewpager;
        mViewPager.setAdapter(sectionsPagerAdapter);

        //setup tab menu
        TabLayout tabLayout = binding.mainDataTablayout;
        tabLayout.setupWithViewPager(mViewPager);

        //cache date input
        dateInput = binding.mainDataDateInput;

        //setup date picker for date input
        //create date picker
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder
                .datePicker()
                .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build())
                .build();
        //setup on confirm button listener
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            long dateInMillis = Long.parseLong(selection.toString());
            Date date = new Date(dateInMillis);
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");  //convert inputted date to dd.mm.yyyy format
            dateInput.setText(formatter.format(date));   //set date as text of text view
            datePickerAdded = false;    //date picker is not shown anymore

            //save inputted date
            sharedPrefManager.setString(SharedPrefManager.KEY_DATA_DATE, dateInput.getText().toString());

            //setup broadcast to inform child fragments about inputted date
            setBroadcast();
        });
        //setup all other buttons to set that date picker is not shown anymore
        materialDatePicker.addOnCancelListener(dialog -> {
            datePickerAdded = false;
        });
        materialDatePicker.addOnDismissListener(dialog -> {
            datePickerAdded = false;
        });
        materialDatePicker.addOnNegativeButtonClickListener(v -> {
            datePickerAdded = false;
        });
        //open date picker on click on birthday text view
        dateInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(!datePickerAdded && hasFocus) {
                datePickerAdded = true;
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        dateInput.setOnClickListener(v -> {
            if(!datePickerAdded) {
                datePickerAdded = true;
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

    }

    //creates broadcast for child fragments containing the inputted date
    private void setBroadcast(){
        Intent intent = new Intent();
        intent.setAction(ConstantsBroadcast.BROADCAST_DATA_DATE_INPUT);
        intent.putExtra(ConstantsBroadcast.BROADCAST_DATA_DATE_INPUT_VALUE, dateInput.getText().toString());
        getContext().sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        //setup broadcast to inform child fragments about inputted date
        setBroadcast();

        //store inputted date
        sharedPrefManager.setString(SharedPrefManager.KEY_DATA_DATE, dateInput.getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //custom adapter for viewpager to with child fragments
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.main_data_week_title ,R.string.main_data_tab_sport_activities, R.string.main_data_tab_questionnaires, R.string.main_data_tab_sensors};
        private final Fragment[] fragments = new Fragment[]{new Main_Data_Week_Fragment() ,new Main_Data_Sports_Fragment(), new Main_Data_Questionnaire_Fragment(), new Main_Data_Sensor_Fragment()};
        private final Context mContext;

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

}
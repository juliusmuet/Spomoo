package com.example.spomoo.mainscreen;

/*
 * Main_Record_Fragment of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spomoo.databinding.FragmentMainRecordBinding;
import com.example.spomoo.questionnaire.Questionnaire_Activity;
import com.example.spomoo.recordsport.Record_Live_Activity;
import com.example.spomoo.recordsport.Save_PastSport_Activity;

/*
 * Record Fragment of Main Activity for navigating to activities Record_Live_Activity, Save_PastSport_Activity and Questionnaire_Activity
 * Contains three clickable card views to navigate to these activities
 */
public class Main_Record_Fragment extends Fragment {

    private FragmentMainRecordBinding binding;  //layout binding

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainRecordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //card view for Record_Live_Activity
        binding.mainRecordCardview1.setOnClickListener(v -> startActivity(new Intent(getActivity(), Record_Live_Activity.class)));

        //card view for Save_PastSport_Activity
        binding.mainRecordCardview2.setOnClickListener(v -> startActivity(new Intent(getActivity(), Save_PastSport_Activity.class)));

        //card view for Questionnaire_Activity
        binding.mainRecordCardview3.setOnClickListener(v -> startActivity(new Intent(getActivity(), Questionnaire_Activity.class)));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
package com.example.spomoo.questionnaire;

/*
 * Questionnaire_MDBF_Fragment of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentQuestionnaireMdbfBinding;
import com.example.spomoo.utility.SharedPrefManager;
import com.example.spomoo.utility.TimeDateFormatter;

/*
 * MDBF Fragment of Questionnaire Activity for MDBF questions
 * Contains seekbars for inputting values and text views to show inputted values and meanings of values
 * Contains a floating action button for saving inputted values and navigating to Questionnaire_EventAppraisal_Fragment
 * Contains a skip text which navigates to Questionnaire_EventAppraisal_Fragment on click and does not save any values
 */
public class Questionnaire_MDBF_Fragment extends Fragment {

    private FragmentQuestionnaireMdbfBinding binding;   //layout binding

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuestionnaireMdbfBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache seekbars
        SeekBar satisfiedBar = binding.questionnaireMdbfBarSatisfied;
        SeekBar calmBar = binding.questionnaireMdbfBarCalm;
        SeekBar wellBar = binding.questionnaireMdbfBarWell;
        SeekBar relaxedBar = binding.questionnaireMdbfBarRelaxed;
        SeekBar energeticBar = binding.questionnaireMdbfBarEnergetic;
        SeekBar awakeBar = binding.questionnaireMdbfBarAwake;

        //setup seekbars' functionality
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_mdbf_satisfied_value), binding.questionnaireMdbfSatisfiedValue, satisfiedBar);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_mdbf_calm_value), binding.questionnaireMdbfCalmValue, calmBar);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_mdbf_well_value), binding.questionnaireMdbfWellValue, wellBar);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_mdbf_relaxed_value), binding.questionnaireMdbfRelaxedValue, relaxedBar);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_mdbf_energetic_value), binding.questionnaireMdbfEnergeticValue, energeticBar);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_mdbf_awake_value), binding.questionnaireMdbfAwakeValue, awakeBar);

        //setup save data floating action button
        binding.questionnaireMdbfFabSave.setOnClickListener(v -> {
            //save inputted values
            QuestionnaireData data = new QuestionnaireData();
            data.setTime(TimeDateFormatter.getTimeString());
            data.setDate(TimeDateFormatter.getDateString());
            if(Questionnaire_Activity.checkInputted(satisfiedBar)) data.setMdbfSatsified(satisfiedBar.getProgress());
            else data.setMdbfSatsified(-1);
            if(Questionnaire_Activity.checkInputted(calmBar)) data.setMdbfCalm(calmBar.getProgress());
            else data.setMdbfCalm(-1);
            if(Questionnaire_Activity.checkInputted(wellBar)) data.setMdbfWell(wellBar.getProgress());
            else data.setMdbfWell(-1);
            if(Questionnaire_Activity.checkInputted(relaxedBar)) data.setMdbfRelaxed(relaxedBar.getProgress());
            else data.setMdbfRelaxed(-1);
            if(Questionnaire_Activity.checkInputted(energeticBar)) data.setMdbfEnergetic(energeticBar.getProgress());
            else data.setMdbfEnergetic(-1);
            if(Questionnaire_Activity.checkInputted(awakeBar)) data.setMdbfAwake(awakeBar.getProgress());
            else data.setMdbfAwake(-1);
            SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

            nextQuestion();
        });

        //setup skip
        binding.bottomBarQuestionnaireMdbf.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                //store default values
                QuestionnaireData data = new QuestionnaireData();
                data.setTime(TimeDateFormatter.getTimeString());
                data.setDate(TimeDateFormatter.getDateString());
                data.setMdbfSatsified(-1);
                data.setMdbfCalm(-1);
                data.setMdbfWell(-1);
                data.setMdbfRelaxed(-1);
                data.setMdbfEnergetic(-1);
                data.setMdbfAwake(-1);
                SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

                nextQuestion();
                return true;
            } else return false;
        });
    }

    //navigate to Questionnaire_Event_Appraisal_Fragment
    private void nextQuestion(){
        NavHostFragment.findNavController(Questionnaire_MDBF_Fragment.this)
                .navigate(R.id.action_questionnaire_MDBF_Fragment_to_questionnaire_EventAppraisal_Fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
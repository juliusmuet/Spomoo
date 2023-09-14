package com.example.spomoo.questionnaire;

/*
 * Questionnaire_EventAppraisal_Fragment of Spomoo Application
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
import com.example.spomoo.databinding.FragmentQuestionnaireEventAppraisalBinding;
import com.example.spomoo.utility.SharedPrefManager;

/*
 * Event Appraisal Fragment of Questionnaire Activity for Event Appraisal questions
 * Contains seekbars for inputting values and text views to show inputted values and meanings of values
 * Contains a floating action button for saving inputted values and navigating to Questionnaire_SocialContext_Fragment
 * Contains a skip text which navigates to Questionnaire_SocialContext_Fragment on click and does not save any values
 */
public class Questionnaire_EventAppraisal_Fragment extends Fragment {

    private FragmentQuestionnaireEventAppraisalBinding binding; //layout binding

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuestionnaireEventAppraisalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache seekbars
        SeekBar negativeBar = binding.questionnaireEventappraisalBarNegative;
        SeekBar positiveBar = binding.questionnaireEventappraisalBarPositive;

        //setup seekbars' functionality
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_eventappraisal_intensity_value),
                binding.questionnaireEventappraisalNegativeValue, negativeBar);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_eventappraisal_intensity_value),
                binding.questionnaireEventappraisalPositiveValue, positiveBar);

        //setup save data floating action button
        binding.questionnaireEventappraisalFabSave.setOnClickListener(v -> {
            //save inputted values
            QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
            if(Questionnaire_Activity.checkInputted(negativeBar)) data.setEventNegative(negativeBar.getProgress());
            else data.setEventNegative(-1);
            if(Questionnaire_Activity.checkInputted(positiveBar)) data.setEventPositive(positiveBar.getProgress());
            else data.setEventPositive(-1);
            SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

            nextQuestion();
        });

        //setup skip
        binding.bottomBarQuestionnaireEventappraisal.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                //store default values
                QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
                data.setEventNegative(-1);
                data.setEventPositive(-1);
                SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

                nextQuestion();
                return true;
            } else return false;
        });

    }

    //navigate to Questionnaire_Social_Context_Fragment
    private void nextQuestion(){
        NavHostFragment.findNavController(Questionnaire_EventAppraisal_Fragment.this)
                .navigate(R.id.action_questionnaire_EventAppraisal_Fragment_to_questionnaire_SocialContext_Fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
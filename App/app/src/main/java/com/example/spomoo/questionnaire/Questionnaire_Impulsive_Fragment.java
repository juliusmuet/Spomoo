package com.example.spomoo.questionnaire;

/*
 * Questionnaire_SelfWorth_Fragment of Spomoo Application
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
import com.example.spomoo.databinding.FragmentQuestionnaireImpulsiveBinding;
import com.example.spomoo.utility.SharedPrefManager;

/*
 * Impulsive Fragment of Questionnaire Activity for Impulsiveness questions
 * Contains seekbars for inputting values and text views to show inputted values and meanings of values
 * Contains floating action button for saving inputted values and navigating to Questionnaire_Message_Fragment
 * Contains a skip text which navigates to Questionnaire_Message_Fragment on click and does not save any values
 */
public class Questionnaire_Impulsive_Fragment extends Fragment {

    private FragmentQuestionnaireImpulsiveBinding binding; //layout binding

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuestionnaireImpulsiveBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache seekbars
        SeekBar impulsiveBar = binding.questionnaireImpulsiveBarImpulsive;
        SeekBar angryBar = binding.questionnaireImpulsiveBarAngry;

        //setup seekbars' functionality
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_approval_value),
                binding.questionnaireImpulsiveValue, impulsiveBar);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_approval_value),
                binding.questionnaireImpulsiveAngryValue, angryBar);

        //setup save data floating action button
        binding.questionnaireImpulsiveFabSave.setOnClickListener(v -> {
            //save inputted values
            QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
            if(Questionnaire_Activity.checkInputted(impulsiveBar)) data.setImpulsive(impulsiveBar.getProgress());
            else data.setImpulsive(-1);
            if(Questionnaire_Activity.checkInputted(angryBar)) data.setImpulsiveAngry(angryBar.getProgress());
            else data.setImpulsiveAngry(-1);
            SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

            nextQuestion();
        });

        //setup skip
        binding.bottomBarQuestionnaireImpulsive.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                //store default values
                QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
                data.setImpulsive(-1);
                data.setImpulsiveAngry(-1);
                SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

                nextQuestion();
                return true;
            } else return false;
        });

    }

    //navigate to Questionnaire_Message_Fragment
    private void nextQuestion(){
        NavHostFragment.findNavController(Questionnaire_Impulsive_Fragment.this)
                .navigate(R.id.action_questionnaire_Impulsive_Fragment_to_questionnaire_Message_Fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
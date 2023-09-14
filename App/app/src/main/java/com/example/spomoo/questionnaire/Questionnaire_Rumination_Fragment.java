package com.example.spomoo.questionnaire;

/*
 * Questionnaire_Rumination_Fragment of Spomoo Application
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
import com.example.spomoo.databinding.FragmentQuestionnaireRuminationBinding;
import com.example.spomoo.utility.SharedPrefManager;

/*
 * Location Fragment of Questionnaire Activity for Rumination questions
 * Contains seekbars for inputting values
 * Contains floating action button for saving the inputted value and navigating to Questionnaire_SelfWorth_Fragment
 * Contains a skip text which navigates to Questionnaire_SelfWorth_Fragment on click and does not save any values
 */
public class Questionnaire_Rumination_Fragment extends Fragment {

    private FragmentQuestionnaireRuminationBinding binding; //layout binding
    private SharedPrefManager sharedPrefManager;    //cache sharedPrefManager

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuestionnaireRuminationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache sharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(getContext());

        //cache seekbars
        SeekBar barProperties = binding.questionnaireRuminationBarProperties;
        SeekBar barRehash = binding.questionnaireRuminationBarRehash;
        SeekBar barTurnOff = binding.questionnaireRuminationBarTurnOff;
        SeekBar barDispute = binding.questionnaireRuminationBarDispute;

        //setup seekbars
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_approval_value), binding.questionnaireRuminationPropertiesValue, barProperties);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_approval_value), binding.questionnaireRuminationRehashValue, barRehash);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_approval_value), binding.questionnaireRuminationTurnoffValue, barTurnOff);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_approval_value), binding.questionnaireRuminationDisputeValue, barDispute);

        //setup save data floating action button
        binding.questionnaireRuminationFabSave.setOnClickListener(v -> {
            //save inputted values
            QuestionnaireData data = sharedPrefManager.loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
            if(Questionnaire_Activity.checkInputted(barProperties)) data.setRuminationProperties(barProperties.getProgress());
            else data.setRuminationProperties(-1);
            if(Questionnaire_Activity.checkInputted(barRehash)) data.setRuminationRehash(barRehash.getProgress());
            else data.setRuminationRehash(-1);
            if(Questionnaire_Activity.checkInputted(barTurnOff)) data.setRuminationTurnOff(barTurnOff.getProgress());
            else data.setRuminationTurnOff(-1);
            if(Questionnaire_Activity.checkInputted(barDispute)) data.setRuminationDispute(barDispute.getProgress());
            else data.setRuminationDispute(-1);
            sharedPrefManager.storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

            nextQuestion();
        });

        //setup skip
        binding.bottomBarQuestionnaireRumination.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                //store default values
                QuestionnaireData data = sharedPrefManager.loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
                data.setRuminationProperties(-1);
                data.setRuminationRehash(-1);
                data.setRuminationTurnOff(-1);
                data.setRuminationDispute(-1);
                sharedPrefManager.storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

                nextQuestion();
                return true;
            } else return false;
        });

    }

    //navigate to Questionnaire_SelfWorth_Fragment
    private void nextQuestion() {
        NavHostFragment.findNavController(Questionnaire_Rumination_Fragment.this)
                .navigate(R.id.action_questionnaire_Rumination_Fragment_to_questionnaire_SelfWorth_Fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
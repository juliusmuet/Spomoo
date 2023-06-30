package com.example.spomoo.questionnaire;

/*
 * Questionnaire_SelfWorth_Fragment of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
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
import com.example.spomoo.databinding.FragmentQuestionnaireSelfWorthBinding;
import com.example.spomoo.utility.SharedPrefManager;

/*
 * Self-Worth Fragment of Questionnaire Activity for Self-Worth questions
 * Contains seekbars for inputting values and text views to show inputted values and meanings of values
 * Contains floating action button for saving inputted values and navigating to Questionnaire_Impulsive_Fragment
 * Contains a skip text which navigates to Questionnaire_Impulsive_Fragment on click and does not save any values
 */
public class Questionnaire_SelfWorth_Fragment extends Fragment {

    private FragmentQuestionnaireSelfWorthBinding binding; //layout binding

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuestionnaireSelfWorthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache seekbars
        SeekBar satisfiedBar = binding.questionnaireSelfworthBarSatisfied;
        SeekBar dissatisfiedBar = binding.questionnaireSelfworthBarDissatisfied;

        //setup seekbars' functionality
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_approval_value),
                binding.questionnaireSelfworthSatisfiedValue, satisfiedBar);
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_approval_value),
                binding.questionnaireSelfworthDissatisfiedValue, dissatisfiedBar);

        //setup save data floating action button
        binding.questionnaireSelfworthFabSave.setOnClickListener(v -> {
            //save inputted values
            QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
            if(Questionnaire_Activity.checkInputted(satisfiedBar)) data.setSelfworthSatisfied(satisfiedBar.getProgress());
            else data.setSelfworthSatisfied(-1);
            if(Questionnaire_Activity.checkInputted(dissatisfiedBar)) data.setSelfworthSatisfied(dissatisfiedBar.getProgress());
            else data.setSelfworthDissatisfied(-1);
            SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

            nextQuestion();
        });

        //setup skip
        binding.bottomBarQuestionnaireSelfworth.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                //store default values
                QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
                data.setSelfworthSatisfied(-1);
                data.setSelfworthDissatisfied(-1);
                SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

                nextQuestion();
                return true;
            } else return false;
        });

    }

    //navigate to Questionnaire_Impulsive_Fragment
    private void nextQuestion(){
        NavHostFragment.findNavController(Questionnaire_SelfWorth_Fragment.this)
                .navigate(R.id.action_questionnaire_SelfWorth_Fragment_to_questionnaire_Impulsive_Fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.spomoo.questionnaire;

/*
 * Questionnaire_Location_Fragment of Spomoo Application
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
import android.widget.ArrayAdapter;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentQuestionnaireLocationBinding;
import com.example.spomoo.customui.ResizeAutoCompleteTextView;
import com.example.spomoo.utility.SharedPrefManager;

/*
 * Location Fragment of Questionnaire Activity for Location questions
 * Contains a dropdown menu
 * Contains floating action button for saving the inputted value and navigating to Questionnaire_Rumination_Fragment
 * Contains a skip text which navigates to Questionnaire_Rumination_Fragment on click and does not save any values
 */
public class Questionnaire_Location_Fragment extends Fragment {

    private FragmentQuestionnaireLocationBinding binding;   //layout binding

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuestionnaireLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache text input
        ResizeAutoCompleteTextView locationInput = binding.questionnaireLocationDropdown;

        //setup dropdown menu
        String[] items = getResources().getStringArray(R.array.questionnaire_locations_array);  //possible options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_items, items);
        locationInput.setAdapter(adapter);

        //setup save data floating action button
        binding.questionnaireLocationFabSave.setOnClickListener(v -> {
            //save inputted values
            QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
            data.setLocation(locationInput.getText().toString().trim());
            SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

            nextQuestion();
        });

        //setup skip
        binding.bottomBarQuestionnaireLocation.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                //store default values
                QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
                data.setLocation("");
                SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

                nextQuestion();
                return true;
            } else return false;
        });

    }

    //navigate to Questionnaire_Rumination_Fragment
    private void nextQuestion(){
        NavHostFragment.findNavController(Questionnaire_Location_Fragment.this)
                .navigate(R.id.action_questionnaire_Location_Fragment_to_questionnaire_Rumination_Fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
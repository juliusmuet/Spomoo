package com.example.spomoo.questionnaire;

/*
 * Questionnaire_SocialContext_Fragment of Spomoo Application
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
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentQuestionnaireSocialContextBinding;
import com.example.spomoo.customui.ResizeAutoCompleteTextView;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

/*
 * Social Context Fragment of Questionnaire Activity for Social Context questions
 * Contains a switch, seekbar and dropdown menu
 * If the switch is turned off, the seekbar and dropdown menu and their corresponding texts become invisible and disabled
 * Contains floating action button for saving inputted values and navigating to Questionnaire_Location_Fragment
 * Contains a skip text which navigates to Questionnaire_Location_Fragment on click and does not save any values
 */
public class Questionnaire_SocialContext_Fragment extends Fragment {

    private FragmentQuestionnaireSocialContextBinding binding;  //layout binding

    //UI elements
    private MaterialSwitch materialSwitch;
    private MaterialTextView barHeading;
    private MaterialTextView barValue;
    private SeekBar bar;
    private MaterialTextView barText1;
    private MaterialTextView barText2;
    private MaterialTextView dropdownHeading;
    private TextInputLayout dropdownLayout;
    private ResizeAutoCompleteTextView peopleInput;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuestionnaireSocialContextBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache switch, seekbar, dropdown menu and text views
        materialSwitch = binding.questionnaireSocialcontextSwitch;
        barHeading = binding.questionnaireSocialcontextHeading3;
        barValue = binding.questionnaireSocialcontextValue;
        bar = binding.questionnaireSocialcontextBar;
        barText1 = binding.questionnaireSocialcontextNo;
        barText2 = binding.questionnaireSocialcontextYes;
        dropdownHeading = binding.questionnaireSocialcontextHeading4;
        dropdownLayout = binding.questionnaireSocialcontextDropdownLayout;
        peopleInput = binding.questionnaireSocialcontextDropdown;

        //setup seekbars' functionality
        Questionnaire_Activity.seekbarFunc(getString(R.string.questionnaire_socialcontext_value),
                binding.questionnaireSocialcontextValue, bar);

        //setup switch's functionality
        materialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                switchDeactivate();
            else
                switchActivate();
        });

        //setup dropdown menu
        String[] items = getResources().getStringArray(R.array.questionnaire_socialcontext_people_array); //possible options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_items, items);
        peopleInput.setAdapter(adapter);

        //setup save data floating action button
        binding.questionnaireSocialcontextFabSave.setOnClickListener(v -> {
            //save inputted values
            QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
            //if switch is turned on, only save switch's value
            if(materialSwitch.isChecked()) {
                data.setSocialAlone(1);
                data.setSocialDislike(-1);
                data.setSocialPeople("");
            } else {
                data.setSocialAlone(0);
                if (Questionnaire_Activity.checkInputted(bar)) data.setSocialDislike(bar.getProgress());
                else data.setSocialDislike(-1);
                data.setSocialPeople(peopleInput.getText().toString().trim());
            }
            SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

            nextQuestion();
        });

        //setup skip
        binding.bottomBarQuestionnaireSocialcontext.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                //store default values
                QuestionnaireData data = SharedPrefManager.getInstance(getContext()).loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
                data.setSocialAlone(-1);
                data.setSocialDislike(-1);
                data.setSocialPeople("");
                SharedPrefManager.getInstance(getContext()).storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

                nextQuestion();
                return true;
            } else return false;
        });

    }

    //deactivate seekbar, dropdown menu and their text views, if switch is turned on
    private void switchDeactivate(){
        barHeading.setVisibility(View.INVISIBLE);
        barHeading.setEnabled(false);
        barValue.setVisibility(View.INVISIBLE);
        barValue.setEnabled(false);
        bar.setVisibility(View.INVISIBLE);
        bar.setEnabled(false);
        barText1.setVisibility(View.INVISIBLE);
        barText1.setEnabled(false);
        barText2.setVisibility(View.INVISIBLE);
        barText2.setEnabled(false);
        dropdownHeading.setVisibility(View.INVISIBLE);
        dropdownHeading.setEnabled(false);
        dropdownLayout.setVisibility(View.INVISIBLE);
        dropdownLayout.setEnabled(false);
        peopleInput.setVisibility(View.INVISIBLE);
        peopleInput.setEnabled(false);
    }

    //activate seekbar, dropdown menu and their text views, if switch is turned off
    private void switchActivate(){
        barHeading.setVisibility(View.VISIBLE);
        barHeading.setEnabled(true);
        barValue.setVisibility(View.VISIBLE);
        barValue.setEnabled(true);
        bar.setVisibility(View.VISIBLE);
        bar.setEnabled(true);
        barText1.setVisibility(View.VISIBLE);
        barText1.setEnabled(true);
        barText2.setVisibility(View.VISIBLE);
        barText2.setEnabled(true);
        dropdownHeading.setVisibility(View.VISIBLE);
        dropdownHeading.setEnabled(true);
        dropdownLayout.setVisibility(View.VISIBLE);
        dropdownLayout.setEnabled(true);
        peopleInput.setVisibility(View.VISIBLE);
        peopleInput.setEnabled(true);
    }

    //navigate to Questionnaire_Location_Fragment
    private void nextQuestion(){
        NavHostFragment.findNavController(Questionnaire_SocialContext_Fragment.this)
                .navigate(R.id.action_questionnaire_SocialContext_Fragment_to_questionnaire_Location_Fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.spomoo.questionnaire;

/*
 * Questionnaire_Message_Fragment of Spomoo Application
 * Author: Julius Müther
 * Only for private usage
 */

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentQuestionnaireMessageBinding;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/*
 * Message Fragment of Questionnaire Activity for inputting own note
 * Contains a text input edit text
 * Contains floating action button for saving the inputted value and navigating back to MainActivity
 * Contains a skip text which navigates back to MainActivity on click and does not save any values
 * The temporarily stored questionnaire data gets stored in the local SQL Database after exiting this fragment
 */
public class Questionnaire_Message_Fragment extends Fragment {

    private FragmentQuestionnaireMessageBinding binding; //layout binding
    private SharedPrefManager sharedPrefManager;    //cache sharedPrefManager

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuestionnaireMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cache text input edit text and sharedPrefManager
        TextInputLayout inputLayout = binding.questionnaireMessageInputLayout;
        TextInputEditText note = binding.questionnaireMessageInputText;
        sharedPrefManager = SharedPrefManager.getInstance(getContext());

        //do not allow paragraphs
        note.setFilters(new InputFilter[]{getEditTextFilter()});

        //setup save data floating action button
        binding.questionnaireMessageFabSave.setOnClickListener(v -> {
            //if inputted note is too long
            if(inputLayout.getCounterMaxLength() < note.getText().toString().length()){
                Toast.makeText(getContext(), getString(R.string.questionnare_message_error), Toast.LENGTH_SHORT).show();
                return;
            }

            //save inputted values
            QuestionnaireData data = sharedPrefManager.loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
            if (!note.getText().toString().isEmpty()) data.setMessage(note.getText().toString().trim());
            else data.setMessage("");
            sharedPrefManager.storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

            exitQuestionnaire();
        });

        //setup skip
        binding.bottomBarQuestionnaireMessage.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.questionnaire_menu_skip){
                //store default values
                QuestionnaireData data = sharedPrefManager.loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
                data.setMessage("");
                sharedPrefManager.storeQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE, data);

                exitQuestionnaire();
                return true;
            } else return false;
        });

    }

    //store questionnaire data in local SQL database and exit the questionnaire
    private void exitQuestionnaire() {
        //store data in local SQL Database
        storeInDB();;

        //feedback for user that data is saved
        Toast.makeText(getContext(), getString(R.string.questionnaire_saved), Toast.LENGTH_SHORT).show();

        //exit questionnaire
        getActivity().finish();
    }

    private void storeInDB(){
        QuestionnaireData data = sharedPrefManager.loadQuestionnaire(SharedPrefManager.KEY_CURRENT_QUESTIONNAIRE);
        LocalDatabaseManager db = new LocalDatabaseManager(getContext());
        db.addQuestionnaireData(data, 0);
    }

    //custom InputFilter to not allow paragraphs
    public static InputFilter getEditTextFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return !String.valueOf(c).equals("\n");
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
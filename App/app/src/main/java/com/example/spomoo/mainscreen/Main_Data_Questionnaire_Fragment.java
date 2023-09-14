package com.example.spomoo.mainscreen;

/*
 * Main_Data_Questionnaire_Fragment of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.spomoo.R;
import com.example.spomoo.databinding.FragmentMainDataQuestionnaireBinding;
import com.example.spomoo.questionnaire.QuestionnaireData;
import com.example.spomoo.utility.ConstantsBroadcast;
import com.example.spomoo.utility.LocalDatabaseManager;
import com.example.spomoo.utility.SharedPrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

/*
 * Contains a listview which contains items defined in layout listview_item_questionnaire
 * By clicking on an item it becomes expanded or collapsed
 * By holding long on a item it can be deleted after confirmation in a dialog
 */
public class Main_Data_Questionnaire_Fragment extends Fragment {

    private FragmentMainDataQuestionnaireBinding binding;   //layout binding
    private LocalDatabaseManager db;    //local SQL database
    private SharedPrefManager sharedPrefManager;    //contains inputted date
    private ListView listView;  //listview holding items
    private MaterialTextView nodataText;    //textview to show if no data is available
    QuestionnaireAdapter questionnaireAdapter;  //adapter for listview items
    private ArrayList<QuestionnaireData> data;  //contains questionnaire data of inputted date
    private Context context;    //cache context to fix errors
    private LayoutInflater inflater;    //cache LayoutInflater

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainDataQuestionnaireBinding.inflate(inflater, container, false);

        //cache components
        db = new LocalDatabaseManager(getContext());
        listView = binding.mainDataQuestionnaireListview;
        if(getContext() != null) context = getContext();
        if(inflater != null) this.inflater = inflater;
        sharedPrefManager = SharedPrefManager.getInstance(context);
        nodataText = binding.mainDataQuestionnaireNodataText;

        //set nodataText invisible at start
        nodataText.setVisibility(View.GONE);

        //create listview containing items based on inputted date of Main_Data_Fragment
        createList();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //creates the listview items based on the inputted date of Main_Data_Fragment which is received via broadcast
    private void createList(){
        //create listview from saved date
        createListHelper(sharedPrefManager.getString(SharedPrefManager.KEY_DATA_DATE));

        //setup broadcast receiver to receive inputted date
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsBroadcast.BROADCAST_DATA_DATE_INPUT);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                createListHelper(intent.getStringExtra(ConstantsBroadcast.BROADCAST_DATA_DATE_INPUT_VALUE));
            }
        };
        getContext().registerReceiver(broadcastReceiver, intentFilter);
    }

    //setup listview based on inputted date
    private void createListHelper(String date){
        //if no date has been inputted, do nothing
        if(date.isEmpty())
            return;

        //get data of inputted date
        data = db.readQuestionnaireDataFromDate(date);
        //if no data available
        if(data.size() == 0){
            nodataText.setText(context.getString(R.string.main_data_no_data));
            nodataText.setVisibility(View.VISIBLE);
            questionnaireAdapter = new QuestionnaireAdapter(context, data, new ArrayList<String>(), inflater);
            listView.setAdapter(questionnaireAdapter);
            return;
        }

        //set nodataText invisible
        nodataText.setText("");
        nodataText.setVisibility(View.GONE);

        //create listview items
        ArrayList<String> dummy = new ArrayList<>();
        for (int ind = 0; ind < data.size(); ind++)
            dummy.add("");
        questionnaireAdapter = new QuestionnaireAdapter(context, data, dummy, inflater);
        listView.setAdapter(questionnaireAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        //re-create listview after reload
        createList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //custom adapter for recyclerview items which can be expanded
    class QuestionnaireAdapter extends ArrayAdapter<String> {

        LayoutInflater layoutInflater;
        ArrayList<QuestionnaireData> questionnaireData;

        QuestionnaireAdapter(Context context, ArrayList<QuestionnaireData> questionnaireData, ArrayList<String> test, LayoutInflater layoutInflater) {
            super(context, R.layout.listview_item_questionnaire, R.id.recyclerview_questionnaire_time_text, test);
            this.layoutInflater = layoutInflater;
            this.questionnaireData = questionnaireData;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            MaterialTextView time, mdbfSatisfied, mdbfCalm, mdbfWell, mdbfRelaxed, mdbfEnergetic, mdbfAwake, eventNegative, eventPositive, socialAlone,
                    socialDislike, socialPeople, location, ruminationProperties, ruminationRehash, ruminationTurnOff, ruminationDispute, selfworthSatisfied,
                    selfworthDissatisfied, impulsive, impulsiveAngry, message;
            ImageView arrow;
            LinearLayout linearLayout;
            LinearLayout expandableLayout;

            //get all UI elements
            View row = this.layoutInflater.inflate(R.layout.listview_item_questionnaire, parent, false);
            time = row.findViewById(R.id.recyclerview_questionnaire_time_text);
            mdbfSatisfied = row.findViewById(R.id.recyclerview_questionnaire_mdbf_satisfied_text);
            mdbfCalm = row.findViewById(R.id.recyclerview_questionnaire_mdbf_calm_text);
            mdbfWell = row.findViewById(R.id.recyclerview_questionnaire_mdbf_well_text);
            mdbfRelaxed = row.findViewById(R.id.recyclerview_questionnaire_mdbf_relaxed_text);
            mdbfEnergetic = row.findViewById(R.id.recyclerview_questionnaire_mdbf_energetic_text);
            mdbfAwake = row.findViewById(R.id.recyclerview_questionnaire_mdbf_awake_text);
            eventNegative = row.findViewById(R.id.recyclerview_questionnaire_event_negative_text);
            eventPositive = row.findViewById(R.id.recyclerview_questionnaire_event_positive_text);
            socialAlone = row.findViewById(R.id.recyclerview_questionnaire_social_alone_text);
            socialDislike = row.findViewById(R.id.recyclerview_questionnaire_social_dislike_text);
            socialPeople = row.findViewById(R.id.recyclerview_questionnaire_social_people_text);
            location = row.findViewById(R.id.recyclerview_questionnaire_location_text);
            ruminationProperties = row.findViewById(R.id.recyclerview_questionnaire_rumination_properties_text);
            ruminationRehash = row.findViewById(R.id.recyclerview_questionnaire_rumination_rehash_text);
            ruminationTurnOff = row.findViewById(R.id.recyclerview_questionnaire_rumination_turnoff_text);
            ruminationDispute = row.findViewById(R.id.recyclerview_questionnaire_rumination_dispute_text);
            selfworthSatisfied = row.findViewById(R.id.recyclerview_questionnaire_selfworth_satisfied_text);
            selfworthDissatisfied = row.findViewById(R.id.recyclerview_questionnaire_selfworth_dissatisfied_text);
            impulsive = row.findViewById(R.id.recyclerview_questionnaire_impulsive_text);
            impulsiveAngry = row.findViewById(R.id.recyclerview_questionnaire_impulsive_angry_text);
            message = row.findViewById(R.id.recyclerview_questionnaire_message_text);
            arrow = row.findViewById(R.id.recyclerview_questionnaire_arrow_image);
            linearLayout = row.findViewById(R.id.recyclerview_questionnaire_linearlayout);
            expandableLayout = row.findViewById(R.id.recyclerview_questionnaire_expandable_linearlayout);

            //load texts
            QuestionnaireData data = questionnaireData.get(position);
            time.setText(makeBold(getString(R.string.main_data_questionnaire_time) + " " + data.getTime()));
            mdbfSatisfied.setText(makeBold(getString(R.string.main_data_questionnaire_mdbf_satisfied) + " " + checkNoInput(data.getMdbfSatsified())));
            mdbfCalm.setText(makeBold(getString(R.string.main_data_questionnaire_mdbf_calm) + " " + checkNoInput(data.getMdbfCalm())));
            mdbfWell.setText(makeBold(getString(R.string.main_data_questionnaire_mdbf_well) + " " + checkNoInput(data.getMdbfWell())));
            mdbfRelaxed.setText(makeBold(getString(R.string.main_data_questionnaire_mdbf_relaxed) + " " + checkNoInput(data.getMdbfRelaxed())));
            mdbfEnergetic.setText(makeBold(getString(R.string.main_data_questionnaire_mdbf_energetic) + " " + checkNoInput(data.getMdbfEnergetic())));
            mdbfAwake.setText(makeBold(getString(R.string.main_data_questionnaire_mdbf_awake) + " " + checkNoInput(data.getMdbfAwake())));
            eventNegative.setText(makeBold(getString(R.string.main_data_questionnaire_event_negative) + " " + checkNoInput(data.getEventNegative())));
            eventPositive.setText(makeBold(getString(R.string.main_data_questionnaire_event_positive) + " " + checkNoInput(data.getEventPositive())));
            socialAlone.setText(makeBold(getString(R.string.main_data_questionnaire_social_alone) + " " + aloneInput(data.getSocialAlone())));
            socialDislike.setText(makeBold(getString(R.string.main_data_questionnaire_social_dislike) + " " + checkNoInput(data.getSocialDislike())));
            socialPeople.setText(makeBold(getString(R.string.main_data_questionnaire_social_people) + " " + checkNoInput(data.getSocialPeople())));
            location.setText(makeBold(getString(R.string.main_data_questionnaire_location) + " " + checkNoInput(data.getLocation())));
            ruminationProperties.setText(makeBold(getString(R.string.main_data_questionnaire_rumination_properties) + " " + checkNoInput(data.getRuminationProperties())));
            ruminationRehash.setText(makeBold(getString(R.string.main_data_questionnaire_rumination_rehash) + " " + checkNoInput(data.getRuminationRehash())));
            ruminationTurnOff.setText(makeBold(getString(R.string.main_data_questionnaire_rumination_turnoff) + " " + checkNoInput(data.getRuminationTurnOff())));
            ruminationDispute.setText(makeBold(getString(R.string.main_data_questionnaire_rumination_dispute) + " " + checkNoInput(data.getRuminationDispute())));
            selfworthSatisfied.setText(makeBold(getString(R.string.main_data_questionnaire_selfworth_satisfied) + " " + checkNoInput(data.getSelfworthSatisfied())));
            selfworthDissatisfied.setText(makeBold(getString(R.string.main_data_questionnaire_selfworth_dissatisfied) + " " + checkNoInput(data.getSelfworthDissatisfied())));
            impulsive.setText(makeBold(getString(R.string.main_data_questionnaire_impulsive) + " " + checkNoInput(data.getImpulsive())));
            impulsiveAngry.setText(makeBold(getString(R.string.main_data_questionnaire_impulsive_angry) + " " + checkNoInput(data.getImpulsiveAngry())));
            message.setText(makeBold(getString(R.string.main_data_questionnaire_message) + " " + checkNoInput(data.getMessage())));

            //check visibility of collapsed part and set arrow direction
            boolean expanded = data.getExpanded();
            expandableLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            arrow.setBackgroundResource(expanded ? R.drawable.baseline_keyboard_arrow_up_24 : R.drawable.baseline_keyboard_arrow_down_24);

            //on click expand or collapse and set arrow direction accordingly
            linearLayout.setOnClickListener(v -> {
                data.setExpanded(!data.getExpanded());
                expandableLayout.setVisibility(data.getExpanded() ? View.VISIBLE : View.GONE);
                arrow.setBackgroundResource(data.getExpanded() ? R.drawable.baseline_keyboard_arrow_up_24 : R.drawable.baseline_keyboard_arrow_down_24);
            });

            //when long clicking an item, show dialog to ask if selected data should be deleted
            linearLayout.setOnLongClickListener(v -> {
                //show dialog to ask for deletion of data
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.main_data_dialog_delete_heading)
                        .setMessage(R.string.main_data_dialog_delete_content)
                        .setCancelable(true)
                        .setIcon(R.drawable.baseline_delete_24)
                        .setNegativeButton(R.string.main_data_dialog_delete_no, (dialog, which) -> {
                        })
                        .setPositiveButton(R.string.main_data_dialog_delete_yes, (dialog, which) -> {
                            //delete data
                            db.deleteQuestionnaireData(String.valueOf(data.getQuestionnaireId()));
                            questionnaireData.remove(position);
                            questionnaireAdapter.remove(questionnaireAdapter.getItem(position));
                            questionnaireAdapter.notifyDataSetChanged();
                        })
                        .show();
                return true;
            });

            return row;
        }

        //makes text before ":" bold
        private SpannableString makeBold(String text){
            SpannableString spannableString = new SpannableString(text);
            StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
            String[] parts = text.split(":");
            spannableString.setSpan(boldStyle, 0, parts[0].length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        //represent internal -1 as "-"
        private String checkNoInput(int input){
            if(input == -1) return "-";
            else return String.valueOf(input);
        }

        //represent internal "" as "-"
        private String checkNoInput(String input){
            if(input.isEmpty()) return "-";
            else return input;
        }

        //represent internal -1, 0, 1 as strings
        private String aloneInput(int input){
            if(input == 0) return getString(R.string.main_data_questionnaire_alone_no);
            if(input == 1) return getString(R.string.main_data_questionnaire_alone_yes);
            return "-";
        }
    }
}
package com.example.aqi.iotapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private View root;

    private EditText textName;

    private EditText textAge;

    private EditText textGoal;

    private CheckBox checkRmbMe;

    private Button btnSave;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =inflater.inflate(R.layout.fragment_profile, container, false);

        textName = (EditText)root.findViewById(R.id.edittext_name);
        textAge = (EditText) root.findViewById(R.id.edittext_age);
        textGoal = (EditText) root.findViewById(R.id.edittext_goal);
        checkRmbMe = (CheckBox) root.findViewById(R.id.check_rememberme);
        btnSave= (Button) root.findViewById(R.id.btn_saveprofile);

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name = textName.getText().toString();
                int age = Integer.parseInt(textAge.getText().toString());
                String goal = textGoal.getText().toString();
                boolean rememberMe = checkRmbMe.isChecked();

                //Set new user settings
                MainActivity.userSettings.name = name;
                MainActivity.userSettings.age = age;
                MainActivity.userSettings.goal = goal;
                MainActivity.userSettings.rememberMe = rememberMe;

                //Save to file
                FileUtils.saveUserSettings(getContext(), "default.txt", MainActivity.userSettings);
                FileUtils.saveUserSettings(getContext(), MainActivity.userSettings.email+".txt",
                        MainActivity.userSettings);

            }
        });

        return root;
    }




}

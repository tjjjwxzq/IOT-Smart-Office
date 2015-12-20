package com.example.aqi.iotapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntervalSettingsFragment extends Fragment {

    private View root;

    private CheckBox checkSmartInterval;

    private Spinner spinnerTimeInterval;

    private Button btnDefault;

    private Button btnSave;

    public IntervalSettingsFragment() {
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
        root = inflater.inflate(R.layout.fragment_interval_settings, container, false);

        checkSmartInterval = (CheckBox) root.findViewById(R.id.check_rememberme);
        spinnerTimeInterval = (Spinner) root.findViewById(R.id.spinner_timeIntervalChoices);
        btnDefault = (Button) root.findViewById(R.id.btn_defaultsettings);
        btnSave = (Button) root.findViewById(R.id.btn_save);

        btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSmartInterval.setChecked(true);
                spinnerTimeInterval.setSelection(0);

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set user settings
                MainActivity.userSettings.smartinterval = checkSmartInterval.isChecked();
                switch(spinnerTimeInterval.getSelectedItem().toString())
                {
                    case "15 min":
                        MainActivity.userSettings.interval = 15;
                        break;
                    case "30 min":
                        MainActivity.userSettings.interval = 15;
                        break;
                    case "45 min":
                        MainActivity.userSettings.interval = 15;
                        break;
                    case "1 hour":
                        MainActivity.userSettings.interval = 15;
                        break;
                }

                //Save to file
                FileUtils.saveUserSettings(getActivity(), "default.txt", MainActivity.userSettings);
                FileUtils.saveUserSettings(getActivity(), MainActivity.userSettings.email + ".txt",
                        MainActivity.userSettings);
            }
        });

        return root;
    }
}

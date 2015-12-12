package com.example.aqi.iotapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceControlFragment extends Fragment {

    private static final String TAG = DeviceControlFragment.class.getSimpleName();

    private View root;

    public static Button btnAlerts;

    public static TextView isSerial;

    public static TextView mConnectionState;

    public static TextView mDataField;


    public DeviceControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_device_control, container, false);

        // Set up UI references
        //((TextView) root.findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) root.findViewById(R.id.connection_state);
        // is serial present?
        isSerial = (TextView) root.findViewById(R.id.isSerial);

        mDataField = (TextView) root.findViewById(R.id.data_value);

        btnAlerts = (Button) root.findViewById(R.id.btn_turnonalerts);

        return inflater.inflate(R.layout.fragment_device_control, container, false);

    }



}

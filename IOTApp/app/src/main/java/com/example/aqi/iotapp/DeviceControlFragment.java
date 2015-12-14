package com.example.aqi.iotapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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

    public  Button btnAlerts;

    public  TextView isSerial;

    public  TextView mConnectionState;

    public  TextView mDataField;

    btnAlertsListener mCallback;


    public DeviceControlFragment() {
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
        root =  inflater.inflate(R.layout.fragment_device_control, container, false);

        // Set up UI references
        //((TextView) root.findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) root.findViewById(R.id.connection_state);
        mConnectionState.setText("LOL");
        // is serial present?
        isSerial = (TextView) root.findViewById(R.id.isSerial);

        mDataField = (TextView) root.findViewById(R.id.data_value);

        btnAlerts = (Button) root.findViewById(R.id.btn_turnonalerts);

        btnAlerts.setOnClickListener(btnTurnOnAlerts);

        Log.d(TAG, "Creating fragment");

        return root;

    }

     public View.OnClickListener btnTurnOnAlerts = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.d(TAG, "blicking aleerts");
            btnAlerts.setText(R.string.btn_turnoffalers);
            btnAlerts.setOnClickListener(btnTurnOffAlerts);
            mCallback.btnTurnOnAlerts();
        }
    };

    public View.OnClickListener btnTurnOffAlerts = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            btnAlerts.setText(R.string.btn_turnonalerts);
            btnAlerts.setOnClickListener(btnTurnOnAlerts);
            mCallback.btnTurnOffAlerts();
        }
    };

    // Container Activity must implement this interface
    public interface btnAlertsListener{
        void btnTurnOnAlerts();
        void btnTurnOffAlerts();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (btnAlertsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement btnAlertsListener");
        }
    }


}

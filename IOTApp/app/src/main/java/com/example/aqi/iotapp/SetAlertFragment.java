package com.example.aqi.iotapp;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetAlertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SetAlertFragment extends Fragment {

    private View root;

    private Button btnSetAlarm;

    private Button btnCancelAlarm;

    final static int REQUEST_CODE =0;

    private BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;

    private AlarmManager alarmManager;

    private PendingIntent pendingIntent;

    private int alarmType = AlarmManager.ELAPSED_REALTIME;

    private final int ALARM_TIME = 5000;


    public SetAlertFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Vibrator
        Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(500);

        bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        ////SETTING UP ALARM///
        //Create an intent for explicitly executing a hard-coded class(component) name
        // First create an intent for the alarm to activate.
        // This code simply starts an Activity, or brings it to the front if it has already
        // been created.
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        // Because the intent must be fired by a system service from outside the application,
        // it's necessary to wrap it in a PendingIntent.  Providing a different process with
        // a PendingIntent gives that other process permission to fire the intent that this
        // application has created.
        // Also, this code creates a PendingIntent to start an Activity.  To create a
        // BroadcastIntent instead, simply call getBroadcast instead of getIntent.
        pendingIntent = PendingIntent.getActivity(getActivity(), REQUEST_CODE,
                intent, 0);

        // The AlarmManager, like most system services, isn't created by application code, but
        // requested from the system.
        alarmManager = (AlarmManager)
                getActivity().getSystemService(getActivity().ALARM_SERVICE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate the fragment view
        root = inflater.inflate(R.layout.fragment_set_alert,container,false);

        btnSetAlarm = (Button) root.findViewById(R.id.btn_setalert);

        btnCancelAlarm = (Button) root.findViewById(R.id.btn_cancelalert);

        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setRepeating takes a start delay and period between alarms as arguments.
                // The below code fires after 15 seconds, and repeats every 15 seconds.  This is very
                // useful for demonstration purposes, but horrendous for production.  Don't be that dev.
                alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime() + ALARM_TIME,
                    ALARM_TIME, pendingIntent);
            }
        });

        btnCancelAlarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if(alarmManager != null)
                    alarmManager.cancel(pendingIntent);
            }

        });


        return root;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

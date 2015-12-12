package com.example.aqi.iotapp;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlertLevel1Fragment extends Fragment {

    private static final String TAG = AlertLevel1Fragment.class.getSimpleName();

    private View root;

    private final int ALARM_DURATION = 10000;

    private final long[] VIBRATE_PATTERN = {0,1000, 1000};

    private AlertSettings alertSettings;

    private MediaPlayer mediaPlayer;

    private TextView textAlertMessage;

    public AlertLevel1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertSettings = MainActivity.userSettings.alertLevel1;

        // Set up media player
        mediaPlayer = MediaPlayer.create(getActivity(), alertSettings.alertSoundResource);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        //Get Vibrator
        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATE_PATTERN, 0);


        //Set up timer
        CountDownTimer timer = new CountDownTimer(ALARM_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, millisUntilFinished + " till finished");

            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Finished");
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                vibrator.cancel();
            }
        };

        timer.start();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_alert_level1, container, false);

        //Get UI refs
        textAlertMessage = (TextView) root.findViewById(R.id.text_alertlevel1);
        textAlertMessage.setText(alertSettings.alertMessage);

        return root;
    }

}

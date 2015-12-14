package com.example.aqi.iotapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlertLevel4Fragment extends Fragment {

    private static final String TAG = AlertLevel4Fragment.class.getSimpleName();

    private static final int REQ_START_STANDALONE_PLAYER = 1;

    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    private View root;

    private final int ALARM_DURATION = 10000;

    private final long[] VIBRATE_PATTERN = {0,1000, 1000};

    private final String EXERCISE_VID_ID = "CKjaFG4YN6g";

    private Intent youtubeIntent;

    private AlertSettings alertSettings;

    private MediaPlayer mediaPlayer;

    private TextView textAlertMessage;



    public AlertLevel4Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertSettings = MainActivity.userSettings.alertLevel4;

        // Set up youtube standalone player or media player
        if(alertSettings.playExerciseVids) {
            youtubeIntent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                        YoutubeDeveloperKey.YOUTUBE_API_KEY, EXERCISE_VID_ID, 40000, true, true);
            Log.d(TAG, "INtent" + youtubeIntent);

            // Set up timer for youtube video
            CountDownTimer youtubeTimer = new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {

                    if (youtubeIntent != null) {
                      if (canResolveIntent(youtubeIntent)) {
                          Log.d(TAG, "Starting youtubeintet");
                        startActivityForResult(youtubeIntent, REQ_START_STANDALONE_PLAYER);
                      } else {
                        // Could not resolve the intent - must need to install or update the YouTube API service.
                        YouTubeInitializationResult.SERVICE_MISSING
                            .getErrorDialog(getActivity(), REQ_RESOLVE_SERVICE_MISSING).show();
                      }
                    }
                }
            };
            youtubeTimer.start();


        }else {
            mediaPlayer = MediaPlayer.create(getActivity(), alertSettings.alertSoundResource);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }

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
                if(mediaPlayer!=null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                vibrator.cancel();
                closeFragment();
            }
        };

        timer.start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_alert_level4, container, false);

        //Get UI refs
        textAlertMessage = (TextView) root.findViewById(R.id.text_alertlevel1);
        textAlertMessage.setText(alertSettings.alertMessage);

        return root;
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void closeFragment()
    {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }
}


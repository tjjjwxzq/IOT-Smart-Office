package com.example.aqi.iotapp;

/**
 * Created by Arbiter on 12/12/2015.
 */
public class AlertSettings {

    public int alertSoundResource = R.raw.laughteralarm;

    public String alertMessage = "Butts off!";

    public boolean playExerciseVids = false;

    public int vibrationStrength = 1; // scale out of 5

    //Default constructor
    public AlertSettings() {}

    public AlertSettings(int alertSoundResource, String alertMessage, boolean playExerciseVids,
                         int vibrationStrength)
    {
        this.alertSoundResource = alertSoundResource;
        this. alertMessage = alertMessage;
        this.playExerciseVids = playExerciseVids;
        this. vibrationStrength = vibrationStrength;
    }

    @Override
    public String toString()
    {
        return alertSoundResource + "\n" + alertMessage + "\n" + playExerciseVids + "\n"
                + vibrationStrength;
    }


}

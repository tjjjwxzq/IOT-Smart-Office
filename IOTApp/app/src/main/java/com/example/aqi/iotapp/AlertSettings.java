package com.example.aqi.iotapp;

/**
 * Created by Arbiter on 12/12/2015.
 */
public class AlertSettings {

    public int alertSoundResource = R.raw.laughteralarm;

    public String alertMessage = "Butts off!";

    public String idExerciseVid = "CKjaFG4YN6g";

    public boolean playExerciseVids = false;

    public int vibrationStrength = 1; // scale out of 5

    public int upTimeInterval = 30; // no. of seconds user should stay up for alert to be dismissed

    //Default constructor
    public AlertSettings() {}

    public AlertSettings(int alertSoundResource, String alertMessage, String idExerciseVid,
                         boolean playExerciseVids, int vibrationStrength)
    {
        this.alertSoundResource = alertSoundResource;
        this.alertMessage = alertMessage;
        this.idExerciseVid = idExerciseVid;
        this.playExerciseVids = playExerciseVids;
        this. vibrationStrength = vibrationStrength;
    }

    @Override
    public String toString()
    {
        return alertSoundResource + "\n" + alertMessage + "\n" + idExerciseVid + "\n"
                + playExerciseVids + "\n" + vibrationStrength;
    }


}

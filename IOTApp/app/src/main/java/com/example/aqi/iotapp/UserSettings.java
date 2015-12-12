package com.example.aqi.iotapp;

/**
 * Created by Arbiter on 12/12/2015.
 */
public class UserSettings {

    //Remember user
    public boolean rememberMe;

    //User profile
    public String name;

    public int age;

    public String goal = "Sit less, live more!";

    public String email;

    public String password;

    //Time interval setting
    public int interval;

    public boolean smartinterval; // enable smart interval timing

    //Alert Settings
    public AlertSettings alertLevel1 = new AlertSettings();

    public AlertSettings alertLevel2 = new AlertSettings(R.raw.laughteralarm, "Butts off!", false, 2);

    public AlertSettings alertLevel3 = new AlertSettings(R.raw.laughteralarm, "Butts off for real!", false, 3);

    public AlertSettings alertLevel4 = new AlertSettings(R.raw.laughteralarm, "You asked for it...", false, 4);

    //Constructor with default goal and alert settings
    public UserSettings(boolean rememberMe, String name, int age, String email, String password,
                        int interval, boolean smartinterval){
        this.rememberMe = rememberMe;
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
        this.interval = interval;
        this.smartinterval = smartinterval;
    }

    public UserSettings(boolean rememberMe, String name,
                        int age, String goal, String email, String password,
                        int interval, boolean smartinterval,
                        AlertSettings alertLevel1, AlertSettings alertLevel2,
                        AlertSettings alertLevel3, AlertSettings alertLevel4)
    {
        this.rememberMe = rememberMe;
        this.name = name;
        this.age = age;
        this.goal = goal;
        this.email = email;
        this. password = password;

        this.interval = interval;
        this.smartinterval = smartinterval;

        this.alertLevel1 = alertLevel1;
        this.alertLevel2 = alertLevel2;
        this.alertLevel3 = alertLevel3;
        this.alertLevel4 = alertLevel4;
    }
}

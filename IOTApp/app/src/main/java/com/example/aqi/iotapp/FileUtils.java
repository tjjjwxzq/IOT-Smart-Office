package com.example.aqi.iotapp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Arbiter on 12/12/2015.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static void saveUserSettings(Context context, String filename, UserSettings userSettings){

        try{
            FileOutputStream fileout = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(Boolean.toString(userSettings.rememberMe));
            bufferedWriter.newLine();
            bufferedWriter.write(userSettings.name);
            bufferedWriter.newLine();
            bufferedWriter.write(Integer.toString(userSettings.age));
            bufferedWriter.newLine();
            bufferedWriter.write(userSettings.goal);
            bufferedWriter.newLine();
            bufferedWriter.write(userSettings.email);
            bufferedWriter.newLine();
            bufferedWriter.write(userSettings.password);
            bufferedWriter.newLine();
            bufferedWriter.write(Integer.toString(userSettings.interval));
            bufferedWriter.newLine();
            bufferedWriter.write(Boolean.toString(userSettings.smartinterval));
            bufferedWriter.newLine();
            bufferedWriter.write(userSettings.alertLevel1.toString());
            bufferedWriter.newLine();
            bufferedWriter.write(userSettings.alertLevel2.toString());
            bufferedWriter.newLine();
            bufferedWriter.write(userSettings.alertLevel3.toString());
            bufferedWriter.newLine();
            bufferedWriter.write(userSettings.alertLevel4.toString());

            bufferedWriter.close();

        }
        catch(Exception e)
        {
            Log.d(TAG, "Can't find file");
            e.printStackTrace();
        }
    }

    public static UserSettings readUserSettings(Context context, String filename)
    {
        try{
            FileInputStream filein = context.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(filein, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            boolean rememberMe;
            String name;
            int age;
            String goal;
            String email;
            String password;
            int interval;
            boolean smartinterval;
            int[] alertSoundResources = new int[4];
            String[] alertMessages = new String[4];
            String[] idExerciseVids = new String[4];
            boolean[] playExerciseVids = new boolean[4];
            int[] vibrationStrength = new int[4];
            AlertSettings[] alertSettings = new AlertSettings[4];


            rememberMe = Boolean.parseBoolean(bufferedReader.readLine());
            name = bufferedReader.readLine();
            age = Integer.parseInt(bufferedReader.readLine());
            goal = bufferedReader.readLine();
            email = bufferedReader.readLine();
            password = bufferedReader.readLine();
            interval = Integer.parseInt(bufferedReader.readLine());
            smartinterval = Boolean.parseBoolean(bufferedReader.readLine());

            for(int i =0; i<4; i++)
            {
                alertSoundResources[i] = Integer.parseInt(bufferedReader.readLine());
                alertMessages[i] = bufferedReader.readLine();
                idExerciseVids[i] = bufferedReader.readLine();
                playExerciseVids[i] = Boolean.parseBoolean(bufferedReader.readLine());
                vibrationStrength[i] = Integer.parseInt(bufferedReader.readLine());

                alertSettings[i] = new AlertSettings(alertSoundResources[i],
                        alertMessages[i], idExerciseVids[i], playExerciseVids[i], vibrationStrength[i]);
            }

            bufferedReader.close();

            return new UserSettings(rememberMe, name, age, goal, email, password,
                    interval, smartinterval, alertSettings[0], alertSettings[1],
                    alertSettings[2], alertSettings[3]);

        }
        catch(Exception e)
        {
            Log.d(TAG, "Couldn't read file");
            e.printStackTrace();
        }

        return null;
    }





}

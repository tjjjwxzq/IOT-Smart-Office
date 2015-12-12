import java.util.*;
import java.io.*;

public class FileUtilsTest{
  public static void main(String[] args){

    
    UserSettings userSettings = new UserSettings(
        true, "lol", 10, "lollol", "lol@gmail.com",
        "lolololol", 30, true, new AlertSettings(),
        new AlertSettings(), new AlertSettings(), 
        new AlertSettings());

    saveUserSettings("usersettings.txt", userSettings);

    UserSettings set = readUserSettings("usersettings.txt");

    System.out.println(set.name + " " + set.age);
    System.out.println(set.alertLevel4);
        

  }

  
    public static void saveUserSettings(String filename, UserSettings userSettings){

        try{
            FileOutputStream fileout = new FileOutputStream(filename);
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
            e.printStackTrace();
        }
    }

    public static UserSettings readUserSettings(String filename)
    {
        try{
            FileInputStream filein = new FileInputStream(filename);
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
                playExerciseVids[i] = Boolean.parseBoolean(bufferedReader.readLine());
                vibrationStrength[i] = Integer.parseInt(bufferedReader.readLine());

                alertSettings[i] = new AlertSettings(alertSoundResources[i],
                        alertMessages[i], playExerciseVids[i], vibrationStrength[i]);
            }

            bufferedReader.close();

            return new UserSettings(rememberMe, name, age, goal, email, password,
                    interval, smartinterval, alertSettings[0], alertSettings[1],
                    alertSettings[2], alertSettings[3]);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

public static class UserSettings {

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

    public AlertSettings alertLevel2 = new AlertSettings(1, "Butts off!", false, 2);

    public AlertSettings alertLevel3 = new AlertSettings(2, "Butts off for real!", false, 3);

    public AlertSettings alertLevel4 = new AlertSettings(3, "You asked for it...", false, 4);

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

public static class AlertSettings {

    public int alertSoundResource = 1;

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
}



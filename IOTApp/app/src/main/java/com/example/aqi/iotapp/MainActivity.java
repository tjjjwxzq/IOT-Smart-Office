package com.example.aqi.iotapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";

    final static String ALERTFRAGTAG = "SetAlertFrag";

    final static String STARTFRAGTAG = "StartFrag";

    //Default user is loaded first. Other users settings files are identified by the email
    public static ArrayList<String> settingsFiles = new ArrayList<>(Arrays.asList("default.txt"));

    public static UserSettings userSettings;

    private Button btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Window flags to allow activity to
        // 1) Show over lock screens
        // 2) Dismiss non-secure keyguards (keyguard doesn't pop up when navigating to another UI)
        // 3) Turn the phone screen on
        // 4) Keeps the screen on when the activity is active
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Load User settings
        if(userSettings ==null)
        {
            userSettings = FileUtils.readUserSettings(this, settingsFiles.get(0));
        }

        // Get UI refs
        btnGetStarted = (Button) findViewById(R.id.btn_getstarted);

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userSettings != null && userSettings.rememberMe) {
                    Intent intent = new Intent(getBaseContext(), DeviceScanActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "going to userlogin");
                    Intent intent = new Intent(getBaseContext(), UserLoginActivity.class);
                    startActivity(intent);
                }
            }

        });

        //Set up firebase context
        Firebase.setAndroidContext(getApplicationContext());

        FirebaseController.setUpFirebase();


        /*FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ActivityLogFragment fragment = new ActivityLogFragment();
        transaction.add(R.id.frag_test, fragment);
        transaction.commit();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}

package com.example.aqi.iotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class UserLoginActivity extends AppCompatActivity {

    private static final String TAG = UserLoginActivity.class.getSimpleName();

    public static final String FIREBASE_URL = "https://theinvisiblehand.firebaseio.com/";

    //Firebase ref
    private Firebase mFirebaseRef;

    //UI fields
    private EditText textEmail;

    private EditText textPassword;

    private Button btnLogin;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set up UI refs
        textEmail = (EditText) findViewById(R.id.text_email);
        textPassword = (EditText) findViewById(R.id.text_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(clickLogin);
        progressBar = (ProgressBar) findViewById(R.id.progress_login);

        // Set up Firebase library and references
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(FIREBASE_URL);

        //Check internet connection
        if(!isNetworkAvailable())
            createNetErrorDialog();
    }

    View.OnClickListener clickLogin = new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            //Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            final String email = textEmail.getText().toString();
            final String password=  textPassword.getText().toString();
            //Write invalid input handling if time permits

            //Check if there are valid user settings saved
            MainActivity.userSettings = FileUtils.readUserSettings(getBaseContext(),
                    email+".txt");

            //If user account does not exist
            if(MainActivity.userSettings==null)
            {
                mFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        //Hide progressbar
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(getBaseContext(), "New account created", Toast.LENGTH_SHORT).show();

                        //Login user
                        mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                Log.d(TAG, "logging in");
                                Toast.makeText(getBaseContext(), "Welcome!", Toast.LENGTH_SHORT).show();

                                //Create default user settings
                                MainActivity.userSettings = new UserSettings(true, "", -1, email, password,
                                        30, true);
                                FileUtils.saveUserSettings(getBaseContext(), email + ".txt", MainActivity.userSettings);
                                FileUtils.saveUserSettings(getBaseContext(), "default.txt", MainActivity.userSettings);

                                //Write user to firebase
                                FirebaseController.writeNewUserToFirebase();

                                //Launch device scan activity
                                Intent intent = new Intent(getBaseContext(), DeviceScanActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                    Toast.makeText(getBaseContext(), "Failed to login",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(FirebaseError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(!isNetworkAvailable())
                            Toast.makeText(getBaseContext(), "Please connect to the internet to create an account"
                                , Toast.LENGTH_SHORT).show();
                        else
                        {
                            Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                            Log.d(TAG, error.getMessage());
                    }
                });


            }
            else
            {
                mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getBaseContext(), "Welcome back" + MainActivity.userSettings.name + "!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), DeviceScanActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if(!isNetworkAvailable())
                        {
                            Toast.makeText(getBaseContext(), "You are not connected to the internet."
                                            + "Data collection will be disabled",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getBaseContext(), DeviceScanActivity.class);
                            startActivity(intent);
                        }
                        else
                            Log.d(TAG, firebaseError.getMessage());
                    }
                });

            }


        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void createNetErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to use some features of this application. Please turn on mobile network or Wi-Fi.")
            .setTitle("Unable to connect")
            .setCancelable(false)
            .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(
                                    Context.WIFI_SERVICE);
                            wifiManager.setWifiEnabled(true);
                        }
                    }
            )
        .setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getBaseContext(),"Data collection will be disabled until you are " +
                                "connected to the internet and log in",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        AlertDialog alert = builder.create();
        alert.show();
    }


}

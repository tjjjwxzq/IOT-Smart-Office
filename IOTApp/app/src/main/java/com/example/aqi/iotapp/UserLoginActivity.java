package com.example.aqi.iotapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        // Set up Firebase library and references
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(FIREBASE_URL);
    }

    View.OnClickListener clickLogin = new View.OnClickListener(){
        @Override
        public void onClick(View v)
        {
            final String email = textEmail.getText().toString();
            final String password=  textPassword.getText().toString();
            //Write invalid input handling if time permits

            MainActivity.userSettings = FileUtils.readUserSettings(getBaseContext(),
                    email+".txt");

            //If user account does not exist
            if(MainActivity.userSettings==null)
            {
                mFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Toast.makeText(getBaseContext(), "New account created", Toast.LENGTH_SHORT).show();
                        mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                Log.d(TAG, "logging in");
                                Toast.makeText(getBaseContext(), "Welcome!", Toast.LENGTH_SHORT).show();

                                //Create default user settings
                                MainActivity.userSettings = new UserSettings(true, "", -1, email, password,
                                        30, true);
                                FileUtils.saveUserSettings(getBaseContext(), email + ".txt", MainActivity.userSettings);

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
                        Toast.makeText(getBaseContext(), "Failed to create account"
                                , Toast.LENGTH_SHORT).show();
                    }
                });


            }
            else
            {
                mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Toast.makeText(getBaseContext(), "Welcome back" + MainActivity.userSettings.name + "!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), DeviceScanActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(getBaseContext(), "Failed to login. Are you connected to the internet?",
                                Toast.LENGTH_SHORT).show();

                    }
                });

            }


        }
    };

}

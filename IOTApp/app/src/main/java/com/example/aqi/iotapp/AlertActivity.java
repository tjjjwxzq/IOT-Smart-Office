package com.example.aqi.iotapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AlertActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set up fragment
        int alertLevel = getIntent().getIntExtra("Level",1);
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment alertFragment;
        switch(alertLevel)
        {
            case 1:
                alertFragment = new AlertLevel1Fragment();
                break;
            case 2:
                alertFragment = new AlertLevel2Fragment();
                break;
            case 3:
                alertFragment = new AlertLevel3Fragment();
                break;
            case 4:
                alertFragment = new AlertLevel4Fragment();
                break;
            default:
                alertFragment = new AlertLevel1Fragment();
        }
        fragmentTransaction.replace(R.id.fragment_alert, alertFragment);
        fragmentTransaction.commit();


    }

}

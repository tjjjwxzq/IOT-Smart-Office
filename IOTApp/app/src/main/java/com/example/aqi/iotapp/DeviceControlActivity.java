package com.example.aqi.iotapp;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeviceControlActivity extends AppCompatActivity
implements DeviceControlFragment.btnAlertsListener{

    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    private final static String FRAG_CONTROL = "DeviceControlFragment";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";

    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String mActivityTitle;

    private ActionBarDrawerToggle mDrawerToggle;

    private ArrayAdapter<String> mDrawerAdapter;

    private ListView mDrawerList;

    private DrawerLayout mDrawerLayout;

    private final String[] drawerArray = {"Activity Log","Profile", "Interval Settings", "Alert Settings"};

    private FragmentManager fragmentManager;

    private FragmentTransaction fragmentTransaction;

    private DeviceControlFragment deviceControlFragment;

    private String mDeviceName;

    private String mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;

    private boolean mConnected = false;

    private BluetoothGattCharacteristic characteristicTX;

    private BluetoothGattCharacteristic characteristicRX;

    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";

    private final String LIST_UUID = "UUID";

    final static int REQUEST_CODE =0;
private AlarmManager alarmManager;

    private PendingIntent pendingIntent;

    private int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

    private final int ALARM_TIME = 10000;



    // Code to manage Service lifecycle
    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            // Automatically connects to the device upon successful start-up initialization
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT DISCONNECTED: disconnected from a GATT server
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services
    // ACTION_DATA_AVAILABLE: recieved data from the device. This can
    //                        be a result of read or notification operations
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.d(TAG, "connected?" + mConnected);
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Respond to bluetooth data here

                displayData(intent.getStringExtra(mBluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void clearUI() {
        deviceControlFragment.mDataField.setText(R.string.no_data);
    }

    private void updateConnectionState(final int resourceId) {
        Log.d(TAG, "Updating conenction state");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceControlFragment.mConnectionState.setText(resourceId);
                Log.d(TAG, "connection text" + deviceControlFragment.mConnectionState.getText().toString());
            }
        });

    }

    private void displayData(String mdata) {
        String[] strArr = mdata.split("\\+");
        String data = strArr[0];
        if (data != null) {
            Log.i(TAG, "Data:" + data);
            deviceControlFragment.mDataField.setText(data);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch(data){

                case "tSD":
                    Log.d(TAG, "totalSittingDuration" + strArr[1]);
                    FirebaseController.updateFirebase("totalSittingDuration", Integer.parseInt(strArr[1]));
                    break;
                case "sittingTimes":
                    Log.d(TAG, "sittingTimes");
                    FirebaseController.updateFirebase(strArr[0], 0);
                    break;
                case "Alert Level 1":
                    AlertLevel1Fragment alertLevel1Fragment = new AlertLevel1Fragment();
                    fragmentTransaction.replace(R.id.fragment_control, alertLevel1Fragment);
                    fragmentTransaction.commit();
                    break;
                case "Alert Level 2":
                    Log.d(TAG, "Alert level 2");
                    AlertLevel2Fragment alertLevel2Fragment = new AlertLevel2Fragment();
                    fragmentTransaction.replace(R.id.fragment_control,alertLevel2Fragment);
                    fragmentTransaction.commit();
                    break;
                case "Alert Level 3":
                    AlertLevel3Fragment alertLevel3Fragment = new AlertLevel3Fragment();
                    fragmentTransaction.replace(R.id.fragment_control,alertLevel3Fragment);
                    fragmentTransaction.commit();
                    break;
                case "Alert Level 4":
                    AlertLevel4Fragment alertLevel4Fragment = new AlertLevel4Fragment();
                    fragmentTransaction.replace(R.id.fragment_control,alertLevel4Fragment);
                    fragmentTransaction.commit();
                    break;

            }
        }
    }

    // Demonstrates how to iterate through teh supported GATT Services/Characteristics
    // In this sample, we populate the data structure that is bound to the ExpandableLIstView
    // on the UI
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

        // Loops through available Gatt Services
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so
            if (SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
                deviceControlFragment.isSerial.setText("Yes, serial :-)");
                Log.d(TAG, "SERIAL?" + deviceControlFragment.isSerial.getText().toString());
            } else {
                deviceControlFragment.isSerial.setText("No, serial :-(");
            }
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characterstic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);

        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    //On click listener to turn on alerts
    public void btnTurnOnAlerts(){
        Log.d(TAG, "turning on alerts");
        String str = "Turn on alerts";
        final byte[] tx=  str.getBytes();
        if(mConnected)
        {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX,true);
        }
    }

    public void btnTurnOffAlerts(){
        String str = "Turn off alerts";
        final byte[] tx=  str.getBytes();
        if(mConnected)
        {
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
        }

    }

    public void btnSettings(View view)
    {
        mDrawerLayout.openDrawer(mDrawerLayout);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.title_devices);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        deviceControlFragment = new DeviceControlFragment();
        fragmentTransaction.replace(R.id.fragment_control, deviceControlFragment, FRAG_CONTROL);
        fragmentTransaction.addToBackStack(FRAG_CONTROL);
        fragmentTransaction.commit();

        //Set up navigation drawer
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();


        // Bind BluetoothLeService
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent,mServiceConnection, BIND_AUTO_CREATE);


        /*//Get Vibrator
        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(500);

        ////SETTING UP ALARM///
        //Create an intent for explicitly executing a hard-coded class(component) name
        // First create an intent for the alarm to activate.
        // This code simply starts an Activity, or brings it to the front if it has already
        // been created.
        Intent alarmintent = new Intent(this, DeviceControlActivity.class);
        intent.setAction(Intent.ACTION_MAIN);

        // Because the intent must be fired by a system service from outside the application,
        // it's necessary to wrap it in a PendingIntent.  Providing a different process with
        // a PendingIntent gives that other process permission to fire the intent that this
        // application has created.
        // Also, this code creates a PendingIntent to start an Activity.  To create a
        // BroadcastIntent instead, simply call getBroadcast instead of getIntent.
        pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                alarmintent, 0);

        // The AlarmManager, like most system services, isn't created by application code, but
        // requested from the system.
        alarmManager = (AlarmManager)
                this.getSystemService(this.ALARM_SERVICE);

        // setRepeating takes a start delay and period between alarms as arguments.
        // The below code fires after 15 seconds, and repeats every 15 seconds.  This is very
        // useful for demonstration purposes, but horrendous for production.  Don't be that dev.
        alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime() + ALARM_TIME,
         ALARM_TIME, pendingIntent);*/




    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if(mBluetoothLeService != null)
        {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result" + result);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;

        if(alarmManager != null)
            alarmManager.cancel(pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_gatt_services, menu);
        if(mConnected){
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        }else{
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDrawerItems(){

        mDrawerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerArray);
        mDrawerList.setAdapter(mDrawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment;
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (position) {
                    case 0:
                        fragment = new ActivityLogFragment();
                        transaction.replace(R.id.fragment_control, fragment);
                        transaction.commit();
                        break;
                    case 1:
                        fragment = new ProfileFragment();
                        transaction.replace(R.id.fragment_control, fragment);
                        transaction.commit();
                        break;
                    case 2:
                        fragment = new IntervalSettingsFragment();
                        transaction.replace(R.id.fragment_control, fragment);
                        transaction.commit();
                        break;
                    case 3:
                        fragment = new AlertSettingsFragment();
                        transaction.replace(R.id.fragment_control, fragment);
                        transaction.commit();
                        break;
                }
                setTitle(drawerArray[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


}

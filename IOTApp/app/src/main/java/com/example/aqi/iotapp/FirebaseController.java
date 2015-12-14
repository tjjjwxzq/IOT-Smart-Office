package com.example.aqi.iotapp;

import android.os.Handler;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Arbiter on 13/12/2015.
 */
public class FirebaseController {

    private static String TAG = FirebaseController.class.getSimpleName();

    public static final String FIREBASE_USERS_URL = "https://theinvisiblehand.firebaseio.com/users";

    public static Firebase usersRef = new Firebase(FIREBASE_USERS_URL);

    public static Firebase userDataRef;

    public static Calendar mCalendar = Calendar.getInstance();

    public static int time = mCalendar.get(Calendar.HOUR_OF_DAY);

    public static int sittingTimes;

    public static int totalSittingDuration;

    public static double aveSittingDuration;

    //ArrayList to store data points
    public static ArrayList<DataPoint>dataPoints = new ArrayList<DataPoint>();

    public static void setUpFirebase()
    {
        final UserSettings userSettings = MainActivity.userSettings;

        //Query by email which should be unique, unlike name
        Log.d(TAG, "current email" + userSettings.email);
        Query userQueryRef = usersRef.orderByChild("email").equalTo(userSettings.email);
        Log.d(TAG, "user query" + userQueryRef);

        userQueryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String userId = dataSnapshot.getKey();
                userDataRef = usersRef.child(userId).child("data");
                Log.d(TAG, "User data ref" + userDataRef);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static void writeNewUserToFirebase()
    {
        Log.d(TAG, "users ref " + usersRef);
        Log.d(TAG, "user data ref" + userDataRef);

        //Push new user data; use push even though email is unique
        //as email contains invalid characters
        Firebase newUserRef = usersRef.push();
        newUserRef.setValue(MainActivity.userSettings);
        String userId = newUserRef.getKey();

        if(userDataRef==null)
            userDataRef = usersRef.child(userId).child("data");

        Log.d(TAG, "user data ref" + userDataRef);
    }

    public static void readFirebase(int time)
    {
        final String finalTime = Integer.toString(time);
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Check for valid userDataRef
                while(userDataRef==null)
                {
                    try{
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "in thread user data ref" + userDataRef);
                            }
                        });
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                //Once a valid reference is found
                userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "userdataref read" + userDataRef);
                        if (dataSnapshot.child(finalTime).exists()) {
                            Log.d(TAG, "snapshot" + dataSnapshot);
                            sittingTimes = ((Long) dataSnapshot.child(finalTime)
                                    .child("sittingTimes").getValue()).intValue();
                            totalSittingDuration = ((Long) dataSnapshot.child(finalTime)
                                    .child("totalSittingDuration").getValue()).intValue();
                            aveSittingDuration = (double) dataSnapshot.child(finalTime)
                                    .child("aveSittingDuration").getValue();
                        } else {
                            sittingTimes = 0;
                            totalSittingDuration = 0;
                            aveSittingDuration = 0;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.d(TAG, "Read failed");
                    }
                });

            }
        }).start();
    }

    public static void updateFirebase(String mkey, Object mvalue)
    {
        final Handler mHandler = new Handler();
        final String key = mkey;
        final Object value = mvalue;

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Check for valid userDataRef
                while (userDataRef == null) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "in thread user data ref" + userDataRef);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "HOUR " + mCalendar.get(Calendar.HOUR_OF_DAY));

                int time = mCalendar.get(Calendar.HOUR_OF_DAY);

                Firebase timeRef = userDataRef.child(Integer.toString(time));
                DataObject dataObject;

                switch(key){
                    case "sittingTimes":
                        sittingTimes += 1;
                        dataObject = new DataObject(sittingTimes, totalSittingDuration,
                                aveSittingDuration);
                        timeRef.setValue(dataObject);
                        break;
                    case "totalSittingDuration":
                        totalSittingDuration += (int) value;
                        dataObject = new DataObject(sittingTimes, totalSittingDuration,
                                aveSittingDuration);
                        timeRef.setValue(dataObject);
                        break;
                }
            }
        }).start();

    }


    public static ArrayList<DataPoint> getDataNodes(int start, int end)
    {
        final String finalStart = Integer.toString(start);
        final String finalEnd = Integer.toString(end);
        final Handler mHandler = new Handler();


        new Thread(new Runnable() {
            @Override
            public void run() {
                //Check for valid userDataRef
                while (userDataRef == null) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "in thread user data ref" + userDataRef);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                //Order by hour-of-day
                Query queryRef = userDataRef.orderByKey().startAt(finalStart).endAt(finalEnd);

                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        int time = Integer.parseInt(dataSnapshot.getKey());
                        double totalSittingDuration = ((Long) dataSnapshot.child("totalSittingDuration")
                                .getValue()).doubleValue();
                        DataPoint dataPoint = new DataPoint(time, totalSittingDuration);
                        dataPoints.add(dataPoint);
                        Log.d(TAG, "Time" + dataSnapshot.getKey());
                        Log.d(TAG, "Value" + dataSnapshot.getValue());
                        Log.d(TAG, "total duration" + totalSittingDuration);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        }).start();

        return dataPoints;

    }

    public static class DataPoint{
        public int x;
        public double y;

        public DataPoint(int x, double y)
        {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString()
        {
            return "x: " + x + ", y: " + y;
        }
    }


    public static class DataObject {

        public int sittingTimes;

        public int totalSittingDuration;

        public double aveSittingDuration;

        public DataObject(int sittingTimes, int totalSittingDuration,
                          double aveSittingDuration) {
            this.sittingTimes = sittingTimes;
            this.totalSittingDuration = totalSittingDuration;
            this.aveSittingDuration = aveSittingDuration;
        }
    }


}

package com.example.aqi.iotapp;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityLogFragment extends Fragment {

    private static final String TAG = ActivityLogFragment.class.getSimpleName();

    private View root;

    private LineGraph li;

    public ActivityLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_activity_log, container, false);

        //Get data points
        //Order data by hour of day
        Firebase.setAndroidContext(getActivity());
        Log.d(TAG, "Querying data base");
        FirebaseController.getDataNodes(0,24);

        li = (LineGraph)root.findViewById(R.id.graph);
        li.setRangeX(0,24);
        li.setRangeY(0,100);
        li.setLineToFill(0);

        new RenderGraphTask().execute();

        return root;
    }

    private class RenderGraphTask extends AsyncTask<Void,Void,Line>{
        /** The system calls this to perform work in a worker thread and
          * delivers it the parameters given to AsyncTask.execute() */
        protected Line doInBackground(Void... params) {
            while(FirebaseController.dataPoints.isEmpty()){}
            Log.d(TAG, "DATA points" +FirebaseController.dataPoints);
            Line l = new Line();
            for(FirebaseController.DataPoint point: FirebaseController.dataPoints) {
                LinePoint p = new LinePoint();
                p.setX(point.x);
                p.setY(point.y);
                l.addPoint(p);
            }

            l.setColor(Color.parseColor("#FFBB33"));
            return l;
        }

        /** The system calls this to perform work in the UI thread and delivers
          * the result from doInBackground() */
        protected void onPostExecute(Line result) {
            li.addLine(result);
        }
    }

}

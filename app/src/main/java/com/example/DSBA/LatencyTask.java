package com.example.DSBA;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class LatencyTask extends AsyncTask<String, Void, Integer> {
    private final WeakReference<StreamVideoActivity> activityReference;

    LatencyTask(StreamVideoActivity activity) {
        activityReference = new WeakReference<>(activity);
    }

    @Override
    protected Integer doInBackground(String... params) {
        String host = params[0];
        try {
            URL url = new URL(host);
            long startTime = System.currentTimeMillis();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                long endTime = System.currentTimeMillis();
                return (int) (endTime - startTime);
            }
        } catch (IOException e) {
            Log.d("latency error", "Error is: " + e);
            e.printStackTrace();
        }
        return -1; // Error getting latency
    }

    @Override
    protected void onPostExecute(Integer result) {
        StreamVideoActivity activity = activityReference.get();
        if (activity != null) {
            // Update the latency value and call the calculatePerformance() method
            activity.setLatency(result);
        }
    }
}


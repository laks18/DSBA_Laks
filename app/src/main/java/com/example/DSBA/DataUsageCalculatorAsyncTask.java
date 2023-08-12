package com.example.DSBA;

import android.net.TrafficStats;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataUsageCalculatorAsyncTask extends AsyncTask<Void, Void, Long> {
    private static final int BYTES_TO_KB = 1024;
    private static final int KB_TO_MB = 1024;
    private final WeakReference<StreamVideoActivity> activityReference;

    public DataUsageCalculatorAsyncTask(StreamVideoActivity activity) {
        activityReference = new WeakReference<>(activity);
    }

    @Override
    protected Long doInBackground(Void... voids) {
        StreamVideoActivity act = activityReference.get();
        long initialBytes = TrafficStats.getTotalRxBytes();

        try {
            URL link = new URL(act.url);
            HttpURLConnection connection = (HttpURLConnection) link.openConnection();
            connection.connect();
            connection.getInputStream().close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long dataUsageBytes = TrafficStats.getTotalRxBytes() - initialBytes;
        return dataUsageBytes;
    }

    @Override
    protected void onPostExecute(Long dataUsageBytes) {
        StreamVideoActivity activity = activityReference.get();
        long dataUsageKB = dataUsageBytes / BYTES_TO_KB;
        long dataUsageMB = dataUsageKB / KB_TO_MB;

        if (activity != null && dataUsageBytes != 0) {
//            activity.showToast("data Usage Bytes: " + dataUsageKB);
            activity.setDataUsage(dataUsageKB);
        }

    }

}


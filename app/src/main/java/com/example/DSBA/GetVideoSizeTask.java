package com.example.DSBA;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetVideoSizeTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<StreamVideoActivity> activityReference;
    private static final int BYTES_TO_KB = 1024;
    private static final int KB_TO_MB = 1024;
    GetVideoSizeTask(StreamVideoActivity activity) {
        activityReference = new WeakReference<>(activity);
    }

    @Override
    protected String doInBackground(Void... voids) {
        StreamVideoActivity activity = activityReference.get();
        if (activity == null) {
            return null;
        }

        String videoDataSize = null;
        try {
            URL vurl = new URL(activity.url);
            HttpURLConnection urlConnection = (HttpURLConnection) vurl.openConnection();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                videoDataSize = urlConnection.getHeaderField("Content-Length");
            } else {
                videoDataSize = "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoDataSize;
    }

    @Override
    protected void onPostExecute(String videoSize) {
            StreamVideoActivity activity = activityReference.get();
        if (videoSize != null) {
            long videoDataSize = Long.parseLong(videoSize);
            long dataUsageKB = videoDataSize / BYTES_TO_KB;
            long dataUsageMB = dataUsageKB / KB_TO_MB;
            if (activity != null && videoDataSize != 0) {
    //            activity.showToast("Video Size: " + dataUsageKB);
                activity.setVideoDataSize(dataUsageKB);
            }
        } else {
            activity.showToast("Video Size not calculated");
        }
    }
}

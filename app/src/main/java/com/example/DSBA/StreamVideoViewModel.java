package com.example.DSBA;

import androidx.lifecycle.ViewModel;

public class StreamVideoViewModel extends ViewModel {
    private GetVideoSizeTask videoSizeTask;
    private LatencyTask latencyTask;
    private DataUsageCalculatorAsyncTask dataUsageTask;

    // Add any other state variables that you need to retain here

    public void startVideoSizeTask(GetVideoSizeTask task) {
        videoSizeTask = task;
        if (videoSizeTask != null) {
            videoSizeTask.execute();
        }

    }

    public void stopVideoSizeTask() {
        if (videoSizeTask != null) {
            videoSizeTask.cancel(true);
            videoSizeTask = null;
        }
    }

    public void startLatencyTask(LatencyTask task) {
        latencyTask = task;
        if (latencyTask != null) {
            latencyTask.execute();
        }
    }

    public void stopLatencyTask() {
        if (latencyTask != null) {
            latencyTask.cancel(true);
            latencyTask = null;
        }
    }

    public void startDataUsageTask(DataUsageCalculatorAsyncTask task) {
        dataUsageTask = task;
        if (dataUsageTask != null) {
            dataUsageTask.execute();
        }

    }

    public void stopDataUsageTask() {
        if (dataUsageTask != null) {
            dataUsageTask.cancel(true);
            dataUsageTask = null;
        }
    }

    // Add getter and setter methods for other state variables as needed
}

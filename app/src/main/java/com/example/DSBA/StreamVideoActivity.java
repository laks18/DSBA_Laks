package com.example.DSBA;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaFormat;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.video.VideoFrameMetadataListener;

import com.androidnetworking.AndroidNetworking;
import com.example.DSBA.databinding.ActivityStreamVideoBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


@OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)

public class StreamVideoActivity extends AppCompatActivity implements VideoFrameMetadataListener {
    ActivityStreamVideoBinding binding;

    Intent batteryInfo;
    ConnectivityManager conmgr;


    ExoPlayer player;
    String url, userUrl;
    ProgressDialog progressDialog;

    private Handler handler;
    private List<String> taskList = new ArrayList<>();
    double avaiMemPct;
    String totalMem;
    boolean isCharging;
    boolean usbCharge;
    boolean acCharge;
    float batteryPct;

    boolean isConnected;
    boolean isWifiConn;
    boolean isMobileConn;

    boolean cloud;
    boolean local;
    boolean fogNode;


    Integer complexityIndex;
    boolean isDelay;
    FirebaseFirestore db;

    long videoDataSize, internetUsedDataSize;
    int batteryPercentage;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;
    BatteryManager batteryManager;
    long videoStartTime, videoEndTime;
    double totalEnergyConsume;
    int avgCurrentMicroAh;
    int taskInQueue;
    long startTime, endTime;
    int latencyValue;
    List<String> urlsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStreamVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new ProgressDialog(StreamVideoActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        db = FirebaseFirestore.getInstance();

        // add links in list
        urlsList = new ArrayList<>();

//Storage 4
     //   String common = "https://firebasestorage.googleapis.com/v0/b/dsba--02.appspot.com/o/";
       url="https://firebasestorage.googleapis.com/v0/b/dsba--4.appspot.com/o/video01-7MB.MP4?alt=media&token=ebd1db36-0c7a-4038-b1c5-439a08862e63";
     // url ="https://firebasestorage.googleapis.com/v0/b/dsba--4.appspot.com/o/video02-15MB.MP4?alt=media&token=318251fd-334a-4c50-93d6-ff8c183ce2e7";
        //video 01 - 29.7MB
       // urlsList.add(common+"bird_small_animal_feathers_river_679.mp4?alt=media&token=7ee57090-989f-40a3-99bb-6e64133d70e7");


        /*Storage 3
        String common = "https://firebasestorage.googleapis.com/v0/b/dsba--02.appspot.com/o/";
        urlsList.add(common+"9845e051-c024-45f9-ab84-6749e54388da.MP4?alt=media&token=3a460470-f905-4f07-be01-2d2089b2eb6b");
        //video 01 - 4.7MB
        urlsList.add(common+"f169ff96-073d-404f-b6cc-5f7c48284fd1.MP4?alt=media&token=5a96b235-9a29-4888-8177-ca3e2c5d8dad");
        //video 02 - 71.9MB
*/
        // Generate a random index within the bounds of the list size
       // Random random = new Random();
       // int randomIndex = random.nextInt(urlsList.size());
        //url = urlsList.get(randomIndex);


        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VideoStreamingApp:WakeLock");

        try {
            player = new ExoPlayer.Builder(StreamVideoActivity.this).build();
            binding.playerView.setPlayer(player);
        } catch (Exception e) {
            showToast(e.getLocalizedMessage());
        }

        binding.startStreamButton.setOnClickListener(view -> {
            if (binding.startStreamButton.getText().toString().trim().equals("Start Stream")) {

                getBatteryConsumption();
                binding.startStreamButton.setText("Stop Stream");
            } else {
                player.stop();
                binding.startStreamButton.setText("Start Stream");
            }
        });

        binding.decisionEngineStartBtn.setOnClickListener(view -> {
            player.stop();
            startCFLDecisionEngine();
        });

        binding.resetScreenBtn.setOnClickListener(view -> {
            recreate();
        });


        // initialize networking
//        AndroidNetworking.initialize(StreamVideoActivity.this);
    }

    private void getBatteryConsumption() {
        progressDialog.show();
        try {
            showToast("battery Consumption checker start");
            batteryInfo = this.registerReceiver(null, new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));
            conmgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            // check battery leve
            checkBatteryLevel();
        } catch (Exception e) {
            showToast(e.getMessage());
        }


        //  Schedule battery level checking every 10 seconds
//        handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkBatteryLevel();
//            }
//        }, 10000);
    }

    private void checkBatteryLevel() {
        // Request the battery information
        try {
            showToast("battery information checking");
            taskList.add("battery information checking");
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = this.registerReceiver(null, filter);

            int batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            batteryPercentage = (batteryLevel * 100) / batteryScale;
            binding.batteryPercentage.setText(batteryPercentage + "");
            showToast("Battery Level: " + batteryPercentage + "%");
        } catch (Exception e) {
            showToast(e.getMessage());
        }

        // Start streaming video
        startStream();
    }
        // Schedule the next battery level checking after 10 seconds
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkBatteryLevel();
//            }
//        }, 10000);

// ----- Copy and Paste the below Code -----
// ------Start at line number 215 -----



    private void startStream() {
        showToast("video stream starting...");
        progressDialog.show();

        videoStartTime = System.currentTimeMillis();
        try {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
            player.setMediaItem(mediaItem);
            player.setVideoFrameMetadataListener(StreamVideoActivity.this);
            player.prepare();
            taskList.add("stream start");
            player.play();
            binding.startStreamButton.setText("Stop Stream");
            progressDialog.dismiss();

            // calculate energy consumption
            videoEndTime = System.currentTimeMillis();
            int currentNow = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            }
            totalEnergyConsume = (((batteryPercentage * videoStartTime) - (currentNow * videoEndTime)) / 3600000.0)*60;

            getVideoSize();
            getLatency();
            // get values

            taskInQueue = getTotalTaskCount() + 2; // the 2 task is get data size
            binding.taskQueueing.setText("" + taskInQueue);
            binding.energyEfficiency.setText(totalEnergyConsume + " mAh/mins");
            binding.decisionEngineStartBtn.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            showToast(e.getMessage());
        }

    }


    @Override
    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    public void onVideoFrameAboutToBeRendered(long presentationTimeUs, long releaseTimeNs, @NonNull Format format, MediaFormat mediaFormat) {
        // Update the values when a video frame is about to be rendered
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getDataUsageSize();
            }
        });
    }

    private void startCFLDecisionEngine() {
        progressDialog.show();
        cloud = false;
        local = false;
        fogNode = false;
        showToast("CFL Decision engine start");

        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        HashMap<String, Object> deviceMap = DeviceProfiler.getDeviceParameters(activityManager, batteryInfo);
        totalMem = (String) deviceMap.get("totalMem");
        avaiMemPct = (Double) deviceMap.get("avaiMemPct");
        isCharging = (Boolean) deviceMap.get("isCharging");
        usbCharge = (Boolean) deviceMap.get("usbCharge");
        acCharge = (Boolean) deviceMap.get("acCharge");
        batteryPct = (float) deviceMap.get("batteryPct");

        HashMap<String, Boolean> networkMap = NetworkProfiler.updateNetworkInfo(conmgr);
        isConnected = networkMap.get("isConnected");
        isWifiConn = networkMap.get("isWifiConn");
        isMobileConn = networkMap.get("isMobileConn");

        HashMap<String, Object> taskMap = TaskProfiler.getTaskdetails("localcall", "video");
        isDelay = (boolean) taskMap.get("isDelay");
        complexityIndex = (Integer) taskMap.get("complexity");

        int checkVideoDataSize = (int) videoDataSize / 1024;  // convert data KB to MB


        if (isConnected) {
            if (isWifiConn) {
                if (batteryPct >= 20 || totalEnergyConsume > 5) {
                    if (checkVideoDataSize > 10) {  //10MB
                        //fogNodeCall("fogNode");
                        cloudCall("cloud");
                    } else {
                        /*---  video size below 5 MB ---*/
                        localCall("local");
                    }

                } else if (batteryPct <= 20 || totalEnergyConsume < 5) {
                    /*--- battery below 20 percent  ---*/
                    if (checkVideoDataSize > 10) {  //5MB
                        //fogNodeCall("fogNode");
                        cloudCall("cloud");
                    } else {
                        /*---  video size below 5 MB ---*/
                        localCall("local");
                    }
                } else {
                    localCall("local");
                }
            } else {
                /*--- internet connected but not wifi  ---*/
                if (batteryPct >= 20 || totalEnergyConsume > 5) {
                    if (checkVideoDataSize > 5) {  //100MB
                        //fogNodeCall("fogNode");
                        cloudCall("cloud");
                    } else {
                        /*---  video size below 5 MB ---*/
                        localCall("local");
                    }

                } else if (batteryPct <= 20 || totalEnergyConsume < 5) {
                    /*--- battery below 20 percent  ---*/
                    if (checkVideoDataSize > 5) {  //100MB
                        //fogNodeCall("fogNode");
                        cloudCall("cloud");
                    } else {
                        /*---  video size below 5 MB ---*/
                        localCall("local");
                    }
                } else {
                    localCall("local");
                }
            }
        } else {
            /*--- no internet  ---*/
            showToast("no network");
        }


    }

    private void cloudCall(String s) {
        progressDialog.show();
        showToast("cloud Start");
        startTime = System.nanoTime();

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("bandwidth", getBandwidth() + " MB");
//            jsonObject.put("dataSize", videoDataSize + " KB");
//            jsonObject.put("energyConsumed", totalEnergyConsume + " mAh/hour");
//            jsonObject.put("taskQueue", taskInQueue + "Task in Queue");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        AndroidNetworking.post("aws link")
        // https://2z813fc9m7.execute-api.eu-west-1.amazonaws.com/default
//                .addJSONObjectBody(jsonObject)
//                .setTag("test")
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
        endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime) / 1000000000;
        sendDataToFirebase(s, totalTime, "Cloudoffload");
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                     sendDataToFirebase("LocalCall",0);
//                    }
//                });


    }

    private void localCall(String s) {
        progressDialog.show();
        showToast("local Start");
        sendDataToFirebase(s, 0, "Localoffload");

    }

    private void fogNodeCall(String s) {
        progressDialog.show();
        showToast("fogNode Start");
        sendDataToFirebase(s, 0, "fogNodeoffload");
    }

    private void sendDataToFirebase(String s, long timeTaken, String dbName) {
        Map<String, Object> cloudData = new HashMap<>();
        cloudData.put("bandwidth", getBandwidth() + " MB");
        cloudData.put("dataSize", videoDataSize + " KB");
        cloudData.put("energyConsumed", binding.energyEfficiency.getText().toString().trim());
        cloudData.put("taskQueue", taskInQueue + " Task in Queue");
        cloudData.put("offLoadTime", timeTaken + " Seconds");
        cloudData.put("latency", latencyValue + " milliSeconds");
        cloudData.put("database", s);

        db.collection(dbName).document(s + " Call at " + currentTime())
                .set(cloudData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("data add successful");
                        //  showDataToNext(s);
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                recreate();
                            }
                        }, 2000);


                    }
                })

                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showToast("Network Error : Failed to data add");
                });
    }

    private void showDataToNext(String s) {
        Intent intent = new Intent(StreamVideoActivity.this, CFLDataShowActivity.class);
        intent.putExtra("bandwidth", String.valueOf(getBandwidth()));
        intent.putExtra("dataSize", String.valueOf(videoDataSize));
        intent.putExtra("energyConsumed", String.valueOf(totalEnergyConsume));
        intent.putExtra("taskQueue", String.valueOf(getTotalTaskCount()));
        intent.putExtra("database", s);
        intent.putExtra("battery", String.valueOf(batteryPercentage));
        progressDialog.dismiss();
        startActivity(intent);
    }

    private String currentTime() {
        Date now = null;
        SimpleDateFormat formatter = null;
        try {
            now = new Date();
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            return String.valueOf(System.currentTimeMillis());
        }
        return formatter.format(now);
    }

    private int getBandwidth() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities networkCapabilities = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            }

            if (networkCapabilities != null) {
                int bandwidthMbps = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    bandwidthMbps = networkCapabilities.getLinkDownstreamBandwidthKbps() / 1000;
                }
                return bandwidthMbps;
            }
        }
        return -1; // Error getting bandwidth
    }

    void setVideoDataSize(long videoData) {
        videoDataSize = videoData;
        binding.videoDataSize.setText(videoDataSize + " KB");
    }

    void setDataUsage(long dataUsageKB) {
        internetUsedDataSize = dataUsageKB;
        binding.netDataSize.setText(dataUsageKB + " KB");
    }

    void setLatency(int latency) {
        latencyValue = latency;
        binding.latency.setText(latency + " milliseconds");
    }

    private void getVideoSize() {
        taskList.add("Get Video Data Size");
        new GetVideoSizeTask(this).execute();
    }

    public void getDataUsageSize() {
        new DataUsageCalculatorAsyncTask(this).execute();
    }

    private void getLatency() {
        try {
            taskList.add("Get Latency");
            String host = "www.example.com"; // Replace with the host you want to ping
            LatencyTask latencyTask = new LatencyTask(StreamVideoActivity.this);
            latencyTask.execute(host);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.stop();
    }

    void showToast(String s) {
        Toast.makeText(StreamVideoActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    private int getTotalTaskCount() {
        return taskList.size();
    }

    private static class LatencyTask extends AsyncTask<String, Void, Integer> {
        private WeakReference<StreamVideoActivity> activityReference;

        LatencyTask(StreamVideoActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                InetAddress inetAddress = InetAddress.getByName(params[0]);

                long startTime = System.currentTimeMillis();
                if (inetAddress.isReachable(3000)) { // Timeout set to 3000 milliseconds (3 seconds)
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

}

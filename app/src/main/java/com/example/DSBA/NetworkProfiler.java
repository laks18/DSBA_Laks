package com.example.DSBA;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.HashMap;


public class NetworkProfiler {

    public static HashMap<String, Boolean> updateNetworkInfo(ConnectivityManager conmgr){
        Log.i("QRcode-", "Entered Update network info");
        HashMap<String, Boolean> NetworkProMap = new HashMap<String, Boolean>();
        NetworkInfo activeNetwork = conmgr.getActiveNetworkInfo();
        Boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        Boolean isWifiConn = false;
        Boolean isMobileConn = false;

        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn=true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn=true;
            }
        }
        Log.i("QRcode-Network", String.valueOf(isConnected));

        NetworkProMap.put("isConnected",isConnected);
        NetworkProMap.put("isWifiConn",isWifiConn);
        NetworkProMap.put("isMobileConn",isMobileConn);
        return NetworkProMap;
    }
}

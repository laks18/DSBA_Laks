package com.example.DSBA;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.HashMap;

public class DeviceProfiler {


    public static HashMap<String, Object> getDeviceParameters(ActivityManager activityManager, Intent batteryInfo) {
        Log.i("QRcode-", "Entered available memory");
        HashMap<String, Object> DeviceMap = new HashMap<String, Object>();

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        Double avaiMemPct=roundTwoDecimals((memoryInfo.availMem/ (double)memoryInfo.totalMem)*100);
        String totalMem=String.valueOf(roundTwoDecimals(memoryInfo.totalMem/(1000*1000*1000)) +" GB");
        Log.i("QRcode- memory", String.valueOf(avaiMemPct));

        DeviceMap.put("totalMem",totalMem);
        DeviceMap.put("avaiMemPct",avaiMemPct);


        int status = batteryInfo.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        Boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;


        Boolean usbCharge = false;
        Boolean acCharge = false;
        if(isCharging) {
            int chargePlug = batteryInfo.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
             usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
             acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        }
        int level = batteryInfo.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryInfo.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;
        Log.i("QRcode- battery Pct", String.valueOf(batteryPct));
        Log.i("QRcode- battery ch", String.valueOf(isCharging));

        DeviceMap.put("isCharging",isCharging);
        DeviceMap.put("usbCharge",usbCharge);
        DeviceMap.put("acCharge",acCharge);
        DeviceMap.put("batteryPct",batteryPct);
        return DeviceMap;
    }

    public static double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}

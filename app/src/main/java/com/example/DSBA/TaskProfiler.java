package com.example.DSBA;

import java.util.HashMap;

public class TaskProfiler {



    public static HashMap<String, Object> getTaskdetails(String methodName, String taskName){

        HashMap<String, Integer> taskQueue = new HashMap<String, Integer>();
        taskQueue.put("videoStream-onCreate", 1);
        taskQueue.put("videoStream-getBatteryConsumption",2);
        taskQueue.put("videoStream-checkBatteryLevel",1);
        taskQueue.put("videoStream-startStream",3);

        HashMap<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("complexity",taskQueue.get(taskName+"-"+methodName));
        taskMap.put("isDelay",true );

        return taskMap;


    }
}

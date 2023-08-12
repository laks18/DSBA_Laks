package com.example.DSBA;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.DSBA.databinding.ActivityCfldataShowBinding;

public class CFLDataShowActivity extends AppCompatActivity {
    ActivityCfldataShowBinding binding;

    String bandwidth;
    String dataSize;
    String energyConsumed;
    String taskQueue;
    String database;
    String battery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCfldataShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bandwidth = getIntent().getStringExtra(bandwidth);
        dataSize = getIntent().getStringExtra(dataSize);
        energyConsumed = getIntent().getStringExtra(energyConsumed);
        taskQueue = getIntent().getStringExtra(taskQueue);
        database = getIntent().getStringExtra(database);
        battery = getIntent().getStringExtra(battery);

        binding.bandwidth.setText("Bandwidth : "+bandwidth);
        binding.datasize.setText("Data Size : "+dataSize+" KB");
        binding.energyConsumed.setText("Battery Consumed : "+energyConsumed+" mAh");
        binding.taskQueue.setText("Task Queue : "+taskQueue);
        binding.database.setText("Database Call : "+database);
        binding.batteryPercentage.setText("Battery : "+battery+"%");

    }
}
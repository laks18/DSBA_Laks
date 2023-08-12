package com.example.DSBA;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;

import com.example.DSBA.databinding.ActivityYtBinding;

public class YtActivity extends AppCompatActivity {
    ActivityYtBinding binding;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYtBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        url = "https://www.youtube.com/watch?v=6srYfC-GWsM";

        Uri uri = Uri.parse(url);
        binding.videoView.setVideoURI(uri);

        // Create a MediaController to control playback.
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediaController);

        // Start playing the video.
        binding.videoView.start();






    }
}
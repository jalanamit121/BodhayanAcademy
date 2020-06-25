package com.bodhayanacademy.Activity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bodhayanacademy.Model.UrlName;
import com.bodhayanacademy.R;
import com.bodhayanacademy.Utils.ProgressBarUtil;


public class DriveVideoActivity extends AppCompatActivity {
    VideoView drive_video;
    private UrlName urlName;
    private ProgressBarUtil progressBarUtil;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_video);
        drive_video=findViewById(R.id.drive_video);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            urlName = (UrlName) bundle.getSerializable("URL");
            if(urlName!=null){
                System.out.println("Suree:"+urlName.getType().equalsIgnoreCase("URL"));
            }
        }
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mediaController);
        drive_video.setVideoURI(Uri.parse("https://drive.google.com/file/d/1pc_0tMyXs67N-YxUQSmPNEtzgFfD-py7/view?usp=sharing"));
        drive_video.setMediaController(mediaController);
       // drive_video.setVideoURI(Uri.parse(urlName.getURL()));

        drive_video.requestFocus();
        drive_video.start();

    }
}

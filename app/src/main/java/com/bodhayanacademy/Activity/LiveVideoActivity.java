package com.bodhayanacademy.Activity;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.bodhayanacademy.R;


public class LiveVideoActivity extends AppCompatActivity {
    WebView myWebView;
    String html="<iframe scrolling src=\"http://winbeesolutions.livebox.co.in/livebox/player/?chnl=WinbeeDemo\" width=\"400px\" height=\"400px\" allowfullscreen webkitallowfullscreen mozallowfullscreen oallowfullscreen msallowfullscreen allow=\"autoplay\" ></iframe>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);
        myWebView=findViewById(R.id.myWebView);
        myWebView.loadData(html, "text/html", null);
    }
}

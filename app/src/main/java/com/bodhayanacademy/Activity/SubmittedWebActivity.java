package com.bodhayanacademy.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bodhayanacademy.LoginActivity;
import com.bodhayanacademy.Model.SubmittedDatum;
import com.bodhayanacademy.R;
import com.bodhayanacademy.SharedPrefManager;
import com.bodhayanacademy.Utils.AssignmentData;
import com.bodhayanacademy.Utils.ProgressBarUtil;

import java.util.Objects;

public class SubmittedWebActivity extends AppCompatActivity {
    private WebView webView;
    private SubmittedDatum assignmentDatum;
    private ProgressBarUtil progressBarUtil;
    RelativeLayout home,histroy,logout;
    Button btm_asked_question;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_web);
        webView =findViewById(R.id.myWebView);
        home=findViewById(R.id.layout_home);
        histroy=findViewById(R.id.layout_history);
        btm_asked_question=findViewById(R.id.btm_asked_question);
        logout=findViewById(R.id.layout_logout);
        histroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bodhayancoaching.com/"));
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubmittedWebActivity.this, GecHomeActivity.class);
                startActivity(intent);
            }
        });


        progressBarUtil   =  new ProgressBarUtil(this);
        progressBarUtil.showProgress();
        displayWebView();
        btm_asked_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SubmittedWebActivity.this,SubmitAssignmentActivity.class);
                startActivity(intent);
            }//ye butto hai
        });
    }

    private void displayWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(AssignmentData.Content);
                return true;

            }
        });
        webView.loadUrl(AssignmentData.Content);

    }

    private void logout() {
        SharedPrefManager.getInstance(this).logout();
        startActivity(new Intent(this, LoginActivity.class));
        Objects.requireNonNull(this).finish();
    }

}

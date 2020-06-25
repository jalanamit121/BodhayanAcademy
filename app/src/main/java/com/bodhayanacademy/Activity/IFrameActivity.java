package com.bodhayanacademy.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bodhayanacademy.Model.UrlName;
import com.bodhayanacademy.R;
import com.bodhayanacademy.Utils.FullScreenMediaController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IFrameActivity extends AppCompatActivity {
    VideoView vimeo_video;
    private UrlName urlName;
    Button play_btn;
    String videoUrl;
    String v_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_iframe);
        vimeo_video=findViewById(R.id.vimeo_video);


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            urlName = (UrlName) bundle.getSerializable("URL");
            if(urlName!=null){
                System.out.println("Suree:"+urlName.getType().equalsIgnoreCase("URL"));
                videoUrl=urlName.getURL();
            }
        }
        String fullScreen =  getIntent().getStringExtra("fullScreenInd");
        if("y".equals(fullScreen)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        String urlreplace= videoUrl.replace("https://vimeo.com/","https://player.vimeo.com/video/");
        String Url=urlreplace;
        Url+="/config";
        vimeoVideo(Url);
    }

    private void vimeoVideo(String Url) {
       StringRequest str = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {

              try {
                  JSONObject jsonObject = new JSONObject(response);
                  JSONObject req=jsonObject.getJSONObject("request");
                  JSONObject files=req.getJSONObject("files");
                  JSONArray progressive=files.getJSONArray("progressive");
                  JSONObject array1=progressive.getJSONObject(1);
                   v_url=array1.getString("url");

              }catch (JSONException e){
                  e.printStackTrace();
              }

               MediaController mediaController = new FullScreenMediaController(IFrameActivity.this);
               mediaController.setAnchorView(mediaController);
               vimeo_video.setVideoURI(Uri.parse(v_url));
               vimeo_video.setMediaController(mediaController);
               vimeo_video.start();
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {

           }
       });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(str);
    }


}

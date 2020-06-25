package com.bodhayanacademy.Activity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bodhayanacademy.Adapter.MessageAdapter;
import com.bodhayanacademy.Model.FireBaseUserId;
import com.bodhayanacademy.Model.LiveChatModel;
import com.bodhayanacademy.Model.Message;
import com.bodhayanacademy.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class ExoPlayer extends AppCompatActivity {

    private static final String TAG = "ExoPlayer";

    private SimpleExoPlayer player;
   private PlayerView simpleExoPlayerView;
   // private String videoUrl = "http://winbeesolutions.livebox.co.in/WinbeeDemohls/WinbeeDemo.m3u8";
   // private String videoUrl = "https://vimeo.com/showcase/7265364/embed";
    private String videoUrl = "http://winbeesolutions.livebox.co.in/BodhayanAcademyhls/LiveClasses.m3u8";
    boolean fullscreen = false;
    private FirebaseDatabase database;
    private RecyclerView recyclerView;
    DatabaseReference messagedb;
    //Firebase groupChat;
    MessageAdapter adapter;
    FireBaseUserId u;
    List<Message> messages;
    EditText txt_message;
    ImageButton btn_send;
    FirebaseAuth auth;

    //editTExt , ImageView and recyclerview for live chat
    EditText textMessage;
    ImageView sendButton;
    RecyclerView messageRecyclerView;
    private TextView textView;
    ProgressBar progressBar;
    RelativeLayout relativeLayoutLive;

    //Adapter
    MessageAdapter messageAdapter;

    // arraylist for message
    ArrayList<LiveChatModel> messagesArrayList;
    ArrayList<LiveChatModel> userDetails;

    //Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
   // FirebaseAuth auth=FirebaseAuth.getInstance();
    public static String userIdAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        Handler mainHandler = new Handler();
        database = FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        u=new FireBaseUserId();
       // txt_message=findViewById(R.id.txt_message);
//        btn_send=findViewById(R.id.btn_send);
//        btn_send.setOnClickListener(this);
        messages= new ArrayList<>();




        textMessage=findViewById(R.id.editTextMessageLive);
        sendButton=findViewById(R.id.sendMessageLive);
        messageRecyclerView=findViewById(R.id.liveMessageRecyclerViewLive);
        textView=findViewById(R.id.textNoMessageLive);
        progressBar=findViewById(R.id.progressBarLive);
        relativeLayoutLive=findViewById(R.id.relative_layout_live);


        messagesArrayList=new ArrayList<>();




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        retriveMessages();












//        recyclerView=findViewById(R.id.userComments);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();


        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        simpleExoPlayerView =  findViewById(R.id.exoplayer_view);
        simpleExoPlayerView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Exo2"), defaultBandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(Uri.parse(videoUrl));


        player.prepare(hlsMediaSource);
        simpleExoPlayerView.requestFocus();
        player.setPlayWhenReady(true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState){

                    case STATE_BUFFERING:
                        Toast.makeText(ExoPlayer.this, "buffering", Toast.LENGTH_SHORT).show();
                        break;
                    case STATE_READY:
                        Toast.makeText(ExoPlayer.this, "ready", Toast.LENGTH_SHORT).show();

                        break;
                    case STATE_ENDED:
                        Toast.makeText(ExoPlayer.this, "Ended", Toast.LENGTH_SHORT).show();

                        break;
                    case STATE_IDLE:
                        Toast.makeText(ExoPlayer.this, "Ideal", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });



        final ImageView fullscreenButton = simpleExoPlayerView.findViewById(R.id.exo_fullscreen_icon);
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fullscreen) {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ExoPlayer.this, R.drawable.ic_fullscreen_black_24dp));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                   // Log.d(TAG, "onClick: going to normal");
                    if(getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    simpleExoPlayerView.setLayoutParams(params);
                    fullscreen = false;

                    relativeLayoutLive.setVisibility(View.VISIBLE);
                    textMessage.setVisibility(View.VISIBLE);
                    messageRecyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.VISIBLE);

                }else{
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ExoPlayer.this, R.drawable.ic_fullscreen_exit_black_24dp));
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if(getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    simpleExoPlayerView.setLayoutParams(params);
                    fullscreen = true;

                    relativeLayoutLive.setVisibility(View.GONE);
                    textMessage.setVisibility(View.GONE);
                    messageRecyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    sendButton.setVisibility(View.GONE);


                   // Log.d(TAG, "onClick: going to land");
                }
            }
        });

    }



    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false); //to pause a video because now our video player is not in focus
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

//    @Override
//    public void onClick(View view) {
//
//        if (!TextUtils.isEmpty(txt_message.getText().toString())){
//            Message message = new Message(txt_message.getText().toString(),u.getName());
//            txt_message.setText("");
//            messagedb.push().setValue(message);
//
//        }else{
//            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        final FirebaseUser currentuser = auth.getCurrentUser();
//        u.setUid(currentuser.getUid());
//        u.setEmail(currentuser.getEmail());
//
//        database.getReference("Users").child(currentuser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                u=dataSnapshot.getValue(FireBaseUserId.class);
//                u.setUid(currentuser.getUid());
//                AllModel.name=u.getName();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        messagedb=database.getReference("messages");
//        messagedb.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                Message message = dataSnapshot.getValue(Message.class);
//                message.setKey(dataSnapshot.getKey());
//                messages.add(message);
//                displayMessages(messages);
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                Message message = dataSnapshot.getValue(Message.class);
//                message.setKey(dataSnapshot.getKey());
//                List<Message> newMessages  = new ArrayList<Message>();
//                for (Message m:messages){
//                    if (m.getKey().equals(message.getKey())){
//                        newMessages.add(message);
//                    }else{
//                        newMessages.add(m);
//                    }
//                }
//                messages = newMessages;
//                displayMessages(messages);
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                Message message = dataSnapshot.getValue(Message.class);
//                message.setKey(dataSnapshot.getKey());
//                List<Message> newMessages = new ArrayList<Message>();
//
//                for (Message m:messages){
//                    if (!m.getKey().equals(message.getKey())){
//                        newMessages.add(m);
//                    }
//                }
//                messages = newMessages;
//                displayMessages(messages);
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    @Override
//    protected  void onResume(){
//        super.onResume();
//        messages=new ArrayList<>();
//
//
//    }
//
//    private void displayMessages(List<Message> messages) {
////        recyclerView.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
//        adapter = new MessageAdapter(ExoPlayer.this,messages,messagedb);
//        recyclerView.setAdapter(adapter);
//    }
private void retriveMessages() {
    progressBar.setVisibility(View.VISIBLE);

    db.collection("LiveONE").orderBy("time",Query.Direction.DESCENDING)

            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    messagesArrayList = new ArrayList<>();
                    if (e != null) {
                        progressBar.setVisibility(View.GONE);
                        Log.w("YourTag", "Listen failed.", e);
                        return;

                    }

                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() == 0) {
                        messagesArrayList = new ArrayList<>();
                        progressBar.setVisibility(View.GONE);
                        //   progressBar.setVisibility(View.GONE);
                        Animation in = new AlphaAnimation(0.0f, 1.0f);
                        in.setDuration(1000);
                        textView.setAnimation(in);
                        textView.setText("No Messages Yet...");
                        addingDataToMessagedAdapter(messagesArrayList);
                        messageAdapter.notifyDataSetChanged();

                    } else {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (doc.exists()) {
                                // messagesArrayList = new ArrayList<>();
                                progressBar.setVisibility(View.GONE);
                                LiveChatModel message = doc.toObject(LiveChatModel.class);
                                messagesArrayList.add(message);
                                textView.setText("");
                                for ( int i=0;i<messagesArrayList.size();i++) {
                                    //    Log.d(TAG, "onEvent: " + messagesArrayList.get(i).getMessage());
                                    Log.d(TAG, "onEvent: " + messagesArrayList.get(i).getDocId());
                                }
                                //progressBar.setVisibility(View.GONE);
                                //  Log.d(TAG, "onEvent: "+messagesArrayList.size());
                                addingDataToMessagedAdapter(messagesArrayList);
                                messageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });




}

    private void addingDataToMessagedAdapter(ArrayList<LiveChatModel> messagesArrayList) {
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter=new MessageAdapter(this,messagesArrayList);
        messageRecyclerView.setAdapter(messageAdapter);


    }





private void sendMessage() {

    userIdAuth = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    final String Message= textMessage.getText().toString();
    if (Message.isEmpty()){
        Toast.makeText(this, "Please enter something", Toast.LENGTH_SHORT).show();


    }else{
        final Date currentTime = Calendar.getInstance().getTime();
        progressBar.setVisibility(View.VISIBLE);
        final Map<String,String> messageInfo = new HashMap<>();
        messageInfo.put("userId",userIdAuth);
        messageInfo.put("message",Message);
        messageInfo.put("time", String.valueOf(currentTime));
        textMessage.setText("");


        db.collection("LiveONE")
                .add(messageInfo)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            String docId =Objects.requireNonNull(task.getResult()).getId();

                            Log.d(TAG, "onComplete: message sent"+Message);
                            final Map<String,String> updatedMessageInfo = new HashMap<>();
                            updatedMessageInfo.put("docId", docId);
                            updatedMessageInfo.put("userId",userIdAuth);
                            updatedMessageInfo.put("message",Message);
                            updatedMessageInfo.put("time", String.valueOf(currentTime));


                            db.collection("LiveONE")
                                    .document(docId)
                                    .set(updatedMessageInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.d(TAG, "onComplete: done");
                                        Log.d(TAG, "onComplete: "+updatedMessageInfo);
                                    }else {
                                        Log.d(TAG, "onComplete: error");
                                    }
                                }
                            });




                            progressBar.setVisibility(View.GONE);
                        }else
                        {
                            Toast.makeText(ExoPlayer.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: failed");
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });








    }}
}

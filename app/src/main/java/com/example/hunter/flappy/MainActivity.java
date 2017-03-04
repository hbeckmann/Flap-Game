package com.example.hunter.flappy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.Window;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements View.OnClickListener, Runnable {

    private MediaPlayer mPlayer;
    private MainView mainView;
    private int viewHeight;
    private int viewWidth;
    private DisplayMetrics displayMetrics;
    private LinearLayout menu;
    private FrameLayout mainMenu;
    private Thread mainThread;
    private volatile boolean clicking;
    private Activity mainActivity;
    private Handler handler;
    private ImageButton playButton;
    private ImageButton creditsButton;
    private Intent gameActivity;
    private Intent creditActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clicking = false;

        mainActivity = this;
        gameActivity = new Intent(this, GameActivity.class);
        creditActivity = new Intent(this, CreditActivity.class);
        //Converts the layout xml into view objects
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemInflater = (View) inflater.inflate(R.layout.activity_main, null);
        //The frame layout is used to house the Surface View and the Relative view on top
        mainMenu = new FrameLayout(this);
        menu = (LinearLayout)itemInflater.findViewById(R.id.activity_main);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        viewHeight = displayMetrics.heightPixels;
        viewWidth = displayMetrics.widthPixels;
        mainView = new MainView(this, mainActivity, viewWidth, viewHeight);

        //uses the layout xml instead of drawing on a surfaceHolder
        //setContentView(R.layout.activity_main);
        //setContentView(mainView);
        //mainView.setOnClickListener(this);



        mainMenu.addView(mainView);
        mainMenu.addView(menu);
        setContentView(mainMenu);


        if(mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.roccow_welcome);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setVolume(.5f, .5f);
            mPlayer.start();
        }

    }

    @Override
    public void onClick(View v) {


    }


    @Override
    public void onStop() {
        if(mPlayer != null) {
            mPlayer.reset();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

        super.onStop();
    }

    @Override
    public void onStart() {

        super.onStart();


    }

    protected void onResume() {
        super.onResume();
        mainView.resume();
        if(mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.roccow_welcome);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.start();
        }
        playButton = (ImageButton) findViewById(R.id.playButton);
        playButton.setOnClickListener(playButtonHandler);
        creditsButton = (ImageButton) findViewById(R.id.creditButton);
        creditsButton.setOnClickListener(creditButtonHandler);
    }

    protected void onPause() {
        super.onPause();
        mainView.pause();
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.reset();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mainView.getBackgroundObject().releaseBitmaps();
        mainView.background = null;
    }

    @Override
    public void run() {


    }

    View.OnClickListener playButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {

            startActivity(gameActivity);

        }
    };

    View.OnClickListener creditButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {

            startActivity(creditActivity);

        }
    };

}

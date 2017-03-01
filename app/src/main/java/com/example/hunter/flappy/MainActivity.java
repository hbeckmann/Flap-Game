package com.example.hunter.flappy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.view.Window;
import android.util.DisplayMetrics;

public class MainActivity extends Activity implements View.OnClickListener, Runnable {

    private Button playButton;
    private MediaPlayer mPlayer;
    private MainView mainView;
    private int viewHeight;
    private int viewWidth;
    private DisplayMetrics displayMetrics;
    private Intent gameActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameActivity = new Intent(this, GameActivity.class);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        viewHeight = displayMetrics.heightPixels;
        viewWidth = displayMetrics.widthPixels;
        mainView = new MainView(this, viewWidth, viewHeight);

        //uses the layout xml instead of drawing on a surfaceHolder
        //setContentView(R.layout.activity_main);
        setContentView(mainView);
        mainView.setOnClickListener(this);

        if(mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.roccow_welcome);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setVolume(.5f, .5f);
            mPlayer.start();
        }

        //playButton = (Button) findViewById(R.id.commenceFlapButton);

        //playButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        startActivity(gameActivity);

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
        if(mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.roccow_welcome);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.start();
        }

    }

    protected void onResume() {
        super.onResume();
        mainView.resume();
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

}

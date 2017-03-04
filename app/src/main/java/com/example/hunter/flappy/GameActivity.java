package com.example.hunter.flappy;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {

    private GameView gameView;
    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int viewHeight;
    private int viewWidth;
    private MediaPlayer mPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        viewHeight = displayMetrics.heightPixels;
        viewWidth = displayMetrics.widthPixels;
        gameView = new GameView(this, viewWidth, viewHeight);
        setContentView(gameView);

        mPlayer = MediaPlayer.create(this, R.raw.something_elated);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setVolume(.5f, .5f);
        mPlayer.start();


        gameView.setOnTouchListener(gameView);


    }

    //pauses game when activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.reset();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    //running game when activity is resumed
    @Override
    protected void onResume() {

        super.onResume();
        gameView.resume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mPlayer == null && !mPlayer.isPlaying()) {
            mPlayer = MediaPlayer.create(this, R.raw.something_elated);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.start();
        }

    }

    @Override
    protected void onStop() {
        System.out.println("------------ EXITING AND DRESTROYING BACKGROUND");
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.reset();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        super.onStop();
        super.finish();
    }






}

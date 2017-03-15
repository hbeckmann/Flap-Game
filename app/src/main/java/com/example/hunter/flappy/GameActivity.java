package com.example.hunter.flappy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
    private float musicVolume;
    private SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        viewHeight = displayMetrics.heightPixels;
        viewWidth = displayMetrics.widthPixels;
        gameView = new GameView(this, viewWidth, viewHeight);
        setContentView(gameView);

        sharedPref = getSharedPreferences(
                "Settings", Context.MODE_PRIVATE);
        musicVolume = ((float) sharedPref.getInt("music_volume", 5) / 10);

        mPlayer = MediaPlayer.create(this, R.raw.something_elated);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setVolume(musicVolume, musicVolume);
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

        musicVolume = ((float) sharedPref.getInt("music_volume", 5) / 10);
        mPlayer.setVolume(musicVolume, musicVolume);

    }



    @Override
    protected void onStart() {
        super.onStart();
        if(mPlayer == null) {
            mPlayer = MediaPlayer.create(this, R.raw.something_elated);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.start();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        super.finish();
        Log.w("Stopping Game Activity", "True");
        if(mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.reset();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

    }






}

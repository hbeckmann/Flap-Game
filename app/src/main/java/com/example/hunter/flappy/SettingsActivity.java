package com.example.hunter.flappy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**
 * Created by Hunter on 3/6/2017.
 */

public class SettingsActivity extends Activity{

    private FrameLayout frame;
    private MainView backdrop;
    private Context context;
    private Activity settingsActivity;
    private DisplayMetrics displayMetrics;
    private int viewHeight;
    private int viewWidth;
    private LayoutInflater inflater;
    private View itemInflater;
    private LinearLayout settingsMenu;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int musicVolume;
    private int sfxVolume;
    private SeekBar musicSeekbar;
    private SeekBar sfxSeekbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        settingsActivity = this;
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemInflater = (View) inflater.inflate(R.layout.activity_settings, null);
        settingsMenu = (LinearLayout)itemInflater.findViewById(R.id.activity_settings);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        viewHeight = displayMetrics.heightPixels;
        viewWidth = displayMetrics.widthPixels;
        backdrop = new MainView(context, settingsActivity, viewWidth, viewHeight);
        frame = new FrameLayout(this);


        musicSeekbar = (SeekBar) settingsMenu.findViewById(R.id.musicSeekbar);
        sfxSeekbar = (SeekBar) settingsMenu.findViewById(R.id.sfxSeekbar);




        musicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor = sharedPref.edit();
                editor.clear();
                editor.putInt("music_volume", progress);
                editor.commit();

                System.out.println("Current progress on the bar is: " + progress);
            }
        });

        sfxSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor = sharedPref.edit();
                editor.clear();
                editor.putInt("sfx_volume", progress);
                editor.commit();

                System.out.println("Current progress on the bar is: " + progress);
            }
        });

        //TODO: warning checkbox


        frame.addView(backdrop);
        frame.addView(settingsMenu);
        setContentView(frame);

    }

    @Override
    public void onResume() {
        super.onResume();
        backdrop.resume();
        retrieveSfxVolume();
        retriveMusicVolume();

    }

    @Override
    public void onPause() {
        super.onPause();
        backdrop.pause();
        backdrop.getBackgroundObject().releaseBitmaps();
        backdrop.background = null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void retriveMusicVolume() {

        sharedPref = getSharedPreferences(
                "Settings", Context.MODE_PRIVATE);
        musicVolume = sharedPref.getInt("music_volume", 5);
        musicSeekbar.setProgress(musicVolume);
    }

    public void retrieveSfxVolume() {
        sharedPref = getSharedPreferences(
                "Settings", Context.MODE_PRIVATE);
        sfxVolume = sharedPref.getInt("sfx_volume", 5);
        sfxSeekbar.setProgress(sfxVolume);
    }


}

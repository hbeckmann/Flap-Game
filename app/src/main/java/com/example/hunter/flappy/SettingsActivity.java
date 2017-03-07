package com.example.hunter.flappy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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

        frame.addView(backdrop);
        frame.addView(settingsMenu);
        setContentView(frame);

    }

    @Override
    public void onResume() {
        super.onResume();
        backdrop.resume();
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

}

package com.example.hunter.flappy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Hunter on 3/4/2017.
 */

public class CreditActivity extends Activity {



    private Background background;
    private FrameLayout frameLayout;
    private MainView mainView;
    private LinearLayout credits;
    private DisplayMetrics displayMetrics;
    private int viewHeight;
    private int viewWidth;
    private Activity creditActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        creditActivity = this;
        frameLayout = new FrameLayout(this);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        viewHeight = displayMetrics.heightPixels;
        viewWidth = displayMetrics.widthPixels;
        mainView = new MainView(this, creditActivity, viewWidth, viewHeight);
        System.out.println("hello????????????????????????????");
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemInflater = (View) inflater.inflate(R.layout.activity_credits, null);
        credits = (LinearLayout)itemInflater.findViewById(R.id.activity_credits);



        frameLayout.addView(mainView);
        frameLayout.addView(credits);

        setContentView(frameLayout);
        //setContentView(credits);
        //setContentView(R.layout.activity_credits);

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainView.pause();
        mainView.getBackgroundObject().releaseBitmaps();
        mainView.background = null;
        mainView = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mainView.resume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


}

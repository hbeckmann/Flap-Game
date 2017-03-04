package com.example.hunter.flappy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Hunter on 2/28/2017.
 */

public class MainView extends SurfaceView implements Runnable {

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    public Background background;
    private volatile Boolean viewing;
    private Thread menuThread;
    private Context currentContext;
    private int viewWidth;
    private int viewHeight;


    public MainView(Context context, Activity mainActivity, int viewWidth, int viewHeight) {

        super(context);
        currentContext = context;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        surfaceHolder = getHolder();
        background = new Background(context, R.drawable.background01_small, viewWidth, viewHeight);
        background.setScrollSpeed(1);

    }

    public void run() {

        while (viewing) {
            //update frame
            update();
            //draw frame
            draw();
            //control
            control();
        }

    }

    private void update() {
        background.scrollBackground();
    }

    private void draw() {
        //check if surface is valid
        //System.out.println("Attempting Draw!!!!!!!!!!!!!!!!!!!!!");
        //System.out.println(surfaceHolder.getSurface().isValid());

        if (surfaceHolder.getSurface().isValid()) {
            //lock the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color on canvas
            canvas.drawColor(Color.WHITE);
            if(background.getBackgroundBitmap() != null) {

                canvas.drawBitmap(
                        background.getBackgroundBitmap(),
                        background.getBackgroundX(),
                        0,
                        paint
                );

            }

            if(background.getReverseBackground() != null) {

                canvas.drawBitmap(
                        background.getReverseBackground(),
                        background.getReversebackgroundX(),
                        0,
                        paint
                );

            }
            //unlock the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try{
            menuThread.sleep(1000 / 60);
        } catch(InterruptedException e) {

        }
    }

    public void pause() {
        //when the game is paused
        viewing = false;
        System.out.println("-----------------PAUSING MAIN, VIEWING IS NOW FALSE");
        try {
            //stopping thread
            menuThread.join();
        } catch(InterruptedException e) {

        }

    }

    public void resume() {
        //when the game is resumed
        //start thread
        System.out.println("hello!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if(background == null) {
            System.out.println("------------------------------------ reimplementing background");
            background = new Background(currentContext, R.drawable.background01_small, viewWidth, viewHeight);
            background.setScrollSpeed(viewWidth / 500);
        }
        viewing = true;

        menuThread = new Thread(this);
        menuThread.start();

    }




    public Background getBackgroundObject() {
        return background;
    }

}

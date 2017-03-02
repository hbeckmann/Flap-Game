package com.example.hunter.flappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Hunter on 2/26/2017.
 */

public class GameView extends SurfaceView implements Runnable, View.OnTouchListener{

    //boolean var to track if game is being played
    volatile boolean playing;
    private Thread gameThread = null;
    private int vHeight;
    private int vWidth;
    private int jumpScale = 55;

    private Player player;
    private Pipe pipe;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Background background;
    private Boolean readyToDraw;
    private MediaPlayer mediaPlayer;
    private Context currentContext;
    private boolean firstFrame;



    //Class constructor
    public GameView(Context context, int viewWidth, int viewHeight) {
        super(context);

        surfaceHolder = getHolder();
        //surfaceHolder.addCallback(new MyCallback());
        background = new Background(context, R.drawable.background01_small, viewWidth, viewHeight);
        player = new Player(context, viewWidth, viewHeight);
        pipe = new Pipe(context, viewWidth, viewHeight);
        paint = new Paint();
        readyToDraw = false;
        vHeight = viewHeight;
        vWidth = viewWidth;
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(context, R.raw.jump2);
        //Won't work without the while loop - P R O G R A M M I N G
        while (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.jump2);
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(1f, 1f);


    }
//
//    public class MyCallback implements SurfaceHolder.Callback {
//
//        public void surfaceChanged(SurfaceHolder holder, int format,
//                                   int width, int height) {
//        }
//
//        @Override
//        public void surfaceCreated(SurfaceHolder holder) {
//            // you need to start your drawing thread here
//            System.out.println("surface created!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            readyToDraw = true;
//
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder holder) {
//            // and here you need to stop it
//        }
//    }

    @Override
    public void run() {
        while (playing) {
            //update frame
            update();
            //draw frame
            draw();
            //control
            control();

        }

    }

    private void update() {
        pipe.update();
        player.update();
        background.scrollBackground();
        detectHits();
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

            //draw the player
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint
            );
            //draw the top pipe
            canvas.drawBitmap(
                    pipe.getAboveBitmap(),
                    pipe.getX(),
                    pipe.getAboveY(),
                    paint
            );
            //bottom pipe
            canvas.drawBitmap(
                    pipe.getBelowBitmap(),
                    pipe.getX(),
                    pipe.getBelowY(),
                    paint
            );
            //unlock the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }


    }

    private void control() {
        if(firstFrame && surfaceHolder.getSurface().isValid()) {
            playing = false;
        }
        try{
            gameThread.sleep(1000 / 60);
        } catch(InterruptedException e) {

        }
    }

    public void pause() {
        //when the game is paused
        try {
            //stopping thread
            gameThread.join();
        } catch(InterruptedException e) {

        }
        playing = false;
        player.releaseSprites();
        pipe.recycleBitmaps();
        background.releaseBitmaps();
       if(mediaPlayer != null) {
           mediaPlayer.stop();
           mediaPlayer.release();
           mediaPlayer = null;
       }

    }

    public void resume() {
        //when the game is resumed
        //start thread
        playing = true;
        firstFrame = true;

        gameThread = new Thread(this);
        gameThread.start();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(playing && event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            //Player is jumping
           player.setVelocity((vHeight / jumpScale));
           player.animateJump();
            if(mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }

        }

        if(!playing && firstFrame && event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            //Player is starting the Game
            background.reset();
            pipe.reset();
            player.reset();
            playing = true;
            firstFrame = false;
            gameThread = new Thread(this);
            gameThread.start();
            player.setVelocity((vHeight / jumpScale));
            player.animateJump();
        }

        if(!playing && event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            //Player Died
            background.reset();
            pipe.reset();
            player.reset();
            playing = true;
            firstFrame = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        return true;
    }

    public Background getBackgroundObject() {
        return background;
    }


    public void detectHits() {

        if(player.getY() + player.getHeight() > vHeight || player.getY() < 0) {
            playing = false;
            firstFrame = false;
        }

    }

}

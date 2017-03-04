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

    private final static int FPS = 1000 / 60;
    private long beginTime;
    private int framesSkipped;
    private long timeDiff;
    private int sleepTime;

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

    @Override
    public void run() {
        System.out.println("beginning to run!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        while (playing) {
            beginTime = System.currentTimeMillis();
            framesSkipped = 0;

            synchronized (surfaceHolder) {

                update();
                //draw frame
                draw();
                //control
                control();

                timeDiff = System.currentTimeMillis() - beginTime;

                sleepTime = (int) (FPS - timeDiff);
                //System.out.println(sleepTime);

                if(sleepTime > 0) {
                    try {
                        gameThread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        System.out.println(e.getCause());
                    }

                }

//                while(sleepTime < 0 && framesSkipped < 5) {
//                    //catch up without a draw
//                    update();
//                    sleepTime += FPS;
//                    framesSkipped++;
//                }
            }

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
            try {
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
            } catch (Exception e) {
                System.out.println(e.getCause());
            }

            //unlock the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }


    }

    private void control() {
        if(firstFrame && surfaceHolder.getSurface().isValid()) {
            playing = false;
        }
//        try{
//            gameThread.sleep(1);
//        } catch(InterruptedException e) {
//
//        }
    }

    public void pause() {
        //when the game is paused
//        try {
//            stopping thread
//            gameThread.join();
//        } catch(InterruptedException e) {
//
//        }
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
            player.setVelocity(0);
//            player.animateJump();
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

        if(player.getHitBoxX() + player.getHitBoxWidth() >= pipe.getX()
                && player.getHitBoxX() < pipe.getX() + pipe.getWidth()
                && (player.getHitBoxY() < pipe.getAboveY() + pipe.getAboveOpening() - pipe.getAboveHitboxLeniency()
                || player.getHitBoxY() + player.getHitBoxHeight() > pipe.getBelowY() + pipe.getBelowHitboxLeniency())) {
            System.out.println("Hitbox Y: " + player.getHitBoxY() + "   Pipe Opening is " + pipe.getAboveY() + pipe.getAboveOpening());
            playing = false;
            firstFrame = false;
        }


    }

}

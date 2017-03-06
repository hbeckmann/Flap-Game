package com.example.hunter.flappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
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
    volatile boolean dying;
    private Thread gameThread = null;
    private int vHeight;
    private int vWidth;
    private int jumpScale = 55;
    private int currentScore;
    private int highScore;
    private boolean passedPipes;

    private Player player;
    private Pipe pipe;
    private Score scoreObj;

    private final static int FPS = 1000 / 60;
    private long beginTime;
    private int framesSkipped;
    private long timeDiff;
    private int sleepTime;


    private Paint paint;
    private Paint scorePaint;
    private Paint hscorePaint;
    private Typeface typeface;

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Background background;
    private Bitmap expSpriteRaw;
    private Bitmap expSprite;
    private Rect src;
    private Rect dst;
    private int spriteRow;


    private Boolean readyToDraw;
    private MediaPlayer mediaPlayer;
    private MediaPlayer coinMediaPlayer;
    private Context currentContext;
    private boolean firstFrame;
    private int deathFrame;




    //Class constructor
    public GameView(Context context, int viewWidth, int viewHeight) {
        super(context);

        surfaceHolder = getHolder();
        background = new Background(context, R.drawable.background01_small, viewWidth, viewHeight);
        player = new Player(context, viewWidth, viewHeight);
        pipe = new Pipe(context, viewWidth, viewHeight);
        scoreObj = new Score(context);
        highScore = scoreObj.retrieveHighScore();

        paint = new Paint();
        hscorePaint = new Paint();
        scorePaint = new Paint();
        paint.setColor(Color.WHITE);
        scorePaint.setColor(Color.WHITE);
        hscorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(viewWidth / 5);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.otf");
        scorePaint.setTypeface(typeface);
        hscorePaint.setTypeface(typeface);
        hscorePaint.setTextSize(viewWidth / 20);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        hscorePaint.setShadowLayer(10f, 5f, 10f, Color.BLACK );
        scorePaint.setShadowLayer(10f, 5f, 10f, Color.BLACK );

        deathFrame = 0;
        src = new Rect(0, 0, 100, 100);
        spriteRow = 0;


        expSpriteRaw = BitmapFactory.decodeResource(context.getResources(), R.drawable.testingxpl);
        expSprite = Bitmap.createBitmap(expSpriteRaw);

        readyToDraw = false;
        vHeight = viewHeight;
        vWidth = viewWidth;
        passedPipes = false;

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(context, R.raw.jump2);
        //Won't work without the while loop - P R O G R A M M I N G
        while (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.jump2);
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(1f, 1f);

        coinMediaPlayer = new MediaPlayer();
        coinMediaPlayer = MediaPlayer.create(context, R.raw.coin2);
        //Won't work without the while loop - P R O G R A M M I N G
        while (coinMediaPlayer == null) {
            coinMediaPlayer = MediaPlayer.create(context, R.raw.coin2);
        }
        coinMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        coinMediaPlayer.setVolume(1f, 1f);


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

        while (dying ) {
            System.out.println("we died boyzzzzzzzzzzzzzzzzzzzzzzzz!");
            animateDeath();
            if(deathFrame >= 39) {
                dying = false;
            }
        }

    }

    private void update() {
        passedPipes = pipe.update(passedPipes);
        player.update();
        background.scrollBackground();
        currentScore = scoreObj.getCurrentScore();
        detectHits();
        detectScore();
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
                //canvas.drawBitmap(expSprite, 200, 200, paint );
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

                //Current Score
                canvas.drawText(Integer.toString(currentScore), vWidth / 2, vHeight/8, scorePaint);

                canvas.drawText("High Score: " + Integer.toString(highScore), vWidth * .6f, vHeight * .9f , hscorePaint);

                if(dying) {
                    System.out.println("drawing explosion");
                    canvas.drawBitmap(expSprite, src, dst, paint );
                }


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

        if(coinMediaPlayer != null) {
            coinMediaPlayer.stop();
            coinMediaPlayer.release();
            coinMediaPlayer = null;
        }

        expSpriteRaw.recycle();
        expSprite.recycle();
        expSprite=null;
        expSpriteRaw=null;



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
            dying = false;
            spriteRow = 0;
            highScore = scoreObj.retrieveHighScore();
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
            dying = true;
            deathFrame = 0;
            scoreObj.saveHighScore();
            scoreObj.reset();
        }

        if(player.getHitBoxX() + player.getHitBoxWidth() >= pipe.getX()
                && player.getHitBoxX() < pipe.getX() + pipe.getWidth()
                && (player.getHitBoxY() < pipe.getAboveY() + pipe.getAboveOpening() - pipe.getAboveHitboxLeniency()
                || player.getHitBoxY() + player.getHitBoxHeight() > pipe.getBelowY() + pipe.getBelowHitboxLeniency())) {
            //Player has hit a pipe or wall
            playing = false;
            firstFrame = false;
            dying = true;
            deathFrame = 0;
            scoreObj.saveHighScore();
            scoreObj.reset();
        }


    }

    public void detectScore() {

        if (player.getHitBoxX() > pipe.getX() + pipe.getWidth() && !passedPipes) {
            passedPipes = true;
            scoreObj.incrementScore();
            if(coinMediaPlayer != null) {
                coinMediaPlayer.seekTo(0);
                coinMediaPlayer.start();
            }
        }

    }

    public void animateDeath() {
        System.out.println("attempting to animate death");
        updateSpriteSheet();
        draw();
        deathFrame++;
        try {
            gameThread.sleep(10);
        } catch (InterruptedException e) {
            e.getCause();
        }


    }

    public void updateSpriteSheet() {

        src.offset(100, 0);
        if(deathFrame % 10 == 0) {
            spriteRow++;
            src = new Rect(0, 100 * spriteRow, 100, 100 * (spriteRow + 1));
        }

        //dst = new Rect(player.getX(), player.getY(), player.getX() + player.getWidth(), player.getY() + player.getHeight());
        dst = new Rect(player.getX() - player.getWidth(), player.getY() - player.getHeight(), player.getX() + player.getWidth() * 2, player.getY() + player.getHeight() * 2);


    }





}

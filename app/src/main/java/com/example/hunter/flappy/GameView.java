package com.example.hunter.flappy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Timer;
import java.util.TimerTask;

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
    private int fadeCounter;
    private boolean fadingOut;
    private boolean currentlyAnimating;
    private boolean warningsDisabled;
    private Timer t;

    private HitDetector hitDetector;
    private SoundManager soundManager;

    private Paint paint;
    private Paint scorePaint;
    private Paint hscorePaint;
    private Paint fadePaint;
    private Typeface typeface;

    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Background background;

    private SharedPreferences sharedPref;
    private Context currentContext;
    private boolean firstFrame;
    private int deathFrame;

    Powerups powerup;

    //Class constructor
    public GameView(Context context, int viewWidth, int viewHeight) {
        super(context);

        currentContext = context;
        surfaceHolder = getHolder();
        background = new Background(context, R.drawable.background01_small, viewWidth, viewHeight);
        player = new Player(context, viewWidth, viewHeight, this);
        pipe = new Pipe(context, viewWidth, viewHeight);
        scoreObj = new Score(context);
        highScore = scoreObj.retrieveHighScore();



        paint = new Paint();
        hscorePaint = new Paint();
        scorePaint = new Paint();
        fadePaint = new Paint();
        paint.setColor(Color.WHITE);
        scorePaint.setColor(Color.WHITE);
        hscorePaint.setColor(Color.WHITE);
        fadePaint.setColor(Color.RED);
        scorePaint.setTextSize(viewWidth / 5);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.otf");
        scorePaint.setTypeface(typeface);
        hscorePaint.setTypeface(typeface);
        fadePaint.setTypeface(typeface);
        hscorePaint.setTextSize(viewWidth / 20);
        fadePaint.setTextSize(viewWidth / 10);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        fadePaint.setTextAlign(Paint.Align.CENTER);
        hscorePaint.setShadowLayer(10f, 5f, 10f, Color.BLACK );
        scorePaint.setShadowLayer(10f, 5f, 10f, Color.BLACK );
        fadePaint.setAlpha(0);

        deathFrame = 0;
        t = new Timer();
        fadeCounter = 0;
        currentlyAnimating = false;

        vHeight = viewHeight;
        vWidth = viewWidth;
        passedPipes = false;

        sharedPref = context.getSharedPreferences(
                "Settings", Context.MODE_PRIVATE);
        warningsDisabled = sharedPref.getBoolean("warning", true);

        powerup = new Powerups(player, pipe);
        hitDetector = new HitDetector(this);
        soundManager = new SoundManager(this);
        soundManager.setAllPlayers();



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
        hitDetector.update();
        powerup.updatePosition();
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

                //Testing PowerupS REMOVE LATER
                //canvas.drawRect((float) powerup.getX(), (float) powerup.getY(), (float) (powerup.getX() + powerup.getWidth()), (float) (powerup.getY() + powerup.getHeight()), scorePaint);

                canvas.drawText("High Score: " + Integer.toString(highScore), vWidth * .6f, vHeight * .9f , hscorePaint);

                canvas.drawText("DANGER!", vWidth / 2, vHeight/3, fadePaint);

                if(dying) {
                    canvas.drawBitmap(player.getExpSprite(), player.getSrc(), player.getDst(), paint );
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
        player.destroyExSprite();



    }

    public void resume() {
        //when the game is resumed
        //start thread
        playing = true;
        firstFrame = true;

        gameThread = new Thread(this);
        gameThread.start();



        sharedPref = currentContext.getSharedPreferences(
                "Settings", Context.MODE_PRIVATE);
        warningsDisabled = sharedPref.getBoolean("warning", false);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(playing && event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            //Player is jumping
           player.setVelocity((vHeight / jumpScale));
           player.animateJump();
            if(soundManager.getMediaPlayer() != null) {
                soundManager.getMediaPlayer().seekTo(0);
                soundManager.getMediaPlayer().start();
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
            player.setSpriteRow(0);
            highScore = scoreObj.retrieveHighScore();
            gameThread = new Thread(this);
            gameThread.start();



        }

        return true;
    }

    public void animateDeath() {
        player.updateSpriteSheet();
        draw();
        deathFrame++;
        try {
            gameThread.sleep(10);
        } catch (InterruptedException e) {
            e.getCause();
        }


    }



    public void animateCloseCall() {


        if(!currentlyAnimating && !warningsDisabled) {
            currentlyAnimating = true;
            t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {

                              @Override
                              public void run() {
                                  if(fadeCounter >= 0 && !fadingOut) {
                                      fadeCounter+=5;
                                  }
                                  if(fadeCounter > 254 && !fadingOut) {
                                      fadingOut = true;
                                  }

                                  if(fadingOut && fadeCounter != 0) {
                                      fadeCounter-=5;
                                  } else if (fadingOut && fadeCounter == 0) {
                                      currentlyAnimating = false;
                                      t.cancel();
                                  }

                                  fadePaint.setAlpha(fadeCounter);

                              }

                          },

            0,

            5);

        }




    }

    public boolean isPassedPipes() {
        return passedPipes;
    }

    public void setPassedPipes(boolean passedPipes) {
        this.passedPipes = passedPipes;
    }

    public Player getPlayer() {
        return player;
    }

    public Pipe getPipe() {
        return pipe;
    }

    public Score getScoreObj() {
        return scoreObj;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isDying() {
        return dying;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public Paint getFadePaint() {
        return fadePaint;
    }

    public boolean isFirstFrame() {
        return firstFrame;
    }

    public void setFirstFrame(boolean firstFrame) {
        this.firstFrame = firstFrame;
    }

    public int getDeathFrame() {
        return deathFrame;
    }

    public void setDeathFrame(int deathFrame) {
        this.deathFrame = deathFrame;
    }

    public boolean isFadingOut() {
        return fadingOut;
    }

    public void setFadingOut(boolean fadingOut) {
        this.fadingOut = fadingOut;
    }

    public int getvHeight() {
        return vHeight;
    }

    public int getvWidth() {
        return vWidth;
    }

    public Powerups getPowerup() {
        return powerup;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }
}

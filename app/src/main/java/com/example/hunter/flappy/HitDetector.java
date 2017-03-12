package com.example.hunter.flappy;

/**
 * Created by Hunter on 3/12/2017.
 */

public class HitDetector {

    private GameView gv;
    private Player player;
    private Pipe pipe;
    private Score scoreObj;
    private Powerups powerup;

    public HitDetector(GameView gv) {
        this.gv = gv;
        player = gv.getPlayer();
        pipe = gv.getPipe();
        scoreObj = gv.getScoreObj();
        powerup = gv.getPowerup();
    }

    public void detectScore() {



        if (player.getHitBoxX() > pipe.getX() + pipe.getWidth() && !gv.isPassedPipes()) {
            gv.setPassedPipes(true);
            scoreObj.incrementScore();
            gv.getSoundManager().restartCoinMediaPlayer();

        }

    }

    public void detectHits() {

        if(player.getY() + player.getHeight() > gv.getvHeight() || player.getY() < 0) {
            gv.setPlaying(false);
            gv.setFirstFrame(false);
            gv.setDying(true);
            gv.getFadePaint().setAlpha(0);
            gv.setDeathFrame(0);
            gv.getSoundManager().restartDeathMediaPlayer();
            scoreObj.saveHighScore();
            scoreObj.reset();
        }

        if(player.getHitBoxX() + player.getHitBoxWidth() >= pipe.getX()
                && player.getHitBoxX() < pipe.getX() + pipe.getWidth()
                && (player.getHitBoxY() < pipe.getAboveY() + pipe.getAboveOpening() - pipe.getAboveHitboxLeniency()
                || player.getHitBoxY() + player.getHitBoxHeight() > pipe.getBelowY() + pipe.getBelowHitboxLeniency())) {
            //Player has hit a pipe or wall
            gv.setPlaying(false);
            gv.setFirstFrame(false);
            gv.setDying(true);
            gv.getFadePaint().setAlpha(0);
            gv.setDeathFrame(0);
            gv.getSoundManager().restartDeathMediaPlayer();
            scoreObj.saveHighScore();
            scoreObj.reset();
        }

        Boolean inPipeCenter = player.getHitBoxX() + player.getHitBoxWidth() >= pipe.getX() + pipe.getWidth() / 3
                && player.getHitBoxX() < pipe.getX() + pipe.getWidth() - pipe.getWidth() / 3;

        Boolean inAboveHitboxLeniency = (player.getHitBoxY() > pipe.getAboveY() + pipe.getAboveOpening() - pipe.getAboveHitboxLeniency()
                && player.getHitBoxY() < pipe.getAboveY() + pipe.getAboveOpening());

        Boolean inBelowHitboxLeniency = (player.getHitBoxY() + player.getHitBoxHeight() < pipe.getBelowY() + pipe.getBelowHitboxLeniency()
                && player.getHitBoxY() + player.getHitBoxHeight() > pipe.getBelowY());

        //Close Calls
        if(inPipeCenter && (inAboveHitboxLeniency || inBelowHitboxLeniency)) {
            gv.setFadingOut(false);
            gv.animateCloseCall();
        }


    }

    public void detectPowerupGet() {

        boolean leftCollision = (player.getX() > powerup.getX() - player.getWidth() && player.getX() < powerup.getX() + powerup.getWidth());
        boolean topCollision = player.getY() >= powerup.getY() - player.getHeight() && player.getY() <= powerup.getY() + powerup.getHeight();

        if (topCollision && leftCollision) {

            //Log.d("POWERING UP   :", "ANTIGRAVITY ACTIVATED!!");
            //Powerup Collected

        }

    }

    public void update() {
        detectPowerupGet();
        detectHits();
        detectScore();
    }


}

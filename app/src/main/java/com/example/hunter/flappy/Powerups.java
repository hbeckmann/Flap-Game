package com.example.hunter.flappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Hunter on 3/5/2017.
 */

public class Powerups {

    private int x;
    private int y;
    private int width;
    private int height;
    private List<Powerup> powerupList;
    private Powerup randomPowerup;
    private Player player;
    private Pipe pipe;
    private int vWidth;
    private int vHeight;
    private Random rand;
    private Boolean powerupAppeared;
    private Bitmap bitmap;



    public Powerups(Player player, Pipe pipe, int vWidth, int vHeight, Context currentContext) {
        this.vWidth = vWidth;
        this.vHeight = vHeight;
        this.width = vWidth / 7;
        this.height = vHeight / 12;
        this.player = player;
        this.pipe = pipe;
        this.powerupAppeared = true;
        rand = new Random();
        powerupList = new ArrayList<Powerup>();
        powerupList.add(new Powerup("Anti-Gravity", new ReverseGravity()));
        randomizePosition();
        this.x = pipe.getX() + (vWidth / 2);
        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(currentContext.getResources(), R.drawable.box), width, height, true);
        //powerupList.get(0).activatePowerup();


    }


    public void randomizePowerup() {
        //Randomizes the powerup

    }

    public void startPowerupEffect() {

    }

    public void randomizePowerupTiming() {
        if(rand.nextInt(4) < 1) {
            powerupAppeared = true;
            randomizePosition();
        } else  {
            powerupAppeared = false;
            this.y = 0 - this.height - 100;
        }
    }

    public void randomizePosition() {
        this.y = rand.nextInt(vHeight);
    }

    public void updatePosition() {
        if(powerupAppeared && x + width > 0) {
            this.x -= pipe.getSpeed();
        } else if (powerupAppeared && x + width < 0) {
            randomizePowerupTiming();
            this.x = pipe.getX() + (vWidth / 2) + pipe.getWidth();
        } else if(!powerupAppeared && x + width > 0) {
            this.x -= pipe.getSpeed();
        } else {
            this.x = pipe.getX() + (vWidth / 2) + pipe.getWidth();
            randomizePowerupTiming();
        }

    }


    public int getX() {
      return x;
    };

    public void setX(int x) {
      this.x = x;
    };

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    interface Callback {
        void func();
    }

    class ReverseGravity implements Callback {
        public void func() {
            player.setAcceleration(-player.getAcceleration());
        }
    }




    private class Powerup {

        private String name;
        private Callback callback;


        public Powerup(String name, Callback func) {
            this.name = name;
            this.callback = func;
        }

        public String getName() {
            return name;
        }

        public void activatePowerup() {
            callback.func();
        }
    }








}

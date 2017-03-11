package com.example.hunter.flappy;

import java.util.ArrayList;
import java.util.List;

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


    public Powerups(Player player, Pipe pipe) {
        this.width = 50;
        this.height = 50;
        this.player = player;
        this.pipe = pipe;
        powerupList = new ArrayList<Powerup>();
        powerupList.add(new Powerup("Anti-Gravity", new ReverseGravity()));
        randomizePosition();

        //powerupList.get(0).activatePowerup();
    }

    public void randomizePowerup() {
        //Randomizes the powerup

    }

    public void randomizePosition() {

    }

    public void updatePosition() {
        this.x = pipe.getX() - 500;
        this.y = 1000;
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

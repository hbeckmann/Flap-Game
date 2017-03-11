package com.example.hunter.flappy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hunter on 3/5/2017.
 */

public class Powerups {

    private int x;
    private int y;
    private List<Powerup> powerupList;
    private Powerup randomPowerup;
    private Player player;


    public Powerups(Player player) {
        this.player = player;
        powerupList = new ArrayList<Powerup>();
        powerupList.add(new Powerup("Anti-Gravity", new ReverseGravity()));

        //powerupList.get(0).activatePowerup();
    }

    public void randomizePowerup() {
        //Randomizes the powerup
    }

    public void randomizePosition() {

    }

    public int getX() {
      return x;
    };

    public void setX(int x) {
      this.x = x;
    };

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

package com.example.hunter.flappy;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by Hunter on 3/4/2017.
 */

public class Score {

    private int currentScore;
    private int highScore;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Context context;


    public Score (Context context) {

        this.context = context;
        highScore = 0;
        currentScore = 0;


    }

    public void reset() {
        currentScore = 0;
    }

    public void incrementScore() {
        currentScore++;
    }

    public void saveHighScore() {

        System.out.println("Attempting to save a new score");

        highScore = this.retrieveHighScore();
        if(highScore < currentScore) {
            sharedPref = context.getSharedPreferences(
                    context.getString(R.string.flap_high_scores), Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putInt(context.getString(R.string.flap_high_scores), currentScore);
            editor.commit();
            System.out.println("Saving new highscore! = " + currentScore);
        } else {
            System.out.println("Not a new highscore. Bummer!! Your old one was " + highScore);
        }




    }

    public int retrieveHighScore() {

        sharedPref = context.getSharedPreferences(
                context.getString(R.string.flap_high_scores), Context.MODE_PRIVATE);
        int score = sharedPref.getInt(context.getString(R.string.flap_high_scores), 0);


        return score;

    }

    public int getCurrentScore() {
        return currentScore;
    }
}

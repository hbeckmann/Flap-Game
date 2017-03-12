package com.example.hunter.flappy;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;

/**
 * Created by Hunter on 3/12/2017.
 */

public class SoundManager {

    private GameView gv;
    private float sfxVolume;
    private Context currentContext;
    private MediaPlayer mediaPlayer;
    private MediaPlayer coinMediaPlayer;
    private MediaPlayer deathMediaPlayer;
    private SharedPreferences sharedPref;


    public SoundManager(GameView gv) {

        this.currentContext = gv.getCurrentContext();
        sharedPref = currentContext.getSharedPreferences(
                "Settings", Context.MODE_PRIVATE);
        this.sfxVolume = ((float) sharedPref.getInt("sfx_volume", 5) / 10);

    }

    public void setAllPlayers() {
        setCoinMediaPlayer();
        setDeathMediaPlayer();
        setMediaPlayer();
    }

    public void setMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(currentContext, R.raw.jump2);
        //Won't work without the while loop - P R O G R A M M I N G
        while (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(currentContext, R.raw.jump2);
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(sfxVolume, sfxVolume);

    }

    public void setDeathMediaPlayer() {
        deathMediaPlayer = new MediaPlayer();
        deathMediaPlayer = MediaPlayer.create(currentContext, R.raw.explosion2);
        //Won't work without the while loop - P R O G R A M M I N G
        while (deathMediaPlayer == null) {
            deathMediaPlayer = MediaPlayer.create(currentContext, R.raw.explosion2);
        }
        deathMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        deathMediaPlayer.setVolume(sfxVolume, sfxVolume);

    }

    public void setCoinMediaPlayer() {
        coinMediaPlayer = new MediaPlayer();
        coinMediaPlayer = MediaPlayer.create(currentContext, R.raw.coin2);
        //Won't work without the while loop - P R O G R A M M I N G
        while (coinMediaPlayer == null) {
            coinMediaPlayer = MediaPlayer.create(currentContext, R.raw.coin2);
        }
        coinMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        coinMediaPlayer.setVolume(sfxVolume, sfxVolume);

    }

    public void updateVolume() {
        this.sfxVolume = ((float) sharedPref.getInt("sfx_volume", 5) / 10);
        mediaPlayer.setVolume(sfxVolume, sfxVolume);
        deathMediaPlayer.setVolume(sfxVolume, sfxVolume);
        coinMediaPlayer.setVolume(sfxVolume, sfxVolume);
    }

    public void destroyAllMediaPlayers() {
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


        if(deathMediaPlayer != null) {
            deathMediaPlayer.stop();
            deathMediaPlayer.release();
            deathMediaPlayer = null;
        }
    }

    public void restartDeathMediaPlayer() {
        if(deathMediaPlayer != null) {
            deathMediaPlayer.seekTo(0);
            deathMediaPlayer.start();
        }
    }

    public void restartCoinMediaPlayer() {
        if(coinMediaPlayer != null) {
            coinMediaPlayer.seekTo(0);
            coinMediaPlayer.start();
        }
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public MediaPlayer getCoinMediaPlayer() {
        return coinMediaPlayer;
    }

    public MediaPlayer getDeathMediaPlayer() {
        return deathMediaPlayer;
    }
}

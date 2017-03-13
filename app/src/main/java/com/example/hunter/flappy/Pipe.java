package com.example.hunter.flappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.Random;

/**
 * Created by Hunter on 2/26/2017.
 */

public class Pipe {

    private Bitmap rawImage;
    private Bitmap reverseRawImage;
    private Bitmap aboveBitmap;
    private Bitmap belowBitmap;
    private int minOpening;
    private int maxOpening;
    private int aboveOpening;
    private int belowOpening;
    private int aboveY;
    private int belowY;
    private int vWidth;
    private int vHeight;
    private int width;
    private double speed;
    private int aboveHitboxLeniency;
    private int belowHitboxLeniency;
    private int x;
    private Random rand;



    public Pipe(Context context, int viewWidth, int viewHeight) {

        vHeight = viewHeight;
        vWidth = viewWidth;
        minOpening =  viewHeight / 5;
        maxOpening =  viewHeight / 4;
        width = viewWidth / 6;
        x = viewWidth;
        speed = viewWidth / 70;
        rand = new Random();
        aboveOpening = randomizeAboveOpening(viewHeight);
        belowOpening = randomizeBelowOpening(viewHeight);
        BitmapFactory.Options bitmapLoadingOptions = new BitmapFactory.Options();
        bitmapLoadingOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        rawImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.spike_2,  bitmapLoadingOptions);
        reverseRawImage = RotateBitmap(rawImage, 180);
        aboveBitmap = Bitmap.createScaledBitmap(reverseRawImage, width, aboveOpening, true);
        belowBitmap = Bitmap.createScaledBitmap(rawImage, width, belowOpening, true);
        aboveY = 0;
        belowY = (aboveOpening + (viewHeight - aboveOpening - belowOpening));
        aboveHitboxLeniency = (aboveOpening / 10) * 2;
        belowHitboxLeniency = (belowOpening / 10) * 2;
    }

    public void update() {
        if (x <= 0 - width) {
            x = vWidth;
            aboveOpening = randomizeAboveOpening(vHeight);
            aboveHitboxLeniency = (aboveOpening / 10) * 2;
            belowOpening = randomizeBelowOpening(vHeight);
            belowHitboxLeniency = (belowOpening / 10) * 2;
            belowY = (aboveOpening + (vHeight - aboveOpening - belowOpening));
            aboveBitmap = Bitmap.createScaledBitmap(reverseRawImage, width, aboveOpening, true);
            belowBitmap = Bitmap.createScaledBitmap(rawImage, width, belowOpening, true);
            speed += .2;

        }

        x -= speed;
    }

    public Boolean update(boolean passedFlag) {
        if (x <= 0 - width) {
            x = vWidth;
            aboveOpening = randomizeAboveOpening(vHeight);
            aboveHitboxLeniency = (aboveOpening / 10) * 2;
            belowOpening = randomizeBelowOpening(vHeight);
            belowHitboxLeniency = (belowOpening / 10) * 2;
            belowY = (aboveOpening + (vHeight - aboveOpening - belowOpening));
            aboveBitmap = Bitmap.createScaledBitmap(reverseRawImage, width, aboveOpening, true);
            belowBitmap = Bitmap.createScaledBitmap(rawImage, width, belowOpening, true);
            speed += .2;
            return false;
        }

        x -= speed;

        return passedFlag;
    }



    public void reset() {

        this.x = vWidth;
        this.randomizeAboveOpening(vHeight);
        this.randomizeBelowOpening(vWidth);
        this.speed = vWidth / 70;

    }

    public int randomizeAboveOpening ( int viewHeight) {

        return rand.nextInt((viewHeight - minOpening - 200) - minOpening) + minOpening;

    }

    public int randomizeBelowOpening ( int viewHeight ) {

        // Plus 5 at end guarantees that it doesn't have a width of 0 and creates a bitmap exception
        return rand.nextInt((viewHeight - aboveOpening - minOpening) - (viewHeight - aboveOpening - maxOpening)) + (viewHeight - aboveOpening - maxOpening) + 5;

    }

    public int getAboveOpening() {
        return aboveOpening;
    }

    public int getBelowOpening() {
        return belowOpening;
    }

    public double getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public Bitmap getAboveBitmap() {
        return aboveBitmap;
    }

    public Bitmap getBelowBitmap() {
        return belowBitmap;
    }

    public Bitmap getRawImage() {
        return rawImage;
    }

    public int getAboveY() {
        return aboveY;
    }

    public int getBelowY() {
        return belowY;
    }

    public int getAboveHitboxLeniency() {
        return aboveHitboxLeniency;
    }

    public int getBelowHitboxLeniency() {
        return belowHitboxLeniency;
    }



    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void recycleBitmaps() {

        aboveBitmap.recycle();
        aboveBitmap = null;
        belowBitmap.recycle();
        belowBitmap = null;
        rawImage.recycle();
        rawImage = null;

    }
}

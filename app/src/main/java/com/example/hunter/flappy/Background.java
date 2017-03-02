package com.example.hunter.flappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by Hunter on 2/27/2017.
 */

public class Background {

    //Takes a single background, flips it and makes a loop
    private Bitmap rawBackground;
    private Bitmap background;
    private Bitmap reverseBackground;
    private int scaledBackgroundWidth;
    private int backgroundX;
    private int reversebackgroundX;
    private Matrix reverse;
    private int scrollSpeed;
    private int vWidth;
    private int vHeight;

    public Background(Context context, int pictureSource, int viewWidth, int viewHeight) {

        backgroundX = 0;
        vWidth = viewWidth;
        BitmapFactory.Options bitmapLoadingOptions = new BitmapFactory.Options();
        bitmapLoadingOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        rawBackground = BitmapFactory.decodeResource(context.getResources(), pictureSource, bitmapLoadingOptions);
        scaledBackgroundWidth = rawBackground.getWidth() * viewHeight / rawBackground.getHeight();
        background = Bitmap.createScaledBitmap(rawBackground, scaledBackgroundWidth, viewHeight, true);
        rawBackground.recycle();
        rawBackground = null;
        reversebackgroundX = background.getWidth();
        reverse = new Matrix();
        reverse.preScale(-1, 1);
        reverseBackground = Bitmap.createBitmap(background, 0, 0, background.getWidth(), background.getHeight(), reverse, true);
        scrollSpeed = 5;

    }

    public void releaseBitmaps() {
        background.recycle();
        background = null;
        reverseBackground.recycle();
        reverseBackground = null;
    }

    public void reset() {

        this.backgroundX = 0;
        this.reversebackgroundX = background.getWidth();
        this.scrollSpeed = 5;

    }

    public void scrollBackground() {

        if(backgroundX + background.getWidth() * 2 - vWidth <= 0) {

            backgroundX = backgroundX + background.getWidth() * 2;

        } else if(reversebackgroundX + background.getWidth() * 2 - vWidth <= 0) {

            reversebackgroundX = reversebackgroundX + background.getWidth() * 2;

        }

        backgroundX -= scrollSpeed;
        reversebackgroundX -= scrollSpeed;

    }

    public int getBackgroundX() {
        return backgroundX;
    }

    public int getReversebackgroundX() {
        return reversebackgroundX;
    }

    public int getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public Bitmap getBackgroundBitmap() {
        return background;
    }

    public Bitmap getReverseBackground() {
        return reverseBackground;
    }
}

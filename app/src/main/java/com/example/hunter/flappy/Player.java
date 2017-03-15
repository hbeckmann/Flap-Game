package com.example.hunter.flappy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.provider.Settings;

/**
 * Created by Hunter on 2/26/2017.
 */

public class Player {
    //Bitmap to get char from image
    private Bitmap bitmap;
    private Bitmap rawImage;

    private int height;
    private int width;
    private int heightScale = 13;
    private int widthScale = 8;
    private double velocity;
    private double acceleration;
    private int x;
    private int y;
    private Thread spriteTimer;
    private int currentSprite = 0;
    private Context currentContext;
    private Bitmap[] sprites;
    private int frameCount;
    private boolean jumping;
    private int vHeight;
    private int vWidth;
    private int hitBoxX;
    private int hitBoxY;
    private int hitBoxWidth;
    private int hitBoxHeight;
    private Bitmap expSpriteRaw;
    private Bitmap expSprite;
    private Rect src;
    private Rect dst;
    private int spriteRow;
    private GameView gv;

    public Player(Context context, int vWidth, int vHeight, GameView gv) {

        currentContext = context;
        this.gv = gv;
        BitmapFactory.Options bitmapLoadingOptions = new BitmapFactory.Options();
        bitmapLoadingOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        height = (vHeight + 168) / heightScale;
        width = vWidth / widthScale;
        hitBoxHeight = (height / 5) * 4;
        hitBoxWidth = (width /5 ) * 4;


        sprites = new Bitmap[] {
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(currentContext.getResources(), R.drawable.frame_1, bitmapLoadingOptions), height, width, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(currentContext.getResources(), R.drawable.frame_2, bitmapLoadingOptions), height, width, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(currentContext.getResources(), R.drawable.frame_3, bitmapLoadingOptions), height, width, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(currentContext.getResources(), R.drawable.frame_4, bitmapLoadingOptions), height, width, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(currentContext.getResources(), R.drawable.frame_1, bitmapLoadingOptions), height, width, true)
        };
        this.vWidth = vWidth;
        this.vHeight = vHeight;
        velocity = -(vHeight / 100);
        acceleration = -(vHeight / 900); //-1.02;
        //Update this to be relative to screen
        System.out.println(vWidth + " " + vHeight);
        x =  (vWidth / 2) - (width + 100);
        hitBoxX = x + ((width - hitBoxWidth) / 2);
        y = (vHeight / 2) - 168;
        hitBoxY = y + ((height - hitBoxHeight) / 2);
        rawImage = sprites[currentSprite];
        bitmap = Bitmap.createScaledBitmap(rawImage, height, width, true);
        //spriteTimer = new Thread();
        jumping = false;
        src = new Rect(0, 0, 100, 100);
        spriteRow = 0;
        expSpriteRaw = BitmapFactory.decodeResource(context.getResources(), R.drawable.testingxpl2);
        expSprite = Bitmap.createBitmap(expSpriteRaw);
    }

    //Method to update character data
    public void update() {
        velocity += acceleration;
        y -= (int) velocity;
        hitBoxY = y + ((height - hitBoxHeight) / 2);
        if(jumping) {
            animateJump();
        }
    }

    public void animateJump() {

        jumping = true;
        frameCount++;

        if (currentSprite + 1 < sprites.length && jumping){
            //spriteTimer.sleep(1000 / 30);
            if(frameCount % 3 == 0 ) {
                currentSprite += 1;
                rawImage = sprites[currentSprite];
                bitmap = Bitmap.createScaledBitmap(rawImage, height, width, true);

            }

        } else {
            currentSprite = 0;
            jumping = false;
        }

    }

    public void updateSpriteSheet() {

        src.offset(100, 0);
        if(gv.getDeathFrame() % 10 == 0) {
            spriteRow++;
            src = new Rect(0, 100 * spriteRow, 100, 100 * (spriteRow + 1));
        }

        dst = new Rect(x - width, y - height, x + width * 2, y + height * 2);

    }

    public void destroyExSprite() {
        expSpriteRaw.recycle();
        expSprite.recycle();
        expSprite=null;
        expSpriteRaw=null;
    }

    public void reset () {
        this.jumping = false;
        this.velocity = -(vHeight / 100);
        y = (vHeight / 2) - 168;
        src = new Rect(0, 0, 100, 100);
        dst = null;

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public int getHitBoxX() {
        return hitBoxX;
    }

    public int getHitBoxY() {
        return hitBoxY;
    }

    public int getHitBoxWidth() {
        return hitBoxWidth;
    }

    public int getHitBoxHeight() {
        return hitBoxHeight;
    }

    public Bitmap getExpSpriteRaw() {
        return expSpriteRaw;
    }

    public Bitmap getExpSprite() {
        return expSprite;
    }

    public Rect getSrc() {
        return src;
    }

    public Rect getDst() {
        return dst;
    }

    public int getSpriteRow() {
        return spriteRow;
    }

    public void setSpriteRow(int spriteRow) {
        this.spriteRow = spriteRow;
    }

    public void releaseSprites() {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].recycle();
            sprites[i] = null;
        }
        sprites = null;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }
}

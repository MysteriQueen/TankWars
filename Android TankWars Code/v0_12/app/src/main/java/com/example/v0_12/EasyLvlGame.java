package com.example.v0_12;

/**
 * EasyLvlGame Class
 * This class provides the functionality of the game.
 *
 * @author Riya Mayor
 */

import android.graphics.*;
import android.os.CountDownTimer;
import java.util.ArrayList;

public class EasyLvlGame extends EasyGameThread {
    private HighScoreActivity oHighScoreActivity;
    //Will store the image of the spike
    private Bitmap oSpikeOriginal;
    protected Bitmap oSpikeBitmap;
    //Will store the image of the player tank
    private Bitmap oPlayerBitmap;
    //Will store the image of the rock
    private Bitmap oRockOriginal;
    private Bitmap oRockBitmap;
    //Will store the image of the enemy tank
    private Bitmap oEnemyBitmap;
    //Will store the image of the bullet
    private Bitmap oBulletBitmap;
    private Canvas oCanvas;
    private boolean bBulletFired = false;
    protected ArrayList<Bullet> oBulletList;
    private final int iBulletType0= 0;
    private final int iBulletType1 = 1;
    private final int iBulletType2 = 2;
    private float fBulletX, fBulletY, fBulletSpeed;
    private int iBulletType;
    private CountDownTimer oCountDownTimer;
    private int iCount;
    private float fTouchX, fTouchY;
    private float mSpikeX = -100;
    private float mSpikeY = -100;
    private float mSpikeSpeedX = 0;
    private float mSpikeSpeedY = 0;
    private float fBulletSpeedX = 0;
    private float fBulletSpeedY = 0;
    //Player's x and y position. Y will always be the bottom of the screen
    private float mPlayerX = 0;
    private float mPlayerY;
    //The speed (pixel/second) of the player and enemy in direction X and Y
    private float mPlayerSpeedX = 0;
    private float mPlayerSpeedY = 0;
    private float mEnemyX = -100;
    private float mEnemyY = -100;
    private float mEnemySpeedX = 10;   //initially move to the right
    private float[] mRockX = {-100, -100, -100};
    private float[] mRockY = new float[3];
    //This is used to check collisions
    private float mMinDistanceBetweenSpikeAndPlayer = 0;

    /**
     * CONSTRUCTOR
     * The constructor initialises bitmaps and resized large ones. A bullet list is also created.
     * @param gameView View of the game
     */
    public EasyLvlGame(EasyGameView gameView) {
        //House keeping
        super(gameView);
        //Prepare the image so we can draw it on the screen (using a canvas)
        oSpikeOriginal = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.spike);
        //Resize image
        oSpikeBitmap = Bitmap.createScaledBitmap(oSpikeOriginal, 80, 80, true);

        //Prepare the image of the player so we can draw it on the screen (using a canvas)
        oPlayerBitmap = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.smallorangetank_up);

        //Prepare the image of the rock so we can draw it on the screen (using a canvas)
        oEnemyBitmap = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.smallgreentank_down);

        //Prepare the image of the SadBall(s) so we can draw it on the screen (using a canvas)
        oRockOriginal = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.target);
        //Resize image
        oRockBitmap = Bitmap.createScaledBitmap(oRockOriginal, 150, 150, true);
        //Initialise bullet array list
        oBulletList = new ArrayList<Bullet>();
    }

    /**
     * setupBeginning()
     * This function initialises locations and speeds of the spike, enemy and player.
     */
    @Override
    public void setupBeginning() {
        //Initialise speeds
        mSpikeSpeedX = mCanvasWidth / 3;
        mSpikeSpeedY = mCanvasHeight / 3;

        iBulletType = iBulletType0;
        fBulletSpeed = 35;

        //Place the spike in the middle of the screen.
        mSpikeX = mCanvasWidth / 2;
        mSpikeY = mCanvasHeight / 2;

        //Place Player at the bottom of the screen
        mPlayerX = mCanvasWidth / 2;
        mPlayerY = mCanvasHeight;

        //Place enemy tank in the top middle of the screen
        mEnemyX = mCanvasWidth / 2;
        mEnemyY = oEnemyBitmap.getHeight() / 2;

        //Place all rocks
        mRockX[0] = mCanvasWidth / 3;
        mRockY[0] = mCanvasHeight / 3;

        mRockX[1] = mCanvasWidth - mCanvasWidth / 3;
        mRockY[1] = mCanvasHeight / 4;

        mRockX[2] = mCanvasWidth / 2;
        mRockY[2] = mCanvasHeight / 5;

        //Minimum distance between a spike and the player
        mMinDistanceBetweenSpikeAndPlayer = (oPlayerBitmap.getWidth() + oSpikeBitmap.getWidth()) * (oPlayerBitmap.getWidth() + oSpikeBitmap.getWidth());
    }

    /**
     * doDraw()
     * This function draws the bitmaps of all the objects on the canvas
     * @param canvas Canvas of the game
     */
    @Override
    protected void doDraw(Canvas canvas) {
        oCanvas = canvas;
        //If canvas is null
        if (canvas == null) return;
        //House keeping
        super.doDraw(canvas);
        canvas.drawBitmap(oSpikeBitmap, mSpikeX - oSpikeBitmap.getWidth() / 2, mSpikeY - oSpikeBitmap.getHeight() / 2, null);
        canvas.drawBitmap(oPlayerBitmap, mPlayerX - oPlayerBitmap.getWidth() / 2, mPlayerY - oPlayerBitmap.getHeight(), null);
        canvas.drawBitmap(oEnemyBitmap, mEnemyX - oEnemyBitmap.getWidth() / 2, mEnemyY - oEnemyBitmap.getHeight() / 2, null);
        //Loop through all the rocks
        for (int i = 0; i < mRockX.length; i++) {
            //Draw SadBall in position i
            canvas.drawBitmap(oRockBitmap, mRockX[i] - oRockBitmap.getWidth() / 2, mRockY[i] - oRockBitmap.getHeight() / 2, null);
        }
        //Draw bullet
        if(bBulletFired) {
            fBulletX += fBulletSpeedX;
            fBulletY += fBulletSpeedY;
            canvas.drawBitmap(oBulletBitmap, fBulletX, fBulletY, null);
        }
    }

    /**
     * startTimer()
     * This function handles the timer. Once the game starts, the time will start.
     */
    @Override
    protected void startTimer() {
        iCount = 0;
        //Start countdown timer
        oCountDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                oTimerView.setText(String.valueOf(iCount));
                iCount++;
            }
            public void onFinish(){
                //Maximum time is 30 seconds
                oTimerView.setText("30");
                setState(EasyGameThread.STATE_GAMEOVER);
                //Invoke function
                gameFinished();
            }
        }.start();
    }

    /**
     * gameFinished()
     * This function initialises the score list and writes the current score and player name to the database.
     */
    @Override
    protected void gameFinished() {
        //Save score to database
        int iScore = (int) getScore();
        oHighScoreActivity = new HighScoreActivity();
        oHighScoreActivity.writeData(1, "Player", iScore);
    }

    /**
     * makeBullet()
     * Every time a bullet is fired this function is invoked.
     */
    protected void makeBullet() {
        bBulletFired = true;
        oBulletBitmap = BitmapFactory.decodeResource(mGameView.getContext().getResources(), R.drawable.red_bullet);
        //Position co-ordinates of the bullet
        fBulletX = mPlayerX - oPlayerBitmap.getWidth() / 4;
        fBulletY = mPlayerY - oPlayerBitmap.getHeight() - 50;
        fBulletSpeedX = 0;
        fBulletSpeedY= -35;
        oCanvas.drawBitmap(oBulletBitmap, fBulletX, fBulletY, null);
        oBulletList.add(new Bullet(oBulletBitmap, iBulletType, fBulletX, fBulletY, fBulletSpeed, fBulletSpeedX, fBulletSpeedY));
    }

    /**
     * actionOnTouch()
     * This function handles the event when the user touches the screen.
     * @param x Position co-ordinate
     * @param y Position co-ordinate
     */
    @Override
    protected void actionOnTouch(float x, float y) {
        //Move the tank to the x position of the touch
        if (mPlayerX >= x) {
            mPlayerX -= 10;
        }
        else {
            mPlayerX += 10;
        }
        //Move the tank to the y position of the touch
        if(mPlayerY >= y && mPlayerY >= mCanvasHeight - 300) {
            mPlayerY -= 10;
        }
        else if (mPlayerY <= y && mPlayerY <= mCanvasHeight) { // - oPaddle.getHeight() / 2){
            mPlayerY += 10;
        }
        fTouchX = x;
        fTouchY = y;
        //If bullet is fired
        if(!bBulletFired) {
            //If touch is within the left and right borders of the tank
            if((x > mPlayerX - oPlayerBitmap.getWidth() / 2) && (x < mPlayerX + oPlayerBitmap.getWidth())) {
                //If touch is within the top and bottom borders of the tank
                if((y < mPlayerY) && (y > mCanvasHeight - oPlayerBitmap.getHeight() - 300)) {
                    makeBullet();
                }
            }
        }
    }

    /**
     * actionWhenPhoneMoved()
     * This function is invoked whenever the phone moves around its axises.
     * @param xDirection
     * @param yDirection
     * @param zDirection
     */
    @Override
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
        mPlayerSpeedX = mPlayerSpeedX + 70f * xDirection;
        mPlayerSpeedY += 70f * yDirection;
        //If player is outside the screen and moving further away
        //Move it into the screen and set its speed to 0
        if(mPlayerX <= 0 && mPlayerSpeedX < 0) {
            mPlayerSpeedX = 0;
            mPlayerX = 0;
        }
        //If the player is moving and hits the walls on the left and right
        if(mPlayerX >= mCanvasWidth && mPlayerSpeedX > 0) {
            mPlayerSpeedX = 0;
            mPlayerX = mCanvasWidth;
        }
    }

    /**
     * updateGame()
     * After every milliseconds elapsed, this function is invoked to update the canvas. This function checks for any collisions
     * and assigns new speed and position co-ordinates for each object on the canvas.
     * @param secondsElapsed
     */
    @Override
    protected void updateGame(float secondsElapsed) {
        //move the enemy tank from side to side
        if ((mEnemyX <= oEnemyBitmap.getWidth() && mEnemySpeedX < 0) || (mEnemyX >= mCanvasWidth - oEnemyBitmap.getWidth() && mEnemySpeedX > 0)) {
            mEnemySpeedX = -mEnemySpeedX;
        }
        mEnemyX += mEnemySpeedX;
        //If the spike moves down on the screen
        if(mSpikeSpeedY > 0) {
            //Check for a collision
            updateSpikeCollision(mPlayerX, mCanvasHeight);
        }
        //Move the X and Y using the speed (pixel/sec)
        mSpikeX = mSpikeX + secondsElapsed * mSpikeSpeedX;
        mSpikeY = mSpikeY + secondsElapsed * mSpikeSpeedY;
        //Move the player's X and Y using the speed (pixel/sec)
        mPlayerX = mPlayerX + secondsElapsed * mPlayerSpeedX;
        //Check for bullet collision
        //Check if bullet hits left or right side of the screen
        if(bBulletFired) {
            if ((fBulletX <= oBulletBitmap.getWidth() / 2 && fBulletSpeedX < 0) || (fBulletX >= mCanvasWidth - oBulletBitmap.getWidth() / 2 && fBulletSpeedX > 0)) {
                fBulletSpeedX = -fBulletSpeedX;
            }
            //Check for bullet hitting enemy tank
            if(updateBulletCollision(mEnemyX, mEnemyY, oEnemyBitmap.getWidth())) {
                //Increase score
                updateScore(1);
            }
            //Check for bullet bouncing off my tank
            updateBulletCollision(mPlayerX, mPlayerY, oPlayerBitmap.getWidth());
            //If the bullet goes near bottom of screen, change direction
            //change the direction of the bullet in the Y direction
            if (fBulletY >= mCanvasHeight * 0.90 && fBulletSpeedY > 0) {
                fBulletSpeedY = -fBulletSpeedY;
            }
            //Loop through all rocks to check if bullet is going to hit them
            for (int i = 0; i < mRockX.length; i++) {
                updateBulletCollision(mRockX[i], mRockY[i], oEnemyBitmap.getWidth());
            }
        }
        //Check if the spike hits either the left side or the right side of the screen
        //But only do something if the spike is moving towards that side of the screen
        //If it does that => change the direction of the spike in the X direction
        if((mSpikeX <= oSpikeBitmap.getWidth() / 2 && mSpikeSpeedX < 0) || (mSpikeX >= mCanvasWidth - oSpikeBitmap.getWidth() / 2 && mSpikeSpeedX > 0) ) {
            mSpikeSpeedX = -mSpikeSpeedX;
        }
        //Check for enemy tank collision
        if(updateSpikeCollision(mEnemyX, mEnemyY)) {
            //Increase score
            updateScore(1);
        }
        //Loop through all rocks
        for(int i = 0; i < mRockX.length; i++) {
            updateSpikeCollision(mRockX[i], mRockY[i]);
        }
        //If the spike goes out of the top of the screen and moves towards the top of the screen =>
        //change the direction of the spike in the Y direction
        if(mSpikeY <= oSpikeBitmap.getWidth() / 2 && mSpikeSpeedY < 0) {
            mSpikeSpeedY = -mSpikeSpeedY;
        }
        //If the spike goes under a certain height
        //change the direction of the spike in the Y direction
        if(mSpikeY >= 4 * mCanvasHeight / 5) {
            mSpikeSpeedY = -mSpikeSpeedY;
        }
        //If the spike goes out of the bottom of the screen => lose the game
        if(mSpikeY >= mCanvasHeight) {
            setState(EasyGameThread.STATE_LOSE);
        }
        if(bBulletFired) {
            //Check if bullet is near the top
            if(fBulletY < 30) {
                bBulletFired = false;
            }
        }
        //If tank is not near the last finger touch, move it there
        if (fTouchX < mPlayerX && mPlayerX > oPlayerBitmap.getWidth()/2) {
            mPlayerX -= 10;
        }
        if (fTouchX > mPlayerX && mPlayerX < mCanvasWidth - oPlayerBitmap.getWidth()/2) {
            mPlayerX += 10;
        }
        if (fTouchY < mPlayerY && mPlayerY >= mCanvasHeight - 300) {
            mPlayerY -= 10;
        }
        if (fTouchY > mPlayerY && mPlayerY + oPlayerBitmap.getHeight() < mCanvasHeight) {
            mPlayerY += 10;
        }
    }

    /**
     * updateSpikeCollision()
     * This function is responsible to check for any collisions between the spike and player.
     * @param x Position co-ordinate
     * @param y Position c-ordinate
     * @return Boolean value that determines whether there has been a spike collision
     */
    private boolean updateSpikeCollision(float x, float y) {
        //Get actual distance between the spike and the player being checked
        float distanceBetweenSpikeAndPlayer = (x - mSpikeX) * (x - mSpikeX) + (y - mSpikeY) *(y - mSpikeY);
        //Check if the actual distance is lower than the allowed => collision
        if(mMinDistanceBetweenSpikeAndPlayer >= distanceBetweenSpikeAndPlayer) {
            //Get the present speed (this should also be the speed going away after the collision)
            float speedOfSpike = (float) Math.sqrt(mSpikeSpeedX * mSpikeSpeedX + mSpikeSpeedY * mSpikeSpeedY);
            //Change the direction of the spike
            mSpikeSpeedX = mSpikeX - x;
            mSpikeSpeedY = mSpikeY - y;
            //Get the speed after the collision
            float newSpeedOfSpike = (float) Math.sqrt(mSpikeSpeedX * mSpikeSpeedX + mSpikeSpeedY * mSpikeSpeedY);
            //using the fraction between the original speed and present speed to calculate the needed
            //velocities in X and Y to get the original speed but with the new angle.
            mSpikeSpeedX = mSpikeSpeedX * speedOfSpike / newSpeedOfSpike;
            mSpikeSpeedY = mSpikeSpeedY * speedOfSpike / newSpeedOfSpike;
            return true;
        }
        return false;
    }

    /**
     * updateBulletCollision()
     * This function is responsible to check for any collisions between the bullet and any other object on the canvas.
     * @param x Position co-ordinate
     * @param y Position c-ordinate
     * @return bCollision Boolean variable that determines whether there has been a bullet collision
     */
    protected boolean updateBulletCollision(float x, float y, float fObjWidth) {
    //protected boolean updateBulletCollision(float x, float y) {
        boolean bCollision = false;
        //Get actual distance between the bullet and another object being checked
        float distanceBetweenBulletAndObject = (x - fBulletX) * (x - fBulletX) + (y - fBulletY) * (y - fBulletY);
        float fMinObjDist = (fObjWidth) * (fObjWidth);
        //Check if the actual distance is lower than the allowed => collision
        if(fMinObjDist >= distanceBetweenBulletAndObject) {
            //Get the present speed (this should also be the speed going away after the collision)
            bCollision = true;
            if (iBulletType > iBulletType0) {
                float speedOfBullet = (float) Math.sqrt(fBulletSpeedX * fBulletSpeedX + fBulletSpeedY * fBulletSpeedY);
                //Change the direction of the bullet
                fBulletSpeedX = fBulletX - x;
                fBulletSpeedY = fBulletY - y;
                //Get the speed after the collision
                float newSpeedOfBullet = (float) Math.sqrt(fBulletSpeedX * fBulletSpeedX + fBulletSpeedY * fBulletSpeedY);
                //using the fraction between the original speed and present speed to calculate the needed
                //velocities in X and Y to get the original speed but with the new angle.
                fBulletSpeedX = fBulletSpeedX * speedOfBullet / newSpeedOfBullet;
                fBulletSpeedY = fBulletSpeedY * speedOfBullet / newSpeedOfBullet;
            }
            else {  //bullet type is 0 so just terminate the bullet
                bBulletFired = false;
            }
        }
        return bCollision;
    }

}
package com.example.v0_12;

import android.graphics.*;
import android.os.CountDownTimer;
import java.util.ArrayList;


/**
 * clMediumETank Class
 * This class provides the attributes of the enemy tank.
 *
 * @author Riya Mayor
 */
class clMediumETank {
    Bitmap oTankBmp;
    float fTankX, fTankY, fTankSpeedX;
    boolean bBulletFired;
    Bitmap oTankBulletBmp;
    float fBulletX, fBulletY, fBulletSpeed, fBulletSpeedX, fBulletSpeedY;

    clMediumETank(Bitmap oTankBmp, float fTankX, float fTankY, float fTankSpeedX, boolean bBulletFired, Bitmap oTankBulletBmp, float fBulletX, float fBulletY, float fBulletSpeed, float fBulletSpeedX, float fBulletSpeedY) {
        this.oTankBmp = oTankBmp;
        this.fTankX = fTankX;
        this.fTankY = fTankY;
        this.fTankSpeedX = fTankSpeedX;
        this.bBulletFired = bBulletFired;
        this.oTankBulletBmp = oTankBulletBmp;
        this.fBulletX = fBulletX;
        this.fBulletY = fBulletY;
        this.fBulletSpeed = fBulletSpeed;
        this.fBulletSpeedX = fBulletSpeedX;
        this.fBulletSpeedY = fBulletSpeedY;
    }
}

/**
 * MediumLvlGame Class
 * This class provides the functionality of the game.
 *
 * @author Riya Mayor
 */

public class MediumLvlGame extends MediumGameThread {
    private HighScoreActivity oHighScoreActivity;
    //Will store the image of the bouncy ball
    private Bitmap oSpikeOriginal;
    protected Bitmap oSpikeBitmap;
    //Will store the image of the player tank
    private Bitmap oPlayerBitmap;
    //Will store the image of the enemy tank
    private Bitmap oEnemyOriginal;
    private Bitmap oEnemyBitmap;
    //Will store the image of the bullet
    private Bitmap oBulletBitmap;
    private Canvas oCanvas;
    protected ArrayList<Bullet> oBulletList;
    private final int iBulletType0= 0;
    private final int iBulletType1 = 1;
    private final int iBulletType2 = 2;
    private int iBulletType;
    private CountDownTimer oCountDownTimer;
    private int iCount;
    private float fTouchX, fTouchY;
    //The X and Y position of the spike on the screen
    private float mSpikeX = -100;
    private float mSpikeY = -100;
    //The speed (pixel/second) of the spike in direction X and Y
    private float mSpikeSpeedX = 0;
    private float mSpikeSpeedY = 0;
    //Player's x and y position. Y will always be the bottom of the screen
    private float mPlayerX = 0;
    private float mPlayerY;
    //The speed (pixel/second) of the player in direction X and Y
    private float mPlayerSpeedX = 0;
    private float mPlayerSpeedY = 0;

    //Will store the image of the enemy tank
    private Bitmap oETankBulletBitmap;                //Enemy tank bullet bitmap

    //The X and Y position of the enemy tank on the screen
    //The point is the top left corner, not middle, of the ball
    //Initially at -100 to avoid them being drawn in 0,0 of the screen
    private float mEnemyX = -100;
    private float mSmileyBallY = -100;
    private float mSmileyBallSpeedX = 10;   //initially move to the right

    //The X and Y position of the SadBalls on the screen
    //The point is the top left corner, not middle, of the balls
    //Initially at -100 to avoid them being drawn in 0,0 of the screen
    private float[] mRockX = {-100, -100, -100, -100};
    private float[] mRockY = new float[4];

    //This will store the min distance allowed between a big ball and the small ball
    //This is used to check collisions
    private float mMinDistanceBetweenSpikeAndPlayer = 0;

    //ARR
    Bitmap oTankBmp;
    float fTankX, fTankY, fTankSpeedX;
    boolean bBulletFired = false;
    Bitmap oTankBulletBmp;
    float fBulletX, fBulletY, fBulletSpeed, fBulletSpeedX, fBulletSpeedY;

    private ArrayList<clHardETank> arETank;

    /**
     * CONSTRUCTOR
     * The constructor initialises bitmaps and resized large ones. A bullet list is also created.
     * @param gameView View of the game
     */
    public MediumLvlGame(MediumGameView gameView) {
        //House keeping
        super(gameView);
        //Prepare the image so we can draw it on the screen (using a canvas)
        oSpikeOriginal = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.spike);
        //Resize image
        oSpikeBitmap = Bitmap.createScaledBitmap(oSpikeOriginal, 80, 80, true);

        //Prepare the image of the paddle so we can draw it on the screen (using a canvas)
        oPlayerBitmap = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.smallorangetank_up);

        //Prepare the image of the enemy tank so we can draw it on the screen (using a canvas)
//        oSmileyBallBitmap = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.smallbluetank_down);
        oTankBmp = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.smallbluetank_down);      //ARR
        oTankBulletBmp = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.tank_bullet);        //ARR

        //Prepare the image of the enemy tank bullet so we can draw it on the screen (using a canvas)
        oETankBulletBitmap = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.tank_bullet);

        //Prepare the image of the SadBall(s) so we can draw it on the screen (using a canvas)
        oEnemyOriginal = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.target);
        //Resize image
        oEnemyBitmap = Bitmap.createScaledBitmap(oEnemyOriginal, 150, 150, true);
        //Initialise bullet array list
        oBulletList = new ArrayList<Bullet>();

        //Initialise enemy array list
        arETank = new ArrayList<>();
    }

    /**
     * setupBeginning()
     * This function initialises locations and speeds of the spike, enemy and player.
     */
    @Override
    public void setupBeginning() {
        //Initialise speeds
        //mCanvasWidth and mCanvasHeight are declared and managed elsewhere
        mSpikeSpeedX = mCanvasWidth / 3;
        mSpikeSpeedY = mCanvasHeight / 3;

        fBulletSpeed = 35;
        iBulletType = iBulletType1;

        //Place the spike in the middle of the screen
        mSpikeX = mCanvasWidth / 2;
        mSpikeY = mCanvasHeight / 2;

        //Place player in the middle of the screen
        mPlayerX = mCanvasWidth / 2;
        mPlayerY = mCanvasHeight;

        //Place enemy in the top middle of the screen
        mEnemyX = mCanvasWidth / 2;

        //Place all rocks forming a pyramid underneath the SmileyBall
        mRockX[0] = mCanvasWidth / 4;
        mRockY[0] = 2 * mCanvasHeight / 3;

        mRockX[1] = mCanvasWidth - mCanvasWidth / 3;
        mRockY[1] = mCanvasHeight / 3;

        mRockX[2] = mCanvasWidth / 2;
        mRockY[2] = mCanvasHeight / 5;

        mRockX[3] = 6 * mCanvasWidth / 7;
        mRockY[3] = 2 * mCanvasHeight / 5;

        //Get the minimum distance between spike and player
        mMinDistanceBetweenSpikeAndPlayer = (oPlayerBitmap.getWidth() + oSpikeBitmap.getWidth()) * (oPlayerBitmap.getWidth() + oSpikeBitmap.getWidth());
        //1st enemy tank
        if (arETank.size() < 2) {
            fTankX = mCanvasWidth / 3;
            fTankY = 5;
            fTankSpeedX = 5;
            bBulletFired = false;
            fBulletX = fTankX + oTankBmp.getWidth() / 2;
            fBulletY = oTankBmp.getHeight() + 5;
            fBulletSpeed = 5;
            fBulletSpeedX = 0;
            fBulletSpeedY = 5;
            arETank.add(new clHardETank(oTankBmp, fTankX, fTankY, fTankSpeedX, bBulletFired, oTankBulletBmp, fBulletX, fBulletY, fBulletSpeed, fBulletSpeedX, fBulletSpeedY));
            //2nd enemy tank
            fTankX = mCanvasWidth * 2 / 3;
            fTankSpeedX = 7;
            fBulletX = fTankX + oTankBmp.getWidth() / 2;
            arETank.add(new clHardETank(oTankBmp, fTankX, fTankY, fTankSpeedX, bBulletFired, oTankBulletBmp, fBulletX, fBulletY, fBulletSpeed, fBulletSpeedX, fBulletSpeedY));
        }
    }

    /**
     * doDraw()
     * This function draws the bitmaps of all the objects on the canvas
     * @param canvas Canvas of the game
     */
    @Override
    protected void doDraw(Canvas canvas) {
        oCanvas = canvas;
        //If there isn't a canvas do nothing
        if (canvas == null) return;
        //House keeping
        super.doDraw(canvas);
        //draw the image of the spike using the X and Y of the spike
        canvas.drawBitmap(oSpikeBitmap, mSpikeX - oSpikeBitmap.getWidth() / 2, mSpikeY - oSpikeBitmap.getHeight() / 2, null);
        //Draw player using X of paddle and the bottom of the screen (top/left is 0,0)
        canvas.drawBitmap(oPlayerBitmap, mPlayerX - oPlayerBitmap.getWidth() / 2, mPlayerY - oPlayerBitmap.getHeight(), null);
        //For loop to go through all enemy tanks
        for(int i = 0; i < arETank.size(); i++) {
            //Store element from arraylist into variable
            clHardETank oElement = arETank.get(i);
            //Draw bitmap using oElement.oTankBmp
            canvas.drawBitmap(oElement.oTankBmp, oElement.fTankX, oElement.fTankY, null);
            //Draw enemy tank bullet
            if(oElement.bBulletFired) {
                oElement.fBulletX += oElement.fBulletSpeedX;
                oElement.fBulletY += oElement.fBulletSpeedY;
                canvas.drawBitmap(oElement.oTankBulletBmp, oElement.fBulletX, oElement.fBulletY, null);
            }
            arETank.set(i, oElement);
        }
        //Loop through all rocks
        for (int i = 0; i < mRockX.length; i++) {
            //Draw targets in position i
            canvas.drawBitmap(oEnemyBitmap, mRockX[i] - oEnemyBitmap.getWidth() / 2, mRockY[i] - oEnemyBitmap.getHeight() / 2, null);
        }
        //Draw player bullet
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
        oCountDownTimer = new CountDownTimer(30000, 1000){
            public void onTick(long millisUntilFinished){
                oTimerView.setText(String.valueOf(iCount));
                iCount++;
            }
            public void onFinish(){

                oTimerView.setText("30");
                setState(EasyGameThread.STATE_GAMEOVER);
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
        if (iBulletType == iBulletType0)
            oBulletBitmap = BitmapFactory.decodeResource(mGameView.getContext().getResources(), R.drawable.red_bullet);
        else
            oBulletBitmap = BitmapFactory.decodeResource(mGameView.getContext().getResources(), R.drawable.lvl1_bullet);
        fBulletX = mPlayerX - oPlayerBitmap.getWidth() / 4;
        fBulletY = mPlayerY - 50;
        fBulletSpeedX = 0;
        fBulletSpeedY= -35;
        oCanvas.drawBitmap(oBulletBitmap, fBulletX, fBulletY, null);
        oBulletList.add(new Bullet(oBulletBitmap, iBulletType0, fBulletX, fBulletY, fBulletSpeed, fBulletSpeedX, fBulletSpeedY));
    }

    /**
     * makeETankBullet()
     * Every time an enemy bullet is fired this function is invoked.
     */
    protected void makeETankBullet(int i) {
        clHardETank oElement = arETank.get(i);
        oElement.bBulletFired = true;
        oElement.fBulletX = oElement.fTankX + oElement.oTankBmp.getWidth() / 2;
        oElement.fBulletY = oElement.fTankY + oElement.oTankBmp.getHeight() + 5;
        oElement.fBulletSpeedX = 0;
        oElement.fBulletSpeedY = 20;
        oCanvas.drawBitmap(oElement.oTankBulletBmp, oElement.fBulletX, oElement.fBulletY, null);

        //rewrite enemy tank record
        arETank.set(i, oElement);
   }

    /**
     * actionOnTouch()
     * This function handles the event when the user touches the screen.
     * @param x Position co-ordinate
     * @param y Position co-ordinate
     */
    @Override
    protected void actionOnTouch(float x, float y) {
        //Move the player to the x position of the touch
        if (mPlayerX >= x) {
            mPlayerX -= 10;
        }
        else {
            mPlayerX += 10;
        }
        fTouchX = x;
        fTouchY = y;

        //Move the tank to the y position of the touch
        if(mPlayerY >= y && mPlayerY >= mCanvasHeight - 300) {
            mPlayerY -= 10;
        }
        else if (mPlayerY <= y && mPlayerY <= mCanvasHeight) { // - oPaddle.getHeight() / 2){
            mPlayerY += 10;
        }
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
        //Change the player speed
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
        for(int i = 0; i < arETank.size(); i++) {
            //Store element from arraylist into variable
            clHardETank oElement = arETank.get(i);

            //check if enemy tanks traversed their half of the screen, left tank only goes to the centre, right tank goes on
            float startX = mCanvasWidth - mCanvasWidth / (i+1);
            if ((oElement.fTankX <= startX + oElement.oTankBmp.getWidth() / 4 && oElement.fTankSpeedX < 0) ||
                (oElement.fTankX >= startX + mCanvasWidth / 2 - oElement.oTankBmp.getWidth() / 2 && oElement.fTankSpeedX > 0)) {
                oElement.fTankSpeedX = -oElement.fTankSpeedX;
            }
            oElement.fTankX += oElement.fTankSpeedX;
            //rewrite enemy tank record
            arETank.set(i, oElement);

            if (!oElement.bBulletFired) {
                makeETankBullet(i);
            }
            else {
                //check progress of tank bullet
                if (oElement.fBulletY >= mCanvasHeight - oElement.oTankBulletBmp.getHeight() || oElement.fBulletY <= oElement.oTankBulletBmp.getHeight()/2) {
                    //if the tank bullet reaches the top or bottom of the screen, recreate
                    makeETankBullet(i);
                }
                else {
                    //check if enemy tank bullet collides with targets, if so, dump it and recreate
                    for (int j = 0; j < mRockX.length; j++) {
                        if (updateTankBulletCollision(mRockX[j], oElement.fBulletX, mRockY[j], oElement.fBulletY, oEnemyBitmap.getWidth())) {
                            makeETankBullet(i);
                        }
                        else { //check for collision with player, if so restart enemy bullet
                            if (updateTankBulletCollision(mPlayerX, oElement.fBulletX, mPlayerY, oElement.fBulletY, oPlayerBitmap.getWidth())) {
//                                updateScore(-1);
                                makeETankBullet(i);
                            }
                        }
                    }
                    //check if enemy tank bullet collides with player bullet if it exists
                    if (bBulletFired) {
                        if (updateTankBulletCollision(fBulletX, oElement.fBulletX, fBulletY, oElement.fBulletY, oBulletBitmap.getWidth())) {
                            makeETankBullet(i);         //restart enemy tank bullet
                            bBulletFired = false;       //make player bullet disappear
                        }
                        else {
                            //check if player bullet hit the enemy tank
                            if (updateBulletCollision(oElement.fTankX, oElement.fTankY, oElement.oTankBmp.getWidth())) {
                                bBulletFired = false;
                                //Increase score
                                updateScore(1);

                            }
                        }
                    }
                }
            }
            //rewrite enemy tank record
            arETank.set(i, oElement);
        }

        //If the spike moves down on the screen
        if(mSpikeSpeedY > 0) {
            //Check for a paddle collision
            updateSpikeCollision(mPlayerX, mCanvasHeight);
            //updateSpikeCollision(mPaddleX, mCanvasHeight + 100);
        }

        //Move the spike's X and Y using the speed (pixel/sec)
        mSpikeX = mSpikeX + secondsElapsed * mSpikeSpeedX;
        mSpikeY = mSpikeY + secondsElapsed * mSpikeSpeedY;

        //Move the player's X and Y using the speed (pixel/sec)
        mPlayerX = mPlayerX + secondsElapsed * mPlayerSpeedX;

        //Check for bullet collision
        //Check if bullet hits left or right side of the screen
        if(bBulletFired) {
            if ((fBulletX <= oBulletBitmap.getWidth() / 2 && fBulletSpeedX < 0) ||
                    (fBulletX >= mCanvasWidth - oBulletBitmap.getWidth() / 2 && fBulletSpeedX > 0)) {
                fBulletSpeedX = -fBulletSpeedX;
            }
            //If the bullet goes near bottom of screen, change direction
            //change the direction of the bullet in the Y direction
            if (fBulletY >= mCanvasHeight * 0.90 && fBulletSpeedY > 0) {
                fBulletSpeedY = -fBulletSpeedY;
            }
            //Loop through all rocks to check if bullet is going to hit them
            for (int i = 0; i < mRockX.length; i++) {
                //Perform collisions (if necessary) between SadBall in position i and the red ball
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
        if(updateSpikeCollision(mEnemyX, mSmileyBallY)) {
            //Increase score
            updateScore(1);
        }

        //Loop through all rocks
        for(int i = 0; i < mRockX.length; i++) {
            //Perform collisions (if necessary) between rock in position i and the spike
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
            setState(MediumGameThread.STATE_LOSE);
        }

        if(bBulletFired) {
            //Check if bullet is near the top
            if(fBulletY < 30) {
                bBulletFired = false;
            }
        }

        //if tanks is not near the last finger touch, move it there
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
        //Get actual distance (without square root - remember?) between the spike and the player being checked
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
        boolean bCollision = false;
        //Get actual distance (without square root - remember?) between the mBall and the ball being checked
        float distanceBetweenBulletAndObject = (x - fBulletX) * (x - fBulletX) +
                (y - fBulletY) * (y - fBulletY);

        float fMinObjDist = (fObjWidth) * (fObjWidth);

        //Check if the actual distance is lower than the allowed => collision
        if(fMinObjDist >= distanceBetweenBulletAndObject) {
            bCollision = true;
            //Get the present speed (this should also be the speed going away after the collision)
            if (iBulletType > iBulletType0) {
                float speedOfBullet = (float) Math.sqrt(fBulletSpeedX * fBulletSpeedX +
                        fBulletSpeedY * fBulletSpeedY);

                //Change the direction of the bullet
                fBulletSpeedX = fBulletX - x;
                fBulletSpeedY = fBulletY - y;

                //Get the speed after the collision
                float newSpeedOfBullet = (float) Math.sqrt(fBulletSpeedX * fBulletSpeedX +
                        fBulletSpeedY * fBulletSpeedY);

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

    /**
     * updateTankBulletCollision()
     * This function is responsible to check for any collisions between the enemy bullet and any other object on the canvas.
     * @param x Position co-ordinate
     * @param y Position c-ordinate
     * @return bCollision Boolean variable that determines whether there has been a bullet collision
     */
    protected boolean updateTankBulletCollision(float x, float fEBullX, float y, float fEBullY, float fObjWidth) {
        boolean bCollision = false;
        //Get actual distance (without square root - remember?) between the mBall and the ball being checked
        float distanceBetweenBulletAndObject = (x - fEBullX) * (x - fEBullX) + (y - fEBullY) * (y - fEBullY);

        float fMinObjDist = (fObjWidth) * (fObjWidth);

        //Check if the actual distance is lower than the allowed => collision
        if(fMinObjDist >= distanceBetweenBulletAndObject) {
            bCollision = true;
            //Get the present speed (this should also be the speed going away after the collision)
        }
        return bCollision;
    }

}
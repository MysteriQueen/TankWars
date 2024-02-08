package com.example.v0_12;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

public abstract class MediumGameThread extends Thread {
    //Different mMode states
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;
    public static final int STATE_GAMEOVER = 6;
    public boolean bDownPressed = false;
    protected Canvas canvasRun;
    protected TextView oTimerView;
    private HighScoreActivity oHighScoreActivity;

    //Control variable for the mode of the game (e.g. STATE_WIN)
    protected int mMode = 1;

    //Control of the actual running inside run()
    private boolean mRun = false;

    //The surface this thread (and only this thread) writes upon
    private SurfaceHolder mSurfaceHolder;

    //the message handler to the View/Activity thread
    private Handler mHandler;

    //Android Context - this stores almost all we need to know
    private Context mContext;

    //The view
    public MediumGameView mGameView;

    //We might want to extend this call - therefore protected
    protected int mCanvasWidth = 1;
    protected int mCanvasHeight = 1;

    //Last time we updated the game physics
    protected long mLastTime = 0;

    protected Bitmap mBackgroundImage;

    protected long score = 0;

    //Used for time keeping
    private long now;
    private float elapsed;

    //Rotation vectors used to calculate orientation
    float[] mGravity;
    float[] mGeomagnetic;

    //Used to ensure appropriate threading
    static final Integer monitor = 1;


    public MediumGameThread(MediumGameView gameView) {
        mGameView = gameView;

        mSurfaceHolder = gameView.getHolder();
        mHandler = gameView.getmHandler();
        mContext = gameView.getContext();

        mBackgroundImage = BitmapFactory.decodeResource
                (gameView.getContext().getResources(),
                        R.drawable.gamebackground);
    }

    /*
     * Called when app is destroyed, so not really that important here
     * But if (later) the game involves more thread, we might need to stop a thread, and then we would need this
     * Dare I say memory leak...
     */
    public void cleanup() {
        this.mContext = null;
        this.mGameView = null;
        this.mHandler = null;
        this.mSurfaceHolder = null;
    }

    //Pre-begin a game
    abstract public void setupBeginning();

    //Starting up the game
    public void doStart() {
        synchronized(monitor) {

            setupBeginning();

            mLastTime = System.currentTimeMillis() + 100;

            setState(STATE_RUNNING);

            startTimer();

            setScore(0);
        }
    }

    public void setTimerView(TextView oTimerView) { this.oTimerView = oTimerView; }

    abstract protected void startTimer();

    //The thread start
    @Override
    public void run() {
        while (mRun) {
            canvasRun = null;
            try {
                canvasRun = mSurfaceHolder.lockCanvas(null);
                synchronized (monitor) {
                    if (mMode == STATE_RUNNING) {
                        updatePhysics();
                    }
                    doDraw(canvasRun);
                }
            }
            finally {
                if (canvasRun != null) {
                    if(mSurfaceHolder != null)
                        mSurfaceHolder.unlockCanvasAndPost(canvasRun);
                }
            }
        }
    }

    /*public void initMakeBullet(Bitmap oBullet, float mPaddleX, Bitmap oPaddle, int mCanvasHeight) {
        canvasRun.drawBitmap(oBullet, mPaddleX - oPaddle.getWidth() / 2, mCanvasHeight - oPaddle.getHeight() - 10, null);
    }*/

    /*
     * Surfaces and drawing
     */
    public void setSurfaceSize(int width, int height) {
        synchronized (monitor) {
            mCanvasWidth = width;
            mCanvasHeight = height;

            // don't forget to resize the background image
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
        }
    }


    protected void doDraw(Canvas canvas) {

        if(canvas == null) return;

        if(mBackgroundImage != null) canvas.drawBitmap(mBackgroundImage, 0, 0, null);
    }

    private void updatePhysics() {
        now = System.currentTimeMillis();
        elapsed = (now - mLastTime) / 1000.0f;

        updateGame(elapsed);

        mLastTime = now;
    }

    abstract protected void updateGame(float secondsElapsed);

    /*
     * Control functions
     */

    //Finger touches the screen
    /*public boolean onTouch(MotionEvent e) {
        if(e.getAction() != MotionEvent.ACTION_DOWN) return false;

        if(mMode == STATE_READY || mMode == STATE_LOSE || mMode == STATE_WIN) {
            doStart();
            return true;
        }

        if(mMode == STATE_PAUSE) {
            unpause();
            return true;
        }

        synchronized (monitor) {
            this.actionOnTouch(e.getRawX(), e.getRawY());
        }

        return false;
    }*/

    public boolean onTouch(MotionEvent e) {
        //If user touches screen on fire button, set fire to true
        //If user doesn't touch screen and down hasn't already been pressed, return false
        if((e.getAction() != MotionEvent.ACTION_DOWN) && (!bDownPressed)) return false;
        //If user touch screen
        if(e.getAction() == MotionEvent.ACTION_DOWN) bDownPressed = true;
        //If user release
        if(e.getAction() == MotionEvent.ACTION_UP) bDownPressed = false;
        if(mMode == STATE_READY || mMode == STATE_LOSE || mMode == STATE_WIN || mMode == STATE_GAMEOVER) {
            doStart();
            return true;
        }

        if(mMode == STATE_PAUSE) {
            unpause();
            return true;
        }

        synchronized (monitor) {
            this.actionOnTouch(e.getRawX(), e.getRawY());
        }

        return false;
    }

    /**
     * method for handling player input to move the ship up and down
     *   handles the behaviour of the ships when the action_down and action_up events are triggered
     * @return true if event has been triggered
     */
    /*public boolean onTouch(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (e.getX() < sX / 6) {
                    playerL1F.shipUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                playerL1F.shipUp = false;
                if (e.getX() > sX / 5.5) // if player is touching the right side of the screen, shoot beam
                    playerL1F.toShoot++;
                break;
        }
        return true;
    }*/

    protected void actionOnTouch(float x, float y) {
        //Override to do something
    }

    //The Orientation has changed
    @SuppressWarnings("deprecation")
    public void onSensorChanged(SensorEvent event) {
        synchronized (monitor) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    actionWhenPhoneMoved(orientation[2],orientation[1],orientation[0]);
                }
            }
        }
    }

    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
        //Override to do something
    }

    /*
     * Game states
     */

    /**
     * gameFinished()
     *This function is implemented in the EasyLvlGame class.
     */
    protected abstract void gameFinished();

    public void pause() {
        synchronized (monitor) {
            if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
        }
    }

    public void unpause() {
        // Move the real time clock up to now
        synchronized (monitor) {
            mLastTime = System.currentTimeMillis();
        }
        setState(STATE_RUNNING);
    }

    //Send messages to View/Activity thread
    public void setState(int mode) {
        synchronized (monitor) {
            setState(mode, null);
        }
    }

    public void setState(int mode, CharSequence message) {
        synchronized (monitor) {
            mMode = mode;

            if (mMode == STATE_RUNNING) {
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("text", "");
                b.putInt("viz", View.INVISIBLE);
                b.putBoolean("showAd", false);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
            else {
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();

                Resources res = mContext.getResources();
                CharSequence str = "";
                if (mMode == STATE_READY)
                    str = res.getText(R.string.mode_ready);
                else if (mMode == STATE_PAUSE)
                    str = res.getText(R.string.mode_pause);
                else if (mMode == STATE_LOSE)
                    str = res.getText(R.string.mode_lose);
                else if (mMode == STATE_WIN)
                    str = res.getText(R.string.mode_win);
                else if (mMode == STATE_GAMEOVER) {
                    str = res.getText(R.string.mode_gameover);
                }

                if (message != null) {
                    str = message + "\n" + str;
                }

                b.putString("text", str.toString());
                b.putInt("viz", View.VISIBLE);

                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }
    }

    /*
     * Getter and setter
     */
    public void setSurfaceHolder(SurfaceHolder h) {
        mSurfaceHolder = h;
    }

    public boolean isRunning() {
        return mRun;
    }

    public void setRunning(boolean running) {
        mRun = running;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mMode) {
        this.mMode = mMode;
    }


    /* ALL ABOUT SCORES */

    //Send a score to the View to view
    //Would it be better to do this inside this thread writing it manually on the screen?
    public void setScore(long score) {
        this.score = score;

        synchronized (monitor) {
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("score", true);
            b.putString("text", getScoreString().toString());
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }

    public float getScore() {
        return score;
    }

    public void updateScore(long score) {
        this.setScore(this.score + score);
    }


    protected CharSequence getScoreString() {
        return Long.toString(Math.round(this.score));
    }

}
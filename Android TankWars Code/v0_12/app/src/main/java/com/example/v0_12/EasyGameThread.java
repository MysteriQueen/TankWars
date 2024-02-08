package com.example.v0_12;

/**
 * EasyGameThread Class
 * This class sets up another thread and handles events for easy level.
 *
 * @author Riya Mayor
 */

import android.content.*;
import android.content.res.Resources;
import android.graphics.*;
import android.hardware.*;
import android.os.*;
import android.view.*;
import android.widget.TextView;

public abstract class EasyGameThread extends Thread {
    //Different mMode states
    protected static final int STATE_LOSE = 1;
    protected static final int STATE_PAUSE = 2;
    protected static final int STATE_READY = 3;
    protected static final int STATE_RUNNING = 4;
    protected static final int STATE_WIN = 5;
    protected static final int STATE_GAMEOVER = 6;
    //If user is touching the screen for long durations
    protected boolean bDownPressed = false;
    protected Canvas oCanvas;
    protected TextView oTimerView;
    //Control variable for the mode of the game
    protected int mMode = 1;
    //Control of the actual running inside run()
    private boolean mRun = false;
    //The surface this thread (and only this thread) writes upon
    private SurfaceHolder mSurfaceHolder;
    //the message handler to the View/Activity thread
    private Handler mHandler;
    //Android Context - this stores almost all we need to know
    private Context mContext;
    //The view of the game
    protected EasyGameView mGameView;
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
    private float[] mGravity;
    private float[] mGeomagnetic;
    //Used to ensure appropriate threading
    protected static final Integer iMonitor = 1;

    /**
     * CONSTRUCTOR
     * The constructor holds attributes of EasyGameView as well as the background image.
     * @param gameView Object of class EasyGameView
     */
    public EasyGameThread(EasyGameView gameView) {
        mGameView = gameView;
        mSurfaceHolder = gameView.getHolder();
        mHandler = gameView.getmHandler();
        mContext = gameView.getContext();
        mBackgroundImage = BitmapFactory.decodeResource(gameView.getContext().getResources(), R.drawable.gamebackground);
    }

    /**
     * cleanup()
     * This function is responsible for setting everything to null when the app is destroyed.
     */
    public void cleanup() {
        this.mContext = null;
        this.mGameView = null;
        this.mHandler = null;
        this.mSurfaceHolder = null;
    }

    /**
     * setupBeginning()
     * This function is implemented in the EasyLvlGame class.
     */
    abstract public void setupBeginning();

    /**
     * doStart()
     * This function is responsible for setting up the game.
     */
    protected void doStart() {
        synchronized(iMonitor) {
            //Invoke function which will be executed in EasyLvlGame
            setupBeginning();
            mLastTime = System.currentTimeMillis() + 100;
            setState(STATE_RUNNING);
            startTimer();
            //Set 0 as the initial score
            setScore(0);
        }
    }

    /**
     * setTimerView()
     * This function stores the timer.
     * @param oTimerView TextView for the timer
     */
    protected void setTimerView(TextView oTimerView) { this.oTimerView = oTimerView; }

    /**
     * startTimer()
     * This function is implemented in the EasyLvlGame class.
     */
    abstract protected void startTimer();

    /**
     * run()
     * This function is responsible for starting the thread.
     */
    @Override
    public void run() {
        //While the game is  running, set the canvas to null
        while (mRun) {
            oCanvas = null;
            try {
                oCanvas = mSurfaceHolder.lockCanvas(null);
                synchronized (iMonitor) {
                    //If the game is still running, update the game
                    if (mMode == STATE_RUNNING) {
                        updatePhysics();
                    }
                    //Draw the canvas
                    doDraw(oCanvas);
                }
            }
            //Execute code regardless if there is an exception
            finally {
                if (oCanvas != null) {
                    if(mSurfaceHolder != null)
                        mSurfaceHolder.unlockCanvasAndPost(oCanvas);
                }
            }
        }
    }

    /**
     * setSurfaceSize()
     * This function sets the size of the canvas and scales the background image so that it will
     * fill the whole canvas.
     * @param width X size of canvas
     * @param height Y size of canvas
     */
    protected void setSurfaceSize(int width, int height) {
        synchronized (iMonitor) {
            mCanvasWidth = width;
            mCanvasHeight = height;
            //Resize background image
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
        }
    }

    /**
     * doDraw()
     * This function draws the background image.
     * @param canvas Canvas of the game
     */
    protected void doDraw(Canvas canvas) {
        if(canvas == null) return;
        //Draw bitmap
        if(mBackgroundImage != null) canvas.drawBitmap(mBackgroundImage, 0, 0, null);
    }

    /**
     * updatePhysics()
     * This function updates the game every time it is called.
     */
    private void updatePhysics() {
        now = System.currentTimeMillis();
        elapsed = (now - mLastTime) / 1000.0f;
        //Invoke function, passing the elapsed time
        updateGame(elapsed);
        //Reset variable to current time
        mLastTime = now;
    }

    /**
     * updateGame()
     * This function is implemented in the EasyLvlGame class.
     */
    abstract protected void updateGame(float secondsElapsed);

    /*
     * Control functions
     */

    /**
     * onTouch()
     * This function is invoked when the user touches the screen.
     * @param e Event for any motion on the screen
     * @return boolean which determines whether the user has touched the screen or not
     */
    public boolean onTouch(MotionEvent e) {
        //If user doesn't touch screen and down hasn't already been pressed, return false
        if((e.getAction() != MotionEvent.ACTION_DOWN) && (!bDownPressed)) return false;
        //If user touch screen, set bDownPressed to true
        if(e.getAction() == MotionEvent.ACTION_DOWN) bDownPressed = true;
        //If user release, sey bDownPressed to false
        if(e.getAction() == MotionEvent.ACTION_UP) bDownPressed = false;
        //If user touches the screen in ready mode, the game will start
        if(mMode == STATE_READY) {
            //Start the game
            doStart();
            return true;
        }
        if(mMode == STATE_LOSE || mMode == STATE_WIN || mMode == STATE_GAMEOVER) {
            //Load up the next activity
        }
        if(mMode == STATE_PAUSE) {
            unpause();
            return true;
        }
        synchronized (iMonitor) {
            this.actionOnTouch(e.getRawX(), e.getRawY());
        }
        return false;
    }

    /**
     * actionOnTouch()
     *This function is implemented in the EasyLvlGame class.
     */
    protected abstract void actionOnTouch(float x, float y);

    @SuppressWarnings("deprecation")
    /**
     * onSensorChanged()
     * This function is invoked when the orientation of the screen has changed.
     */
    public void onSensorChanged(SensorEvent event) {
        synchronized (iMonitor) {
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

    /**
     * actionWhenPhoneMoved()
     *This function is implemented in the EasyLvlGame class.
     */
    protected abstract void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection);

    /*
     * Game states
     */

    /**
     * gameFinished()
     *This function is implemented in the EasyLvlGame class.
     */
    protected abstract void gameFinished();

    /**
     * pause()
     * This function handles when the game gets paused.
     */
    public void pause() {
        synchronized (iMonitor) {
            //First check if the game is running before setting pause as game states
            if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
        }
    }

    /**
     * unpause()
     * This function handles when the game resumes.
     */
    public void unpause() {
        // Move the real time clock up to now
        synchronized (iMonitor) {
            //Set the previous time (when the game was paused)
            mLastTime = System.currentTimeMillis();
        }
        //Invoke function
        setState(STATE_RUNNING);
    }

    /**
     * setState()
     * This function sends messages to View/Activity thread which then gets displayed on the UI.
     * @param mode game state
     */
    public void setState(int mode) {
        synchronized (iMonitor) {
            setState(mode, null);
        }
    }

    /**
     * setState()
     * This function sends messages to View/Activity thread which then gets displayed on the UI.
     * @param mode Game state
     * @param message Data packaged in CharSequence
     */
    public void setState(int mode, CharSequence message) {
        synchronized (iMonitor) {
            mMode = mode;
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            if (mMode == STATE_RUNNING) {
                b.putString("text", "");
                b.putInt("viz", View.INVISIBLE);
                b.putBoolean("showAd", false);
            }
            else {
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
                    gameFinished();
                }
                if (message != null) {
                    str = message + "\n" + str;
                }
                b.putString("text", str.toString());
                b.putInt("viz", View.VISIBLE);
            }
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }

    /*
     * Getters & Setters
     */

    /**
     * SETTER - setRunning()
     * @param running A boolean variable which determines if the game is running or not.
     */
    public void setRunning(boolean running) {
        mRun = running;
    }

    /**
     * GETTER - getMode()
     * Retrieve game state
     * @return mMode Int
     */
    public int getMode() {
        return mMode;
    }

    /**
     * SETTER - setScore()
     * This function obtains sets the score, in the message, to the view via the handler.
     * @param score Long
     */
    public void setScore(long score) {
        this.score = score;
        synchronized (iMonitor) {
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("score", true);
            b.putString("text", getScoreString().toString());
            //Change data to new data
            msg.setData(b);
            //Send new data to oEasyLvlActivity View via handler
            mHandler.sendMessage(msg);
        }
    }

    /**
     * GETTER - getScore()
     * @return score Float
     */
    public float getScore() {
        return score;
    }

    /**
     * SETTER - updateScore()
     * Evrey time elapse, the score is updated.
     * @param score New score
     */
    public void updateScore(long score) { this.setScore(this.score + score);
    }

    /**
     * GETTER - getScoreString()
     * @return score String
     */
    protected CharSequence getScoreString() {
        //Round the score and convert it to string
        return Long.toString(Math.round(this.score));
    }
}
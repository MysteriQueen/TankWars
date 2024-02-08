package com.example.v0_12;

/**
 * EasyGameView Class
 * This class sets the game view of easy difficulty level.
 *
 * @author Riya Mayor
 */

import android.content.*;
import android.hardware.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class EasyGameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
    private volatile EasyGameThread thread;
    //Handle communication from the GameThread to the View/Activity Thread
    private Handler mHandler;
    //Pointers to the views
    private TextView mScoreView;
    private TextView mStatusView;
    Sensor accelerometer;
    Sensor magnetometer;

    /**
     * CONSTRUCTOR
     * Retrieves Holder and invokes handler function.
     * @param context
     * @param attrs Attributes
     */
    public EasyGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        //Invoke function
        handler();
    }

    /**
     * handler()
     * Initiates handler, passing the looper.
     */
    private void handler() {
        //Set up a handler for messages from GameThread
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message m) {
                if(m.getData().getBoolean("score")) {
                    mScoreView.setText(m.getData().getString("text"));
                }
                else {
                    int i = m.getData().getInt("viz");
                    switch(i) {
                        case View.VISIBLE:
                            mStatusView.setVisibility(View.VISIBLE);
                            break;
                        case View.INVISIBLE:
                            mStatusView.setVisibility(View.INVISIBLE);
                            break;
                        case View.GONE:
                            mStatusView.setVisibility(View.GONE);
                            break;
                    }
                    mStatusView.setText(m.getData().getString("text"));
                }
            }
        };
    }

    /**
     * cleanup()
     * This function is used to destroy all the resources.
     */
    public void cleanup() {
        this.thread.setRunning(false);
        this.thread.cleanup();
        this.removeCallbacks(thread);
        thread = null;
        this.setOnTouchListener(null);
        SurfaceHolder holder = getHolder();
        holder.removeCallback(this);
    }

    /*
     * Setters and Getters
     */

    /**
     * SETTER - setThread()
     * @param newThread Thread from the EasyGameThread class
     */
    public void setThread(EasyGameThread newThread) {
        //Store thread
        thread = newThread;
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return thread != null && thread.onTouch(event);
            }
        });
        setClickable(true);
        setFocusable(true);
    }

    /**
     * SETTER - setStatusView()
     * @param mStatusView TextView of the status
     */
    public void setStatusView(TextView mStatusView) {
        this.mStatusView = mStatusView;
    }

    /**
     * SETTER - setScoreView
     * @param mScoreView TextView of the score
     */
    public void setScoreView(TextView mScoreView) {
        this.mScoreView = mScoreView;
    }

    /**
     * GETTER - getmHandler
     * @return mHandler
     */
    public Handler getmHandler() {
        return mHandler;
    }

    /*
     * Screen functions
     */

    /**
     * onWindowFocusChanged()
     * This function ensures that that program goes into pause state if it goes out of focus
     * (i.e the user opening up another app on their phone causing this game to go into
     * pause state).
     * @param hasWindowFocus A boolean variable that determines if the activity has gone out of focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        //Check if thread is empty
        if(thread!=null) {
            //Check if the activity has gone out of focus
            if (!hasWindowFocus)
                //Pause the thread
                thread.pause();
        }
    }

    /**
     * surfaceCreated()
     * This function starts two threads. The first thread is created when the state of the thread is NEW.
     * When this function is called again and the state of thread is terminated, another string will be
     * created. This is to update the screen and set all the fields in the old thread.
     * @param holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        if(thread!=null) {
            thread.setRunning(true);
            if(thread.getState() == Thread.State.NEW){
                //Start new thread
                thread.start();
            }
            else {
                if(thread.getState() == Thread.State.TERMINATED){
                    thread = new EasyLvlGame(this);
                    thread.setRunning(true);
                    //Start new thread
                    thread.start();
                }
            }
        }
    }

    /**
     * surfaceChanged()
     * This function is responsible for setting the size of the canvas to the game thread. It is always
     * invoked after the surfaceCreated.
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(thread!=null) {
            thread.setSurfaceSize(width, height);
        }
    }

    /**
     * surfaceDestroyed()
     * This function is there to stop the game thread if the canvas has been destroyed.
     * @param arg0 The first argument
     */
    public void surfaceDestroyed(SurfaceHolder arg0) {
        boolean retry = true;
        if(thread!=null) {
            thread.setRunning(false);
        }
        //While true
        while (retry) {
            try {
                if(thread!=null) {
                    //Join 2 threads together
                    thread.join();
                }
                retry = false;
            }
            catch (InterruptedException e) {
                Log.d("Tag", "Exception: surfaceDestroyed");
            }
        }
    }

    /*
     * Accelerometer
     */

    /**
     * startSensor()
     * This function creates sensors.
     * @param sm
     */
    public void startSensor(SensorManager sm) {
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * removeSensor()
     * This function removes sensors.
     * @param sm
     */
    public void removeSensor(SensorManager sm) {
        sm.unregisterListener(this);
        accelerometer = null;
        magnetometer = null;
    }

    /**
     * onSensorChanged()
     * This function is invoked when the sensor has changed.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(thread!=null) {
            thread.onSensorChanged(event);
        }
    }

    /**
     * onAccuracyChanged()
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
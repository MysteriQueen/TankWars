package com.example.v0_12;

/**
 * EasyLvlActivity Class
 * This class sets up the easy level and handles events.
 *
 * @author Riya Mayor
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class EasyLvlActivity extends Activity {
    private static final int MENU_RESUME = 1;
    private static final int MENU_START = 2;
    private static final int MENU_STOP = 3;
    private EasyGameView oGameView;
    private EasyGameThread oGameThread;
    private TextView oMenuText;

    public EasyLvlActivity() {
    }

    /**
     * onCreate()
     * Main function which is responsible for creating the main menu.
     * @param savedInstanceState contains the previously saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the window to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set up screen layout
        setContentView(R.layout.activity_easy_lvl);
        //Store the game area layout into a variable
        oGameView = (EasyGameView)findViewById(R.id.gameAreaID);
        oGameView.setStatusView((TextView)findViewById(R.id.readyID));
        oGameView.setScoreView((TextView)findViewById(R.id.scoreID));
        //Start the game thread
        this.startGame(oGameView, null, savedInstanceState);
    }

    /**
     * startGame()
     * This function starts the easy level.
     * @param gView
     * @param gThread
     * @param savedInstanceState
     */
    private void startGame(EasyGameView gView, EasyGameThread gThread, Bundle savedInstanceState) {
        //Set up a new game, we don't care about previous states
        oGameThread = new EasyLvlGame(oGameView);
        //Create a thread
        oGameView.setThread(oGameThread);
        oGameThread.setTimerView((TextView) findViewById(R.id.timerID));
        oGameThread.setState(EasyGameThread.STATE_READY);
        oGameView.startSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        //startActivity(new Intent(EasyLvlActivity.this, GameOverActivity.class));
    }

    /*
     * Activity state functions
     */

    /**
     * onPause()
     * This function pauses the game.
     */
    @Override
    protected void onPause() {
        super.onPause();

        if(oGameThread.getMode() == EasyGameThread.STATE_RUNNING) {
            oGameThread.setState(EasyGameThread.STATE_PAUSE);
        }
    }

    /**
     * onStop()
     * This function stops the game.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * onDestroy()
     * Destroys the activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        oGameView.cleanup();
        oGameThread.cleanup();
        oGameView.removeSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        oGameThread = null;
        oGameView = null;
    }

    /*
     * UI Functions
     */

    /**
     * onCreateOptionsMenu()
     * This function creates options menu.
     * @param menu
     * @return Boolean variable that determines whether the menu has been created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
        return true;
    }

    /**
     * onOptionsItemSelected()
     * This function handles events based on the item.
     * @param item
     * @return Boolean variable that determines whether the item has been selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_START:
                oGameThread.doStart();
                return true;
            case MENU_STOP:
                oGameThread.setState(EasyGameThread.STATE_LOSE, getText(R.string.message_stopped));
                return true;
            case MENU_RESUME:
                oGameThread.unpause();
                return true;
        }
        return false;
    }
}
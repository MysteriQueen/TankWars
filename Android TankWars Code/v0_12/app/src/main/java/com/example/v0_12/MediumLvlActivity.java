package com.example.v0_12;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MediumLvlActivity extends Activity {
    private static final int MENU_RESUME = 1;
    private static final int MENU_START = 2;
    private static final int MENU_STOP = 3;
    private MediumGameView oGameView;
    private MediumLvlGame oMediumLvlGame;
    public MediumGameThread oGameThread;
    public Activity oActivity;
    //private MediumLvlGame oMediumLvlGame = new MediumLvlGame(oGameView, 1);
    private Button bShoot;
    private String sShootButtonText;


    public MediumLvlActivity() {
    }

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the window to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Set up screen layout
        setContentView(R.layout.activity_medium_lvl);
        //Store the game area layout into a variable
        oGameView = (MediumGameView)findViewById(R.id.gameAreaID);
        oGameView.setStatusView((TextView)findViewById(R.id.readyID));
        oGameView.setScoreView((TextView)findViewById(R.id.scoreID));
        //Create a GameView
        //oGameView = new MediumGameView(this);
        //Start the game thread
        this.startGame(oGameView, null, savedInstanceState);
        //shootButton();
    }

    /**
     * shootButton()
     * This function handles the event when the EASY LEVEL button is clicked.
     */
    /*protected void shootButton() {
        //Initialise object
        sShootButtonText = getString(R.string.sShootResource);
        //Invoke function
        displayShootButtonText(sShootButtonText);
        //Event handler when the "SHOOT" button is clicked
        bShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create a bullet
                oEasyLvlGame.makeBullet();
                //oEasyLvlGame.makeBullet(getResources());
                //oEasyLvlGame = new MediumLvlGame(oGameView, 0);
                //oEasyLvlGame.makeBullet(oGameView);
                //oGameThread.initMakeBullet(oGameView);
            }
        });
    }

    protected void displayShootButtonText(String sShootButtonText) {
        //Set the ButtonID to a variable
        bShoot = (Button)findViewById(R.id.shootButtonID);
        //Display button text
        bShoot.setText(sShootButtonText);
    }*/

    private void startGame(MediumGameView gView, MediumGameThread gThread, Bundle savedInstanceState) {
        //Set up a new game, we don't care about previous states
        //oGameThread = new MediumLvlGame(oGameView, oActivity);
        oGameThread = new MediumLvlGame(oGameView);
        //Create a thread
        oGameView.setThread(oGameThread);
        oGameThread.setTimerView((TextView) findViewById(R.id.timerID));
        oGameThread.setState(MediumGameThread.STATE_READY);
        oGameView.startSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
    }

    /*
     * Activity state functions
     */

    @Override
    protected void onPause() {
        super.onPause();

        if(oGameThread.getMode() == MediumGameThread.STATE_RUNNING) {
            oGameThread.setState(MediumGameThread.STATE_PAUSE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        oGameView.cleanup();
        oGameView.removeSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        oGameThread = null;
        oGameView = null;
    }

    /*
     * UI Functions
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_START:
                oGameThread.doStart();
                return true;
            case MENU_STOP:
                oGameThread.setState(MediumGameThread.STATE_LOSE,  getText(R.string.message_stopped));
                return true;
            case MENU_RESUME:
                oGameThread.unpause();
                return true;
        }

        return false;
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing if nothing is selected
    }
}
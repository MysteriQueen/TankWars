package com.example.v0_12;

/**
 * MainActivity Class
 * This class sets up the main menu and handles events.
 *
 * @author Riya Mayor
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    //Declare global variables
    private TextView oStartText, oScoreText;

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
        setContentView(R.layout.activity_main);
        //Set up the main menu
        setUp();
    }

    /**
     * setUp()
     * This function is responsible for setting up the main menu.
     */
    protected void setUp() {
        //Invoke function
        startText();
        //Invoke function
        scoreText();
    }

    /**
     * startText()
     * This function handles the event when the START textview is pressed.
     */
    protected void startText() {
        //Set the startID to a variable
        oStartText = (TextView)findViewById(R.id.startID);
        //Set the java string resource to oStartText
        oStartText.setText(R.string.sStartResource);
        //Event handler when START is pressed
        findViewById(R.id.startID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start another activity
                startActivity(new Intent(MainActivity.this, LvlsActivity.class));
            }
        });
    }

    /**
     * scoreText()
     * This function handles the event when the HIGH SCORE textview is pressed.
     */
    protected void scoreText() {
        //Set the highScoreID to a variable
        oScoreText = (TextView)findViewById(R.id.highScoreID);
        //Set the java string resource to oScoreText
        oScoreText.setText(R.string.sScoreResource);
        //Event handler when HIGH SCORE is pressed
        findViewById(R.id.highScoreID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start another activity
                startActivity(new Intent(MainActivity.this, HighScoreActivity.class));
            }
        });
    }
}

package com.example.v0_12;

/**
 * LvlsActivity Class
 * This class sets up the levels menu and handles events.
 *
 * @author Riya Mayor
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

public class LvlsActivity extends Activity {
    //Declare global variables
    private TextView oEasyLvlText, oMediumLvlText, oHardLvlText, oBackText;

    /**
     * onCreate()
     * Main function which is responsible for creating the levels menu.
     * @param savedInstanceState contains the previously saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the window to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lvls_menu);
        //Set up the game
        setUp();
    }

    /**
     * setUp()
     * This function is responsible for setting up the levels menu.
     */
    protected void setUp() {
        //Invoke function
        easyLvlText();
        //Invoke function
        mediumLvlText();
        //Invoke function
        hardLvlText();
        //Invoke function
        backText();
    }

    /**
     * easyLvlText()
     * This function handles the event when the EASY LEVEL textview is pressed.
     */
    protected void easyLvlText() {
        //Set the  to a variable
        oEasyLvlText = (TextView)findViewById(R.id.easyID);
        //Set the java string resource to oEasyLvlText
        oEasyLvlText.setText(R.string.sEasyLvlResource);
        //Event handler when EASY LEVEL is pressed
        findViewById(R.id.easyID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start another activity
                startActivity(new Intent(LvlsActivity.this, EasyLvlActivity.class));
            }
        });
    }

    /**
     * mediumLvlText()
     * This function handles the event when the MEDIUM LEVEL textview is pressed.
     */
    protected void mediumLvlText() {
        //Set the mediumID to a variable
        oMediumLvlText = (TextView)findViewById(R.id.mediumID);
        //Set the java string resource to oBackText
        oMediumLvlText.setText(R.string.sMediumLvlResource);
        //Event handler when MEDIUM LEVEL is pressed
        findViewById(R.id.mediumID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start another activity
                startActivity(new Intent(LvlsActivity.this, MediumLvlActivity.class));
            }
        });
    }

    /**
     * hardLvlText()
     * This function handles the event when the HARD LEVEL textview is pressed.
     */
    protected void hardLvlText() {
        //Set the hardID to a variable
        oHardLvlText = (TextView)findViewById(R.id.hardID);
        //Set the java string resource to oBackText
        oHardLvlText.setText(R.string.sHardLvlResource);
        //Event handler when HARD LEVEL is pressed
        findViewById(R.id.hardID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start another activity
                startActivity(new Intent(LvlsActivity.this, HardLvlActivity.class));
            }
        });
    }

    /**
     * backText()
     * This function handles the event when the BACK textview is pressed.
     */
    protected void backText() {
        //Set the backID to a variable
        oBackText = (TextView)findViewById(R.id.backID);
        //Set the java string resource to oBackText
        oBackText.setText(R.string.sBackResource);
        //Event handler when BACK is clicked
        findViewById(R.id.backID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Destroy activity
                finish();
            }
        });
    }
}
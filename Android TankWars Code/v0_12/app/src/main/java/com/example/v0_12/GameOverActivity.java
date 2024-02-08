package com.example.v0_12;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class GameOverActivity extends Activity {
    private TextView oMenuText;
    private EditText oNameInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the window to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_over);
        //Set up the game over
        setUp();
    }

    private void setUp() {
        //Invoke function
        gameOverScreen();
        //Invoke function
        //save
    }

   /* private void gameOverText() {
        //Set the startID to a variable
        oGameOverText = (TextView)findViewById(R.id.gameOverID);
        //Set the java string resource to oStartText
        oGameOverText.setText(R.string.sGameOverResource);
    }*/

    protected void gameOverScreen() {
        //Set the startID to a variable
        oMenuText = (TextView)findViewById(R.id.gameOverID);
        //Set the java string resource to oStartText
        oMenuText.setText(R.string.sGameOverResource);
        //Event handler when START is pressed
        findViewById(R.id.gameOverID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start another activity
                startActivity(new Intent(GameOverActivity.this, LvlsActivity.class));
            }
        });
    }
}
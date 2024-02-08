package com.example.v0_12;

/**
 * HighScoreActivity Class
 * This class sets up the high score menu and handles events.
 *
 * @author Riya Mayor
 */

import android.app.Activity;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.RequiresApi;
import com.google.firebase.database.*;
import java.util.*;

public class HighScoreActivity extends Activity {
    //Declare global variables
    private TextView oBackText, oScoreListText;
    private FirebaseDatabase oFirebaseInst;
    private DatabaseReference oReference;
    private String TAG = "HighScoreActivity";
    private long lCountPosts = 0;
    private String sEasyDBScore, sMediumDBScore, sHardDBScore;

    /**
     * onCreate()
     * Main function which is responsible for creating the high score menu.
     *
     * @param savedInstanceState contains the previously saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the window to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_high_score);
        // Toast testing Freebase connection
        Toast.makeText(HighScoreActivity.this, "Connection was successful!", Toast.LENGTH_LONG).show();
        //Set up the game
        setUp();
        //Invoke function
        readData();
    }

    /**
     * setUp()
     * This function is responsible for setting up the high score menu.
     */
    protected void setUp() {
        //Invoke function
        initVariables();
        //Invoke function
        backText();
    }


    /**
     * initVariables()
     * This function is responsible for initialising the variables for the database.
     */
    protected void initVariables() {
        //Initialise score list TextView
        oScoreListText = findViewById(R.id.showScoreListID);
        //Write message to the firebase database instance
        oFirebaseInst = FirebaseDatabase.getInstance();
        //Get reference of the firebase instance
        oReference = oFirebaseInst.getReference("ScoreList");
    }

    /**
     * backText()
     * This function handles the event when the BACK textview is pressed.
     */
    private void backText() {
        //Set the backID to a variable
        oBackText = (TextView) findViewById(R.id.backID2);
        //Set the java string resource to oBackText
        oBackText.setText(R.string.sBackResource);
        //Event handler when BACK is pressed
        findViewById(R.id.backID2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Destroy activity
                finish();
            }
        });
    }

    private void readData() {
        //Listen for any connections from the database
        oReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot oDataSnapshot) {
                //Initialise local variables
                ArrayList<String> alValues = new ArrayList<String>();
                String sRead = "";
                String sUserKey = "";
                String sName = "";
                String sScore = "";
                String sDisplay = "";

                //Loop through all the nodes in the database
                for (DataSnapshot oNode : oDataSnapshot.getChildren()) {
                    //Store all the values
                    sUserKey = "\n\n" + oNode.getKey();
                    sName = oNode.child("name").getValue().toString();
                    sScore = oNode.child("score").getValue().toString();
                    //
                    sRead = sName + "\t\t" + sScore;
                    //Add sRead to the array list
                    alValues.add(sUserKey + ": " + "\n\t" + sRead); //add result into array list

                    /*switch(Character.getNumericValue(sUserKey.charAt(sUserKey.length() - 1))) {
                        case 1:
                            sEasyDBScore = sScore;
                        case 2:
                            sMediumDBScore = sScore;
                        case 3:
                            sHardDBScore = sScore;
                    }*/
                }

                sDisplay = "HIGHSCORE LIST";
                for (String sValue : alValues) {
                    sDisplay += sValue;
                }
                Log.d(TAG, "Value is: " + sDisplay);
                oScoreListText.setText(sDisplay);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /*private void readData() {
        //Listen for any connections from the database
        oReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot oDataSnapshot) {
                //Initialise arraylist
                ArrayList alValues = new ArrayList();
                //Declare variables
                String sData, sRead;
                int iCount;
                Iterator oIterator;
                //Loop through all the objects in DataSnapshot
                for(DataSnapshot oObject : oDataSnapshot.getChildren()) {
                    //Retrieve playerID
                    sData = oObject.getKey() + ": ";
                    //Loop though all the children in the DataSnapshot
                    for (DataSnapshot oChildren : oObject.getChildren()) {
                        //Retrieve nickname and score
                        sData += " " + oChildren.getValue();
                    }
                    //Add data to array list
                    alValues.add(sData);
                }
                iCount = 0;
                //Assign iterator to arraylist
                oIterator = alValues.iterator();
                sRead = ""; // collect 5 String in five lines
                while (oIterator.hasNext() && iCount < 5) {// iterate for five names in database
                    sRead = sRead + oIterator.next() + "\n"; // "\n" will enable line
                    iCount++; // increasing count♦
                }
                Log.d(TAG, "Value is: " + sRead);

                //Set all the data from the database to the GUI
                oScoreListText.setText("HighScore List" + "\n\n" + sRead);
                /*oPlayerIDText.setText("");
                oNameText.setText("");
                oScoreText.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }*/

    protected void writeData(int iLevel, String sPlayerName, int iScore) {
        //Initialise firebase instance
        oFirebaseInst = FirebaseDatabase.getInstance();
        //Initialise reference of the firebase instance
        oReference = oFirebaseInst.getReference("ScoreList");

        //Declare and initialise local variables
        String sGameLevel, sScore;
        sGameLevel = Integer.toString(iLevel);
        sScore = Integer.toString(iScore);

        //If player name is null, display "Anon"
        if(sPlayerName.isEmpty()) {
            sPlayerName = "Anon";
        }

        /*switch(iLevel) {
            case 1:
                //If sScore is greater than stored score, replace the score
                if (Integer.parseInt(sScore) > Integer.parseInt(sEasyDBScore)) {
                    sEasyDBScore = sScore;
                }
            case 2:
                //If sScore is greater than stored score, replace the score
                if (Integer.parseInt(sScore) > Integer.parseInt(sMediumDBScore)) {
                    sMediumDBScore = sScore;
                }
            case 3:
                //If sScore is greater than stored score, replace the score
                if (Integer.parseInt(sScore) > Integer.parseInt(sHardDBScore)) {
                    sHardDBScore = sScore;
                }
        }*/

        //Set the properties of the player
        Player oPlayer =  new Player(sPlayerName, sScore);

        //Write the player name and the score to the database under game level
        oReference.child("Level " + sGameLevel).setValue(oPlayer);
        }
    }

    /**
     * saveData()
     * - iLevel - the game level which was being played
     * - sName - player Name
     * - iScore - score to be saved
     */
    /*protected void saveData(int iLevel, String sPlayerName, int iScore) {
        boolean bRetVal = false;
        //Write message to the firebase database instance
        initVariables();
        //oFirebaseInst = FirebaseDatabase.getInstance();
        //Get reference of the firebase instance
        //oReference = oFirebaseInst.getReference("Score");
        //Listen for any connections from the database
       /*oReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            String sGameLevel, sName, sScore;
            @Override
            public void onDataChange(DataSnapshot oDataSnapshot) {
                //Get player data from global variables and convert it to string
                sGameLevel = Integer.toString(iLevel);
                sName = sPlayerName;
                sScore = Integer.toString(iScore);
                //Pass values to Player constructor
                Player oDatabase = new Player(sGameLevel, sName, sScore);
                //If null, display a message
                if (sName.equalsIgnoreCase("")) {
                    sName = "Anon";
                }
                oReference.child("TankWars Score").setValue(oDatabase);
                //oReference.setValue(oDatabase);
                //oReference.child("").setValue(oDatabase);
                //oScoreListText.setText("Status : Data Stored");
                //Toast.makeText(HighScoreActivity.this, "Data Stored was successful!", Toast.LENGTH_LONG).show();
                //return bRetVal;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
        /*String sGameLevel, sName, sScore;
        //Get player data from global variables and convert it to string
        sGameLevel = Integer.toString(iLevel);
        sName = sPlayerName;
        sScore = Integer.toString(iScore);
        //Pass values to Player constructor
        Player oDatabase = new Player(sGameLevel, sName, sScore);
        //If null, display a message
        if (sName.equalsIgnoreCase("")) {
            sName = "Anon";
        }
        //oReference.child("TankWars Score").setValue(oDatabase);
        oReference.setValue(oDatabase);
        //oReference.setValue(oDatabase);
        //oReference.child("").setValue(oDatabase);
        //oScoreListText.setText("Status : Data Stored");
        //Toast.makeText(HighScoreActivity.this, "Data Stored was successful!", Toast.LENGTH_LONG).show();
        //return bRetVal;
    }*/

    /*int iCount = 0;

    protected void saveData(int iLevel, String sPlayerName, int iScore) {
        iCount++;
        //Initialise FirebaseDatabase
        oFirebaseInst = FirebaseDatabase.getInstance();
        //Initialise DatabaseReference
        oReference = oFirebaseInst.getReference().child(Integer.toString(iCount));
        //Set the values
        Player oDataObject = new Player(Integer.toString(iLevel), sPlayerName, Integer.toString(iScore));
        oReference.setValue(oDataObject);
    }
*/
    /*protected void saveData(int iLevel, String sPlayerName, int iScore) {
        oReference = oFirebaseInst.getReference();
        //Listen for any connections from the database
        oReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot oDataSnapshot) {
                //Declare variables
                String sData = "";
                if (iLevel == 1) {
                    sData = oDataSnapshot.child("1").getValue(String.class);
                }

                if (sData != null) {
                    if (iScore > Integer.parseInt(sData)) {
                        oReference.child("Score").setValue(iScore);
                    } else {
                        oReference.child("Score").setValue(sData);
                    }
                }
            }

            /**
             * @param error A description of the error that occurred
             */
            /*@Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}*/

/**
* saveScoreBtn()
* This function handles the event when the SAVE SCORE button is pressed.
*/
    /*protected void saveScoreBtn() {
        //Initialise object
        sSaveText = getString(R.string.sSaveScoreResource);
        //Invoke function
        displaySaveScoreBtnText(sSaveText);
        //Event handler when the SAVE SCORE button is pressed
        oSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sPlayerID, sName, sScore;
                //Get player data from global variables and convert it to string
                sPlayerID = oPlayerIDText.getText().toString();
                sName = oNameText.getText().toString();
                sScore = oScoreText.getText().toString();
                //Pass values to Player constructor
                Player oDatabase = new Player(sName, sScore);
                //If null, display a message
                if (sPlayerID.equalsIgnoreCase("")) {
                    oScoreListText.setText("Status : Enter Value");
                }
                //Else write values to the database and display message
                else {
                    oReference.child(sPlayerID).setValue(oDatabase);
                    oScoreListText.setText("Status : Data Stored");
                    Toast.makeText(HighScoreActivity.this, "Data Stored was successful!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * displaySaveScoreBtnText()
     * Displays the text on the button.
     *
     * @param sText is a string that will be displayed
     */
   /* protected void displaySaveScoreBtnText(String sText) {
        //Set the ButtonID to a variable
        oSaveBtn = (Button) findViewById(R.id.saveScoreID);
        //Display button text
        oSaveBtn.setText(sText);
    }

    /**
     * showScoresBtn()
     * This function handles the event when the GET SCORES button is pressed.
     */
    /*protected void showScoresBtn() {
        //Initialise object
        sGetText = getString(R.string.sGetScoresResource);
        //Invoke function
        displayShowScoresBtnText(sGetText);
        //Event handler when the GET SCORES button is pressed
        oShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Listen for any connection from the database
                oReference.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(DataSnapshot oDataSnapshot) {
                        //Initialise arraylist
                        ArrayList arValues = new ArrayList();
                        //Declare variables
                        String sData, sRead;
                        int iCount;
                        Iterator oIterator;
                        //Loop through all the objects in DataSnapshot
                        for (DataSnapshot oObject : oDataSnapshot.getChildren()) {
                            //Retrieve playerID
                            sData = oObject.getKey() + ": ";
                            //Loop though all the children in the DataSnapshot
                            for (DataSnapshot oChildren : oObject.getChildren()) {
                                //Retrieve nickname and score
                                sData += " " + oChildren.getValue();
                            }
                            //Add data to array list
                            arValues.add(sData);
                        }
                        iCount = 0;
                        //Assign iterator to arraylist
                        oIterator = arValues.iterator();
                        sRead = ""; // collect 5 String in five lines
                        while (oIterator.hasNext() && iCount < 5) {// iterate for five names in database
                            sRead = sRead + oIterator.next() + "\n"; // "\n" will enable line
                            iCount++; // increasing count♦
                        }
                        Log.d(TAG, "Value is: " + sRead);

                        //Set all the data from the database to the GUI
                        oScoreListText.setText("HighScore List" + "\n" + sRead);
                        oPlayerIDText.setText("");
                        oNameText.setText("");
                        oScoreText.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            }
        });
    }

    /**
     * displayShowScoresBtnText()
     * Displays the text on the button.
     * @param sText is a string that will be displayed
     */
    /*protected void displayShowScoresBtnText(String sText) {
        //Set the ButtonID to a variable
        oShowBtn = (Button)findViewById(R.id.showScoresID);
        //Display button text
        oShowBtn.setText(sText);
    }*/
package com.application.sb.moodtacker.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.application.sb.moodtacker.R;
import com.application.sb.moodtacker.model.Moods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    // I create my GESTURE DETECTOR
    private GestureDetector gestureDetector;

    //I create my VIEW FLIPPER
    private ViewFlipper vFlipper;

    // I create the activity
    private MainActivity activity;

    // Shared preferences VALUES
    private SharedPreferences moodPreferences;
    private String mood = "mood";

    // The current Mood position in the ViewFlipper
    private int thisMood = 1;

    // Tableau d'objets MOOD
    public static ArrayList<Moods> moodsArrayList = new ArrayList<>();

    // Tableau de musiques
    int musicTab[] = {R.raw.very_happy, R.raw.happy, R.raw.normal, R.raw.disapointed, R.raw.sad};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        System.out.println("MainActivity::onCreate");

        // References
        this.activity = this;
        ImageButton commentsButton = findViewById(R.id.commentsButton);
        ImageButton historyButton = findViewById(R.id.historyButton);
        moodPreferences = getPreferences(MODE_PRIVATE);
        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);
        // I create my Constraint Layout
        vFlipper = findViewById(R.id.flipperView);

        // Default ViewFlipper view
        vFlipper.setDisplayedChild(1);

        // Comments alert dialogue
        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // New AlertDialogue
                AlertDialog.Builder myComment = new AlertDialog.Builder(activity);

                // On instancie notre layout en tant que View
                LayoutInflater inflater = activity.getLayoutInflater();
                final View alertDialogView = inflater.inflate(R.layout.comments, null );

                    // The "CANCEL" button
                myComment.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                    // The "CONFIRM" button
                myComment.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // I get the comment
                        EditText commentsEdit = alertDialogView.findViewById(R.id.editText);
                        String comment = commentsEdit.getText().toString();

                        // New Mood
                        thisMood = vFlipper.getDisplayedChild();
                        Moods currentMood = new Moods(thisMood, comment);
                        moodsArrayList.add(currentMood);

                        // New Toast to confirm the save
                        Toast.makeText(activity , "Save",Toast.LENGTH_LONG).show();
                    }
                });
                myComment.setView(alertDialogView);
                myComment.show();
            }
        });

// History Button redirection
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyActivity = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(historyActivity);
            }
        });
    }

    // The Mood swipe

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Swipe to the top
        // I create media player
        MediaPlayer mediaPlayer;
        if (e1.getY() - e2.getY() > 500) {
            Toast.makeText(MainActivity.this, "You Swiped Down!", Toast.LENGTH_LONG).show();
            //vFlipper.setInAnimation(this, R.anim.abc_slide_in_bottom);
            //vFlipper.setOutAnimation(this, R.anim.abc_slide_out_top);
            vFlipper.showNext();
            thisMood++;
            if(thisMood>4){
                thisMood=0;
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), musicTab[thisMood]);
            mediaPlayer.start();
            return true;
        }

        // Swipe to the bottom
        if (e2.getY() - e1.getY() > 500) {
            Toast.makeText(MainActivity.this, "You Swiped Up!", Toast.LENGTH_LONG).show();
            //vFlipper.setInAnimation(this,R.anim.abc_slide_in_top);
            //vFlipper.setOutAnimation(this, R.anim.abc_slide_out_bottom);
            vFlipper.showPrevious();
            thisMood--;
            if(thisMood<0){
                thisMood=4;
            }
            mediaPlayer = MediaPlayer.create(getApplicationContext(), musicTab[thisMood]);
            mediaPlayer.start();
            return true;
        }

/*        if (e1.getX() - e2.getX() > 500) {
            Toast.makeText(MainActivity.this, "You Swiped Left!", Toast.LENGTH_LONG).show();
            return true;
        }

        if (e2.getX() - e1.getX() > 500) {
            Toast.makeText(MainActivity.this, "You Swiped Right!", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return false;
                }
*/
       return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //
        return gestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //loadData();
        System.out.println("MainActivity::onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();

        //We verify if preference is allready create
        moodPreferences = getBaseContext().getSharedPreferences(mood, MODE_PRIVATE);

        // If she is allready create ...
            if(moodPreferences.contains(mood)) {
                //... We get it
                String json = moodPreferences.getString(mood, null);
                // And we get Mood object tab
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Moods>>() {}.getType();
                moodsArrayList = gson.fromJson(json, type);
            }
        System.out.println("MainActivity::onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // We make the tab to json
         Gson gson = new Gson();
         String jsonMood = gson.toJson(moodsArrayList);

        //And we save it in a preference
        SharedPreferences.Editor moodEditor = moodPreferences.edit();
        moodEditor.putString(mood, jsonMood).apply();

        System.out.println("MainActivity::onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("MainActivity::onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("MainActivity::onDestroy");
    }

}

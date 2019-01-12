package com.application.sb.moodtacker.controller;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import com.application.sb.moodtacker.model.MyAlarmManager;
import com.google.gson.Gson;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    // I create my GESTURE DETECTOR
    private GestureDetector gestureDetector;

    //I create my VIEW FLIPPER
    private ViewFlipper vFlipper;

    // I create the activity
    private MainActivity activity;

    // Shared preferences VALUES
    private String currentMood = "currentMood";

    // The current Mood position in the ViewFlipper
    private int thisMood = 1;

    // The music tab
    private int musicTab[] = {R.raw.very_happy, R.raw.happy, R.raw.normal, R.raw.disapointed, R.raw.sad};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        System.out.println("MainActivity::onCreate");

        startAlarm();

        // References
        this.activity = this;
        ImageButton commentsButton = findViewById(R.id.commentsButton);
        ImageButton historyButton = findViewById(R.id.historyButton);
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
                final View alertDialogView = inflater.inflate(R.layout.comments, null);

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

                        // I create the Mood mood of the day
                        thisMood = vFlipper.getDisplayedChild();
                        Moods mood = new Moods(thisMood, comment);

                        // We make the Mood of the Day to json
                        Gson gson = new Gson();
                        String json = gson.toJson(mood);
                        // And we save it in a preference
                        SharedPreferences moodPreferences = getSharedPreferences(currentMood, MODE_PRIVATE);
                        SharedPreferences.Editor moodOfTheDayPrefEditor = moodPreferences.edit();
                        moodOfTheDayPrefEditor.putString(currentMood, json).apply();

                        // New Toast to confirm the save
                        Toast.makeText(activity, "Save", Toast.LENGTH_LONG).show();
                    }
                }).setView(alertDialogView);
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

    public void startAlarm() {
        // L'intention de l'alarme
        Intent alarmIntent = new Intent(getApplicationContext(), MyAlarmManager.class);

        // Le PendingIntant
        PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), 0, alarmIntent, 0);

        // Je créé l'alarme
        getBaseContext();
        AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

        // L'heure de déclanchement de l'alarme
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);                       // REMPLACER PAR 0
        calendar.set(Calendar.MINUTE, 55);                           // REMPLACER PAR 0
        calendar.set(Calendar.SECOND, 0);

        // Elle se déclenche toutes les minutes
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15, pi);
        // AlarmManager.INTERVAL_DAY

        Toast.makeText(getApplicationContext(), "L'alarme est lancée", Toast.LENGTH_LONG).show();

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
            // TODO : pourquoi cela donne une alerte ?
            //vFlipper.setInAnimation(this, R.anim.abc_slide_in_bottom);
            //vFlipper.setOutAnimation(this, R.anim.abc_slide_out_top);
            vFlipper.showNext();
            thisMood = vFlipper.getDisplayedChild();
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
            thisMood = vFlipper.getDisplayedChild();
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
        //TODO : demander pourquoi ?
        return gestureDetector.onTouchEvent(motionEvent);
    }
}
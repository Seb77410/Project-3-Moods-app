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
import android.util.DisplayMetrics;
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
import com.application.sb.moodtacker.tool.Constantes;
import com.google.gson.Gson;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    // I create my GESTURE DETECTOR
    private GestureDetector gestureDetector;

    //I create my VIEW FLIPPER
    private ViewFlipper vFlipper;

    // I create the activity
    private MainActivity activity;

    // The current Mood position in the ViewFlipper
    private int thisMood = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        System.out.println("MainActivity::onCreate");

        // We create my ViewFlipper
        vFlipper = findViewById(R.id.flipperView);
        // Default ViewFlipper view
        vFlipper.setDisplayedChild(1);

        startAlarm();

        // References
        this.activity = this;
        ImageButton commentsButton = findViewById(R.id.commentsButton);
        ImageButton historyButton = findViewById(R.id.historyButton);
        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);


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
                        SharedPreferences moodPreferences = getSharedPreferences(Constantes.CURRENT_MOOD, MODE_PRIVATE);
                        SharedPreferences.Editor moodOfTheDayPrefEditor = moodPreferences.edit();
                        moodOfTheDayPrefEditor.putString(Constantes.CURRENT_MOOD, json).apply();

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

    /**
     * We create an alarm that react with MyAlarmManager.class at midnight every day
     */
    public void startAlarm() {
        // The alarm Intent
        Intent alarmIntent = new Intent(getApplicationContext(), MyAlarmManager.class);

        // The PendingIntent
        PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), 0, alarmIntent, 0);

        // I create the alarm
        getBaseContext();
        AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

        // The hour for the alarm
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        // Alarm repeat everyday at midnight
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

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


    /**
     * We change the mood on the screen and make a sound when the user make a swipe.
     * If the user swipe to the top, we show the next ImageView in the ViewFlipper and we play next sound in musicTab.
     * If the user swipe to the bottom, we show the previous ImageView in the ViewFlipper and we play previous sound in musicTab.
     *
     * @param e1 : swipe beginning location on the screen
     * @param e2 : swipe ending location on the screen
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // We get the screen size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels ;

        // I create media player
        MediaPlayer mediaPlayer;

        // Swipe to the top
        if (e1.getY() - e2.getY() > height/10) {
            vFlipper.showNext();
            thisMood = vFlipper.getDisplayedChild();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Constantes.MUSIC_TAB[thisMood]);
            mediaPlayer.start();
            return true;
        }

        // Swipe to the bottom
        if (e2.getY() - e1.getY() > height/10) {
            vFlipper.showPrevious();
            thisMood = vFlipper.getDisplayedChild();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Constantes.MUSIC_TAB[thisMood]);
            mediaPlayer.start();
            return true;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
}
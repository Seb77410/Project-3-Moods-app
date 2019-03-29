package com.application.sb.moodtacker.controller;

import android.app.AlertDialog;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    // I create my GESTURE DETECTOR
    private GestureDetector gestureDetector;

    //I create my VIEW FLIPPER
    private ViewFlipper vFlipper;

    // I create the activity
    private MainActivity activity = this;

    // The current Mood position in the ViewFlipper
    private int thisMood = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // The alarm start
        MyAlarmManager.startAlarm(this);


        // VIEW FLIPPER
            // References
        vFlipper = findViewById(R.id.flipperView);
        SharedPreferences moodPreferences = getApplicationContext().getSharedPreferences(getString(R.string.CURRENT_MOOD), MODE_PRIVATE);
            // View flipper default view
        Moods mood;
        if(moodPreferences.contains(getString(R.string.CURRENT_MOOD))) {
            //... We get it
            String json = moodPreferences.getString(getString(R.string.CURRENT_MOOD), null);
            // And we get Mood object tab
            Gson gson = new Gson();
            Type type = new TypeToken<Moods>() {}.getType();
            mood = gson.fromJson(json, type);
            assert mood != null;
            vFlipper.setDisplayedChild(mood.getPosition());
        if (mood.getComment().length() >= 1){
            Toast.makeText(activity, mood.getComment(), Toast.LENGTH_LONG).show();}
        }else {
            vFlipper.setDisplayedChild(1);}


        // ALERT DIALOG
            // References
        ImageButton commentsButton = findViewById(R.id.commentsButton);
        ImageButton historyButton = findViewById(R.id.historyButton);
        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);

            // Comments alert dialogue
        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // New AlertDialogue
                AlertDialog.Builder myComment = new AlertDialog.Builder(activity);

                // We instantiate our layout as a view
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

                        // I create the Mood of the day
                        thisMood = vFlipper.getDisplayedChild();
                        Moods mood = new Moods(thisMood, comment);

                        // We make the Mood of the Day to json
                        Gson gson = new Gson();
                        String json = gson.toJson(mood);
                        // And we save it in a preference
                        SharedPreferences moodPreferences = getSharedPreferences(getString(R.string.CURRENT_MOOD), MODE_PRIVATE);
                        SharedPreferences.Editor moodOfTheDayPrefEditor = moodPreferences.edit();
                        moodOfTheDayPrefEditor.putString(getString(R.string.CURRENT_MOOD), json).apply();

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

    // MOOD SWIPE

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
            if(vFlipper.getDisplayedChild() != 0){
                vFlipper.showPrevious();
                thisMood = vFlipper.getDisplayedChild();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Constantes.MUSIC_TAB[thisMood]);
                mediaPlayer.start();}
            return true;
        }

        // Swipe to the bottom
        if (e2.getY() - e1.getY() > height/10) {
            if(vFlipper.getDisplayedChild() != 4){
                vFlipper.showNext();
                thisMood = vFlipper.getDisplayedChild();
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Constantes.MUSIC_TAB[thisMood]);
                mediaPlayer.start();}
                return true;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

}

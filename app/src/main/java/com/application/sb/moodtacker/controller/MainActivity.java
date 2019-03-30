package com.application.sb.moodtacker.controller;

import android.app.AlertDialog;
import android.content.Context;
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
import com.application.sb.moodtacker.AlarmManager.MyAlarmManager;
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


    /**
     *This method will set the default view of this screen
     *
     * @param context is the context
     * @param vFlipper is the view from this screen
     */
    private void setDefaultView(Context context, ViewFlipper vFlipper){
        // VIEW FLIPPER
            // References
        SharedPreferences moodPreferences = context.getSharedPreferences(getString(R.string.CURRENT_MOOD), MODE_PRIVATE);
        Moods mood;
            // View flipper default view
        if(moodPreferences.contains((context.getResources().getString(R.string.CURRENT_MOOD)))) {
                //... We get it
            String json = moodPreferences.getString(context.getResources().getString(R.string.CURRENT_MOOD), null);
                // And we get Mood object tab
            Gson gson = new Gson();
            Type type = new TypeToken<Moods>() {}.getType();
            mood = gson.fromJson(json, type);
            if (mood != null) {
                vFlipper.setDisplayedChild(mood.getPosition());
            }
            if (mood != null && mood.getComment().length() >= 1) {
                Toast.makeText(context, mood.getComment(), Toast.LENGTH_LONG).show();
            }
        }else {
            vFlipper.setDisplayedChild(1);}
    }


    /**
     *The method will create the alertDialog
     *
     * @param commentsButton is the button that will allow the user to open the alert dialog
     */
    private void setAlertDialog(final ImageButton commentsButton){

        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // New AlertDialogue
                AlertDialog.Builder myComment = new AlertDialog.Builder(activity);

                // We instantiate our layout as a view
                LayoutInflater inflater = activity.getLayoutInflater();
                final View alertDialogView = inflater.inflate(R.layout.comments, null);

                // If a comment of the day is already save, wet modify the editText hint
                setEditTextHint(activity, alertDialogView);

                // The "CANCEL" button
                myComment.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                    });

                // The "CONFIRM" button
                setConfirmButton(myComment, alertDialogView );
            }
        });
    }

    /**
     *This method will modify the edit text hint by mood comment
     *
     * @param context is the context
     * @param alertDialogView is the alertDialog
     */
    private void setEditTextHint(Context context, View alertDialogView){
        // References
        SharedPreferences moodPreferences = context.getSharedPreferences(getString(R.string.CURRENT_MOOD), MODE_PRIVATE);
        Moods mood;
        EditText commentsEdit = alertDialogView.findViewById(R.id.editText);

        if(moodPreferences.contains((context.getResources().getString(R.string.CURRENT_MOOD)))) {
            //... We get it
            String json = moodPreferences.getString(context.getResources().getString(R.string.CURRENT_MOOD), "");
            // And we get Mood object tab
            Gson gson = new Gson();
            Type type = new TypeToken<Moods>() {}.getType();
            mood = gson.fromJson(json, type);
            assert mood != null;
            String comment = mood.getComment();

            if (mood.getComment().length() >= 1) {
                commentsEdit.setHint(context.getResources().getString(R.string.COMMENT_SAVED) + "\n" + comment  );
            }
        }
    }

    /**
     * It will configure the "Confirm" button from the AlertDialog
     *
     * @param myComment is the AlertDialog Builder
     * @param alertDialogView is a view that inflate a layout. It must be final
     */
    private void setConfirmButton(AlertDialog.Builder myComment, final View alertDialogView){
        // The "CONFIRM" button
        myComment.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // I get the comment
                EditText commentsEdit = alertDialogView.findViewById(R.id.editText);
                commentsEdit.setHint("je suis un gros boulet");
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
            // View flipper default view
        setDefaultView(activity ,vFlipper);

        // ALERT DIALOG
            // References
        ImageButton commentsButton = findViewById(R.id.commentsButton);
        ImageButton historyButton = findViewById(R.id.historyButton);
        gestureDetector = new GestureDetector(MainActivity.this, MainActivity.this);
            // Set comment button
        setAlertDialog(commentsButton);

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
                setMoodOfTheDay(activity);
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Constantes.MUSIC_TAB[thisMood]);
                mediaPlayer.start();}
            return true;
        }

        // Swipe to the bottom
        if (e2.getY() - e1.getY() > height/10) {
            if(vFlipper.getDisplayedChild() != 4){
                vFlipper.showNext();
                thisMood = vFlipper.getDisplayedChild();
                setMoodOfTheDay(activity);
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Constantes.MUSIC_TAB[thisMood]);
                mediaPlayer.start();}
                return true;
        }
        return true;
    }

    /**
     * This method will update the mood of the day
     *
     * @param context is the context
     */
    private void setMoodOfTheDay(Context context){

        Moods mood = new Moods(thisMood, "") ;
        Gson gson = new Gson();
        SharedPreferences moodPreferences = context.getSharedPreferences(context.getString(R.string.CURRENT_MOOD), MODE_PRIVATE);
        String json = moodPreferences.getString(context.getString(R.string.CURRENT_MOOD), null);

        // We verify if the Mood ArrayList is already saved
        if (moodPreferences.contains(context.getString(R.string.CURRENT_MOOD))) {
            // And we get it
            Type type = new TypeToken<Moods>() {
            }.getType();
            mood = gson.fromJson(json, type);
        }

        // We modify this mood
        if(mood != null){
            mood.setPosition(thisMood);
        }

        // And we save it
        json = gson.toJson(mood);
        SharedPreferences.Editor moodPrefEditor = moodPreferences.edit();
        moodPrefEditor.putString(context.getString(R.string.CURRENT_MOOD), json).apply();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

}

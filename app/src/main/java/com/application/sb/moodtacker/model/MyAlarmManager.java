package com.application.sb.moodtacker.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.application.sb.moodtacker.model.Moods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class MyAlarmManager extends BroadcastReceiver {


    // Tableau d'objets MOOD
    public static ArrayList<Moods> moodsArrayList = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "L'alarme est déclenchée", Toast.LENGTH_LONG).show();

        // We verify if the Mood ArrayList is already saved
        // La préférence
        String moodArrayList = "moodArrayList";
        SharedPreferences arrayPreferences = context.getSharedPreferences(moodArrayList, MODE_PRIVATE);
        // And we get it
        if(arrayPreferences.contains(moodArrayList)) {
            //... We get it
            String json = arrayPreferences.getString(moodArrayList, null);
            // And we get Mood object tab
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Moods>>() {
            }.getType();
            moodsArrayList = gson.fromJson(json, type);
            }

            // We get the Mood we want to add
        String currentMood = "currentMood";
        SharedPreferences moodPreferences = context.getSharedPreferences(currentMood, MODE_PRIVATE);
        Moods mood;
        if(moodPreferences.contains(currentMood)) {
                //... We get it
                String json = moodPreferences.getString(currentMood, null);
                // And we get Mood object tab
                Gson gson = new Gson();
                Type type = new TypeToken<Moods>() {}.getType();
                mood = gson.fromJson(json, type);
                context.getSharedPreferences(currentMood, MODE_PRIVATE).edit().clear().apply();
            }else{ mood = new Moods(1, "Default Mood");}

            // We add the mood to the mood tab
            moodsArrayList.add(mood);

            // And we save it
        Gson gson = new Gson();
        String json = gson.toJson(moodsArrayList);
        SharedPreferences.Editor arrayPrefEditor = arrayPreferences.edit();
        arrayPrefEditor.putString(moodArrayList, json).apply();
    }
}


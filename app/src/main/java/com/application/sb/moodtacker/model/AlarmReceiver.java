package com.application.sb.moodtacker.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.application.sb.moodtacker.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    private  ArrayList<Moods> moodsArrayList = new ArrayList<>();


    /**
     * We update the mood arrayList by adding a new mood of the day
     *
     * @param context is the context
     */
    private void updateMoodArrayList(Context context){

        // We verify if the Mood ArrayList is already saved
        SharedPreferences arrayPreferences = context.getSharedPreferences(context.getString(R.string.MOOD_ARRAYLIST), MODE_PRIVATE);
        if(arrayPreferences.contains(context.getString(R.string.MOOD_ARRAYLIST))) {
            //... We get it
            String json = arrayPreferences.getString(context.getString(R.string.MOOD_ARRAYLIST), null);
            // And we get Mood object tab
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Moods>>() {
            }.getType();
            moodsArrayList = gson.fromJson(json, type);
        }

        // We get the Mood we want to add
        SharedPreferences moodPreferences = context.getSharedPreferences(context.getString(R.string.CURRENT_MOOD), MODE_PRIVATE);
        Moods mood;
        if(moodPreferences.contains(context.getString(R.string.CURRENT_MOOD))) {
            //... We get it
            String json = moodPreferences.getString(context.getString(R.string.CURRENT_MOOD), null);
            // And we get Mood object tab
            Gson gson = new Gson();
            Type type = new TypeToken<Moods>() {}.getType();
            mood = gson.fromJson(json, type);
            context.getSharedPreferences(context.getString(R.string.CURRENT_MOOD), MODE_PRIVATE).edit().clear().apply();
        }else{mood = new Moods(1, context.getString(R.string.DEFAULT_COMMENT));}

        // We add the mood to the mood tab
        moodsArrayList.add(mood);

        // And we save it
        Gson gson = new Gson();
        String json = gson.toJson(moodsArrayList);
        SharedPreferences.Editor arrayPrefEditor = arrayPreferences.edit();
        arrayPrefEditor.putString(context.getString(R.string.MOOD_ARRAYLIST), json).apply();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        updateMoodArrayList(context);
    }
}


package com.application.sb.moodtacker.controller;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.sb.moodtacker.R;
import com.application.sb.moodtacker.model.Moods;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    // Date tab
    private String dateTab[] = {"One day ago", "Two days ago", "Three days ago", "Four days ago", "Five days ago", "Six days ago", "Seven days ago"};

    private ArrayList<Moods> moodsArrayList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Number of Mood
        int moodNb = 5;

        // Screen size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // Sreen size values
        int width = metrics.widthPixels;
        int height = metrics.heightPixels ;
        // TextView size
        int sizeTab[] = {width, (width / moodNb) * (moodNb - 1), (width / moodNb) * (moodNb - 2), (width / moodNb) * (moodNb - 3), width / moodNb};

        // View
        LinearLayout historyActivity = findViewById(R.id.historyLayout);
        // Layout parameters
        ViewGroup.LayoutParams textViewParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // Background color
        historyActivity.setBackgroundColor(getResources().getColor(R.color.grey));

        // We get the Mood ArrayList Saved
        // La préférence
        String moodArrayList = "moodArrayList";
        SharedPreferences moodPreferences = getBaseContext().getSharedPreferences(moodArrayList, MODE_PRIVATE);
        if(moodPreferences.contains(moodArrayList)) {
            //... We get it
            String json = moodPreferences.getString(moodArrayList, null);
            // And we get Mood object tab
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Moods>>() {}.getType();
            moodsArrayList = gson.fromJson(json, type);
            }

        // Dynamic View
        if (moodsArrayList.size() > 0) {  // Si le tableau Mood comprend au moins 1 element
            int i = 0;
            // Number of history
            int historyNb = 7;
            for(int j = moodsArrayList.size()-1 ; j > (moodsArrayList.size() - 1) - historyNb; j--){

                if(j >= 0){
                // New Relative Layout
                RelativeLayout relativeLayout = new RelativeLayout(this);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(sizeTab[moodsArrayList.get(j).getPosition()], height / historyNb);
                relativeLayout.setLayoutParams(layoutParams);
                historyActivity.addView(relativeLayout);

                // New TextView for the relative layout
                TextView textView = new TextView(this);
                textView.setLayoutParams(textViewParams);
                textView.setText(dateTab[i]);
                textView.setBackgroundColor(getResources().getColor(Moods.colorsTab[moodsArrayList.get(j).getPosition()]));
                relativeLayout.addView(textView);

                //New imageButton for the relative layout
                if (moodsArrayList.get(j).getComment().length() >= 1){
                    ImageButton imageButton = new ImageButton(this);
                    imageButton.setImageResource(R.mipmap.ic_comment_black_48px);
                    RelativeLayout.LayoutParams imageButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    imageButton.setLayoutParams(imageButtonParams);
                    imageButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    imageButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
                    imageButton.setBackgroundColor(getResources().getColor(R.color.trans));
                    imageButton.setPadding(0, 0, 20, 0);
                    final int finalX = j;
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(HistoryActivity.this, moodsArrayList.get(finalX).getComment(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    relativeLayout.addView(imageButton);
                }
                i++;}
            }
        }
    }
}

package com.application.sb.moodtacker.controller;

import android.app.ActionBar;
import android.content.Context;
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

import com.application.sb.moodtacker.R;
import com.application.sb.moodtacker.model.Moods;
import com.application.sb.moodtacker.tool.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.widget.Toast.*;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<Moods> moodsArrayList = new ArrayList<>();
    private Context applicationContext = this;
    private RelativeLayout relativeLayout;
    private TextView textView;
    private ImageButton imageButton;

    /**
     *  Modify the Relative Layout with Mood saved values
     *
     * @param context Is the context
     * @param tab Is a tab that contains every possible layout width
     * @param arrayList Is the array that contains already saved Mood
     * @param j Is the Mood position in the arrayList
     * @param screenSize is the height screen size
     */
    private void setRelativeLayout(Context context, int[] tab, ArrayList<Moods> arrayList, int j, int screenSize){
        // New Relative Layout
        RelativeLayout layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(tab[arrayList.get(j).getPosition()], screenSize / Constantes.HISTORY_NB);
        layout.setLayoutParams(layoutParams);
        relativeLayout = layout;
    }

    /**
     *Modify the TextView with Mood saved values
     *
     * @param context Is the context
     * @param layoutPosition Is the RelativeLayout position in the LinearLayout
     * @param arrayList Is the array that contains already saved Mood
     * @param j Is the Mood position in the arrayList
     */
    private void setTextView(Context context, int layoutPosition, ArrayList<Moods> arrayList, int j){
        TextView myTextView = new TextView(context);
        ViewGroup.LayoutParams params = new  ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myTextView.setLayoutParams(params);
        myTextView.setText(Constantes.DATE_TAB[layoutPosition]);
        myTextView.setBackgroundColor(getResources().getColor(Constantes.COLOR_TAB[arrayList.get(j).getPosition()]));
        textView = myTextView;
    }

    /**
     * Modify the ImageButton with Mood saved values
     *
     * @param context Is the context
     * @param arrayList Is the array that contains already saved Mood
     * @param j Is the Mood position in the arrayList
     */
    private void setImageButton(final Context context, final ArrayList<Moods> arrayList, int j){
        ImageButton myImageButton = new ImageButton(context);
        myImageButton.setImageResource(R.mipmap.ic_comment_black_48px);
        RelativeLayout.LayoutParams imageButtonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        myImageButton.setLayoutParams(imageButtonParams);
        imageButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        myImageButton.setBackgroundColor(getResources().getColor(R.color.trans));
        myImageButton.setPadding(0, 0, 20, 0);
        final int finalJ = j;
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeText(context, arrayList.get(finalJ).getComment(), LENGTH_SHORT).show();
            }
        });
        imageButton = myImageButton;
    }

    /**
     *  We add a new RelativeLayout for every mood saved.
     *  We add a TextView to the RelativeLayout. The TextView specifies how many days ago the mood was saved.
     *  If the mood contains a comment, we display the button that allows to access it with a Toast.
     */
    private void makeHistory(final ArrayList<Moods> arrayList, int tab[], int screenSize, LinearLayout linearLayout, final Context context){
        if (arrayList.size() > 0) {  // Si le tableau Mood comprend au moins 1 element
            int layoutPosition = 0;
            for (int j = arrayList.size() - 1; j > (arrayList.size() - 1) - Constantes.HISTORY_NB; j--)
                if (j >= 0) {

                    // We set and add the RelativeLayout
                    setRelativeLayout(context,tab,arrayList,j,screenSize);
                    linearLayout.addView(relativeLayout);

                    // We set and add the TextView
                    setTextView(context, layoutPosition, moodsArrayList, j);
                    relativeLayout.addView(textView);

                    // If the mood contains a comment, we display the button that allows to access it with a Toast.
                    if (arrayList.get(j).getComment().length() >= 1) {
                        setImageButton(context, arrayList, j);
                        relativeLayout.addView(imageButton);
                    }
                    layoutPosition++;
                }
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        /*
         *  We get the screen size. And we create a tab that contains every history size
         */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // Screen size values
        int width = metrics.widthPixels;
        int height = metrics.heightPixels ;
        // TextView size
        int sizeTab[] = {width, (width / Constantes.MOOD_NB) * (Constantes.MOOD_NB - 1), (width / Constantes.MOOD_NB) * (Constantes.MOOD_NB - 2), (width / Constantes.MOOD_NB) * (Constantes.MOOD_NB - 3), width / Constantes.MOOD_NB};

        /*
         * We create a LinearLayout that will contains every history we want show
         */
        LinearLayout historyActivity = findViewById(R.id.historyLayout);
        historyActivity.setBackgroundColor(getResources().getColor(R.color.grey));

        /*
         *  We verify if at least one mood is already saved. If it is, we get it
         */
        SharedPreferences moodPreferences = getBaseContext().getSharedPreferences(Constantes.MOOD_ARRAYLIST, MODE_PRIVATE);
        if(moodPreferences.contains(Constantes.MOOD_ARRAYLIST)) {
            //... We get it
            String json = moodPreferences.getString(Constantes.MOOD_ARRAYLIST, null);
            // And we get Mood object tab
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Moods>>() {}.getType();
            moodsArrayList = gson.fromJson(json, type);
            }

        /*
         * We create and add views for every mood saved 7 days ago
         */
        if (moodsArrayList != null) {
            makeHistory(moodsArrayList, sizeTab, height, historyActivity, applicationContext);
        }
    }

}

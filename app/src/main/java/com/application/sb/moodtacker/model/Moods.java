package com.application.sb.moodtacker.model;

import com.application.sb.moodtacker.R;

public class Moods {

    // Values
    private int image;
    private String comment;
    private int position;

        //Tableau de couleurs
    public static int colorsTab[] = {R.color.banana_yellow, R.color.light_sage, R.color.cornflower_blue_65, R.color.warm_grey, R.color.faded_red};

    public int getImage() {return image;}
    public void setImage(int image) {this.image = image;}

    public String getComment() {return comment;}

    public int getPosition() {return position;}

    public Moods(int position, String comment) {
        this.position = position;
        this.comment = comment;
    }

}
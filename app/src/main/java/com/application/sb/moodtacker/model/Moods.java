package com.application.sb.moodtacker.model;

public class Moods {

    // Values
    private int image;
    private String comment;
    private int position;


    public int getImage() {return image;}
    public void setImage(int image) {this.image = image;}

    public String getComment() {return comment;}

    public int getPosition() {return position;}

    public Moods(int position, String comment) {
        this.position = position;
        this.comment = comment;
    }

}
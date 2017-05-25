package com.fyusuf.tvrehberi;


import android.graphics.Bitmap;

public class Program {
    private String txt;
    private String time;
    private Bitmap image;
    private Bitmap logo;
    public Program(String txt,String time,Bitmap image,Bitmap logo){
        this.txt = txt;
        this.time = time;
        this.image = image;
        this.logo = logo;
    }

    public String getTxt(){
        return this.txt;
    }


    public Bitmap getImage(){
        return this.image;
    }

    public Bitmap getLogo(){
        return this.logo;
    }

    public String getTime(){
        return this.time;
    }
}

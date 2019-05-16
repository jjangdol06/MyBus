package com.cookandroid.mybus;

import android.graphics.drawable.Drawable;

public class BusItem {
    private Drawable iconDrawable ;
    private String gpsX ;
    private String gpsY ;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setGpsX(String title) {
        gpsX = title ;
    }
    public void setGpsY(String desc) {
        gpsY = desc ;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getGpsX() {
        return this.gpsX ;
    }
    public String getGpsY() {
        return this.gpsY ;
    }
}

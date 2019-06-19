package com.example.myexchange;

import android.graphics.drawable.Drawable;

public class CountryItem {

    private Drawable iconDrawable ;
    private String Name;
    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setName(String Name) {
        this.Name=Name ;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getName() {
        return this.Name ;
    }

}

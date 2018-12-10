package com.romerock.apps.utilities.cryptocurrencyconverter.model;

/**
 * Created by Ebricko on 16/12/2016.
 */

public class ItemSettings {
    public String tittle;

    public String getTitle() {
        return tittle;
    }

    public void setTitle(String title) {
        this.tittle = title;
    }



    public ItemSettings(String title, boolean selected){

        this.tittle = title;
        this.selected=selected;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;





    
}

package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import java.util.Comparator;

/**
 * Created by Ebricko on 04/04/2018.
 */
public class HourModel {
    private int hour;
    private String displayHour;

    public HourModel(int hour, String displayHour) {
        this.hour = hour;
        this.displayHour = displayHour;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getDisplayHour() {
        return displayHour;
    }

    public void setDisplayHour(String displayHour) {
        this.displayHour = displayHour;
    }

    public static Comparator<HourModel> ItemByHour = new Comparator<HourModel>() {
        public int compare(HourModel s1, HourModel s2) {
            int ItemSettingsName1 = s1.getHour();
            int ItemSettingsName2 = s2.getHour();
            return Integer.compare(ItemSettingsName1,ItemSettingsName2);
        }};
}

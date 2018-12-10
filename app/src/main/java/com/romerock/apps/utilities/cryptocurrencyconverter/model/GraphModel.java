package com.romerock.apps.utilities.cryptocurrencyconverter.model;

/**
 * Created by Ebricko on 28/03/2018.
 */

public class GraphModel {
    String date;
    double value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public GraphModel(String date, double value) {
        this.date = date;
        this.value = value;
    }
}

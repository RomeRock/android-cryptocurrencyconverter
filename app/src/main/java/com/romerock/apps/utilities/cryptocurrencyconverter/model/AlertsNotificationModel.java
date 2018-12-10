package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ebricko on 06/04/2018.
 */
public class AlertsNotificationModel implements Serializable {
    private double high;
    private boolean high_active;
    private double low;
    private boolean low_active;

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public boolean isHigh_active() {
        return high_active;
    }

    public void setHigh_active(boolean high_active) {
        this.high_active = high_active;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public boolean isLow_active() {
        return low_active;
    }

    public void setLow_active(boolean low_active) {
        this.low_active = low_active;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("high", high);
        result.put("high_active", high_active);
        result.put("low", low);
        result.put("low_active", low_active);
        return result;
    }
}

package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ebricko on 29/01/2018.
 */

public class CurrencyApplicationHelper extends Application {
    public static SharedPreferences preferences;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        CurrencyApplicationHelper.context = getApplicationContext();
        preferences = getSharedPreferences(getPackageName() + "_POLL_ROMEROCK", MODE_PRIVATE);
    }

    public static Context getAppContext() {
        return CurrencyApplicationHelper.context;
    }
}

package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.romerock.apps.utilities.cryptocurrencyconverter.R;

import java.io.Serializable;

import me.leolin.shortcutbadger.ShortcutBadger;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Ebricko on 19/12/2017.
 */

public class  PushNotificationModel implements Serializable {
    private static String Title;
    private static String IdPoll;
    private static Context context;
    private static String description;
    private static String participant="-mobile-participant";
    private static String originator="-mobile-originator";

    public PushNotificationModel() {
    }

    public static String getParticipant() {
        return participant;
    }

    public static String getOriginator() {
        return originator;
    }

    public static String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public static String getIdPoll() {
        return IdPoll;
    }

    public void setIdPoll(String idPoll) {
        IdPoll = idPoll;
    }

    private static Context getContext() {
        return context;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        PushNotificationModel.description = description;
    }

    public static void CleanBadges(Context context){
        SharedPreferences sharedPrefs;
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
        SharedPreferences.Editor ed;
        ed = sharedPrefs.edit();
        ed.putInt(context.getString(R.string.badge_settings),0);
        ed.commit();
        ShortcutBadger.removeCount(context);  //remove badges
    }
}

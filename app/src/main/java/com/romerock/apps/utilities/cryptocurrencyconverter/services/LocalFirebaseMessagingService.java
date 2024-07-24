package com.romerock.apps.utilities.cryptocurrencyconverter.services;

import static com.applovin.sdk.AppLovinSdkUtils.runOnUiThread;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.CurrencyApplicationHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.PushNotificationModel;

import me.leolin.shortcutbadger.ShortcutBadger;

//import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;


public class LocalFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "GCM";
    PushNotificationModel pushNotificationModel;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor ed;
    private Context context;

    public LocalFirebaseMessagingService() {
        Log.d("Message noion service","message");  // dont erase this line
        context = CurrencyApplicationHelper.getAppContext();
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
        int badgeCount = sharedPrefs.getInt(context.getString(R.string.badge_settings),0);
        badgeCount++;
        ed = sharedPrefs.edit();
        ed.putInt(context.getString(R.string.badge_settings),badgeCount);
        ed.commit();
        ShortcutBadger.applyCount(context, badgeCount);
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification().getBody() != null) {
            if (remoteMessage.getData().containsKey("symbol")) {
                try {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, remoteMessage.getNotification().getTitle().toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (Exception e){
                    Log.d("push","is: "+e.getMessage());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }
}
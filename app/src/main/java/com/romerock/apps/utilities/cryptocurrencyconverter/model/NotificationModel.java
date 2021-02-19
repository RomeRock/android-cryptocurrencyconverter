package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CheckUDIDListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.NotificationsListListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ebricko on 05/04/2018.
 */
public class NotificationModel implements Serializable {

    private String key;
    private AlertsNotificationModel alertsNotificationModel;
    private String[] hours;

    public NotificationModel(String key, AlertsNotificationModel alertsNotificationModel, String[] hours) {
        this.key = key;
        this.alertsNotificationModel = alertsNotificationModel;
        this.hours = hours;
    }

    public static void addCurrencyNotification(final FirebaseHelper firebaseHelper, final String UDID, final String key, final String isFreeOrPremium, final NotificationModel notificationModel) {
        final String path = String.format(firebaseHelper.getNOTIFICATION_PATH(), isFreeOrPremium, UDID) + "/" + key;
        if(UDID!=null) {
            if (!UDID.isEmpty()) {

       /* firebaseHelper.getDataReference(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                firebaseHelper.getDatabase().getReference().child(path).setValue(notificationModel.toMap());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
                firebaseHelper.getDatabase().getReference().child(path).updateChildren(notificationModel.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UserUdId.setDevCancelRecall(firebaseHelper, UDID, isFreeOrPremium);
                    }
                });
            }
        }
    }

    public static void getMyNotifications(final Context context, final FirebaseHelper firebaseHelper, final String UDID, final String isFreeOrPremium, final NotificationsListListener notificationsListListener) {
        Query myTopPostsQuery = firebaseHelper.getDataReference(String.format(firebaseHelper.getNOTIFICATION_PATH(), isFreeOrPremium, UDID)).getRef();
        myTopPostsQuery.keepSynced(true);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putInt(context.getString(R.string.globalCount), 0);
                ed.commit();
                Map<String, JSONObject> valueSnap;
                AlertsNotificationModel alertsNotificationModel = new AlertsNotificationModel();
                List<NotificationModel> notificationModelList = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {
                    valueSnap = (Map<String, JSONObject>) dataSnapshot.getValue();
                    for (Map.Entry<String, JSONObject> entry : valueSnap.entrySet()) {
                        String hours = null;
                        try {
                            if (new JSONObject((Map) entry.getValue()).has("hours")) {
                                hours = String.valueOf(new JSONObject((Map) entry.getValue()).getJSONArray("hours"))
                                        .replace("\"", "")
                                        .replace("[", "")
                                        .replace("]", "");
                            }
                            if (new JSONObject((Map) entry.getValue()).has("alerts")) {
                                Gson gson = new GsonBuilder().create();
                                alertsNotificationModel = gson.fromJson(String.valueOf(new JSONObject((Map) entry.getValue()).get("alerts")), AlertsNotificationModel.class);
                                if (alertsNotificationModel.isHigh_active())
                                    NotificationModel.checkGlobal(context, true);
                                if (alertsNotificationModel.isLow_active())
                                    NotificationModel.checkGlobal(context, true);
                            } else {
                                alertsNotificationModel = null;
                            }
                            NotificationModel notificationModel;
                            if (hours != null) {
                                notificationModel = new NotificationModel(entry.getKey(), alertsNotificationModel, hours.split(","));
                                for (String item : hours.split(",")) {
                                    NotificationModel.checkGlobal(context, true);
                                }
                            } else
                                notificationModel = new NotificationModel(entry.getKey(), alertsNotificationModel, null);
                            notificationModelList.add(notificationModel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    notificationsListListener.getNotificationList(notificationModelList);
                } else {
                    notificationModelList.clear();
                    notificationsListListener.getNotificationList(notificationModelList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", "");
            }
        });
    }

    public static void removeNotification(final FirebaseHelper firebaseHelper, final String UDID, final String isFreeOrPremium, String child) {
        firebaseHelper.getDataReference(String.format(firebaseHelper.getNOTIFICATION_PATH(), isFreeOrPremium, UDID)).getRef().child(child).removeValue();
    }

    public static boolean checkGlobal(Context context, boolean incrementOrDecrement) {
        //globalCount
        if (SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context).compareTo(UserUdId.getFREE()) == 0) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
            int globalCount = sharedPreferences.getInt(context.getString(R.string.globalCount), 0);
            SharedPreferences.Editor ed = sharedPreferences.edit();
            if (incrementOrDecrement) {
                if (globalCount < 2) {
                    globalCount++;
                    ed.putInt(context.getString(R.string.globalCount), globalCount);
                    ed.commit();
                    return true;
                } else {
                    return false;
                }
            } else {
                globalCount--;
                ed.putInt(context.getString(R.string.globalCount), globalCount);
                ed.commit();
                return true;
            }
        } else {
            return true;
        }
    }

    public static void saveNotification(final Context context, final FirebaseHelper firebaseHelper, String[] splitCurrency) {
        boolean processDefaulNotification = false;
        final SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPrefs.edit();
        String key = "BTC-";

        for (int i = 0; i < splitCurrency.length; i++) {
            if (splitCurrency[i].compareTo("BTC") != 0 && !processDefaulNotification) {
                processDefaulNotification = true;
                key += splitCurrency[i];
            }
        }
        if (processDefaulNotification) {
            ed.putBoolean(context.getString(R.string.preferences_defaultCurrencies_first_time), false);
            ed.commit();
            String UDID = "";

            try {
                if (SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context).compareTo(UserUdId.getFREE()) == 0) {
                    UDID = CipherAES.decipher(sharedPrefs.getString(context.getString(R.string.udidAndroid), ""));
                } else {
                    UDID = CipherAES.decipher(sharedPrefs.getString(context.getString(R.string.purchaseOrder), ""));
                }
            } catch (Exception e) {
                Log.d("error", e.getMessage());
            }
            if (!UDID.isEmpty()) {
                final UserUdId userUdId = new UserUdId(sharedPrefs.getString(context.getResources().getString(R.string.language_settings), ""),
                        "active", "",
                        SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context),
                        Utilities.getCurrentTimeStamp());

                final String finalUDID = UDID;
                final String finalKey = key;
                final String finalUDID1 = UDID;
                userUdId.CheckFreeUDIDNode(UDID, firebaseHelper, new CheckUDIDListener() {
                    @Override
                    public void checkUDIDFromFirebase(boolean haveChild) {
                        try {
                            String fcm = FirebaseInstanceId.getInstance().getToken();
                            SharedPreferences.Editor ed = sharedPrefs.edit();
                                if (fcm != null)
                                    try {
                                        userUdId.checkFreeFMCTocken(firebaseHelper, finalUDID, fcm, userUdId.getCreatedtstamp(), SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context), false);
                                        ed.putString(context.getString(R.string.fcmUser), CipherAES.cipher(fcm));
                                        ed.commit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (finalKey.length() > 4) {
                            int hour = Utilities.roundHour();
                            String[] hoursToSave = {String.valueOf(hour)};
                            NotificationModel notificationModel = new NotificationModel(finalKey, null, hoursToSave);
                            NotificationModel.addCurrencyNotification(firebaseHelper, finalUDID1, finalKey, SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context), notificationModel);
                        }
                    }

                    @Override
                    public void checkPremiumState(boolean status) {

                    }
                }, userUdId, SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context), context);
            }

        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AlertsNotificationModel getAlertsNotification() {
        return alertsNotificationModel;
    }

    public void setAlertsNotification(AlertsNotificationModel alertsNotificationModel) {
        this.alertsNotificationModel = alertsNotificationModel;
    }

    public String[] getHours() {
        return hours;
    }

    public void setHours(String[] hours) {
        this.hours = hours;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        if (alertsNotificationModel != null)
            result.put("alerts", alertsNotificationModel.toMap());
        result.put("hours", Arrays.asList(hours));
        return result;
    }
}

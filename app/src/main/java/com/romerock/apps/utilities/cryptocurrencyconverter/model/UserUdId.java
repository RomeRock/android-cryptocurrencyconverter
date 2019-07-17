package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.BuildConfig;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CheckUDIDListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Ebricko on 05/04/2018.
 */
public class UserUdId {

    private final static String PREMIUM = "premium";
    private final static String FREE = "free";
    private final static String ENVIREMENT = null;
    private final static int DEV_CANCEL_RECALL = 0;
    private final String os = "android";
    private final String version = BuildConfig.VERSION_NAME;
    private double createdtstamp;
    private double expirationtstamp;
    private String expiredate;
    //  private fcmtoken;
    private String language;
    private NotificationModel notifications;
    private String state;
    private String timezone;
    private CheckUDIDListener checkUDIDListener;
    private String isFreeOrPremium;
    private String enviroment;
    private TransactionModel transaction;
    private HashMap<String, Object> udids;

    public UserUdId(String language, String state, String timezone, String isFreeOrPremium, long CreatedStamp) {
        this.createdtstamp = CreatedStamp;
        this.language = language;
        this.state = state;
        this.timezone = TimeZone.getDefault().getID();
        this.isFreeOrPremium = isFreeOrPremium;
        if (isFreeOrPremium.toString().compareTo(PREMIUM) == 0) {
            enviroment = ENVIREMENT;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 3);
            java.util.Date dt = cal.getTime();
            this.expirationtstamp = cal.getTime().getTime() / 1000;
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            String formatted = format1.format(cal.getTime());
            this.expiredate = formatted;
        }
    }

    public static String getPREMIUM() {
        return PREMIUM;
    }

    public static String getFREE() {
        return FREE;
    }

    public static void CheckFreeUDIDNode(final String UDID, final FirebaseHelper firebaseHelper, final CheckUDIDListener checkUDIDListener, final UserUdId userUdId, final String isFreeOrPremium, final Context context) {
        if (isFreeOrPremium.compareTo(UserUdId.getPREMIUM()) == 0) {
            HashMap<String, Object> udidPremium = new HashMap<>();
            try {
                SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
                String idDevice = CipherAES.decipher(sharedPrefs.getString(context.getResources().getString(R.string.udidAndroid), ""));
                udidPremium.put(idDevice, userUdId.getCreatedtstamp());
                userUdId.setUdids(udidPremium);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (UDID != null)
            if (!UDID.isEmpty()) {
                firebaseHelper.getDataReference(String.format(firebaseHelper.getUDID_PATH(), isFreeOrPremium)).child(UDID).updateChildren(userUdId.toMap());
                checkUDIDListener.checkUDIDFromFirebase(false);
            }

    }

    public static void copyNotificationsToPremium(final FirebaseHelper firebaseHelper, final String UDID, final String idOrder) {
        firebaseHelper.getDataReference(String.format(firebaseHelper.getUDID_PATH(), UserUdId.getFREE()) + UDID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("notifications")) {
                    firebaseHelper.copyFirebaseRecord(firebaseHelper.getDataReference(String.format(firebaseHelper.getNOTIFICATION_PATH(), UserUdId.getFREE(), UDID)),
                            firebaseHelper.getDataReference(String.format(firebaseHelper.getNOTIFICATION_PATH(), UserUdId.getPREMIUM(), idOrder)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setToPremiumTransaction(final FirebaseHelper firebaseHelper, final String UDID, final String idOrder, final TransactionModel transaction) {
        firebaseHelper.getDataReference(String.format(firebaseHelper.getTRANSACTION_PATH(), UDID)).setValue(transaction.toMap());
    }

    public static void setDevCancelRecall(final FirebaseHelper firebaseHelper, final String UDID, final String isFreeOrPremium) {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    firebaseHelper.getDataReference(String.format(firebaseHelper.getUDID_PATH(), isFreeOrPremium) + UDID).child("dev_cancel_recall").getRef().setValue("0");
                }
            }
        };
        splashTread.start();
    }

    public static void checkPremiumState(Context context, final CheckUDIDListener checkUDIDListener) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
        String isFreeOrPremium = null;
        String UDID;
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
        try {
            isFreeOrPremium = CipherAES.decipher(sharedPrefs.getString(context.getResources().getString(R.string.purchaseAndroid), ""));
            if (isFreeOrPremium.compareTo(UserUdId.getPREMIUM()) == 0) {
                UDID = CipherAES.decipher(sharedPrefs.getString(context.getString(R.string.purchaseOrder), ""));
                firebaseHelper.getDataReference(String.format(firebaseHelper.getUDID_PATH(), isFreeOrPremium) + UDID).child("state").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Log.d("state User: ", "");
                        String state = (String) snapshot.getValue();
                        if (state != null)
                            if (state.compareTo("active") == 0)
                                checkUDIDListener.checkPremiumState(true);
                            else
                                checkUDIDListener.checkPremiumState(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        checkUDIDListener.checkPremiumState(false);
                    }
                });
            } else {
                checkUDIDListener.checkPremiumState(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Object> getUdids() {
        return udids;
    }

    public void setUdids(HashMap<String, Object> udids) {
        this.udids = udids;
    }

    public void setEnviroment(String enviroment) {
        this.enviroment = enviroment;
    }

    public TransactionModel getTransactionModel() {
        return transaction;
    }

    public void setTransactionModel(TransactionModel transactionModel) {
        this.transaction = transactionModel;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("createdtstamp", createdtstamp);
        result.put("expirationtstamp", expirationtstamp);
        result.put("expiredate", expiredate);
        result.put("language", language);
        result.put("os", os);
        result.put("version", version);
        result.put("state", state);
        result.put("timezone", timezone);
        result.put("enviroment", enviroment);
        if (transaction != null)
            result.put("transaction", transaction);
        // result.put("dev_cancel_recall", DEV_CANCEL_RECALL);
        result.put("udids", udids);
        return result;
    }

    public double getCreatedtstamp() {
        return createdtstamp;
    }

    public void setCreatedtstamp(double createdtstamp) {
        this.createdtstamp = createdtstamp;
    }

    public double getExpirationtstamp() {
        return expirationtstamp;
    }

    public void setExpirationtstamp(double expirationtstamp) {
        this.expirationtstamp = expirationtstamp;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(String expiredate) {
        this.expiredate = expiredate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public NotificationModel getNotifications() {
        return notifications;
    }

    public void setNotifications(NotificationModel notifications) {
        this.notifications = notifications;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void checkFreeFMCTocken(final FirebaseHelper firebaseHelper, final String UDID, final String FMC, final double createdtstamp, final String isFreeOrPremium, final boolean process) {
       /* firebaseHelper.getDataReference(String.format(firebaseHelper.getFMC_PATH(), isFreeOrPremium, UDID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.hasChild(FMC)) {
                        // if not have it, insert
                        Map<String, Object> data = new HashMap<>();
                        data.put(FMC, createdtstamp);
                        firebaseHelper.getDataReference(String.format(firebaseHelper.getFMC_PATH(), isFreeOrPremium, UDID)).child(FMC).setValue(createdtstamp);  // ADD TOKEN
                        //firebaseHelper.getDataReference().setValue(data);   //REPLACE TOKEN
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        Map<String, Object> data = new HashMap<>();
        data.put(FMC, createdtstamp);
        firebaseHelper.getDataReference(String.format(firebaseHelper.getFMC_PATH(), isFreeOrPremium, UDID)).child(FMC).setValue(createdtstamp).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // if(process)
                //  UserUdId.setDevCancelRecall(firebaseHelper, UDID, isFreeOrPremium);
            }
        });

    }
}

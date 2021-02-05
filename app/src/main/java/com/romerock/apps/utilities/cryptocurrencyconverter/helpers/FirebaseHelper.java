package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;

/**
 * Created by Ebricko on 27/03/2017.
 */

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private DatabaseReference dataReference;
    private FirebaseDatabase database ;

    private String UPDATE_TIME="lastest";
    private String CURRENCIES_LIBRARY="library";
    private String NOTIFICATION_PATH="notifications/%s/%s/notifications";
    private String FMC_PATH="notifications/%s/%s/fcmtokens";
    private String UDID_PATH="notifications/%s/";
    private String TRANSACTION_PATH="notifications/premium/%s/transaction";
    private String PREMIUM_PATH="notifications/premium/";
    private boolean persistenceActive =false;


    public String getPREMIUM_PATH() {
        return PREMIUM_PATH;
    }

    public String getTRANSACTION_PATH() {
        return TRANSACTION_PATH;
    }

    public DatabaseReference getDataReference() {
        return dataReference;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public String getCURRENCIES_LIBRARY() {
        return CURRENCIES_LIBRARY;
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public boolean isPersistenceActive() {
        return persistenceActive;
    }

    public FirebaseHelper(){
        if (database == null) {
            database = FirebaseDatabase.getInstance();

            if(!persistenceActive&& SingletonInAppBilling.getFirebaseHelper()==null){
                persistenceActive=true;
                try {
                    database.setPersistenceEnabled(true);
                }catch (Exception e){
                    Log.d("Error firebase",e.getMessage());
                }
                SingletonInAppBilling.Instance().setFirebaseDatabase(database);
            }
        }
    }

    public String getUPDATE_TIME() {
        return UPDATE_TIME;
    }

    public DatabaseReference getDataReference(String path) {
        return dataReference = database.getReference(path);
    }

    public String getNOTIFICATION_PATH() {
        return NOTIFICATION_PATH;
    }

    public String getFMC_PATH() {
        return FMC_PATH;
    }

    public String getUDID_PATH() {
        return UDID_PATH;
    }

    public static void copyFirebaseRecord(final DatabaseReference fromPath, final DatabaseReference toPath)
    {
        fromPath.keepSynced(true);
        fromPath.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener()
                {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        fromPath.setValue(null);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Copy failed");
            }
        });
    }

    public static void subscribeUnsubscribeTopic(Context context, boolean subscribe){
        SharedPreferences sharedPreferences = context. getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
        final String topic="general_"+sharedPreferences.getString(context.getString(R.string.language_settings),"");
        if(subscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("FIREBASE Subscribe","Subscrib topic: "+topic+" is "+task.isSuccessful());
                        }
                    });

        }else{
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("FIREBASE UnSubscribe","UnSubscribe topic: "+topic+" is "+task.isSuccessful());
                }
            });
        }
    }
}

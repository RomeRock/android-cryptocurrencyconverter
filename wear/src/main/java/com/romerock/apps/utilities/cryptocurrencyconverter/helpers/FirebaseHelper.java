package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private String PREMIUM_PATH="notifications/premium/";
    private final String MAIN_PATH = "/";

    public String getMAIN_PATH() {
        return MAIN_PATH;
    }

    public String getPREMIUM_PATH() {
        return PREMIUM_PATH;
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

    public FirebaseHelper(){
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
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



}

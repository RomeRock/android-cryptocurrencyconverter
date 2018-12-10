package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.PurchaseDialog;

/**
 * Created by Ebricko on 20/06/2017.
 */

public class PurchaseModel {
    private String orderID;
    private String nameProduct;
    private long purchaseTime;
    private String itemType;
    private String SKU;
    private String paid;

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getItemType() {
        return itemType;
    }

    public String getOrderID() {

        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public static void setFreeNode(String pathCustom, FirebaseHelper firebaseHelper, DatabaseReference mDatabase) {
       /* Map<String, String> getCurrentUnixTime = new HashMap<>();
        getCurrentUnixTime.put("retrieved_utime",String.valueOf(Utilities.getCurrentUnixTime()));
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + firebaseHelper.getFEATURE_PATH()+ "/" + pathCustom +"/", getCurrentUnixTime);
        mDatabase.updateChildren(childUpdates);*/
    }

    public static void haveThisOrder(FirebaseHelper firebaseHelper, final PurchaseDialog purchaseDialog, final String orderID) {
        Query myTopPostsQuery = firebaseHelper.getDataReference(firebaseHelper.getPREMIUM_PATH()).getRef();
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                purchaseDialog.PurchaseDialogFinish(dataSnapshot.hasChild(orderID));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                purchaseDialog.PurchaseDialogFinish(false);
            }
        });
    }
}

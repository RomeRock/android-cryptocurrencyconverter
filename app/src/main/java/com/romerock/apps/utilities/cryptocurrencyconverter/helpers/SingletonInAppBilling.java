package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.firebase.database.FirebaseDatabase;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.UserUdId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ebricko on 30/01/2017.
 */

public class SingletonInAppBilling {
    private static SingletonInAppBilling instance;
    private static String SKU_PACKAGE_PRO1 = "com.romerock.apps.utilities.cryptocurrencyconverter_proversion_1";
    private static String SKU_PACKAGE_PRO2 = "com.romerock.apps.utilities.cryptocurrencyconverter_proversion_2b";
    private static String SKU_PACKAGE_PRO3 = "com.romerock.apps.utilities.cryptocurrencyconverter_proversion_3";
    private static String finalPricepackage = "";
    private static String finalPricepackagePro1 = "";
    private static String finalPricepackagePro2 = "";
    private static String finalPricepackagePro3 = "";
    private static boolean havePricepackagePro1=false;
    private static boolean havePricepackagePro2=false;
    private static boolean havePricepackagePro3=false;
    private static FirebaseHelper firebaseHelper;
    private static IabHelper mHelper;
    private Inventory inventoryUser;
    private ServiceConnection mServiceConn;
    private IInAppBillingService mService;
    private Bundle skuDetailsProducts;
    private String IS_FREE_OR_PREMIUM;
    private boolean isInvalidated;
    private boolean showPopUp;
    private FirebaseDatabase firebaseDatabase;
    private DialogsHelper dialogsHelper;
    private UserUdId userUdId;
    private RewardedVideoAd rewardedVideoAd;

    public RewardedVideoAd getRewardedVideoAd() {
        return rewardedVideoAd;
    }

    public void setRewardedVideoAd(RewardedVideoAd rewardedVideoAd) {
        this.rewardedVideoAd = rewardedVideoAd;
    }

    public SingletonInAppBilling() {
    }

    public DialogsHelper getDialogsHelper() {
        return dialogsHelper;
    }

    public void setDialogsHelper(DialogsHelper dialogsHelper) {
        this.dialogsHelper = dialogsHelper;
    }

    public UserUdId getUserUdId() {
        return userUdId;
    }

    public void setUserUdId(UserUdId userUdId) {
        this.userUdId = userUdId;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public static FirebaseHelper getFirebaseHelper() {
        return firebaseHelper;
    }

    public static void setFirebaseHelper(FirebaseHelper firebaseHelper) {
        SingletonInAppBilling.firebaseHelper = firebaseHelper;
    }

    public static void setFirebaseDatabase(FirebaseDatabase firebaseDatabase) {
        firebaseDatabase = firebaseDatabase;
    }

    public static ArrayList<String> getSKUList() {
        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add(SKU_PACKAGE_PRO1);
        skuList.add(SKU_PACKAGE_PRO2);
        skuList.add(SKU_PACKAGE_PRO3);
        return skuList;
    }

    public static boolean isHavePricepackagePro1() {
        return havePricepackagePro1;
    }

    public static void setHavePricepackagePro1(boolean havePricepackagePro1) {
        SingletonInAppBilling.havePricepackagePro1 = havePricepackagePro1;
    }

    public static boolean isHavePricepackagePro2() {
        return havePricepackagePro2;
    }

    public static void setHavePricepackagePro2(boolean havePricepackagePro2) {
        SingletonInAppBilling.havePricepackagePro2 = havePricepackagePro2;
    }

    public static boolean isHavePricepackagePro3() {
        return havePricepackagePro3;
    }

    public static void setHavePricepackagePro3(boolean havePricepackagePro3) {
        SingletonInAppBilling.havePricepackagePro3 = havePricepackagePro3;
    }

    public static void setFinalPricepackagePro1(String finalPricepackagePro1) {
        SingletonInAppBilling.finalPricepackagePro1 = finalPricepackagePro1;
    }

    public static void setFinalPricepackagePro2(String finalPricepackagePro2) {
        SingletonInAppBilling.finalPricepackagePro2 = finalPricepackagePro2;
    }

    public static void setFinalPricepackagePro3(String finalPricepackagePro3) {
        SingletonInAppBilling.finalPricepackagePro3 = finalPricepackagePro3;
    }

    public static String getSkuPackagePro1() {
        return SKU_PACKAGE_PRO1;
    }

    public static String getSkuPackagePro2() {
        return SKU_PACKAGE_PRO2;
    }

    public static String getSkuPackagePro3() {
        return SKU_PACKAGE_PRO3;
    }

    public static String getFinalPricepackagePro1() {
        return finalPricepackagePro1;
    }

    public static String getFinalPricepackagePro2() {
        return finalPricepackagePro2;
    }

    public static String getFinalPricepackagePro3() {
        return finalPricepackagePro3;
    }

    public boolean isShowPopUp() {
        return showPopUp;
    }

    public void setShowPopUp(boolean showPopUp) {
        this.showPopUp = showPopUp;
    }

    public boolean isInvalidated() {
        return isInvalidated;
    }

    public void setInvalidated(boolean invalidated) {
        isInvalidated = invalidated;
    }

    public Bundle getSkuDetailsProducts() {
        return skuDetailsProducts;
    }

    public static String getFinalPricepackage() {
        return finalPricepackage;
    }

    public static void setFinalPricePackage(String finalPricepackage) {
        SingletonInAppBilling.finalPricepackage = finalPricepackage;
    }

    public String getIS_FREE_OR_PREMIUM() {
        return IS_FREE_OR_PREMIUM;
    }

    public void setIS_FREE_OR_PREMIUM(String IS_FREE_OR_PREMIUM) {
        this.IS_FREE_OR_PREMIUM = IS_FREE_OR_PREMIUM;
    }

    public static String[] getDetailPackageSelected(Bundle skuDetailsProducts, String packageSend) {
        String[] returnVals = new String[2];
        if (skuDetailsProducts != null) {
            if (skuDetailsProducts.getStringArrayList("DETAILS_LIST") != null) {
                for (String thisResponse : skuDetailsProducts.getStringArrayList("DETAILS_LIST")) {
                    try {
                        JSONObject object = new JSONObject(thisResponse);
                        String sku = object.getString("productId");
                        if (sku.equals(packageSend)) {
                            returnVals[0] = object.getString("price");
                            returnVals[1] = object.getString("price_currency_code");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return returnVals;
    }

    public static SingletonInAppBilling Instance() {
        if (instance == null) {
            instance = new SingletonInAppBilling();
        }
        return instance;
    }

    public static IabHelper getmHelper() {
        return mHelper;
    }

    public void setmHelper(IabHelper mHelper) {
        this.mHelper = mHelper;
    }

    public Inventory getInventoryUser() {
        return inventoryUser;
    }

    public ServiceConnection getmServiceConn() {
        return mServiceConn;
    }

    public void setInventoryUser(Inventory inventoryUser) {
        this.inventoryUser = inventoryUser;
    }

    public void setmServiceConn(final Context contextCall) {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                if(mService!=null) {
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", SingletonInAppBilling.getSKUList());
                    try {
                        skuDetailsProducts = mService.getSkuDetails(3, contextCall.getPackageName(), "inapp", querySkus);
                        if (skuDetailsProducts != null) {
                            String[] valsPackage = new String[2];
                            Log.d("", "");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

}

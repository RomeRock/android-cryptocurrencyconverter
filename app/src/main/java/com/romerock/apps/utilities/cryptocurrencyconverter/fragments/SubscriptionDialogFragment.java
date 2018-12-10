package com.romerock.apps.utilities.cryptocurrencyconverter.fragments;

import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.google.firebase.database.DatabaseReference;
import com.romerock.apps.utilities.cryptocurrencyconverter.MainActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.IabHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.IabResult;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.Inventory;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.Purchase;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CheckUDIDListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.PurchaseDialog;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ThemeInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.TransactionModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.UserUdId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Ebricko on 02/04/2018.
 */

public class SubscriptionDialogFragment extends DialogFragment implements ThemeInterface {
    Unbinder unbinder;
    @BindView(R.id.popButtonDonate1)
    Button popButtonDonate1;
    @BindView(R.id.popButtonDonate2)
    Button popButtonDonate2;
    @BindView(R.id.popButtonDonate3)
    Button popButtonDonate3;
    @BindView(R.id.popUpNoThanks)
    TextView popUpNoThanks;
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    String TAG = "RomeRock";
    IInAppBillingService mService;
    @BindView(R.id.txtfirst)
    TextView txtfirst;
    @BindView(R.id.txtSecond)
    TextView txtSecond;
    @BindView(R.id.txtThree)
    TextView txtThree;
    @BindView(R.id.txtFour)
    TextView txtFour;
    private Inventory inventoryUser;
    private Purchase purchase;
    private IabHelper mHelper;
    private ServiceConnection mServiceConn;
    private String[] valsPackage;
    private Bundle skuDetailsProducts;
    private DatabaseReference fromData;
    private DatabaseReference toData;
    private FirebaseHelper firebaseHelper;
    private SharedPreferences sharedPrefs;
    private String UDID;
    private String FMCTocken;
    private Context context;

    public static SubscriptionDialogFragment newInstance() {
        SubscriptionDialogFragment frag = new SubscriptionDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_up_subscription, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        unbinder = ButterKnife.bind(this, view);
        mHelper = SingletonInAppBilling.Instance().getmHelper();
        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(final IabResult result, final Purchase purchase) {
                DialogsHelper.purchaseFinish(getActivity(), !result.isFailure(), result.getMessage(), new PurchaseDialog() {
                    @Override
                    public void PurchaseDialogFinish(boolean isSuccess) {
                        if (isSuccess) {
                            String price="";
                            if (purchase.getSku().compareTo(SingletonInAppBilling.getSkuPackagePro1()) == 0) {
                                SingletonInAppBilling.setHavePricepackagePro1(true);
                                price=SingletonInAppBilling.getFinalPricepackagePro1();
                            } else if (purchase.getSku().compareTo(SingletonInAppBilling.getSkuPackagePro2()) == 0) {
                                SingletonInAppBilling.setHavePricepackagePro2(true);
                                price=SingletonInAppBilling.getFinalPricepackagePro2();
                            } else if (purchase.getSku().compareTo(SingletonInAppBilling.getSkuPackagePro3()) == 0) {
                                SingletonInAppBilling.setHavePricepackagePro3(true);
                                price=SingletonInAppBilling.getFinalPricepackagePro3();
                            }
                            SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(UserUdId.getPREMIUM());
                            processPublish(purchase, price);
                            dismiss();
                        } else {
                            // IS FAILIRE
                            dismiss();
                            SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(UserUdId.getFREE());
                        }


                    }
                });
            }
        };
        firebaseHelper = FirebaseHelper.getInstance();
        context = getActivity();
        sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preferences_name), getActivity().MODE_PRIVATE);
        try {
            UDID = CipherAES.decipher(sharedPrefs.getString(getResources().getString(R.string.udidAndroid), ""));
            FMCTocken = CipherAES.decipher(sharedPrefs.getString(getResources().getString(R.string.fcmUser), ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (SingletonInAppBilling.isHavePricepackagePro1()) {
            popButtonDonate1.setText(getString(R.string.thanks));
            popButtonDonate1.setClickable(false);
        } else {
            popButtonDonate1.setText(SingletonInAppBilling.getFinalPricepackagePro1());
        }
        if (SingletonInAppBilling.isHavePricepackagePro2()) {
            popButtonDonate2.setText(getString(R.string.thanks));
            popButtonDonate2.setClickable(false);
        } else {
            popButtonDonate2.setText(SingletonInAppBilling.getFinalPricepackagePro2());
        }
        if (SingletonInAppBilling.isHavePricepackagePro3()) {
            popButtonDonate3.setText(getString(R.string.thanks));
            popButtonDonate3.setClickable(false);
        } else {
            popButtonDonate3.setText(SingletonInAppBilling.getFinalPricepackagePro3());
        }


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.closeKeyboard(this.getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.popButtonDonate1, R.id.popButtonDonate2, R.id.popButtonDonate3, R.id.popUpNoThanks})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.popButtonDonate1:
                try {
                    if (mHelper != null && mHelper.isSetupDone()) {
                        mHelper.flagEndAsync();
                        mHelper.launchPurchaseFlow(getActivity(), SingletonInAppBilling.getSkuPackagePro1(), 10004, mPurchaseFinishedListener, "Currency Converter");
                    } else {
                        dismiss();
                        DialogsHelper.showSnackBar(((MainActivity) getActivity()).getCoordinator(), getString(R.string.error_internet), getResources().getColor(R.color.alert_snackbar));
                    }
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.popButtonDonate2:
                try {
                    if (mHelper != null && mHelper.isSetupDone()) {
                        mHelper.flagEndAsync();
                        mHelper.launchPurchaseFlow(getActivity(), SingletonInAppBilling.getSkuPackagePro2(), 10004, mPurchaseFinishedListener, "Currency Converter");
                    } else {
                        dismiss();
                        DialogsHelper.showSnackBar(((MainActivity) getActivity()).getCoordinator(), getString(R.string.error_internet), getResources().getColor(R.color.alert_snackbar));
                    }
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.popButtonDonate3:
                try {
                    if (mHelper != null && mHelper.isSetupDone()) {
                        mHelper.flagEndAsync();
                        mHelper.launchPurchaseFlow(getActivity(), SingletonInAppBilling.getSkuPackagePro3(), 10004, mPurchaseFinishedListener, "Currency Converter");
                    } else {
                        dismiss();
                        DialogsHelper.showSnackBar(((MainActivity) getActivity()).getCoordinator(), getString(R.string.error_internet), getResources().getColor(R.color.alert_snackbar));
                    }
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.popUpNoThanks:
                dismiss();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            getActivity().unbindService(mServiceConn);
        }
    }

    void processPublish(final Purchase purchase, String price) {
        //String itemid, String orderid, String paid, String transtoken
        String idPurchase = purchase.getOrderId().toString().replace(".", "_");
        TransactionModel transactionModel = new TransactionModel(purchase.getSku(),
                idPurchase,
                price,
                purchase.getToken());
        final UserUdId userUdId = new UserUdId(sharedPrefs.getString(getResources().getString(R.string.language_settings), ""),
                "active", "",
                UserUdId.getPREMIUM(),
                purchase.getPurchaseTime());
        userUdId.setTransactionModel(transactionModel);
        try {
            sharedPrefs = getActivity().getSharedPreferences(getString(R.string.preferences_name), getActivity().MODE_PRIVATE);

            SharedPreferences.Editor ed = sharedPrefs.edit();
            if(!sharedPrefs.contains(getString(R.string.purchaseAndroid)))
                ed.putString(getString(R.string.purchaseAndroid), CipherAES.cipher(UserUdId.getPREMIUM()));
            if(!sharedPrefs.contains(getString(R.string.purchaseOrder))) {
                ed.putString(getString(R.string.purchaseOrder), CipherAES.cipher(idPurchase.toString().replace(".", "_")));
                userUdId.copyNotificationsToPremium(firebaseHelper, UDID, idPurchase);
            }else{
                idPurchase=CipherAES.decipher(sharedPrefs.getString(getString(R.string.purchaseOrder),""));
                userUdId.setToPremiumTransaction(firebaseHelper, CipherAES.decipher(sharedPrefs.getString(getResources().getString(R.string.purchaseOrder), "")), idPurchase, transactionModel);
            }
            ed.commit();
            SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(UserUdId.getPREMIUM());

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalIdPurchase = idPurchase;
        userUdId.CheckFreeUDIDNode(idPurchase, firebaseHelper, new CheckUDIDListener() {
            @Override
            public void checkUDIDFromFirebase(boolean haveChild) {
                try {
                    userUdId.checkFreeFMCTocken(firebaseHelper, finalIdPurchase, FMCTocken, purchase.getPurchaseTime(), UserUdId.getPREMIUM(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void checkPremiumState(boolean status) {
            }
        }, userUdId, SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(), getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        setThemeByActivity();
    }

    @Override
    public void setThemeByActivity() {
        String themeSelected = sharedPrefs.getString(getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            txtfirst.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtSecond.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtThree.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtFour.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            popUpNoThanks.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
        } else {
            if (themeSelected.contains("Daylight")) {
                txtfirst.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtSecond.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtThree.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtFour.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                popUpNoThanks.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
            }
        }
    }
}

package com.romerock.apps.utilities.cryptocurrencyconverter.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.romerock.apps.utilities.cryptocurrencyconverter.fragments.FeedbackDialogFragment;
import com.romerock.apps.utilities.cryptocurrencyconverter.fragments.RateusDialogFragment;
import com.romerock.apps.utilities.cryptocurrencyconverter.fragments.SubscriptionDialogFragment;

/**
 * Created by Ebricko on 29/12/2016.
 */

public class Popup {
    private static final String TAG = "QuickConverter";
    private static Activity contextPopup;
    private static Context contextFull;

    private static View viewPop;
    private static AlertDialog alertDialog;


    public static void RatePopup(final Context context) {
        RateusDialogFragment rateusDialogFragment = new RateusDialogFragment();
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        rateusDialogFragment = RateusDialogFragment.newInstance();
        rateusDialogFragment.show(fm, "rateus dialog");
    }

    public static void Feedback(final Context context) {
        FeedbackDialogFragment feedbackDialogFragment = new FeedbackDialogFragment();
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        feedbackDialogFragment = FeedbackDialogFragment.newInstance();
        feedbackDialogFragment.show(fm, "feedbak dialog");
    }

    public static void SubscribeMe(final Context context) {
        SubscriptionDialogFragment subscriptionFragment = new SubscriptionDialogFragment();
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        subscriptionFragment = SubscriptionDialogFragment.newInstance();
        subscriptionFragment.show(fm, "feedbak dialog");
    }
/*
    public static void implementThemePopUpElements(Context context) {
        Button actionButton=((Button) viewPop.findViewById(R.id.relContent));
        switch (Utilities.getThemePreferences(context)) {
            case R.style.AppTheme_NoActionBar:
                ((TextView) viewPop.findViewById(R.id.txtTittleImg)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme1));
                ((TextView) viewPop.findViewById(R.id.txtTittleImg)).setBackground(context.getResources().getDrawable(R.drawable.border_lineal_alert_title_theme1));
                ((TextView) viewPop.findViewById(R.id.txtVotesPurchase)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme1));
                ((TextView) viewPop.findViewById(R.id.popUpNoThanks)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme1));
                ((RelativeLayout) viewPop.findViewById(R.id.relContent)).setBackground(context.getResources().getDrawable(R.drawable.dialog_theme1));
                if(actionButton!=null)
                    actionButton.setBackground(context.getResources().getDrawable(R.drawable.border_buttont_theme1));
                break;
            case R.style.Chill:
                ((TextView) viewPop.findViewById(R.id.txtTittleImg)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme2));
                ((TextView) viewPop.findViewById(R.id.txtTittleImg)).setBackground(context.getResources().getDrawable(R.drawable.border_lineal_alert_title_theme2));
                ((TextView) viewPop.findViewById(R.id.txtVotesPurchase)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme2));
                ((TextView) viewPop.findViewById(R.id.popUpNoThanks)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme2));
                ((RelativeLayout) viewPop.findViewById(R.id.relContent)).setBackground(context.getResources().getDrawable(R.drawable.dialog_theme2));
                if(actionButton!=null)
                    actionButton.setBackground(context.getResources().getDrawable(R.drawable.border_buttont_theme2));
                break;
            case R.style.Electric:
                ((TextView) viewPop.findViewById(R.id.txtTittleImg)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme2));
                ((TextView) viewPop.findViewById(R.id.txtTittleImg)).setBackground(context.getResources().getDrawable(R.drawable.border_lineal_alert_title_theme2));
                ((TextView) viewPop.findViewById(R.id.txtVotesPurchase)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme2));
                ((TextView) viewPop.findViewById(R.id.popUpNoThanks)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme2));
                ((RelativeLayout) viewPop.findViewById(R.id.relContent)).setBackground(context.getResources().getDrawable(R.drawable.dialog_theme2));
                if(actionButton!=null)
                    actionButton.setBackground(context.getResources().getDrawable(R.drawable.border_buttont_theme3));
                break;
            case R.style.Night:
                ((TextView) viewPop.findViewById(R.id.txtTittleImg)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme1));
                ((TextView) viewPop.findViewById(R.id.txtTittleImg)).setBackground(context.getResources().getDrawable(R.drawable.border_lineal_alert_title_theme1));
                ((TextView) viewPop.findViewById(R.id.txtVotesPurchase)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme1));
                ((TextView) viewPop.findViewById(R.id.popUpNoThanks)).setTextColor(context.getResources().getColor(R.color.primaryTextLabelsTheme1));
                ((RelativeLayout) viewPop.findViewById(R.id.relContent)).setBackground(context.getResources().getDrawable(R.drawable.dialog_theme3));
                if(actionButton!=null)
                    actionButton.setBackground(context.getResources().getDrawable(R.drawable.border_buttont_theme4));
                break;

        }
    }

    public static void PurchaseSuccess(final Context context) {
        ProSuccessDialogFragment proSuccessDialogFragment = new ProSuccessDialogFragment();
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        proSuccessDialogFragment = ProSuccessDialogFragment.newInstance();
        proSuccessDialogFragment.show(fm, "purchase dialog");
    }*/
}
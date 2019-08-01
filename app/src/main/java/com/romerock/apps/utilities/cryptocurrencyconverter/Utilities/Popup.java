package com.romerock.apps.utilities.cryptocurrencyconverter.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.romerock.apps.utilities.cryptocurrencyconverter.fragments.FeedbackDialogFragment;
import com.romerock.apps.utilities.cryptocurrencyconverter.fragments.RateusDialogFragment;
import com.romerock.apps.utilities.cryptocurrencyconverter.fragments.RewardedFragment;
import com.romerock.apps.utilities.cryptocurrencyconverter.fragments.SubscriptionDialogFragment;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.FinishVideo;

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
        subscriptionFragment.show(fm, "SubscribeMe dialog");
    }

    public static void ShowRewardedPopup(Context context, RewardedVideoAd rewardedVideoAd, FinishVideo finishVideo){
        RewardedFragment rewardedFragment = RewardedFragment.newInstance();
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        rewardedFragment.setRewardedVideoAd(rewardedVideoAd);
        rewardedFragment.setFinishVideo(finishVideo);
        rewardedFragment.show(fm, "ShowRewardedPopup dialog");
    }
}
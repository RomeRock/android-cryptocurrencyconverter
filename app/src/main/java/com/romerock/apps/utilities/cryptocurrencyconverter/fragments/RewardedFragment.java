package com.romerock.apps.utilities.cryptocurrencyconverter.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Popup;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.FinishVideo;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class RewardedFragment extends DialogFragment implements RewardedVideoAdListener {


    Unbinder unbinder;
    private RewardedVideoAd rewardedVideoAd;
    private OnFragmentInteractionListener mListener;
    private FinishVideo finishVideo;
    private DialogsHelper dialogsHelper;
    public void setFinishVideo(FinishVideo finishVideo) {
        this.finishVideo = finishVideo;
    }

    public RewardedFragment() {
        // Required empty public constructor
    }

    public static RewardedFragment newInstance() {
        RewardedFragment fragment = new RewardedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_up_rewarded, container, false);
        Utilities.ChangeLanguage(getActivity());
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        unbinder = ButterKnife.bind(this, view);
        dialogsHelper=new DialogsHelper(getActivity());
        return view;
    }

    public void setRewardedVideoAd(RewardedVideoAd rewardedVideoAd) {
        this.rewardedVideoAd = rewardedVideoAd;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.popUpViewVideo, R.id.popUpDonate, R.id.popUpNoThanks})
    public void onViewClicked(View view) {
        rewardedVideoAd = SingletonInAppBilling.Instance().getRewardedVideoAd();
        switch (view.getId()) {
            case R.id.popUpViewVideo:
                dialogsHelper.showLoading();
                rewardedVideoAd.setRewardedVideoAdListener(this);
                loadRewardedVideoAd();
                break;
            case R.id.popUpDonate:
                Popup.SubscribeMe(getActivity());
                dismiss();
                break;
            case R.id.popUpNoThanks:
                dismiss();
                break;
        }

    }

    private void loadRewardedVideoAd() {
        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(getActivity().getResources().getString(R.string.banner_rewarded_AD_UNIT_ID) , new AdRequest.Builder()
                    //   .addTestDevice("0423998143E406A5A39DE5E1FEE6C6DA")
                    .build());
        }else{
            rewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if (rewardedVideoAd.isLoaded()) {
            dialogsHelper.hideLoading();
            rewardedVideoAd.show();

        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d("", "");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d("", "");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Log.d("", "");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d("", "");
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d("", "");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        if (rewardedVideoAd.isLoaded())
            rewardedVideoAd.destroy(getActivity());
        dialogsHelper.hideLoading();
        finishVideo.finish(true, false);
        dismiss();
    }

    @Override
    public void onRewardedVideoCompleted() {
        // aqui ya se debe abrir los stickers
        if (rewardedVideoAd.isLoaded())
            rewardedVideoAd.destroy(getActivity());
        finishVideo.finish(true, true);

        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
           finishVideo.finish(false, false);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}

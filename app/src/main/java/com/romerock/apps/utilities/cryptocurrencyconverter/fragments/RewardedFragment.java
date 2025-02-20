package com.romerock.apps.utilities.cryptocurrencyconverter.fragments;

import static com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.TAG;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.reward.RewardItem;
//import com.google.android.gms.ads.reward.RewardedVideoAd;
//import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.romerock.apps.utilities.cryptocurrencyconverter.BuildConfig;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Popup;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.FinishVideo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class RewardedFragment extends DialogFragment {


    Unbinder unbinder;
    private RewardedAd rewardedVideoAd;
    private OnFragmentInteractionListener mListener;
    private FinishVideo finishVideo;
    private DialogsHelper dialogsHelper;
    public void setFinishVideo(FinishVideo finishVideo) {
        this.finishVideo = finishVideo;
    }

    private boolean rewardedView=false;
    private AdRequest adRequest;
    @BindView(R.id.popUpDonate)
    Button popUpDonate;

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
        if(BuildConfig.FLAVOR.compareTo("google")==0){
            popUpDonate.setVisibility(View.VISIBLE);
        }else{
            popUpDonate.setVisibility(View.GONE);
        }
        adRequest = new AdRequest.Builder().build();
        return view;
    }

    public void setRewardedVideoAd(RewardedAd rewardedVideoAd) {
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
        //rewardedVideoAd = SingletonInAppBilling.Instance().getRewardedVideoAd();
        switch (view.getId()) {
            case R.id.popUpViewVideo:
                dialogsHelper.showLoading();
                //rewardedVideoAd.setRewardedVideoAdListener(this);
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
        if (rewardedVideoAd == null) {
            /*rewardedVideoAd.loadAd(getActivity().getResources().getString(R.string.video_rewarded_ad_unit_id), new AdRequest.Builder()
                    //   .addTestDevice("0423998143E406A5A39DE5E1FEE6C6DA")
                    .build());*/

            RewardedAd.load(getActivity(), getResources().getString(R.string.banner_rewarded_AD_UNIT_ID),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.toString());
                            rewardedVideoAd = null;
                            dialogsHelper.hideLoading();
                            finishVideo.finish(true, false);
                            dismiss();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            Log.d(TAG, "Ad was loaded.");
                            dialogsHelper.hideLoading();
                            rewardedVideoAd = ad;
                            showRewardedVideo();
                        }
                    });
        } else {
            rewardedVideoAd.show(getActivity(), new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    rewardedView=true;
                    dismiss();
                }
            });
        }

        /*if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(getActivity().getResources().getString(R.string.banner_rewarded_AD_UNIT_ID) , new AdRequest.Builder()
                    //   .addTestDevice("0423998143E406A5A39DE5E1FEE6C6DA")
                    .build());
        }else{
            rewardedVideoAd.show();
        }*/
    }

    private void showRewardedVideo() {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.show(getActivity(), new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    rewardedView=true;
                    dismiss();
                }
            });

            rewardedVideoAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedVideoAd = null;
                    if(rewardedView){
                        onSuccessRewarded();
                    }
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        }
    }

    private void onSuccessRewarded() {
        finishVideo.finish(true, true);
        dismiss();
    }


    /*@Override
    public void onRewardedVideoAdLoaded() {
        if (rewardedVideoAd.isLoaded()) {
            dialogsHelper.hideLoading();
            rewardedVideoAd.show();

        }
    }

    @Override
    public void onRewardedVideoAdClosed() {
        if(rewardedView){
            onSuccessRewarded();
        }
    }

    private void onSuccessRewarded() {
        if (rewardedVideoAd.isLoaded())
            rewardedVideoAd.destroy(getActivity());
        finishVideo.finish(true, true);

        dismiss();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Log.d("video", "onRewarded");
        rewardedView=true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d("video", "onRewardedVideoAdLeftApplication");
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
        onSuccessRewarded();

    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
           finishVideo.finish(false, false);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}

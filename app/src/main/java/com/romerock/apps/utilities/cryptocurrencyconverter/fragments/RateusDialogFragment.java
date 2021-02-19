package com.romerock.apps.utilities.cryptocurrencyconverter.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Popup;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.BuildConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Ebricko on 07/02/2018.
 */

public class RateusDialogFragment extends DialogFragment {

    @BindView(R.id.popUpAction)
    Button popUpAction;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.txtTittleImg)
    TextView txtTittleImg;
    @BindView(R.id.txtVotesPurchase)
    TextView txtVotesPurchase;
    @BindView(R.id.popUpNoThanks)
    TextView popUpNoThanks;
    @BindView(R.id.relContent)
    RelativeLayout relContent;
    private Unbinder unbinder;


    public RateusDialogFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_up_some_love, container);
        Utilities.ChangeLanguage(getActivity());
        unbinder = ButterKnife.bind(this, view);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1)
                    ratingBar.setRating(1);
            }
        });
        switch (Utilities.getThemePreferences(getActivity())) {
            case R.style.Night:
                default:
                txtTittleImg.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
                txtTittleImg.setBackground(getResources().getDrawable(R.drawable.border_lineal_alert_title_theme1));
                txtVotesPurchase.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                popUpNoThanks.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
                break;
            case R.style.Daylight:
                txtTittleImg.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                txtTittleImg.setBackground(getResources().getDrawable(R.drawable.border_lineal_alert_title_theme2));
                txtVotesPurchase.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorDarkDaylight));
                popUpNoThanks.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                break;
        }
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.closeKeyboard(this.getActivity());
    }


    public static RateusDialogFragment newInstance() {
        RateusDialogFragment frag = new RateusDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null)
            unbinder.unbind();
    }

    @OnClick(R.id.popUpAction)
    public void onClick() {
        dismiss();
    }


    @OnClick({R.id.popUpAction, R.id.popUpNoThanks})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popUpAction:
                String stars = "";
                SharedPreferences sharedPrefs = getActivity().getSharedPreferences(getActivity().getString(R.string.preferences_name), 0);
                SharedPreferences.Editor ed = sharedPrefs.edit();
                ed.putBoolean(getActivity().getString(R.string.preferences_rate), true);
                ed.commit();
                if (ratingBar.getRating() < 4) {
                    dismiss();
                    Popup.Feedback(getActivity());
                } else {
                    try {
                        String feedbackStore;
                        switch (BuildConfig.FLAVOR){
                            case "amazon":
                                feedbackStore="https://www.amazon.com/-/es/dp/B07P9D8VKF/";
                                break;
                            case "samsung":
                                feedbackStore = "http://apps.samsung.com/appquery/appDetail.as?appId=";
                                break;
                            case "huawei":
                                feedbackStore="https://appgallery.huawei.com/#/app/C103855619";        // Cambiar este al ID de huawei
                                break;
                            default:
                                feedbackStore="market://details?id=";
                        }

                        String appId = getActivity().getPackageName();
                        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(feedbackStore + appId));
                        getActivity().startActivity(rateIntent);
                    }catch (Exception e){

                    }
                }
                break;
            case R.id.popUpNoThanks:
                dismiss();
                break;
        }
    }
}

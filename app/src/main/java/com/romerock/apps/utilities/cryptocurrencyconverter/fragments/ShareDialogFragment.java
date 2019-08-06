package com.romerock.apps.utilities.cryptocurrencyconverter.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.romerock.apps.utilities.cryptocurrencyconverter.MainActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Ebricko on 07/02/2018.
 */

public class ShareDialogFragment extends DialogFragment {

    private int TWEET_COMPOSER_REQUEST_CODE = 106;
    private int FACEBOOK_REQUEST_CODE = 64207;
    private static int spinner;
    @BindView(R.id.popUpFacebookBoton)
    Button popUpFacebookBoton;
    @BindView(R.id.popUpTwitterBoton)
    Button popUpTwitterBoton;
    @BindView(R.id.popUpNoThanks)
    TextView popUpNoThanks;
    @BindView(R.id.txtTittleImg)
    TextView txtTittleImg;
    @BindView(R.id.txtVotesPurchase)
    TextView txtVotesPurchase;
    @BindView(R.id.relTittle)
    RelativeLayout relTittle;
    @BindView(R.id.relContent)
    RelativeLayout relContent;
    @BindView(R.id.relContentPopup)
    RelativeLayout relContentPopup;
    private Unbinder unbinder;
    private int typeSpinner;
    private int position;
    private String description;
    private ShareDialog shareDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_up_share, container);
        Utilities.ChangeLanguage(getActivity());
        unbinder = ButterKnife.bind(this, view);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        switch (Utilities.getThemePreferences(getActivity())) {
            case R.style.Night:
                txtTittleImg.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
                txtTittleImg.setBackground(getResources().getDrawable(R.drawable.border_lineal_alert_title_theme1));
                txtVotesPurchase.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
                popUpNoThanks.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case R.style.Daylight:
                txtTittleImg.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                txtTittleImg.setBackground(getResources().getDrawable(R.drawable.border_lineal_alert_title_theme2));
                txtVotesPurchase.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                popUpNoThanks.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorDarkDaylight));
                break;

        }
        MainActivity activity = (MainActivity) getActivity();
        shareDialog = activity.getShareDialog();
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.closeKeyboard(this.getActivity());


    }


    public static ShareDialogFragment newInstance() {
        ShareDialogFragment frag = new ShareDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.popUpFacebookBoton, R.id.popUpTwitterBoton, R.id.popUpNoThanks})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.popUpFacebookBoton:
                dismiss();
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(getString(R.string.share_link)))
                            .build();
                    shareDialog.show(linkContent);
                }
                break;
            case R.id.popUpTwitterBoton:
                dismiss();
                try {
                    URL url;
                    url = new URL(getString(R.string.share_link));
                    Intent intent = null;
                    intent = new TweetComposer.Builder(getActivity())
                            .text(getActivity().getString(R.string.share_via_twitter))
                            .url(url)
                            .createIntent();
                    startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.popUpNoThanks:
                dismiss();
                break;
        }
    }



}

package com.romerock.apps.utilities.cryptocurrencyconverter.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.MainActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Ebricko on 07/02/2018.
 */

public class FeedbackDialogFragment extends DialogFragment {


    private static int spinner;
    @BindView(R.id.popUpFeedbackBoton)
    Button popUpFeedbackBoton;
    @BindView(R.id.txtContentMail)
    EditText txtContentMail;
    @BindView(R.id.relContent)
    RelativeLayout relContent;
    @BindView(R.id.txtTittleImg)
    TextView txtTittleImg;
    @BindView(R.id.popUpNoThanks)
    TextView popUpNoThanks;
    @BindView(R.id.txtMail)
    EditText txtMail;

    private Unbinder unbinder;


    public FeedbackDialogFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_up_feedback, container);
        unbinder = ButterKnife.bind(this, view);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        switch (Utilities.getThemePreferences(getActivity())) {
            case R.style.Night:
            default:
                txtTittleImg.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
                popUpNoThanks.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
                txtTittleImg.setBackground(getResources().getDrawable(R.drawable.border_lineal_alert_title_theme1));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case R.style.Daylight:
                txtTittleImg.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                popUpNoThanks.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                txtTittleImg.setBackground(getResources().getDrawable(R.drawable.border_lineal_alert_title_theme2));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorDarkDaylight));
                break;
        }
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.ChangeLanguage(getActivity());
        Utilities.closeKeyboard(this.getActivity());


    }


    public static FeedbackDialogFragment newInstance() {
        FeedbackDialogFragment frag = new FeedbackDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.popUpFeedbackBoton)
    public void onClick() {
        dismiss();
    }

    @OnClick({R.id.txtVotesPurchase, R.id.txtContentMail, R.id.popUpFeedbackBoton, R.id.popUpNoThanks})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.popUpFeedbackBoton:
                Utilities.closeKeyboard(getActivity());
                String content = "";
                content = txtContentMail.getText().toString();
                Utilities.SendMailToAPi(getActivity(), "FiftyTwoWeeks - Feedback", content, ((MainActivity) getActivity()).getCoordinator(), txtMail.getText().toString());
                break;
            case R.id.popUpNoThanks:
                Utilities.closeKeyboard(getActivity());
                dismiss();
                break;
        }
    }
}

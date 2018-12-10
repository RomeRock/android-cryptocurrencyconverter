package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.PurchaseDialog;

import java.io.Serializable;

/**
 * Created by Ebricko on 11/05/2017.
 */

public class DialogsHelper extends Activity implements Serializable {
    public Dialog settingsDialog;
    private Context context;
    private Activity activity;

    public DialogsHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public static void showSnackBar(CoordinatorLayout coordinatorLayout, String msn, int color) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msn, Snackbar.LENGTH_INDEFINITE);
        snackbar.getView().setBackgroundColor(color);
        snackbar.setDuration(5000);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void showLoading() {
        if(!((Activity) context).isFinishing())
        {
            settingsDialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            settingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            settingsDialog.setContentView(activity.getLayoutInflater().inflate(R.layout.loading, null));
            settingsDialog.setCancelable(false);
            settingsDialog.show();
        }

    }

    public void hideLoading() {
        if (settingsDialog != null)
            if (settingsDialog.isShowing())
                try {
                  /*  Activity activity = settingsDialog.getOwnerActivity();
                    if( activity!=null && !activity.isFinishing()) {*/
                        settingsDialog.dismiss();
                   // }
                }catch (Exception e){}
    }

    public static AlertDialog purchaseFinish(final Context context, final boolean isSuccess, final String purchaseMessage, final PurchaseDialog purhaseDialog) {
        if(isSuccess){
            purhaseDialog.PurchaseDialogFinish(isSuccess);
            return null;
        }else {
            try {
                final AlertDialog alertDialog;
                final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                builder.setCancelable(false);
                View viewPop = inflater.inflate(R.layout.pop_up_purchase_finish, null);
                TextView tittle = (TextView) viewPop.findViewById(R.id.txtTittleImg);
                String themeSelected = sharedPreferences.getString(context.getString(R.string.preferences_theme_tittle), null);
                TextView txtContentPurchase = (TextView) viewPop.findViewById(R.id.txtContentPurchase);
                if (themeSelected.contains("Night")) {
                    tittle.setBackground(context.getResources().getDrawable(R.drawable.border_lineal_alert_title_theme1));
                    tittle.setTextColor(context.getResources().getColor(R.color.primaryTextTheme1));
                    txtContentPurchase.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme1));
                } else if (themeSelected.contains("Daylight")) {
                    tittle.setBackground(context.getResources().getDrawable(R.drawable.border_lineal_alert_title_theme2));
                    tittle.setTextColor(context.getResources().getColor(R.color.primaryTextTheme2));
                    txtContentPurchase.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme2));
                }

                txtContentPurchase.setText(purchaseMessage);
                if (isSuccess) {
                    tittle.setText(context.getString(R.string.tittle_purchse_finish));
                } else {
                    tittle.setText(context.getString(R.string.tittle_purchse_finish_error));
                }
                final Button popClose = (Button) viewPop.findViewById(R.id.btnDone);

                builder.setView(viewPop);
                builder.create();
                alertDialog = builder.show();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final AlertDialog finalAlertDialog = alertDialog;
                popClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        purhaseDialog.PurchaseDialogFinish(isSuccess);
                        alertDialog.dismiss();
                    }
                });
                return finalAlertDialog;
            } catch (Exception e) {
                return null;
            }
        }
    }
}

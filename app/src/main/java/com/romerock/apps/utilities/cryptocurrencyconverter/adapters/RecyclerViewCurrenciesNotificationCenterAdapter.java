package com.romerock.apps.utilities.cryptocurrencyconverter.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.SetupPushNotificationsActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.NotificationsListListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.NotificationModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ebricko on 15/12/2016.
 */
public class RecyclerViewCurrenciesNotificationCenterAdapter extends RecyclerView.Adapter<RecyclerViewCurrenciesNotificationCenterAdapter.ViewHolder> implements View.OnClickListener {
    private final ItemClickLibraryInterface itemClickListener;
    private List<NotificationModel> Items;
    private Context context;
    private List<ItemLibraryCurrencyModel> listDashboardCurrencies;
    private List<ItemLibraryCurrencyModel> listAllCurrencies;
    private RecyclerView recyclerView;
    private NotificationsListListener notificationsListListener;
    private SharedPreferences sharedPreferences;

    public RecyclerViewCurrenciesNotificationCenterAdapter(List<NotificationModel> Items, Context context, @NonNull ItemClickLibraryInterface listener,
                                                           List<ItemLibraryCurrencyModel> listDashboardCurrencies, List<ItemLibraryCurrencyModel> listAllCurrencies,
                                                           RecyclerView recyclerView, NotificationsListListener notificationsListListener, SharedPreferences sharedPreferences) {
        this.Items = Items;
        this.itemClickListener = listener;
        this.context = context;
        this.listDashboardCurrencies = listDashboardCurrencies;
        this.recyclerView = recyclerView;
        this.notificationsListListener=notificationsListListener;
        this.listAllCurrencies=listAllCurrencies;
        this.sharedPreferences=sharedPreferences;
    }

    @Override
    public RecyclerViewCurrenciesNotificationCenterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification_currency_vs_currency, parent, false);
        v.setLayoutParams(new RecyclerView.LayoutParams(
                ((RecyclerView) parent).getLayoutManager().getWidth(),
                context.getResources()
                        .getDimensionPixelSize(R.dimen.height_row_center)));

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final RecyclerViewCurrenciesNotificationCenterAdapter.ViewHolder viewHolder, final int position) {
        // viewHolder.hour.setText(Items.get(position).getDisplayHour());
        String[] splitCurrencies = Items.get(position).getKey().split("-");
        viewHolder.txtCountryCodeFrom.setText(splitCurrencies[0]);
        viewHolder.txtCountryCodeTo.setText(splitCurrencies[1]);
        for (ItemLibraryCurrencyModel item : listDashboardCurrencies) {
            if (item.getName().compareTo(splitCurrencies[0]) == 0) {
                viewHolder.txtCurrencyFrom.setText(item.getCurrency_name());
            }
            if (item.getName().compareTo(splitCurrencies[1]) == 0) {
                viewHolder.txtCurrencyTo.setText(item.getCurrency_name());
            }
        }
        viewHolder.imgDelete.setContentDescription(Items.get(position).getKey());
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.countTotalKeys(context);
                removeItem(((ImageView) view).getContentDescription().toString());


            }
        });
        viewHolder.Currencies[0]=splitCurrencies[0];
        viewHolder.Currencies[1]=splitCurrencies[1];
        viewHolder.linClickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SetupPushNotificationsActivity.class);
                i.putExtra("Currencies", (Serializable) listDashboardCurrencies);
                i.putExtra("positionFrom", viewHolder.Currencies[0]);
                i.putExtra("positionTo",  viewHolder.Currencies[1]);
                i.putExtra("AllListCurrencies",(Serializable) listAllCurrencies);
                i.putExtra("notificationModelList", (Serializable)Items);
                context.startActivity(i);
            }
        });
        String themeSelected = sharedPreferences.getString(context.getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            viewHolder.txtCountryCodeFrom.setTextColor(context.getResources().getColor(R.color.primaryTextTheme1));
            viewHolder.txtCountryCodeTo.setTextColor(context.getResources().getColor(R.color.primaryTextTheme1));
            viewHolder.txtVS.setTextColor(context.getResources().getColor(R.color.primaryTextTheme1));
        } else {
            if (themeSelected.contains("Daylight")) {
                viewHolder.txtCountryCodeFrom.setTextColor(context.getResources().getColor(R.color.primaryTextTheme2));
                viewHolder.txtCountryCodeTo.setTextColor(context.getResources().getColor(R.color.primaryTextTheme2));
                viewHolder.txtVS.setTextColor(context.getResources().getColor(R.color.primaryTextTheme2));
            }
        }

    }


    public void removeItem(String nameDelete) {
        int positionDelete = 0;
        for (int i = 0; i < Items.size(); i++) {
            if (Items.get(i).getKey().equals(nameDelete)) {
                String key = Items.get(i).getKey();
                Items.remove(i);
                positionDelete = i;
                notifyItemRemoved(positionDelete);
                notificationsListListener.removeNotification(key);
            }
        }
        Log.d("","");
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    @Override
    public void onClick(View view) {

    }

   /* public void showAllControllsItems() {
        RecyclerViewCurrenciesNotificationCenterAdapter.ViewHolder holderHide;
        for (int pos = 0; pos < Items.size(); pos++) {
            holderHide = (RecyclerViewCurrenciesNotificationCenterAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
            if (holderHide != null) {
                holderHide.imgDelete.setVisibility(VISIBLE);
                holderHide.imgShow.setVisibility(View.GONE);

            }
        }
    }

    public void hideAllControllsItems() {
        Utilities.closeKeyboard(context);
        RecyclerViewCurrenciesNotificationCenterAdapter.ViewHolder holderHide;
        for (int pos = 0; pos < Items.size(); pos++) {
            holderHide = (RecyclerViewCurrenciesNotificationCenterAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(pos);
            if (holderHide != null) {
                holderHide.imgDelete.setVisibility(View.GONE);
                holderHide.imgShow.setVisibility(VISIBLE);
            }
        }
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCurrencyFrom;
        public TextView txtCountryCodeFrom;
        public TextView txtCurrencyTo;
        public TextView txtCountryCodeTo;
        public ImageView imgShow;
        public ImageView imgDelete;
        public String[]Currencies= new String[2];
        public TextView txtVS;
        public LinearLayout linClickArea;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtCurrencyFrom = (TextView) itemLayoutView.findViewById(R.id.txtCurrencyFrom);
            txtCountryCodeFrom = (TextView) itemLayoutView.findViewById(R.id.txtCountryCodeFrom);
            txtCurrencyTo = (TextView) itemLayoutView.findViewById(R.id.txtCurrencyTo);
            txtCountryCodeTo = (TextView) itemLayoutView.findViewById(R.id.txtCountryCodeTo);
            imgShow = (ImageView) itemLayoutView.findViewById(R.id.imgShow);
            imgDelete = (ImageView) itemLayoutView.findViewById(R.id.imgDelete);
            txtVS=(TextView)itemLayoutView.findViewById(R.id.txtVS);
            linClickArea=(LinearLayout) itemLayoutView.findViewById(R.id.linClickArea);
        }
    }

}
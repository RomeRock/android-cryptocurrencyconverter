package com.romerock.apps.utilities.cryptocurrencyconverter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.romerock.apps.utilities.cryptocurrencyconverter.model.HourModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.NotificationModel;

import java.util.List;

/**
 * Created by Ebricko on 15/12/2016.
 */
public class RecyclerViewHoursAdapter extends RecyclerView.Adapter<RecyclerViewHoursAdapter.ViewHolder> implements View.OnClickListener {
    private final ItemClickLibraryInterface itemClickListener;
    private List<HourModel> Items;
    private Context context;
    private SharedPreferences sharedPreferences;

    public RecyclerViewHoursAdapter(List<HourModel> Items, Context context, @NonNull ItemClickLibraryInterface listener, SharedPreferences sharedPreferences) {
        this.Items = Items;
        this.itemClickListener = listener;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public RecyclerViewHoursAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_hour, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.hour.setText(Items.get(position).getDisplayHour());
        viewHolder.hourToErase = Items.get(position).getHour();
        viewHolder.linDeleteHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationModel.checkGlobal(context, false);
                for (int i = 0; i < Items.size(); i++) {
                    if (Items.get(i).getHour() == viewHolder.hourToErase) {
                        Items.remove(i);
                        Utilities.addIntestitial(context, "");
                        notifyItemRemoved(i);
                        ((SetupPushNotificationsActivity)context).saveNotification();
                    }
                }
            }
        });
        String themeSelected = sharedPreferences.getString(context.getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            viewHolder.imgX.setColorFilter(context.getResources().getColor(R.color.backgroundAccentTheme1));
            viewHolder.hour.setBackgroundColor(context.getResources().getColor(R.color.backgroundAccentTheme1));
        } else if (themeSelected.contains("Daylight")) {
            viewHolder.imgX.setColorFilter(context.getResources().getColor(R.color.backgroundAccentTheme2));
            viewHolder.hour.setBackgroundColor(context.getResources().getColor(R.color.backgroundAccentTheme2));
        }
    }
    @Override
    public int getItemCount() {
        return Items.size();
    }

    @Override
    public void onClick(View view) {

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView hour;
        public LinearLayout linDeleteHour;
        public int hourToErase;
        public ImageView imgX;
        public LinearLayout linConteiner;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            linDeleteHour = (LinearLayout) itemLayoutView.findViewById(R.id.linDeleteHour);
            hour = (TextView) itemView.findViewById(R.id.txtDisplayHour);
            imgX=(ImageView) itemView.findViewById(R.id.imgX);
            linConteiner=(LinearLayout)itemView.findViewById(R.id.linConteir);
        }
    }
}
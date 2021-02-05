package com.romerock.apps.utilities.cryptocurrencyconverter.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemSettings;

import java.util.List;

/**
 * Created by Ebricko on 15/12/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {
    private final ItemClickInterface itemClickListener;
    private List<ItemSettings> ItemSettings;
    private int position;
    private Context context;
    public RecyclerViewAdapter(List<com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemSettings> ItemSettings, @NonNull ItemClickInterface listener, Context context) {
        this.ItemSettings = ItemSettings;
        this.itemClickListener = listener;
        this.context=context;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.txtViewTitle.setText(ItemSettings.get(position).getTitle());

        if (ItemSettings.get(position).isSelected()) {
            viewHolder.item_rel.setBackgroundResource(R.drawable.border_item_recyclerview_selected_theme1);

        } else{
            viewHolder.item_rel.setBackgroundColor(0x00FF00);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClicked(view, position, ItemSettings.get(position).getTitle()); // call the onClick in the OnItemClickListener*/
            }
        });


        SharedPreferences sharedPrefs= context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
        String themeSelected = sharedPrefs.getString(context.getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            viewHolder.txtViewTitle.setTextColor(context.getResources().getColor(R.color.primaryTextTheme1));
            viewHolder.borderButton.setBackground(context.getResources().getDrawable(R.drawable.border_theme1));
        } else if (themeSelected.contains("Daylight")) {
            viewHolder.txtViewTitle.setTextColor(context.getResources().getColor(R.color.primaryTextTheme2));
            viewHolder.borderButton.setBackground(context.getResources().getDrawable(R.drawable.border_theme2));
        }
    }

    @Override
    public int getItemCount() {
        return ItemSettings.size();
    }

    @Override
    public void onClick(View view) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTitle, txtViewPreview , txtViewPreviewfull;
        public RelativeLayout rltViewPreview;
        public RelativeLayout item_rel;
        public LinearLayout borderButton;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            item_rel=(RelativeLayout)itemLayoutView.findViewById(R.id.item_relative);
            borderButton=(LinearLayout)itemLayoutView.findViewById(R.id.linPaddingsContainer);
        }
    }


}
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ebricko on 15/12/2016.
 */
public class RecyclerViewCurrenciesCatalogAdapter extends RecyclerView.Adapter<RecyclerViewCurrenciesCatalogAdapter.ViewHolder> implements View.OnClickListener {
    private final ItemClickLibraryInterface itemClickListener;
    private List<ItemLibraryCurrencyModel> Items;
    private List<ItemLibraryCurrencyModel> itemsInDashboar;
    private int position;
    private Context context;
    private String themeSelected;
    private SharedPreferences sharedPrefs;

    public RecyclerViewCurrenciesCatalogAdapter(List<ItemLibraryCurrencyModel> Items, Context context, @NonNull ItemClickLibraryInterface listener,
                                                List<ItemLibraryCurrencyModel> itemsInDashboar, SharedPreferences sharedPrefs) {
        this.Items = Items;
        this.itemClickListener = listener;
        this.context = context;
        this.itemsInDashboar = itemsInDashboar;
        this.sharedPrefs=sharedPrefs;
    }

    @Override
    public RecyclerViewCurrenciesCatalogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                              int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_items_currencies_catalog, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final String name = Items.get(position).getName();
        themeSelected = sharedPrefs.getString(context.getString(R.string.preferences_theme_tittle), null);
        viewHolder.txtViewTitle.setText(Items.get(position).getName() + " - " + Items.get(position).getCurrency_symbol());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClicked(view, Items.get(position), name); // call the onClick in the OnItemClickListener
            }
        });
        viewHolder.txtCurrent.setText(Items.get(position).getCurrency_name());
        if (Items.get(position).getName() != null) {
            String idSearch = Utilities.removeCharacters(Items.get(position).getName().toLowerCase());
            if (idSearch.compareTo("try") == 0)
                idSearch = idSearch + idSearch;
            idSearch=idSearch.replace("*","").replace("_","").replace("-","");
            int id = context.getResources().getIdentifier(idSearch, "drawable", context.getPackageName());
            viewHolder.imgFlag.setVisibility(View.VISIBLE);
            if (id== 0) {
                id = context.getResources().getIdentifier("generic", "drawable", context.getPackageName());

            }
            viewHolder.imgFlag.setImageResource(id);
        } else {
            viewHolder.imgFlag.setVisibility(View.INVISIBLE);
        }
        boolean haveItem =false;
        for (int i = 0; i < itemsInDashboar.size(); i++ ){
            if (itemsInDashboar.get(i).getName().toString().compareTo(Items.get(position).getName()) == 0){
                viewHolder.item_relative.setBackgroundResource(R.drawable.border_item_recyclerview_selected_theme1);
                haveItem=true;
            }
        }
        if(!haveItem){
            viewHolder.item_relative.setBackgroundResource(0);
        }
        if (themeSelected.contains("Night")) {
            viewHolder.linPrincipal.setBackground(context.getResources().getDrawable(R.drawable.border_theme1));
            viewHolder.txtViewTitle.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme1));
            viewHolder.txtCurrent.setTextColor(context.getResources().getColor(R.color.primaryTextTheme1));
        } else if (themeSelected.contains("Daylight")) {
            viewHolder.linPrincipal.setBackground(context.getResources().getDrawable(R.drawable.border_theme2));
            viewHolder.txtViewTitle.setTextColor(context.getResources().getColor(R.color.secundaryTextColorTheme2));
            viewHolder.txtCurrent.setTextColor(context.getResources().getColor(R.color.primaryTextTheme2));
        }
    }

    @Override
    public int getItemCount() {
        if(Items==null)
            return 0;
        else
            return Items.size();
    }

    @Override
    public void onClick(View view) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTitle;
        public TextView txtCurrent;
        public ImageView imgFlag;
        public RelativeLayout item_relative;
        public LinearLayout linPrincipal;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.item_title);
            txtCurrent = (TextView) itemView.findViewById(R.id.txtCurrent);
            imgFlag = (ImageView) itemLayoutView.findViewById(R.id.item_flag);
            item_relative = (RelativeLayout) itemLayoutView.findViewById(R.id.item_relative);
            linPrincipal=(LinearLayout)itemLayoutView.findViewById(R.id.linPrincipal);
        }
    }

    public void setFilter(List<ItemLibraryCurrencyModel> items) {
        Items = new ArrayList<>();
        Items.addAll(items);
        notifyDataSetChanged();
    }
}

package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.romerock.apps.utilities.cryptocurrencyconverter.MainActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemTouchHelperInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemTouchInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0f;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor ed;
    private final ItemTouchHelperInterface mAdapter;
    private Context context;
    public SimpleItemTouchHelperCallback(ItemTouchHelperInterface adapter, Context context) {
        mAdapter = adapter;
        this.context=context;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchInterface) {
                ItemTouchInterface itemViewHolder = (ItemTouchInterface) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(ALPHA_FULL);
        String defaultCurrencies = "";
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
        ed = sharedPrefs.edit();
        for (ItemLibraryCurrencyModel item : mAdapter.getItems()) {
            defaultCurrencies += item.getName() + "|";
        }
        ed.putString(context.getString(R.string.preferences_defaultCurrencies), defaultCurrencies);
        ed.commit();
      //  ((MainActivity)context).getDashboardAdapter().notifyDataSetChanged();
        setBasePosition();
        if (viewHolder instanceof ItemTouchInterface) {
            ItemTouchInterface itemViewHolder = (ItemTouchInterface) viewHolder;
            itemViewHolder.onItemClear();
        }
    }

    private void setBasePosition(){
        if(((MainActivity)context).baseNameCurrency.compareTo("")!=0)
        for (int i=0; i< mAdapter.getItems().size(); i++) {
            if(mAdapter.getItems().get(i).getName().toString().compareTo(((MainActivity)context).baseNameCurrency)==0)
                ((MainActivity)context).basePosition=i;
        }
    }
}


package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.romerock.apps.utilities.cryptocurrencyconverter.MainActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemTouchHelperInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemTouchInterface;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private static final int ITEM_VIEW_TYPE_LOADING = Integer.MAX_VALUE - 50; // Magic
    public static final float ALPHA_FULL = 1.0f;
    public static int y;
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
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.LEFT;
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
        y = (int) viewHolder.itemView.getY();
        Log.d("22 YYYYY","es y: "+y);

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
            y = (int) viewHolder.itemView.getY();
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }


    /*   if (isLoadingRow(position)) {
        loadingListItemCreator.onBindViewHolder(holder, position);
    } else {
        // Start a drag whenever the handle view it touched
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    if (mDragStartListener != null) mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        wrappedAdapter.onBindViewHolder(holder, position);*/


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
/*        String defaultCurrencies = "";
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
        ed = sharedPrefs.edit();
        for (ItemLibraryCurrencyModel item : mAdapter.getItems()) {
            defaultCurrencies += item.getName() + "|";
        }
        ed.putString(context.getString(R.string.preferences_defaultCurrencies), defaultCurrencies);
        ed.commit();*/
      //  ((MainActivity)context).getDashboardAdapter().notifyDataSetChanged();
    //    setBasePosition();
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

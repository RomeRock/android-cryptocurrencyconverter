package com.romerock.apps.utilities.cryptocurrencyconverter.interfaces;

import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;

import java.util.List;

public interface ItemTouchHelperInterface {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);

    List<ItemLibraryCurrencyModel> getItems();
}

package com.romerock.apps.utilities.cryptocurrencyconverter.interfaces;

import android.view.View;

import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;

/**
 * Created by Ebricko on 20/12/2016.
 */

public interface ItemClickLibraryInterface {
    void onItemClicked(View view, ItemLibraryCurrencyModel item, String code);
}

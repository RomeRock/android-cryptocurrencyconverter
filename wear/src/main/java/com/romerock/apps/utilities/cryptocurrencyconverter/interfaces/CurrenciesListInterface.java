package com.romerock.apps.utilities.cryptocurrencyconverter.interfaces;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Ebricko on 13/04/2018.
 */
public interface CurrenciesListInterface {
    void getCurrenciesList(DataSnapshot dataSnapshot);
}

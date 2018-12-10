package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities_wear;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CurrenciesListInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Ebricko on 14/03/2018.
 */

public class CurrencyConvertApiModel {
    public static int idForDrawable(Context context, String currency) {
        int id = context.getResources().getIdentifier(currency.toLowerCase(), "drawable", context.getPackageName());
        if (id == 0)
            id = context.getResources().getIdentifier("generic", "drawable", context.getPackageName());
        return id;
   }


    public static void getListCurrencies(FirebaseHelper firebaseHelper, final CurrenciesListInterface currenciesListInterface){
        Query myTopPostsQuery = firebaseHelper.getDataReference(firebaseHelper.getMAIN_PATH()).getRef();
        myTopPostsQuery.keepSynced(true);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currenciesListInterface.getCurrenciesList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getListCurrencies", "onCancelled");
            }
        });

    }

    public static List<ItemLibraryCurrencyModel> getListCurrenciesForDashboard(List<ItemLibraryCurrencyModel> listItems, String[] splitCurrencies) {
        List<ItemLibraryCurrencyModel> listDashboardCurrencies = new ArrayList<ItemLibraryCurrencyModel>();
        for (int i = 0; i < splitCurrencies.length; i++) {
            for (int countItems = 0; countItems < listItems.size(); countItems++) {
                if (splitCurrencies[i].compareTo(listItems.get(countItems).getName()) == 0) {
                    listDashboardCurrencies.add(listItems.get(countItems));
                }
            }
        }
        return listDashboardCurrencies;
    }

    public static int getPositionInList(String currentSelected, List<ItemLibraryCurrencyModel> items){
        int position=0;
        for(int i=0; i<items.size();i++){
            if(currentSelected.toUpperCase().compareTo(items.get(i).getName().toUpperCase())==0){
                position=i;
                break;
            }
        }
        return position;
    }

    public static List<ItemLibraryCurrencyModel> matchCurrenciesWithArray(Map<String, String> currenciesMap, List<ItemLibraryCurrencyModel> listItems) {
        if (currenciesMap.size() > 0 && listItems.size() > 0) {
            for (int i = 0; i < listItems.size(); i++) {
                if (currenciesMap.get(listItems.get(i).getName()) != null)
                    listItems.get(i).setCurrency(Double.parseDouble(currenciesMap.get(listItems.get(i).getName())));
                else
                    listItems.get(i).setCurrency(0.0);
            }
        }
        return listItems;
    }

    public static List<ItemLibraryCurrencyModel> matchCurrenciesWithPreferences( List<ItemLibraryCurrencyModel> itemsFromPreferences, List<ItemLibraryCurrencyModel> listItems) {
        if (itemsFromPreferences.size() > 0 && listItems.size() > 0) {
            for(int i=0; i<itemsFromPreferences.size();i++){
                for(int allList=0; allList<listItems.size();allList++){
                    if(itemsFromPreferences.get(i).getName().compareTo(listItems.get(allList).getName())==0){
                        itemsFromPreferences.get(i).setCurrency(listItems.get(allList).getCurrency());
                        break;
                    }
                }
            }
        }
        return itemsFromPreferences;
    }

    public static List<ItemLibraryCurrencyModel> matchCurrenciesWithList(Map<String, String> currenciesMap, List<ItemLibraryCurrencyModel> listAllCurrencies, Map<String,
            JSONObject> valueSnap, TextView txtUpdateTime, Context context) {
        for (Map.Entry<String, JSONObject> entry : valueSnap.entrySet()) {
            try {
                if (entry.getKey().toString().equals("lastest")) {
                    if(txtUpdateTime!=null) {
                        String time = String.valueOf(new JSONObject((Map) entry.getValue()).getString("updated_tstamp"));
                        txtUpdateTime.setText(context.getString(R.string.update_time) + " " + Utilities_wear.getHour(Long.parseLong(time), context));
                    }
                    JSONObject json = new JSONObject(new JSONObject((Map) entry.getValue()).getString("currencies"));
                    Iterator<String> temp = json.keys();
                    while (temp.hasNext()) {
                        String key = temp.next();
                        currenciesMap.put(key, String.valueOf(json.get(key)));
                    }
                    listAllCurrencies = CurrencyConvertApiModel.matchCurrenciesWithArray(currenciesMap, listAllCurrencies);
                } else if (entry.getKey().toString().equals("library")) {
                    try {
                        JSONObject json = new JSONObject((Map) entry.getValue());
                        Iterator<String> temp = json.keys();
                        while (temp.hasNext()) {
                            String key = temp.next();
                            Gson gson = new GsonBuilder().create();
                            ItemLibraryCurrencyModel itemLibraryCurrencyModel = gson.fromJson(String.valueOf((JSONObject) json.get(key)), ItemLibraryCurrencyModel.class);
                            itemLibraryCurrencyModel.setName(key);
                            listAllCurrencies.add(itemLibraryCurrencyModel);
                        }
                        Collections.sort(listAllCurrencies, new Comparator<ItemLibraryCurrencyModel>() {
                            @Override
                            public int compare(ItemLibraryCurrencyModel itemLibraryCurrencyModel, ItemLibraryCurrencyModel t1) {
                                return itemLibraryCurrencyModel.getName().compareTo(t1.getName());
                            }
                        });
                        listAllCurrencies = CurrencyConvertApiModel.matchCurrenciesWithArray(currenciesMap, listAllCurrencies);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return listAllCurrencies;
    }
}

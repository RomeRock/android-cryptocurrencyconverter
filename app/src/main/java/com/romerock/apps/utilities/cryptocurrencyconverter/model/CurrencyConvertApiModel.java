package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CurrenciesListInterface;

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

    private float minPercentage;
    private float maxPercentage;
    private float avgPercentage;
    private float currentValue;
    private float minValue;
    private float maxValue;
    private float avgValue;
    private String fromsymbol;
    private String tosymbol;
    private float startValue;
    private float startPercentage;
    private String startDate;
    private String startDateSQL;
    private float fluctuationValue;
    private float fluctuationPercentage;

    public static int idForDrawable(Context context, String currency) {
        int id = context.getResources().getIdentifier(currency.toLowerCase(), "drawable", context.getPackageName());
        if (id == 0)
            id = context.getResources().getIdentifier("generic", "drawable", context.getPackageName());
        return id;

    }

    public static Animation getAnimationSlide(Context context, boolean isUp) {
        Animation slide;
        if (isUp)
            slide = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide_up);
        else
            slide = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.slide_down);
        return slide;
    }

    public static String setNewCurrencyFromSpinnerList(Context context, boolean isUp, LinearLayout linearLayout, ImageView flagImg, TextView flagText, List<ItemLibraryCurrencyModel> items, int position) {

        if (isUp) {
            linearLayout.startAnimation(CurrencyConvertApiModel.getAnimationSlide(context, true));
            position--;
            if (position < 0) {
                position = items.size() - 1;
            }
        } else {
            linearLayout.startAnimation(CurrencyConvertApiModel.getAnimationSlide(context, false));
            position++;
            if (position > items.size() - 1)
                position = 0;
        }
        String idSearch = items.get(position).getName().toString().toLowerCase();
        if (idSearch.compareTo("try") == 0)
            idSearch = idSearch + idSearch;
        int id = context.getResources().getIdentifier(idSearch, "drawable", context.getPackageName());
        if (id == 0) {
            id = context.getResources().getIdentifier("generic", "drawable", context.getPackageName());
        }
        flagImg.setImageResource(id);
        flagText.setText(items.get(position).getName().toUpperCase());
        return items.get(position).getName().toUpperCase();
    }

    public static void getListCurrencies(final FirebaseHelper firebaseHelper, final CurrenciesListInterface currenciesListInterface, Context context) {
        final boolean[] cantConect = {Utilities.isNetworkAvailable(context)};

        Query myTopPostsQuery = firebaseHelper.getDataReference(firebaseHelper.getUPDATE_TIME()).getRef();
        myTopPostsQuery.keepSynced(true);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshotUpdate) {
                cantConect[0] = true;
                Query myTopPostsQuery = firebaseHelper.getDataReference(firebaseHelper.getCURRENCIES_LIBRARY()).getRef();
                myTopPostsQuery.keepSynced(true);
                myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cantConect[0] = true;
                        currenciesListInterface.getCurrenciesList(dataSnapshotUpdate, dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("getListCurrencies", "onCancelled");
                    }
                });
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

    public static int getPositionInList(String currentSelected, List<ItemLibraryCurrencyModel> items) {
        int position = 0;
        for (int i = 0; i < items.size(); i++) {
            if (currentSelected.toUpperCase().compareTo(items.get(i).getName().toUpperCase()) == 0) {
                position = i;
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

    public static List<ItemLibraryCurrencyModel> matchCurrenciesWithPreferences(List<ItemLibraryCurrencyModel> itemsFromPreferences, List<ItemLibraryCurrencyModel> listItems) {
        if (itemsFromPreferences.size() > 0 && listItems.size() > 0) {
            for (int i = 0; i < itemsFromPreferences.size(); i++) {
                for (int allList = 0; allList < listItems.size(); allList++) {
                    if (itemsFromPreferences.get(i).getName().compareTo(listItems.get(allList).getName()) == 0) {
                        itemsFromPreferences.get(i).setCurrency(listItems.get(allList).getCurrency());
                        break;
                    }
                }
            }
        }
        return itemsFromPreferences;
    }

    public static List<ItemLibraryCurrencyModel> matchCurrenciesWithList(Map<String, JSONObject> valueSnapLastest, Map<String, JSONObject> valueSnapLibrary, TextView txtUpdateTime, Context context) {
        // ------------- Lastest
        Map<String, String> currenciesMap= new ArrayMap<>();
        List<ItemLibraryCurrencyModel>  listAllCurrencies = new ArrayList<>();
        List<ItemLibraryCurrencyModel>  listLibraryCurrencies = new ArrayList<>();
        //-------- Library
        for (Map.Entry<String, JSONObject> entryLibrary : valueSnapLibrary.entrySet()) {
            try {

                JSONObject json = new JSONObject((Map) entryLibrary.getValue());
                ItemLibraryCurrencyModel itemLibraryCurrencyModel= new ItemLibraryCurrencyModel();
                if(json.has("currency_name")){
                    itemLibraryCurrencyModel.setCurrency_name(json.getString("currency_name"));
                }
                if(json.has("country_name")){
                    itemLibraryCurrencyModel.setCountry_name(json.getString("country_name"));
                }
                if(json.has("currency_symbol")){
                    itemLibraryCurrencyModel.setCurrency_symbol(json.getString("currency_symbol"));
                }
                if(json.has("country_iso2")){
                    itemLibraryCurrencyModel.setCountry_iso2(json.getString("country_iso2"));
                }
                itemLibraryCurrencyModel.setName(entryLibrary.getKey());
                listLibraryCurrencies.add(itemLibraryCurrencyModel);
            } catch (Exception e){
                Log.d("","");
            }
        }
        for (Map.Entry<String, JSONObject> entry : valueSnapLastest.entrySet()) {
            try {
                if (entry.getKey().toString().equals("updated_tstamp")) {
                    if (txtUpdateTime != null) {
                        String time = String.valueOf(entry.getValue());
                        txtUpdateTime.setText(context.getString(R.string.update_time) + " " + Utilities.getHour(Long.parseLong(time), context));
                    }
                } else {
                    if (entry.getKey().toString().equals("currencies")){
                        JSONObject json = new JSONObject((Map) entry.getValue());
                        Iterator<String> temp = json.keys();
                        while (temp.hasNext()) {
                            String key = temp.next();
                            //  currenciesMap.put(key, String.valueOf(json.get(key)));
                            for(int i=0; i<listLibraryCurrencies.size(); i++){
                                if(listLibraryCurrencies.get(i).getName().compareTo(key)==0) {
                                    listLibraryCurrencies.get(i).setCurrency(Double.valueOf(String.valueOf(json.get(key))));
                                    break;
                                }
                            }
                        }
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(listLibraryCurrencies, new Comparator<ItemLibraryCurrencyModel>() {
            @Override
            public int compare(ItemLibraryCurrencyModel itemLibraryCurrencyModel, ItemLibraryCurrencyModel t1) {
                return itemLibraryCurrencyModel.getName().compareTo(t1.getName());
            }
        });
        return listLibraryCurrencies;
    }

    public float getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(float minPercentage) {
        this.minPercentage = minPercentage;
    }

    public float getMaxPercentage() {
        return maxPercentage;
    }

    public void setMaxPercentage(float maxPercentage) {
        this.maxPercentage = maxPercentage;
    }

    public float getAvgPercentage() {
        return avgPercentage;
    }

    public void setAvgPercentage(float avgPercentage) {
        this.avgPercentage = avgPercentage;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getAvgValue() {
        return avgValue;
    }

    public void setAvgValue(float avgValue) {
        this.avgValue = avgValue;
    }

    public String getFromsymbol() {
        return fromsymbol;
    }

    public void setFromsymbol(String fromsymbol) {
        this.fromsymbol = fromsymbol;
    }

    public String getTosymbol() {
        return tosymbol;
    }

    public void setTosymbol(String tosymbol) {
        this.tosymbol = tosymbol;
    }

    public float getStartValue() {
        return startValue;
    }

    public void setStartValue(float startValue) {
        this.startValue = startValue;
    }

    public float getStartPercentage() {
        return startPercentage;
    }

    public void setStartPercentage(float startPercentage) {
        this.startPercentage = startPercentage;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDateSQL() {
        return startDateSQL;
    }

    public void setStartDateSQL(String startDateSQL) {
        this.startDateSQL = startDateSQL;
    }

    public float getFluctuationValue() {
        return fluctuationValue;
    }

    public void setFluctuationValue(float fluctuationValue) {
        this.fluctuationValue = fluctuationValue;
    }

    public float getFluctuationPercentage() {
        return fluctuationPercentage;
    }

    public void setFluctuationPercentage(float fluctuationPercentage) {
        this.fluctuationPercentage = fluctuationPercentage;
    }
}

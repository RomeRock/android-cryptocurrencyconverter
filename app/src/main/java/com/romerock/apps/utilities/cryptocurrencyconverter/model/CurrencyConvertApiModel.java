package com.romerock.apps.utilities.cryptocurrencyconverter.model;

import android.content.Context;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
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
        int id = context.getResources().getIdentifier(currency.toLowerCase().replace("*",""), "drawable", context.getPackageName());
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
        String idSearch = items.get(position).getName().toString().toLowerCase().replace("*","");
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

    public static void getListCurrencies(FirebaseHelper firebaseHelper, final CurrenciesListInterface currenciesListInterface, Context context) {
        final boolean[] cantConect = {Utilities.isNetworkAvailable(context)};
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(5000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (!cantConect[0]) {
                        if (currenciesListInterface != null)
                            currenciesListInterface.getCurrenciesList(null);
                    }
                }
            }
        };
        thread.start();

        Query myTopPostsQuery = firebaseHelper.getDataReference(firebaseHelper.getMAIN_PATH()).getRef();
        myTopPostsQuery.keepSynced(true);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cantConect[0] = true;
                thread.interrupt();
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

    public static List<ItemLibraryCurrencyModel> matchCurrenciesWithList(Map<String, String> currenciesMap, List<ItemLibraryCurrencyModel> listAllCurrencies, Map<String,
            JSONObject> valueSnap, TextView txtUpdateTime, Context context) {
        for (Map.Entry<String, JSONObject> entry : valueSnap.entrySet()) {
            try {
                if (entry.getKey().toString().equals("lastest")) {
                    if (txtUpdateTime != null && new JSONObject((Map) entry.getValue()).has("updated_tstamp")) {
                        String time = String.valueOf(new JSONObject((Map) entry.getValue()).getString("updated_tstamp"));
                        txtUpdateTime.setText(context.getString(R.string.update_time) + " " + Utilities.getHour(Long.parseLong(time), context));
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

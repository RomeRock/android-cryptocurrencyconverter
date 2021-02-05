package com.romerock.apps.utilities.cryptocurrencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities_wear;
import com.romerock.apps.utilities.cryptocurrencyconverter.adapters.RecyclerViewCurrenciesDashboardAdapter;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SimpleItemTouchHelperCallback;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CurrenciesListInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.OnStartDragListenerInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.CurrencyConvertApiModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends WearableActivity implements OnStartDragListenerInterface {

    @BindView(R.id.txtUpdate)
    TextView txtUpdate;
    @BindView(R.id.linNoCurrents)
    LinearLayout linNoCurrents;
    @BindView(R.id.linAddCurrency)
    LinearLayout linAddCurrency;
    private int CURRENCY_SELECTED = 210;
    public static final int DISMISS_TIMEOUT = 2000;
    private TextView mTextView;
    private Map<String, JSONObject> valueSnap;
    private FirebaseHelper firebaseHelper;
    private List<ItemLibraryCurrencyModel> listItems;
    private List<ItemLibraryCurrencyModel> listDashboardCurrencies;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor ed;
    @BindView(R.id.recyclerCurrencyDashboard)
    RecyclerView recyclerCurrencyDashboard;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private String[] splitCurrencies;
    private ItemTouchHelper.Callback callback;
    public double baseValue = 1;
    public int basePosition = 0;
    public String baseNameCurrency = "";
    Map<String, String> currenciesMap = new HashMap<String, String>();
    private RecyclerViewCurrenciesDashboardAdapter dashboardAdapter;
    private ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        ed = sharedPrefs.edit();
        firebaseHelper = FirebaseHelper.getInstance();
        setAmbientEnabled();
        checkCurrencySaved();
    }

    private void checkCurrencySaved() {
        if (!sharedPrefs.contains(getString(R.string.preferences_defaultCurrencies))) {
            String defaultCurrencies = "";
            String currency = Utilities_wear.getCurrencyDefault();
            defaultCurrencies = currency.toString() + "|";
            if (currency.toString().compareTo("USD") != 0)
                defaultCurrencies += "USD|";
            if (currency.toString().compareTo("EUR") != 0)
                defaultCurrencies += "EUR|";
            defaultCurrencies += "BTC|";
            ed.putString(getString(R.string.preferences_defaultCurrencies), defaultCurrencies);
            ed.commit();
        }
        getUpdateFirebase();
    }

    void getUpdateFirebase() {
        String currenciesSaved = sharedPrefs.getString(getString(R.string.preferences_defaultCurrencies), "").toString();
        if (currenciesSaved.contains("|")) {
            splitCurrencies = currenciesSaved.split("\\|");
        } else {
            splitCurrencies = new String[1];
            splitCurrencies[0] = currenciesSaved;
        }
        CurrencyConvertApiModel.getListCurrencies(firebaseHelper, new CurrenciesListInterface() {
            @Override
            public void getCurrenciesList(DataSnapshot dataSnapshot) {
                valueSnap = (Map<String, JSONObject>) dataSnapshot.getValue();
                listItems = new ArrayList<ItemLibraryCurrencyModel>();
                listItems = CurrencyConvertApiModel.matchCurrenciesWithList(currenciesMap, listItems, valueSnap, txtUpdate, MainActivity.this);
                listDashboardCurrencies = CurrencyConvertApiModel.getListCurrenciesForDashboard(listItems, splitCurrencies);
                recyclerCurrencyDashboard.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                dashboardAdapter = new RecyclerViewCurrenciesDashboardAdapter(listDashboardCurrencies, MainActivity.this, new ItemClickLibraryInterface() {
                    @Override
                    public void onItemClicked(View view, ItemLibraryCurrencyModel item, String code) {

                    }
                }, MainActivity.this, recyclerCurrencyDashboard);

                recyclerCurrencyDashboard.setAdapter(dashboardAdapter);
                recyclerCurrencyDashboard.setItemAnimator(new DefaultItemAnimator());
                callback = new SimpleItemTouchHelperCallback(dashboardAdapter, MainActivity.this);
                mItemTouchHelper = new ItemTouchHelper(callback);
                mItemTouchHelper.attachToRecyclerView(recyclerCurrencyDashboard);
                progressBar.setVisibility(View.GONE);
                if (splitCurrencies[0].compareTo("") != 0)
                    linNoCurrents.setVisibility(View.GONE);
            }
        });
    }


    @OnClick(R.id.linAddCurrency)
    public void onViewClicked() {
        Intent i = new Intent(MainActivity.this, CurrencyActivity.class);
        i.putExtra("itemsData", (Serializable) listItems);
        i.putExtra("itemsInDashboard", (Serializable) listDashboardCurrencies);
        startActivityForResult(i, CURRENCY_SELECTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle res = data.getExtras();
            if (requestCode == CURRENCY_SELECTED && resultCode == RESULT_OK) {
                boolean haveCurrency = false;
                ItemLibraryCurrencyModel itemReturn = (ItemLibraryCurrencyModel) res.get("Current");
                for (ItemLibraryCurrencyModel item : listDashboardCurrencies) {
                    if (item.getName().toLowerCase().compareTo(itemReturn.getName().toLowerCase()) == 0)
                        haveCurrency = true;
                }
                if (!haveCurrency) {
                    listDashboardCurrencies.add(itemReturn);
                    ed = sharedPrefs.edit();
                    String defaultCurrencies = "";
                    for (ItemLibraryCurrencyModel item : listDashboardCurrencies) {
                        defaultCurrencies += item.getName() + "|";
                    }
                    ed.putString(getString(R.string.preferences_defaultCurrencies), defaultCurrencies);
                    ed.commit();
                    dashboardAdapter.notifyDataSetChanged();
                    dashboardAdapter.makeOperation();
                    if (listDashboardCurrencies.size() > 0) {
                        linNoCurrents.setVisibility(View.GONE);
                    }
                } else {
                    Toast toast = Toast.makeText(
                            MainActivity.this,
                            getString(R.string.have_currency),
                            Toast.LENGTH_LONG);
                }
            }

        }
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}

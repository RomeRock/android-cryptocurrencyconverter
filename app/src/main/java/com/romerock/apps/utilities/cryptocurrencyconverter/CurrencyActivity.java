package com.romerock.apps.utilities.cryptocurrencyconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.adapters.RecyclerViewCurrenciesCatalogAdapter;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ThemeInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities.getThemePreferences;

public class CurrencyActivity extends AppCompatActivity implements ThemeInterface {

    @BindView(R.id.toolbarback)
    Toolbar toolbarback;
    @BindView(R.id.txtTitle)
    TextView tittle;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.toolbarsearch)
    Toolbar toolbarsearch;
    @BindView(R.id.adView)
    AdView adView;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewCurrenciesCatalogAdapter recyclerViewAdapter;
    private String[] locales;
    private RecyclerViewCurrenciesCatalogAdapter mAdapter;

    private List<ItemLibraryCurrencyModel> itemsData;
    private List<ItemLibraryCurrencyModel> itemsInDashboard;
    private SharedPreferences sharedPrefs;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.ChangeLanguage(this);
        setTheme(getThemePreferences(getApplication()));
        setContentView(R.layout.activity_currency);
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        ButterKnife.bind(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerCurrency);
        Window window = this.getWindow();
        Utilities.colorStatusBar(getApplication(), window);
        itemsData = new ArrayList<ItemLibraryCurrencyModel>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            itemsData = (List<ItemLibraryCurrencyModel>) getIntent().getSerializableExtra("itemsData");
            itemsInDashboard = (List<ItemLibraryCurrencyModel>) getIntent().getSerializableExtra("itemsInDashboard");
        }
        if (itemsData==null) {
            itemsData=new ArrayList<ItemLibraryCurrencyModel>();
            DialogsHelper.showSnackBar(coordinator, getString(R.string.error_internet), getResources().getColor(R.color.alert_snackbar));
        }
        if(itemsInDashboard==null)
            itemsInDashboard=new ArrayList<ItemLibraryCurrencyModel>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.scrollToPosition(position);
        mAdapter = new RecyclerViewCurrenciesCatalogAdapter(itemsData, CurrencyActivity.this, new ItemClickLibraryInterface() {
            @Override
            public void onItemClicked(View view, ItemLibraryCurrencyModel item, String code) {
                Intent intent = new Intent();
                intent.putExtra("Current", item);
                setResult(RESULT_OK, intent);
                Utilities.closeKeyboard(CurrencyActivity.this);
                finish();
            }
        }, itemsInDashboard, sharedPrefs);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        AdRequest adRequest = Utilities.addBanner(CurrencyActivity.this, "", getString(R.string.banner_ad_unit_id));
        if (adRequest != null)
            adView.loadAd(adRequest);
        else
            adView.setVisibility(View.GONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String textSearch) {
                List<ItemLibraryCurrencyModel> itemsDataTemp = new ArrayList<ItemLibraryCurrencyModel>();
                if(itemsData!=null) {
                    for (int i = 0; i < itemsData.size(); i++) {
                        int count = 0;
                        if (itemsData.get(i).getName() != null)
                            if (itemsData.get(i).getName().toUpperCase().contains(textSearch.toUpperCase()))
                                count++;
                        if (itemsData.get(i).getCurrency_name() != null)
                            if (itemsData.get(i).getCurrency_name().toUpperCase().contains(textSearch.toUpperCase()))
                                count++;
                        if (itemsData.get(i).getName() != null)
                            if (itemsData.get(i).getName().toUpperCase().contains(textSearch.toUpperCase()))
                                count++;
                        if (itemsData.get(i).getCountry_name() != null)
                            if (itemsData.get(i).getCountry_name().toUpperCase().contains(textSearch.toUpperCase()))
                                count++;
                        if (count > 0)
                            itemsDataTemp.add(itemsData.get(i));
                    }
                    mAdapter.setFilter(itemsDataTemp);
                }
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utilities.closeKeyboard(CurrencyActivity.this);
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOffline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOnline();
        setThemeByActivity();
    }

    @Override
    public void onBackPressed() {
        Utilities.closeKeyboard(CurrencyActivity.this);
        if (searchView.getVisibility() == View.VISIBLE) {
            searchView.setVisibility(View.GONE);
            toolbarsearch.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    @OnClick({R.id.toolbarback, R.id.toolbarsearch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbarback:
                finish();
                break;
            case R.id.toolbarsearch:
                toolbarsearch.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
        }
    }

    @Override
    public void setThemeByActivity() {
        String themeSelected = sharedPrefs.getString(getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            toolbarback.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            tittle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            searchView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            if (themeSelected.contains("Daylight")) {
                toolbarback.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
                tittle.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
                searchView.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
            }
        }
    }
}

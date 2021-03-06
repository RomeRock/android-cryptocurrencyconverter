package com.romerock.apps.utilities.cryptocurrencyconverter;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.adapters.RecyclerViewAdapter;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.LocaleHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ThemeInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemSettings;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities.getThemePreferences;

public class LanguageSettingsActivity extends AppCompatActivity implements ThemeInterface {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerColors;
    @BindView(R.id.toolbarback)
    Toolbar toolbarback;
    @BindView(R.id.tittle)
    TextView tittle;
    @BindView(R.id.relContent)
    RelativeLayout relContent;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Language();
    }

    private void Language() {
        Utilities.ChangeLanguage(this);
        setTheme(getThemePreferences(getApplication()));
        setContentView(R.layout.activity_settings);
        Window window = this.getWindow();
        Utilities.colorStatusBar(getApplication(), window);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        ButterKnife.bind(this);
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        Utilities.colorStatusBar(LanguageSettingsActivity.this, this.getWindow());
        String[] languages = getResources().getStringArray(R.array.languages);
        final String[] symbols = getResources().getStringArray(R.array.language_simbol);

        List<ItemSettings> itemsData = new ArrayList<ItemSettings>(languages.length);
        for (int i = 0; i < languages.length; i++) {
            itemsData.add(new ItemSettings(languages[i].toUpperCase(), sharedPrefs.getString(getString(R.string.language_settings), "").equals(symbols[i]) ? true : false));
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(itemsData, new ItemClickInterface() {
            @Override
            public void onItemClicked(View view, int position, String code) {
                SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
                SharedPreferences.Editor ed;
                ed = sharedPrefs.edit();
                FirebaseHelper.subscribeUnsubscribeTopic(LanguageSettingsActivity.this, false);
                ed.putString(getString(R.string.language_settings), symbols[position]);
                LocaleHelper.setLocale(LanguageSettingsActivity.this, symbols[position]);
                ed.commit();
                tittle.setText(getString(R.string.settings_option_select_lenguage));
                onBackPressed();
            }
        }, LanguageSettingsActivity.this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }



    @OnClick(R.id.toolbarback)
    public void onClick() {
        finish();
    }

    @Override
    public void setThemeByActivity() {
        String themeSelected = sharedPrefs.getString(getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            toolbarback.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tittle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            if (themeSelected.contains("Daylight")) {
                toolbarback.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDaylight));
                tittle.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOnline();
        setThemeByActivity();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOffline();
    }

}

package com.romerock.apps.utilities.cryptocurrencyconverter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.applovin.sdk.AppLovinSdk;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.LocaleHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.PushNotificationModel;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends Activity {
    protected int _splashTime = 2000;
    private SharedPreferences sharedPrefs;
    private Thread splashTread;
    private boolean isAllShoppingsIntetn = false;
    private String action = null;
    private SplashActivity sPlashScreen;
    private SharedPreferences.Editor ed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        AppLovinSdk.initializeSdk(SplashActivity.this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(CipherAES.getApiKeyTwitter(), CipherAES.getApiSecretKeyTwitter());
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        Stetho.initializeWithDefaults(this);  //   TODO  ------- BORRAR ES SOLO PARA DEBUG
        sPlashScreen = this;
        PushNotificationModel.CleanBadges(this);
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        ed = sharedPrefs.edit();

        if (!sharedPrefs.contains(getString(R.string.udidAndroid))) {
            String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            try {
                ed.putString(getString(R.string.udidAndroid), CipherAES.cipher(androidId));
                ed.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (!sharedPrefs.contains(getString(R.string.preferences_theme_tittle))) {
            ed.putString(getString(R.string.preferences_theme_tittle), "Night");
            ed.commit();
        }
        if (sharedPrefs.contains(getString(R.string.preferences_check_inventory))) {
            ed.remove(getString(R.string.preferences_check_inventory));
            ed.commit();
        }
        if (!sharedPrefs.contains(getString(R.string.preferences_interstitial_count))) {
            ed.putInt(getString(R.string.preferences_interstitial_count), 0);
            ed.commit();
        }
        SingletonInAppBilling.Instance().setInvalidated(false);
        if (!sharedPrefs.contains(getString(R.string.purchaseAndroid))) {
            try {
                ed.putString(getString(R.string.purchaseAndroid), CipherAES.cipher("free"));
                ed.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!sharedPrefs.contains(getString(R.string.globalCount))) {
            ed.putInt(getString(R.string.globalCount), 0);
            ed.commit();
        }
        if (!sharedPrefs.contains(getString(R.string.preferences_count_keys))) {
            ed.putInt(getString(R.string.preferences_count_keys), 0);
        }
        SingletonInAppBilling.Instance().setShowPopUp(false);

        if (!sharedPrefs.contains(getString(R.string.preferences_count_some_love))) {
            ed.putInt(getString(R.string.preferences_count_some_love), 0);
            ed.putBoolean(getString(R.string.preferences_rate), false);

        } else {
            int countSomeLove = sharedPrefs.getInt(getString(R.string.preferences_count_some_love), 0);
            countSomeLove++;
            ed.putInt(getString(R.string.preferences_count_some_love), countSomeLove);
        }

        if (!sharedPrefs.contains(getString(R.string.dateForInterstitial))) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            c.add(Calendar.DATE, 3);
            String end_date = df.format(c.getTime());
            ed.putString(getString(R.string.dateForInterstitial), end_date);
        }

        if (!sharedPrefs.contains(getString(R.string.count1ARange))) {
            ed.putInt(getString(R.string.count1ARange), 0);
        }
        if (!sharedPrefs.contains(getString(R.string.count3ARange))) {
            ed.putInt(getString(R.string.count3ARange), 0);
        }
        if (!sharedPrefs.contains(getString(R.string.countCryptoAded))) {
            ed.putInt(getString(R.string.countCryptoAded), 0);
        }

        ed.commit();
        // Detect language
        if (!sharedPrefs.contains(getString(R.string.preferences_defaultCurrencies))) {
            String defaultCurrencies = "";
            String[] defaultCryptoCurrencies = {"USD", "EUR", "JPY", "GBP", "CAD", "CHF", "CNY", "CNH", "AUD", "NZD", "SEK"};

            String currency = Utilities.getCurrencyDefault(SplashActivity.this);
            if (Arrays.asList(defaultCryptoCurrencies).contains(currency))
                defaultCurrencies = currency.toString() + "|";
            if (currency.toString().compareTo("USD") != 0)
                defaultCurrencies += "USD|";
            if (currency.toString().compareTo("EUR") != 0)
                defaultCurrencies += "EUR|";
            defaultCurrencies += "BTC|";
            defaultCurrencies += "BTH|";
            defaultCurrencies += "LTC|";
            defaultCurrencies += "DOGE|";
            defaultCurrencies += "ETH|";
            ed.putString(getString(R.string.preferences_defaultCurrencies), defaultCurrencies);
            ed.commit();
        }
        if (!sharedPrefs.contains(getString(R.string.preferences_defaultCurrencies_first_time))) {
            ed.putBoolean(getString(R.string.preferences_defaultCurrencies_first_time), true);
            ed.commit();
        }
        if (!sharedPrefs.contains(getString(R.string.language_settings))) {
            Locale current = getResources().getConfiguration().locale;
            if (current != null) {
                if (current.getLanguage().toLowerCase().equals("es")) {
                    ed.putString(getString(R.string.language_settings), "es");
                    LocaleHelper.setLocale(this, "es");
                } else {
                    if (current.getLanguage().toLowerCase().equals("fr")) {
                        ed.putString(getString(R.string.language_settings), "fr");
                        LocaleHelper.setLocale(this, "fr");
                    } else {
                        ed.putString(getString(R.string.language_settings), "en");
                        LocaleHelper.setLocale(this, "en");
                    }
                }
                ed.commit();
            }
        }
        //Process idPoll if have extra from push notification
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String symbol = getIntent().getExtras().getString("symbol");
            if (symbol != null) {
                String[] splitSymbol = symbol.split("-");
                Intent i = new Intent(this, DetailsActivity.class);
                i.putExtra("currencySelected", splitSymbol[0]);
                i.putExtra("currencyToSelected", splitSymbol[1]);
                startActivity(i);
            } else {
                processSplashTread();
            }
        } else {
            processSplashTread();
        }
    }

    private void processSplashTread() {
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(_splashTime);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setClass(sPlashScreen, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    finish();
                }
            }
        };
        splashTread.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }


}
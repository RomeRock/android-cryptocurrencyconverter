package com.romerock.apps.utilities.cryptocurrencyconverter.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.romerock.apps.utilities.cryptocurrencyconverter.BuildConfig;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;
import com.romerock.apps.utilities.cryptocurrencyconverter.SplashActivity;
import com.romerock.apps.utilities.cryptocurrencyconverter.api.ApiConfig;
import com.romerock.apps.utilities.cryptocurrencyconverter.api.RetrofitClient;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.LocaleHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ResponseModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.UserUdId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.TAG;

/**
 * Created by Ebricko on 13/03/2018.
 */

public class Utilities {
    public static void openKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void closeKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            if (((Activity) context).getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(
                        ((Activity) context).getCurrentFocus().getWindowToken(), 0);
    }

    public static String getHour(long time, Context context) {
        Date date = new Date(time * 1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm");
        jdf.setTimeZone(TimeZone.getDefault());
        String dateSend = jdf.format(date);
        return dateSend;
    }

    public static int getThemePreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
        String themeSelected = sharedPref.getString(context.getString(R.string.preferences_theme_tittle), null);
        int theme = 0;
        if (themeSelected.contains("Night")) theme = R.style.Night;
        else if (themeSelected.contains("Daylight")) theme = R.style.Daylight;
        return theme;
    }

    public static void colorStatusBar(Context context, Window window) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            int color = 0;
            switch (Utilities.getThemePreferences(context)) {
                case R.style.Night:
                    color = context.getResources().getColor(R.color.colorPrimary);
                    break;
                case R.style.Daylight:
                    color = context.getResources().getColor(R.color.colorAccent_Daylight);
                    break;
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public static void changeColorByValue(Context context, float val, TextView view) {
        if (val > 0)
            view.setTextColor(context.getResources().getColor(R.color.positive_color));
        else if (val < 0)
            view.setTextColor(context.getResources().getColor(R.color.negative_color));
        else
            view.setTextColor(context.getResources().getColor(R.color.dark_color));
    }

    public static void addArrow(Context context, float val, ImageView view) {
        int resID = 0;
        String uri = "@drawable/";
        if (val < 0) {
            resID = context.getResources().getIdentifier(uri + "path_2", null, context.getPackageName());
            view.setRotation(0);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(resID);
        } else if (val > 0) {
            view.setVisibility(View.VISIBLE);
            resID = context.getResources().getIdentifier(uri + "path_2_copy", null, context.getPackageName());
            view.setImageResource(resID);
        } else
            view.setVisibility(View.INVISIBLE);
    }


   /* public static String formatAmountOperation(String amount) {
        String loc = String.valueOf(Resources.getSystem().getConfiguration().locale);
        Locale locale = new Locale(loc);
        String pattern;
        pattern = "###,##0.00##";
        DecimalFormat decimalFormat = (DecimalFormat)
                NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(Double.parseDouble(amount));
    }*/

    public static String makeOperationWithFormat(double baseValue, Double currency, Double currencyDiv) {
        String result = "";
        if (baseValue == 0 || currency == 0 || currencyDiv == 0)
            result = "0";
        else
            result = getNumberString(Double.parseDouble(String.valueOf((baseValue * currency) / currencyDiv)));
        return result;
    }

    public static double eval(final String str, final Context context, final CoordinatorLayout coordinatorLayout) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {

                    //showSnackBar(coordinatorLayout, context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x = 0;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    try {
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } catch (Exception e) {
                        x = 0;
                        //  showSnackBar(coordinatorLayout, context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
                    }
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else {
                        //    showSnackBar(coordinatorLayout, context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
                    }
                } else {
                    //showSnackBar(coordinatorLayout, context.getString(R.string.error_eval), context.getResources().getColor(R.color.alert_snackbar));
                }
                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
                return x;
            }
        }.parse();
    }

   /* public static void SendMail(Context context, String subject, String content) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:" + context.getString(R.string.mail_support) + "?subject=" + subject + "&body=" + content);
        try {
            intent.setData(data);
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, context.getString(R.string.choose_email_error), Toast.LENGTH_SHORT).show();
        }
    }*/


    public static void goToLinks(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        context.startActivity(intent);
    }

    public static void ChangeLanguage(Context context) {
        SharedPreferences sharedPrefs;
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), context.MODE_PRIVATE);
        LocaleHelper.setLocale(context, sharedPrefs.getString(context.getString(R.string.language_settings), ""));
    }

    public static String getCurrencyDefault(SplashActivity splashActivity) {
        String currency;
        try {
            currency = String.valueOf(Currency.getInstance(Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            currency = "?"; // default symbol
        }
        return currency;
    }

    public static String getFormatedDate(String date) {
        Date oneWayTripDate = null;
        SimpleDateFormat input = null;
        SimpleDateFormat output = null;
        input = new SimpleDateFormat("yyyy-MM-dd");
        output = new SimpleDateFormat("MMM dd''yy");
        try {
            oneWayTripDate = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = output.format(oneWayTripDate);
        date = date.substring(0, 1).toUpperCase() + date.substring(1);
        return date;
    }


    public static String getFormatedDateOnlyTime(String date) {
        double dateToUse = Double.valueOf(date.replace(",", ".")) * 1000;
        Date d = new Date((long) dateToUse);
        DateFormat f = new SimpleDateFormat("MMM dd''yy'T'HH:mm a");
        date = f.format(d);
        return date;
    }

    public static String getCurrentLocalDateTimeStamp(boolean isWithTime) {
        SimpleDateFormat sdf;
        if (!isWithTime)
            sdf = new SimpleDateFormat("MMM dd''yy");
        else
            sdf = new SimpleDateFormat("MMM dd''yy HH:mm a");
        Date now = new Date();
        String strDate = sdf.format(now);
        strDate = strDate.substring(0, 1).toUpperCase() + strDate.substring(1);
        return strDate;
    }

    public static float getDateToUnixTimeStamp(String str_date) {
        DateFormat formatter;

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = (Date) formatter.parse(str_date);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getDateFromUnixTimestamp(long unixSeconds) {
        Date date = new java.util.Date(unixSeconds);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String getNumberString(double num) {
        NumberFormat defaultFormat = NumberFormat.getNumberInstance();
        defaultFormat.setMaximumFractionDigits(4);
        defaultFormat.setMinimumFractionDigits(2);
        String numberWithCurrent = defaultFormat.format(num);
        return numberWithCurrent;
    }

    public static Number getNumberDecimal(String numberString) {
        if (!numberString.isEmpty()) {
            if (!android.text.TextUtils.isDigitsOnly(numberString)) {
                try {
                  /*  char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
                    if(String.valueOf(separator).compareTo(",")==0){
                        numberString=numberString.replace(".",",")
                    }*/
                    Number number = new Number() {
                        @Override
                        public int intValue() {
                            return 0;
                        }

                        @Override
                        public long longValue() {
                            return 0;
                        }

                        @Override
                        public float floatValue() {
                            return 0;
                        }

                        @Override
                        public double doubleValue() {
                            return 0;
                        }
                    };
                    try {
                        NumberFormat defaultFormat = NumberFormat.getNumberInstance();
                        defaultFormat.setMaximumFractionDigits(4);
                        defaultFormat.setMinimumFractionDigits(2);
                        number = defaultFormat.parse(numberString);
                    } catch (Exception e) {
                        try {
                            double returnVal = Double.parseDouble(numberString);
                            return returnVal;
                        } catch (Exception val) {
                            return 0;
                        }
                    }
                    return number;
                } catch (Exception e) {
                    return Double.parseDouble(numberString);
                }
            } else {
                return Double.parseDouble(numberString);
            }
        } else return 0;
    }

    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }


    public static final String TestDevice(final String s) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    static InterstitialAd mInterstitialAd;
    public static void addIntestitial(Context context, String isFreeOrPremium) {
        if (todayMayorRegisterDay(context)) {
            if (isFreeOrPremium.isEmpty()) {
                isFreeOrPremium = SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context);
            }
            if (isFreeOrPremium.compareTo(UserUdId.getFREE()) == 0) {
                String deviceId, android_id;
                android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                deviceId = TestDevice(android_id).toUpperCase();
                AdRequest adRequest = new AdRequest.Builder()
                        //              .addTestDevice(deviceId)  // only for test
                        .build();
                //mInterstitialAd = new InterstitialAd(context);
                InterstitialAd.load(context, context.getResources().getString(R.string.interstitial_ad_unit_id), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                // The mInterstitialAd reference will be null until
                                // an ad is loaded.
                                mInterstitialAd = interstitialAd;
                                Log.i(TAG, "onAdLoaded");
                                if (mInterstitialAd != null) {
                                    mInterstitialAd.show(new Activity());
                                }
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error
                                Log.d(TAG, loadAdError.toString());
                                mInterstitialAd = null;
                            }
                        });
                /*mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_unit_id));
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }
                });*/
            }
        }
    }

    public static void addIntestitialWithCount(Context context, String isFreeOrPremium) {
        if (todayMayorRegisterDay(context)) {
            int COUNT_FOR_INTERSTITIAL = 5;
            SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
            if (isFreeOrPremium.isEmpty()) {

                isFreeOrPremium = SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context);

            }
            if (isFreeOrPremium.compareTo(UserUdId.getFREE()) == 0) {
                SharedPreferences.Editor ed = sharedPrefs.edit();
                int countInterstitital = sharedPrefs.getInt(context.getString(R.string.preferences_interstitial_count), 0);
                if (countInterstitital < COUNT_FOR_INTERSTITIAL) {
                    countInterstitital++;
                    ed.putInt(context.getString(R.string.preferences_interstitial_count), countInterstitital);
                    ed.commit();
                } else {
                    ed.putInt(context.getString(R.string.preferences_interstitial_count), 0);
                    ed.commit();
                    String deviceId, android_id;
                    android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    deviceId = TestDevice(android_id).toUpperCase();
                    AdRequest adRequest = new AdRequest.Builder()
                            //              .addTestDevice(deviceId)  // only for test
                            .build();
                    InterstitialAd.load(context, context.getResources().getString(R.string.interstitial_ad_unit_id), adRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    // The mInterstitialAd reference will be null until
                                    // an ad is loaded.
                                    mInterstitialAd = interstitialAd;
                                    Log.i(TAG, "onAdLoaded");
                                    if (mInterstitialAd != null) {
                                        mInterstitialAd.show(new Activity());
                                    }
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Handle the error
                                    Log.d(TAG, loadAdError.toString());
                                    mInterstitialAd = null;
                                }
                            });
                    /*final InterstitialAd mInterstitialAd;
                    mInterstitialAd = new InterstitialAd(context);
                    /*mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_unit_id));
                    AdRequest adRequest = new AdRequest.Builder()
                            //                           .addTestDevice(deviceId)  // only for test
                            .build();
                    mInterstitialAd.loadAd(adRequest);
                    mInterstitialAd.setAdListener(new AdListener() {
                        public void onAdLoaded() {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }
                    });*/
                }
            }
        }
    }

    public static boolean todayMayorRegisterDay(Context context) {
        SharedPreferences sharedPrefs;
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
        if (sharedPrefs.contains(context.getString(R.string.dateForInterstitial))) {
            String dateRegister = sharedPrefs.getString(context.getString(R.string.dateForInterstitial), "");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date strDate = null;
            try {
                strDate = sdf.parse(dateRegister);
                if (System.currentTimeMillis() > strDate.getTime()) {
                    return true;
                } else
                    return false;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }

        } else
            return false;

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public static void countTotalKeys(Context context) {
        if (SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context).compareTo(UserUdId.getFREE()) == 0) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
            int countKeys = sharedPrefs.getInt(context.getString(R.string.preferences_count_keys), 0);
            if (countKeys < 9) {
                countKeys++;
            } else {
                countKeys = 0;
                if(BuildConfig.FLAVOR=="google") {
                    Popup.SubscribeMe(context);
                }
                else{
                    Utilities.addIntestitial(context, UserUdId.getFREE());
                }
            }
            SharedPreferences.Editor ed = sharedPrefs.edit();
            ed.putInt(context.getString(R.string.preferences_count_keys), countKeys);
            ed.commit();
        }
    }

    public static AdRequest addBanner(Context context, String isFreeOrPremium, String banner_ad_unit) {
        String deviceId, android_id;
        AdRequest adRequest = null;
        if (isFreeOrPremium.isEmpty()) {
            isFreeOrPremium = SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context);
        }
        try {
            if (isFreeOrPremium.compareTo(UserUdId.getFREE()) == 0) {
                MobileAds.initialize(context);
                android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                deviceId = TestDevice(android_id).toUpperCase();
                adRequest = new AdRequest.Builder()
                        //   .addTestDevice(deviceId)  // For test
                        .build();
            }
        } catch (Exception e) {
            Log.d("Ads", "Error");
        }
        return adRequest;
    }

    public static void getKeyHashFacebook(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Facebook KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public static void SendMailToAPi(final Context context, final String subject, final String content, final CoordinatorLayout coordinator, final String email) {
        ApiConfig apiConfig = RetrofitClient.getApi().create(ApiConfig.class);
        retrofit2.Call<ResponseModel> call = apiConfig.sendMail(RetrofitClient.getAPIKEY(),
                RetrofitClient.getSourceCode(),
                content,
                RetrofitClient.getSourceLoc(),
                RetrofitClient.getOS(),
                context.getResources().getString(R.string.app_name), email);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseModel> call, Response<ResponseModel> response) {
                Log.d("onResponse", "");
                DialogsHelper.showSnackBar(coordinator, context.getString(R.string.feedback_success), context.getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseModel> call, Throwable t) {
                Log.d("onFailure", "");
            }

        });
    }

    public static int roundHour() {
        int min = 10;
        int max = 22;
        Random r = new Random();
        int hour = r.nextInt(max - min + 1) + min;
        return hour;
    }

    public static String removeCharacters(String s) {
        s = s.replace("*", "").replace("-", "").replace("_", "");
        return s;
    }

    public static void checkForBigBanner(final Context context, RelativeLayout content) {
        SharedPreferences sharedPrefs;
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
        if (context != null) {
            if (SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context).compareTo("free") == 0) {
                final AdView adView = new AdView(context);
                //mayor a 430
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                final int densityDpi = (int) (metrics.density * 160f);
                content.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) content.getLayoutParams();
                relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                content.setLayoutParams(relativeParams);
                final AdView mAdView = new AdView(context);
                if (densityDpi > 480)
                    mAdView.setAdSize(AdSize.LEADERBOARD);
                else
                    mAdView.setAdSize(AdSize.SMART_BANNER);
                mAdView.setAdUnitId(context.getResources().getString(R.string.banner_ad_unit_id));
                content.addView(mAdView);
                final AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.setAdListener(new AdListener() {

                    public void onAdFailedToLoad(int errorCode) {
                        mAdView.loadAd(adRequest);
                    }
                });
                mAdView.loadAd(adRequest);

            } else {
                content.setVisibility(View.GONE);
            }
        }
    }

    //if true set lock and block radio
    public static boolean checkLockStatusForRangeRadios(Context context, RadioButton range) {
        boolean returnSetLock = false;
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
        if (SingletonInAppBilling.Instance() != null) {
            String isFree = "free";
            if (SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context) == null) {
                try {
                    isFree = CipherAES.decipher(sharedPrefs.getString(context.getResources().getString(R.string.purchaseAndroid), "free"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(isFree);
            }
            if (SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(context).compareTo("free") == 0) {
                int MAX_CLICKS = 2;
                int clicks = 0;

                if (range.getId() == R.id.range1Y) {
                    clicks = sharedPrefs.getInt(context.getString(R.string.count1ARange), 0);
                } else {
                    if (range.getId() == R.id.range3Y) {
                        clicks = sharedPrefs.getInt(context.getString(R.string.count3ARange), 0);
                    }
                }

                if (clicks >= MAX_CLICKS) {
                    returnSetLock = true;
                    range.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_lock, 0);
                } else {
                    range.setCompoundDrawables(null, null, null, null);
                    returnSetLock = false;
                }
            } else {
                range.setCompoundDrawables(null, null, null, null);
                returnSetLock = false;
            }
        }
        return returnSetLock;
    }

    public static void IncrementLock(Context context, String rangeCurrency, boolean reset) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preferences_name), MODE_PRIVATE);
        int countKeys = sharedPrefs.getInt(context.getString(R.string.preferences_count_keys), 0);
        SharedPreferences.Editor ed = sharedPrefs.edit();
        if (rangeCurrency.compareTo("1y") == 0) {
            if (reset)
                ed.putInt(context.getString(R.string.count1ARange), 0);
            else
                ed.putInt(context.getString(R.string.count1ARange), countKeys++);
        } else if (rangeCurrency.compareTo("3y") == 0) {
            if (reset)
                ed.putInt(context.getString(R.string.count3ARange), 0);
            else
                ed.putInt(context.getString(R.string.count3ARange), countKeys++);
        }
        ed.commit();
    }
}

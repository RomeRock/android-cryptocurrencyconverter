package com.romerock.apps.utilities.cryptocurrencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.api.ApiConfig;
import com.romerock.apps.utilities.cryptocurrencyconverter.api.RetrofitClient;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.LineChartWidget;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CurrenciesListInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ThemeInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.CurrencyConvertApiModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.CurrencyResponseModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.GraphModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.UserUdId;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities.getThemePreferences;

public class DetailsActivity extends AppCompatActivity implements ThemeInterface {

    @BindView(R.id.txtNotifyNotificationCurrencies)
    TextView txtNotifyNotificationCurrencies;
    @BindView(R.id.range1D)
    RadioButton range1D;
    @BindView(R.id.range1W)
    RadioButton range1W;
    @BindView(R.id.range1M)
    RadioButton range1M;
    @BindView(R.id.range6M)
    RadioButton range6M;
    @BindView(R.id.range1Y)
    RadioButton range1Y;
    @BindView(R.id.range3Y)
    RadioButton range5Y;
    @BindView(R.id.imgUpCurrencyFrom)
    ImageView imgUpCurrencyFrom;
    @BindView(R.id.txtCurrentFrom)
    TextView txtCurrentFrom;
    @BindView(R.id.imgUpCurrencyTo)
    ImageView imgUpCurrencyTo;
    @BindView(R.id.txtCurrentTo)
    TextView txtCurrentTo;
    @BindView(R.id.imgFlagFrom)
    CircleImageView imgFlagFrom;
    @BindView(R.id.imgFlagTo)
    CircleImageView imgFlagTo;
    @BindView(R.id.linFromCurrency)
    LinearLayout linFromCurrency;
    @BindView(R.id.linToCurrency)
    LinearLayout linToCurrency;
    @BindView(R.id.linCurrenciesSelected)
    LinearLayout linCurrenciesSelected;
    @BindView(R.id.imgChangeCurrencies)
    ImageView imgChangeCurrencies;
    @BindView(R.id.imgDownCurrencyFrom)
    ImageView imgDownCurrencyFrom;
    @BindView(R.id.imgDownCurrencyTo)
    ImageView imgDownCurrencyTo;
    @BindView(R.id.chart)
    LineChart chart;
    String rangeCurrency;
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtDatePercentage)
    TextView txtDatePercentage;
    @BindView(R.id.txtDateAmount)
    TextView txtDateAmount;
    @BindView(R.id.txtAvg)
    TextView txtAvg;
    @BindView(R.id.txtAvgPercentage)
    TextView txtAvgPercentage;
    @BindView(R.id.txtAvgAmoun)
    TextView txtAvgAmoun;
    @BindView(R.id.txtMax)
    TextView txtMax;
    @BindView(R.id.txtMaxPercentage)
    TextView txtMaxPercentage;
    @BindView(R.id.txtMaxAmount)
    TextView txtMaxAmount;
    @BindView(R.id.txtMin)
    TextView txtMin;
    @BindView(R.id.txtMinPercentage)
    TextView txtMinPercentage;
    @BindView(R.id.txtMinAmount)
    TextView txtMinAmount;
    @BindView(R.id.txtAmount)
    TextView txtAmount;
    @BindView(R.id.imgDate)
    ImageView imgDate;
    @BindView(R.id.imgAvg)
    ImageView imgAvg;
    @BindView(R.id.imgMax)
    ImageView imgMax;
    @BindView(R.id.imgMin)
    ImageView imgMin;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    String currencyFromSelected;
    String currencyToSelected;
    int positionFrom;
    int positionTo;
    @BindView(R.id.txtFromDate)
    TextView txtFromDate;
    @BindView(R.id.txtCurrentDate)
    TextView txtCurrentDate;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    double maxLimit = 0;
    double minLimit = 0;
    boolean isOnlyTime;
    double baseDate;
    private Map<String, JSONObject> valueSnapLibrary;
    Map<String, String> currenciesMap = new HashMap<String, String>();
    @BindView(R.id.toolbarback)
    Toolbar toolbarback;
    @BindView(R.id.relContent)
    RelativeLayout relContent;
    @BindView(R.id.txtCurrent)
    TextView txtCurrent;
    @BindView(R.id.adView)
    AdView adView;
    private List<ItemLibraryCurrencyModel> itemsFromPreferences;
    private List<ItemLibraryCurrencyModel> listAllCurrencies;
    private int position;
    private LineChartWidget chartWidget;
    private List<GraphModel> listGraphModel;
    private CurrencyConvertApiModel currencyConvertApiModel;
    private DialogsHelper dialogsHelper;
    private Map<String, JSONObject> valueSnap;
    private FirebaseHelper firebaseHelper;
    private SharedPreferences sharedPrefs;
    private String[] splitCurrencies;
    private String isFreeOrPremium = "free";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Utilities.colorStatusBar(getApplication(), window);
        Utilities.ChangeLanguage(this);
        setTheme(getThemePreferences(getApplication()));
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        firebaseHelper = FirebaseHelper.getInstance();
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        try {
            isFreeOrPremium = CipherAES.decipher(sharedPrefs.getString(getResources().getString(R.string.purchaseAndroid), isFreeOrPremium));
            if (isFreeOrPremium.compareTo(UserUdId.getFREE()) == 0) {
                Utilities.addIntestitial(DetailsActivity.this, isFreeOrPremium);
                AdRequest adRequest = Utilities.addBanner(DetailsActivity.this, isFreeOrPremium, getString(R.string.banner_ad_unit_id));
                if (adRequest != null)
                    adView.loadAd(adRequest);
                else
                    adView.setVisibility(View.GONE);
                SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(UserUdId.getFREE());
            } else {
                SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(UserUdId.getPREMIUM());
                adView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String currenciesSaved = sharedPrefs.getString(getString(R.string.preferences_defaultCurrencies), "").toString();
        if (currenciesSaved.contains("|")) {
            splitCurrencies = currenciesSaved.split("\\|");
        } else {
            splitCurrencies = new String[1];
            splitCurrencies[0] = currenciesSaved;
        }
        if (extras != null) {
            itemsFromPreferences = (List<ItemLibraryCurrencyModel>) getIntent().getSerializableExtra("Currencies");
            currencyFromSelected = getIntent().getStringExtra("currencySelected");
            currencyToSelected = getIntent().getStringExtra("currencyToSelected");
        }
        rangeCurrency = "1m";
        dialogsHelper = new DialogsHelper(DetailsActivity.this, this);
        range1D.setOnCheckedChangeListener(listener(getString(R.string.range1D)));
        range1M.setOnCheckedChangeListener(listener(getString(R.string.range1M)));
        range1W.setOnCheckedChangeListener(listener(getString(R.string.range1W)));
        range1Y.setOnCheckedChangeListener(listener(getString(R.string.range1Y)));
        range5Y.setOnCheckedChangeListener(listener(getString(R.string.range3Y)));
        range6M.setOnCheckedChangeListener(listener(getString(R.string.range6M)));
        range1M.setChecked(true);
        Utilities.countTotalKeys(DetailsActivity.this);
        listAllCurrencies = new ArrayList<>();
        if (extras != null) {
            listAllCurrencies = (List<ItemLibraryCurrencyModel>) getIntent().getSerializableExtra("listAllCurrencies");
            if(listAllCurrencies==null)
                listAllCurrencies = new ArrayList<>();
        }
        if (listAllCurrencies.size() < 1) {
            CurrencyConvertApiModel.getListCurrencies(firebaseHelper, new CurrenciesListInterface() {
                @Override
                public void getCurrenciesList(DataSnapshot dataSnapshot, DataSnapshot dataSnapshotUpdate) {
                    if (dataSnapshot != null) {
                        valueSnapLibrary = (Map<String, JSONObject>) dataSnapshotUpdate.getValue();

                        listAllCurrencies = CurrencyConvertApiModel.matchCurrenciesWithList(valueSnapLibrary, valueSnapLibrary, null, DetailsActivity.this);
                        process();
                    } else {
                        if (listAllCurrencies.size() < 1)
                            DialogsHelper.showSnackBar(coordinator, getString(R.string.error_internet), getResources().getColor(R.color.alert_snackbar));
                    }
                }
            }, DetailsActivity.this);
        } else {
            process();
        }

    }

    private void process() {
        if (itemsFromPreferences != null) {
            setCurrencies();
            setClickButton();

        } else {
            itemsFromPreferences = new ArrayList<>();
            for (int i = 0; i < splitCurrencies.length; i++) {
                for (int currencies = 0; currencies < listAllCurrencies.size(); currencies++) {
                    if (listAllCurrencies.get(currencies).getName().toString().compareTo(splitCurrencies[i]) == 0) {
                        itemsFromPreferences.add(listAllCurrencies.get(currencies));
                        break;
                    }
                }
            }

            boolean listHaveCurrency = false;
            for (int i = 0; i < itemsFromPreferences.size(); i++) {
                if (itemsFromPreferences.get(i).getName().toString().compareTo(currencyFromSelected) == 0) {
                    listHaveCurrency = true;
                    break;
                }
            }
            if (!listHaveCurrency) {
                for (int i = 0; i < listAllCurrencies.size(); i++) {
                    if (listAllCurrencies.get(i).getName().toString().compareTo(currencyFromSelected) == 0) {
                        itemsFromPreferences.add(listAllCurrencies.get(i));
                        break;
                    }
                }
            }
            listHaveCurrency = false;
            for (int i = 0; i < itemsFromPreferences.size(); i++) {
                if (itemsFromPreferences.get(i).getName().toString().compareTo(currencyToSelected) == 0) {
                    listHaveCurrency = true;
                    break;
                }
            }
            if (!listHaveCurrency) {
                for (int i = 0; i < listAllCurrencies.size(); i++) {
                    if (listAllCurrencies.get(i).getName().toString().compareTo(currencyToSelected) == 0) {
                        itemsFromPreferences.add(listAllCurrencies.get(i));
                        break;
                    }
                }
            }
            listAllCurrencies = CurrencyConvertApiModel.matchCurrenciesWithArray(currenciesMap, listAllCurrencies);
            for (int i = 0; i < itemsFromPreferences.size(); i++) {
                if (itemsFromPreferences.get(i).getName().compareTo(currencyFromSelected) == 0)
                    positionFrom = i;
                if (itemsFromPreferences.get(i).getName().compareTo(currencyToSelected) == 0)
                    positionTo = i;
            }
            setClickButton();
            setValuesList();
        }

        callApi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOffline();
        Log.d("firebaseCon", "onDestroy");
    }

    private void setClickButton() {
        imgUpCurrencyFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positionFrom = CurrencyConvertApiModel.getPositionInList(CurrencyConvertApiModel.setNewCurrencyFromSpinnerList(DetailsActivity.this, true, linFromCurrency, imgFlagFrom, txtCurrentFrom, itemsFromPreferences, positionFrom), itemsFromPreferences);
                currencyFromSelected = itemsFromPreferences.get(positionFrom).getName();
                callApi();
            }
        });
        imgDownCurrencyFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positionFrom = CurrencyConvertApiModel.getPositionInList(CurrencyConvertApiModel.setNewCurrencyFromSpinnerList(DetailsActivity.this, false, linFromCurrency, imgFlagFrom, txtCurrentFrom, itemsFromPreferences, positionFrom), itemsFromPreferences);
                currencyFromSelected = itemsFromPreferences.get(positionFrom).getName();
                callApi();
            }
        });


        imgUpCurrencyTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positionTo = CurrencyConvertApiModel.getPositionInList(CurrencyConvertApiModel.setNewCurrencyFromSpinnerList(DetailsActivity.this, true, linToCurrency, imgFlagTo, txtCurrentTo, itemsFromPreferences, positionTo), itemsFromPreferences);
                currencyToSelected = itemsFromPreferences.get(positionTo).getName();
                callApi();
            }
        });
        imgDownCurrencyTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positionTo = CurrencyConvertApiModel.getPositionInList(CurrencyConvertApiModel.setNewCurrencyFromSpinnerList(DetailsActivity.this, false, linToCurrency, imgFlagTo, txtCurrentTo, itemsFromPreferences, positionTo), itemsFromPreferences);
                currencyToSelected = itemsFromPreferences.get(positionTo).getName();
                callApi();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOnline();
        Log.d("firebaseCon", "onResume");
        setThemeByActivity();
    }

    private void setCurrencies() {
        if (itemsFromPreferences.size() > 1) {
            for (int i = 0; i < itemsFromPreferences.size(); i++) {
                if (itemsFromPreferences.get(i).getName().toString().compareTo(currencyFromSelected) == 0) {
                    positionFrom = i;
                    if (i + 1 > itemsFromPreferences.size() - 1) {
                        positionTo = 0;
                    } else {
                        positionTo = i + 1;
                    }
                    break;
                }
            }
            currencyToSelected = itemsFromPreferences.get(positionTo).getName();
        } else {
            currencyToSelected = currencyFromSelected;
        }
        setValuesList();
    }

    private void setValuesList() {
        // ------ From currency flag -----
        int id;
        if (currencyFromSelected.toLowerCase().compareTo("try") == 0) {
            id = CurrencyConvertApiModel.idForDrawable(DetailsActivity.this, currencyFromSelected + currencyFromSelected);
        } else
            id = CurrencyConvertApiModel.idForDrawable(DetailsActivity.this, currencyFromSelected);
        if (id == 0) {
            id = getResources().getIdentifier("generic", "drawable", getPackageName());
        }

        imgFlagFrom.setImageResource(id);
        txtCurrentFrom.setText(currencyFromSelected);
        // ------ To currency flag -----
        id = CurrencyConvertApiModel.idForDrawable(DetailsActivity.this, currencyToSelected);
        if (currencyToSelected.toLowerCase().compareTo("try") == 0) {
            id = CurrencyConvertApiModel.idForDrawable(DetailsActivity.this, currencyToSelected + currencyToSelected);
        } else
            id = CurrencyConvertApiModel.idForDrawable(DetailsActivity.this, currencyToSelected);
        if (id == 0) {
            id = getResources().getIdentifier("generic", "drawable", getPackageName());
        }
        imgFlagTo.setImageResource(id);
        txtCurrentTo.setText(currencyToSelected);
    }

    private CompoundButton.OnCheckedChangeListener listener(final String range) {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        SpannableString content = new SpannableString(range);
                        content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_MARK_MARK);
                        buttonView.setText(content);
                        //Make the text BOLD
                        buttonView.setTypeface(null, Typeface.BOLD);
                    } else {
                        //Change the color here and make the Text bold
                        SpannableString content = new SpannableString(range);
                        if(content!=null) {
                            content.setSpan(null, 0, content.length(), 0);
                            buttonView.setText(content);
                            buttonView.setTypeface(null, Typeface.NORMAL);
                        }
                    }
                }catch (Exception e){
                    Log.d("","");
                }
            }
        };
        return listener;
    }


    @OnClick({R.id.range1D, R.id.range1W, R.id.range1M, R.id.range6M, R.id.range1Y, R.id.range3Y, R.id.radioRanges, R.id.btnBack, R.id.imgChangeCurrencies, R.id.linAddNotify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.range1D:
                rangeCurrency = "1d";
                isOnlyTime = true;
                callApi();
                break;
            case R.id.range1W:
                rangeCurrency = "1w";
                isOnlyTime = false;
                callApi();
                break;
            case R.id.range1M:
                rangeCurrency = "1m";
                isOnlyTime = false;
                callApi();
                break;
            case R.id.range6M:
                isOnlyTime = false;
                rangeCurrency = "6m";
                callApi();
                break;
            case R.id.range1Y:
                isOnlyTime = false;
                rangeCurrency = "1y";
                callApi();
                break;
            case R.id.range3Y:
                isOnlyTime = false;
                rangeCurrency = "3y";
                callApi();
                break;
            case R.id.radioRanges:
                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.imgChangeCurrencies:
                imgChangeCurrencies.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
                String tempCurrency = currencyFromSelected;
                int tempCurrencyPosition = positionFrom;
                currencyFromSelected = currencyToSelected;
                positionFrom = positionTo;
                currencyToSelected = tempCurrency;
                positionTo = tempCurrencyPosition;
                setValuesList();
                callApi();
                break;
            case R.id.linAddNotify:
                Intent i = new Intent(DetailsActivity.this, SetupPushNotificationsActivity.class);
                if (itemsFromPreferences.size() > 0)
                    if (itemsFromPreferences.get(0).getCurrency() == null)
                        itemsFromPreferences = CurrencyConvertApiModel.matchCurrenciesWithPreferences(itemsFromPreferences, listAllCurrencies);
                i.putExtra("Currencies", (Serializable) itemsFromPreferences);
                i.putExtra("positionFrom", itemsFromPreferences.get(positionFrom).getName());
                i.putExtra("positionTo", itemsFromPreferences.get(positionTo).getName());
                i.putExtra("AllListCurrencies", (Serializable) listAllCurrencies);
                startActivity(i);
                break;
        }
    }

    private void callApi() {
        dialogsHelper.showLoading();
        ApiConfig apiConfig = RetrofitClient.getApi().create(ApiConfig.class);
        Call<CurrencyResponseModel> call = apiConfig.getCurrencyCompare(RetrofitClient.getAPIKEY(),
                itemsFromPreferences.get(positionFrom).getName(),
                itemsFromPreferences.get(positionTo).getName(),
                RetrofitClient.getSourceLoc(),
                RetrofitClient.getSourceCode(),
                rangeCurrency, 1);
        call.enqueue(new Callback<CurrencyResponseModel>() {
            @Override
            public void onFailure(Call call, Throwable t) {
                dialogsHelper.hideLoading();
                Snackbar snackbar = Snackbar.make(coordinator, "No internet conection", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.alert_snackbar));
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        finish();
                    }
                });
                snackbar.show();
            }

            @Override
            public void onResponse(Call<CurrencyResponseModel> call, Response<CurrencyResponseModel> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        listGraphModel = new ArrayList<GraphModel>();
                        Gson gson = new GsonBuilder().create();
                        currencyConvertApiModel = gson.fromJson(response.body().getResponse().getData(), CurrencyConvertApiModel.class);
                        JsonObject jsonObject = (JsonObject) response.body().getResponse().getSuccess();
                        jsonObject = (JsonObject) response.body().getResponse().getData();
                        jsonObject = (JsonObject) jsonObject.get("graph");
                        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
                        boolean firstValue = true;
                        for (Map.Entry<String, JsonElement> entry : entries) {
                            if (firstValue) {
                                maxLimit = Double.valueOf(entry.getValue().toString());
                                minLimit = Double.valueOf(entry.getValue().toString());
                                firstValue = false;
                                if (isOnlyTime)
                                    baseDate = Long.parseLong(String.valueOf(entry.getKey()));
                            }
                            listGraphModel.add(new GraphModel(entry.getKey(), Double.valueOf(entry.getValue().toString())));
                            if (maxLimit < Double.valueOf(entry.getValue().toString())) {
                                maxLimit = Double.valueOf(entry.getValue().toString());
                            } else {
                                if (minLimit > Double.valueOf(entry.getValue().toString())) {
                                    minLimit = Double.valueOf(entry.getValue().toString());
                                }
                            }
                        }
                        chart.invalidate();
                        chartWidget = new LineChartWidget(DetailsActivity.this, chart, listGraphModel, maxLimit, minLimit, isOnlyTime, dialogsHelper, sharedPrefs);
                        if (currencyConvertApiModel != null)
                            insertModelIntoView();

                    }
                } else {
                    dialogsHelper.hideLoading();
                }
            }
        });
    }

    private void insertModelIntoView() {
        txtCurrentDate.setText(Utilities.getCurrentLocalDateTimeStamp(isOnlyTime));
        txtNotifyNotificationCurrencies.setText(String.format(getString(R.string.notifyMeChangeCurrency), itemsFromPreferences.get(positionFrom).getName().toUpperCase(), itemsFromPreferences.get(positionTo).getName().toUpperCase()));
        txtAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(currencyConvertApiModel.getCurrentValue()))));
        txtDate.setText(Utilities.getFormatedDate(currencyConvertApiModel.getStartDateSQL()));
        if (!isOnlyTime)
            txtFromDate.setText(Utilities.getFormatedDate(currencyConvertApiModel.getStartDateSQL()));
        else {
            txtFromDate.setText(Utilities.getFormatedDateOnlyTime(listGraphModel.get(0).getDate()).replace("T", " "));
        }
        txtDatePercentage.setText(currencyConvertApiModel.getStartPercentage() + "%");
        Utilities.addArrow(DetailsActivity.this, currencyConvertApiModel.getStartPercentage(), imgDate);
        txtDateAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(currencyConvertApiModel.getStartValue()))));
        txtAvgPercentage.setText(currencyConvertApiModel.getAvgPercentage() + "%");
        Utilities.addArrow(DetailsActivity.this, currencyConvertApiModel.getAvgPercentage(), imgAvg);
        txtAvgAmoun.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(currencyConvertApiModel.getAvgValue()))));
        txtMaxPercentage.setText(currencyConvertApiModel.getMaxPercentage() + "%");
        Utilities.addArrow(DetailsActivity.this, currencyConvertApiModel.getMaxPercentage(), imgMax);
        txtMaxAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(currencyConvertApiModel.getMaxValue()))));
        txtMinPercentage.setText(currencyConvertApiModel.getMinPercentage() + "%");
        Utilities.addArrow(DetailsActivity.this, currencyConvertApiModel.getMinPercentage(), imgMin);
        txtMinAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(currencyConvertApiModel.getMinValue()))));
        dialogsHelper.hideLoading();
    }

    @Override
    public void setThemeByActivity() {
        String themeSelected = sharedPrefs.getString(getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            toolbarback.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            txtCurrentTo.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtCurrentFrom.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtDate.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme1));
            txtMax.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme1));
            txtMin.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme1));
            txtAvg.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme1));
            range1D.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme1));
            range1W.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme1));
            range1M.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme1));
            range6M.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme1));
            range1Y.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme1));
            range5Y.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme1));
            txtDatePercentage.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtMinPercentage.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtMaxPercentage.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtAvgPercentage.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtFromDate.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtCurrentDate.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtCurrent.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
        } else {
            if (themeSelected.contains("Daylight")) {
                toolbarback.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDaylight));
                txtCurrentTo.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtCurrentFrom.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtDate.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme2));
                txtMax.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme2));
                txtMin.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme2));
                txtAvg.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme2));
                range1D.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme2));
                range1W.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme2));
                range1M.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme2));
                range6M.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme2));
                range1Y.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme2));
                range5Y.setTextColor(getResources().getColorStateList(R.color.radio_botton_text_selected_theme2));
                txtDatePercentage.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtMinPercentage.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtMaxPercentage.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtAvgPercentage.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtFromDate.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtCurrentDate.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtCurrent.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
            }
        }

    }
}

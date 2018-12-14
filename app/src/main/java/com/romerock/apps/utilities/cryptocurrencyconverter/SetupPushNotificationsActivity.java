package com.romerock.apps.utilities.cryptocurrencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Popup;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.adapters.RecyclerViewHoursAdapter;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CheckUDIDListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.NotificationsListListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ThemeInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.AlertsNotificationModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.CurrencyConvertApiModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.HourModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.NotificationModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.UserUdId;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities.getThemePreferences;

public class SetupPushNotificationsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, ThemeInterface {

    private static final int FINISH_PURCHASE = 10004;
    @BindView(R.id.toolbarback)
    Toolbar toolbarback;
    @BindView(R.id.imgFlagFrom)
    CircleImageView imgFlagFrom;
    @BindView(R.id.txtCurrentFrom)
    TextView txtCurrentFrom;
    @BindView(R.id.linFromCurrency)
    LinearLayout linFromCurrency;
    @BindView(R.id.imgUpCurrencyFrom)
    ImageView imgUpCurrencyFrom;
    @BindView(R.id.imgDownCurrencyFrom)
    ImageView imgDownCurrencyFrom;
    @BindView(R.id.imgFlagTo)
    CircleImageView imgFlagTo;
    @BindView(R.id.txtCurrentTo)
    TextView txtCurrentTo;
    @BindView(R.id.linToCurrency)
    LinearLayout linToCurrency;
    @BindView(R.id.imgUpCurrencyTo)
    ImageView imgUpCurrencyTo;
    @BindView(R.id.imgDownCurrencyTo)
    ImageView imgDownCurrencyTo;
    @BindView(R.id.linCurrenciesSelected)
    LinearLayout linCurrenciesSelected;
    @BindView(R.id.txtCurrent)
    TextView txtCurrent;
    @BindView(R.id.txtAmount)
    TextView txtAmount;
    @BindView(R.id.txtDialy)
    TextView txtDialy;
    @BindView(R.id.linDaily)
    LinearLayout linDaily;
    @BindView(R.id.txtYouWillReceive)
    TextView txtYouWillReceive;
    @BindView(R.id.linHours)
    LinearLayout linHours;
    @BindView(R.id.txtOverAlert)
    TextView txtOverAlert;
    @BindView(R.id.linOverAlert)
    LinearLayout linOverAlert;
    @BindView(R.id.txtReceiveNotification)
    TextView txtReceiveNotification;
    @BindView(R.id.txtOverAmount)
    EditText txtOverAmount;
    @BindView(R.id.switchOver)
    Switch switchOver;
    @BindView(R.id.linOver)
    LinearLayout linOver;
    @BindView(R.id.txtBelowAmount)
    EditText txtBelowAmount;
    @BindView(R.id.switchBelow)
    Switch switchBelow;
    @BindView(R.id.linBelow)
    LinearLayout linBelow;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.recyclerViewHoursAdded)
    RecyclerView recyclerViewHoursAdded;
    @BindView(R.id.relContent)
    RelativeLayout relContent;
    @BindView(R.id.txtVS)
    TextView txtVS;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.txtAddHour)
    TextView txtAddHour;
    @BindView(R.id.txtLabelBelow)
    TextView txtLabelBelow;
    @BindView(R.id.txtLabelOver)
    TextView txtLabelOver;
    @BindView(R.id.imgMore)
    ImageView imgMore;
    @BindView(R.id.linAddHours)
    LinearLayout linAddHours;
    String UDID = "", isFreeOrPremium = "free";
    List<NotificationModel> notificationModelList;
    Map<String, String> currenciesMap = new HashMap<String, String>();
    DecimalFormat df = new DecimalFormat("####0.00##");
    private String themeSelected;
    private List<ItemLibraryCurrencyModel> currenciesFromPreferences;
    private List<ItemLibraryCurrencyModel> allCurrencies;
    private String positionTo;
    private String positionFrom;
    private RecyclerViewHoursAdapter mAdapter;
    private List<HourModel> itemsHours = new ArrayList<>();
    private Double resultAmount;
    private SharedPreferences sharedPrefs;
    private FirebaseHelper firebaseHelper;
    private DatabaseReference mDatabase;
    private NotificationModel notificationModel;
    private double overAlert=0;
    private double belowAlert=0;
    private boolean separatorPress=false;
    private char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
    private boolean block=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        setTheme(getThemePreferences(getApplication()));
        Utilities.colorStatusBar(getApplication(), window);
        Utilities.ChangeLanguage(this);
        setContentView(R.layout.activity_setup_push_notifications);
        ButterKnife.bind(this);
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        themeSelected = sharedPrefs.getString(getString(R.string.preferences_theme_tittle), null);
        firebaseHelper = FirebaseHelper.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currenciesFromPreferences = (List<ItemLibraryCurrencyModel>) getIntent().getSerializableExtra("Currencies");
            positionFrom = getIntent().getStringExtra("positionFrom");
            positionTo = getIntent().getStringExtra("positionTo");
            allCurrencies = (List<ItemLibraryCurrencyModel>) getIntent().getSerializableExtra("AllListCurrencies");
            notificationModelList = (List<NotificationModel>) getIntent().getSerializableExtra("notificationModelList");
        }
        boolean haveFomCurrency = false, haveToCurrency = false;
        for (int i = 0; i < currenciesFromPreferences.size(); i++) {
            if (currenciesFromPreferences.get(i).getName().compareTo(positionFrom) == 0)
                haveFomCurrency = true;
            if (currenciesFromPreferences.get(i).getName().compareTo(positionTo) == 0)
                haveToCurrency = true;
        }
        if (!haveFomCurrency || !haveToCurrency) {
            if(allCurrencies!=null) {
                for (int i = 0; i < allCurrencies.size(); i++) {
                    if (!haveFomCurrency) {
                        if (allCurrencies.get(i).getName().compareTo(positionFrom) == 0)
                            currenciesFromPreferences.add(allCurrencies.get(i));
                    }
                    if (!haveToCurrency) {
                        if (allCurrencies.get(i).getName().compareTo(positionTo) == 0)
                            currenciesFromPreferences.add(allCurrencies.get(i));
                    }
                }
            }
        }


        try {
            isFreeOrPremium = SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM();
            if (isFreeOrPremium.compareTo(UserUdId.getFREE()) == 0) {
                UDID = CipherAES.decipher(sharedPrefs.getString(getString(R.string.udidAndroid), ""));
            } else {
                UDID = CipherAES.decipher(sharedPrefs.getString(getString(R.string.purchaseOrder), ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notificationModelList == null) {
            notificationModelList = new ArrayList<>();
            NotificationModel.getMyNotifications(SetupPushNotificationsActivity.this, firebaseHelper, UDID, isFreeOrPremium, new NotificationsListListener() {
                @Override
                public void getNotificationList(List<NotificationModel> notificationModelListSend) {
                    if (notificationModelListSend.size() > 0) {
                        notificationModelList.addAll(notificationModelListSend);
                        mAdapter.notifyDataSetChanged();
                        setNotificationModelFromSetup();
                    }
                }

                @Override
                public void removeNotification(String key) {

                }
            });
        }

        setValuesList();
        recyclerViewHoursAdded.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewHoursAdapter(itemsHours, SetupPushNotificationsActivity.this, new ItemClickLibraryInterface() {
            @Override
            public void onItemClicked(View view, ItemLibraryCurrencyModel item, String code) {
                Intent intent = new Intent();
                intent.putExtra("Current", item);
                setResult(RESULT_OK, intent);
                Utilities.closeKeyboard(SetupPushNotificationsActivity.this);
                finish();
            }
        }, sharedPrefs);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHoursAdded.setLayoutManager(new GridLayoutManager(SetupPushNotificationsActivity.this, 3));
        recyclerViewHoursAdded.setAdapter(mAdapter);
        recyclerViewHoursAdded.setItemAnimator(new DefaultItemAnimator());
        try {
            isFreeOrPremium = SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM();
        } catch (Exception e) {
            e.printStackTrace();
        }

            enableSwitches();
        setNotificationModelFromSetup();

        txtBelowAmount.addTextChangedListener(new TextWatcher() {
            boolean isEdiging;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (switchBelow.isChecked())
                    saveNotification();
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
                if(String.valueOf(separator).compareTo(",")==0&&charSequence.toString().contains(".")&&!isEdiging){
                    isEdiging=true;
                    txtBelowAmount.setText(txtBelowAmount.getText().toString().replace(".",",").replace(" ", ""));
                    isEdiging=false;
                    Editable etext = txtBelowAmount.getText();
                    Selection.setSelection(etext, txtBelowAmount.getText().length());
                    saveNotification();
                }else{
                    isEdiging=false;
                    saveNotification();
                }
            }
        });

        txtOverAmount.addTextChangedListener(new TextWatcher() {
            boolean isEdiging;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (switchOver.isChecked())
                    saveNotification();
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
                if(String.valueOf(separator).compareTo(",")==0&&charSequence.toString().contains(".")&&!isEdiging){
                    isEdiging=true;
                    txtOverAmount.setText(txtOverAmount.getText().toString().replace(".",",").replace(" ", ""));
                    isEdiging=false;
                    Editable etext = txtOverAmount.getText();
                    Selection.setSelection(etext, txtOverAmount.getText().length());
                    saveNotification();
                }else{
                    isEdiging=false;
                    saveNotification();
                }
            }
        });
        Utilities.countTotalKeys(SetupPushNotificationsActivity.this);
    }

    private void enableSwitches() {
        switchOver.setEnabled(true);
        switchBelow.setEnabled(true);
        switchOver.setClickable(true);
        switchOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchOver.isChecked()){
                    if(NotificationModel.checkGlobal(SetupPushNotificationsActivity.this, true)){
                        saveNotification();
                    }else{
                        switchOver.setChecked(false);
                        Popup.SubscribeMe(SetupPushNotificationsActivity.this);
                    }
                }else {
                    NotificationModel.checkGlobal(SetupPushNotificationsActivity.this, false);
                    saveNotification();
                }
            }
        });
        switchBelow.setClickable(true);
        switchBelow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchBelow.isChecked()){
                    if(NotificationModel.checkGlobal(SetupPushNotificationsActivity.this, true)){
                        saveNotification();
                    }else{
                        switchBelow.setChecked(false);
                        Popup.SubscribeMe(SetupPushNotificationsActivity.this);
                    }
                }else {
                    NotificationModel.checkGlobal(SetupPushNotificationsActivity.this, false);
                    saveNotification();
                }

            }
        });
        switchOver.setOnTouchListener(null);
        switchBelow.setOnTouchListener(null);
    }

    @OnClick({R.id.toolbarback, R.id.imgUpCurrencyFrom, R.id.imgDownCurrencyFrom, R.id.imgUpCurrencyTo, R.id.imgDownCurrencyTo, R.id.linAddHours})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbarback:
                finish();
                break;
            case R.id.imgUpCurrencyFrom:
                positionFrom = CurrencyConvertApiModel.setNewCurrencyFromSpinnerList(SetupPushNotificationsActivity.this, true, linFromCurrency, imgFlagFrom, txtCurrentFrom, currenciesFromPreferences, CurrencyConvertApiModel.getPositionInList(positionFrom, currenciesFromPreferences));
                changeCurrent();
                break;
            case R.id.imgDownCurrencyFrom:
                positionFrom = CurrencyConvertApiModel.setNewCurrencyFromSpinnerList(SetupPushNotificationsActivity.this, false, linFromCurrency, imgFlagFrom, txtCurrentFrom, currenciesFromPreferences, CurrencyConvertApiModel.getPositionInList(positionFrom, currenciesFromPreferences));
                changeCurrent();
                break;
            case R.id.imgUpCurrencyTo:
                positionTo = CurrencyConvertApiModel.setNewCurrencyFromSpinnerList(SetupPushNotificationsActivity.this, true, linToCurrency, imgFlagTo, txtCurrentTo, currenciesFromPreferences, CurrencyConvertApiModel.getPositionInList(positionTo, currenciesFromPreferences));
                changeCurrent();
                break;
            case R.id.imgDownCurrencyTo:
                positionTo = CurrencyConvertApiModel.setNewCurrencyFromSpinnerList(SetupPushNotificationsActivity.this, false, linToCurrency, imgFlagTo, txtCurrentTo, currenciesFromPreferences, CurrencyConvertApiModel.getPositionInList(positionTo, currenciesFromPreferences));
                changeCurrent();
                break;
            case R.id.linAddHours:
                if(NotificationModel.checkGlobal(SetupPushNotificationsActivity.this, true)) {
                    int countHours = itemsHours.size();
                    if (notificationModelList != null) {
                        if (notificationModelList.size() > 0) {
                            for (int i = 0; i < notificationModelList.size(); i++) {
                                if (notificationModelList.get(i).getKey().compareTo(positionFrom + "-" + positionTo) != 0)
                                    countHours++;
                            }
                        }
                    }
                 /*   if (SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM().compareTo(UserUdId.getFREE()) == 0 && (itemsHours.size() > 0 || countHours > 1)) {
                        Popup.SubscribeMe(SetupPushNotificationsActivity.this);
                    } else {*/
                        TimePickerDialog timePickerDialog;
                        timePickerDialog = TimePickerDialog.newInstance(SetupPushNotificationsActivity.this, new Date().getHours(), new Date().getMinutes(), false);
                        timePickerDialog.enableMinutes(false);
                        if (themeSelected.contains("Night")) {
                            timePickerDialog.setThemeDark(true);
                        } else if (themeSelected.contains("Daylight")) {
                            timePickerDialog.setThemeDark(false);
                        }
                        timePickerDialog.show(getFragmentManager(), "tag");
                   // }
                }else{
                    Popup.SubscribeMe(SetupPushNotificationsActivity.this);
                }
                break;
        }
    }

    private void changeCurrent() {
        NotificationModel.getMyNotifications(SetupPushNotificationsActivity.this,firebaseHelper, UDID, isFreeOrPremium, new NotificationsListListener() {
            @Override
            public void getNotificationList(List<NotificationModel> notificationModelListSend) {
                notificationModelList.clear();
                notificationModelList.addAll(notificationModelListSend);
                mAdapter.notifyDataSetChanged();
                setNotificationModelFromSetup();
            }

            @Override
            public void removeNotification(String key) {

            }
        });
    }

    public void saveNotification() {
        if (switchOver.isChecked()||switchBelow.isChecked()||itemsHours.size()>0) {
            final String key = txtCurrentFrom.getText().toString().toUpperCase() + "-" + txtCurrentTo.getText().toString().toUpperCase();
            if (txtCurrentFrom.getText().toString().toUpperCase().compareTo(txtCurrentTo.getText().toString().toUpperCase()) != 0) {
                try {
                    if (isFreeOrPremium.compareTo(UserUdId.getFREE()) == 0) {
                        UDID = CipherAES.decipher(sharedPrefs.getString(getString(R.string.udidAndroid), ""));
                    } else {
                        UDID = CipherAES.decipher(sharedPrefs.getString(getString(R.string.purchaseOrder), ""));
                    }
                } catch (Exception e) {
                }
                final UserUdId userUdId = new UserUdId(sharedPrefs.getString(getResources().getString(R.string.language_settings), ""),
                        "active", "",
                        SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(),
                        Utilities.getCurrentTimeStamp());
                //double createdtstamp, double expirationtstamp, String expiredate, String language, String state, String timezon
                if (txtCurrentFrom.getText().toString().compareTo(txtCurrentTo.getText().toString()) != 0) {
                    userUdId.CheckFreeUDIDNode(UDID, firebaseHelper, new CheckUDIDListener() {
                        @Override
                        public void checkUDIDFromFirebase(boolean haveChild) {
                            try {
                                String fcm = FirebaseInstanceId.getInstance().getToken();
                                SharedPreferences.Editor ed = sharedPrefs.edit();
                                if (sharedPrefs.getString(getResources().getString(R.string.fcmUser), "").compareTo("") == 0) {
                                    if (fcm != null)
                                        try {
                                            userUdId.checkFreeFMCTocken(firebaseHelper, UDID, fcm, userUdId.getCreatedtstamp(), SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(), false);
                                            ed.putString(getString(R.string.fcmUser), CipherAES.cipher(fcm));
                                            ed.commit();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                } else {
                                    String FMCTocken = CipherAES.decipher(sharedPrefs.getString(getResources().getString(R.string.fcmUser), ""));
                                    if (fcm.compareTo(FMCTocken) != 0) {
                                        ed.putString(getString(R.string.fcmUser), CipherAES.cipher(fcm));
                                        ed.commit();
                                        FMCTocken = fcm;
                                    }
                                    userUdId.checkFreeFMCTocken(firebaseHelper, UDID, FMCTocken, userUdId.getCreatedtstamp(), SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(), false);

                                    String[] hoursToSave = new String[itemsHours.size()];
                                    int hours = 0;
                                    for (HourModel hour : itemsHours) {
                                        hoursToSave[hours] = String.valueOf(hour.getHour());
                                        hours++;
                                    }
                                    AlertsNotificationModel alertsNotificationModel = new AlertsNotificationModel();
                                    boolean haveAlert = false;
                                    if (switchOver.isChecked()) {
                                        if (txtOverAmount.getText().toString().compareTo("") != 0)
                                            overAlert = Double.parseDouble(String.valueOf(Utilities.getNumberDecimal(txtOverAmount.getText().toString())));
                                        else
                                            overAlert = Double.parseDouble(String.valueOf(Utilities.getNumberDecimal(txtOverAmount.getHint().toString())));
                                        alertsNotificationModel.setHigh(overAlert);
                                        alertsNotificationModel.setHigh_active(true);
                                        haveAlert = true;
                                    } else {
                                        overAlert = Double.parseDouble(String.valueOf(Utilities.getNumberDecimal(txtOverAmount.getHint().toString())));
                                        alertsNotificationModel.setHigh_active(false);
                                        haveAlert = false;
                                    }
                                    if (switchBelow.isChecked()) {
                                        if (txtBelowAmount.getText().toString().compareTo("") != 0)
                                            belowAlert = Double.parseDouble(String.valueOf(Utilities.getNumberDecimal(txtBelowAmount.getText().toString())));
                                        else
                                            belowAlert = Double.parseDouble(String.valueOf(Utilities.getNumberDecimal(txtBelowAmount.getHint().toString())));
                                        alertsNotificationModel.setLow(belowAlert);
                                        alertsNotificationModel.setLow_active(true);
                                        haveAlert = true;
                                    } else {
                                        belowAlert =  Double.parseDouble(String.valueOf(Utilities.getNumberDecimal(txtBelowAmount.getHint().toString())));
                                        alertsNotificationModel.setLow_active(false);
                                        haveAlert = false;
                                    }
                                    notificationModel = new NotificationModel(key, alertsNotificationModel, hoursToSave);
                                    NotificationModel.addCurrencyNotification(firebaseHelper, UDID, key, SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(), notificationModel);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void checkPremiumState(boolean status) {

                        }
                    }, userUdId, SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(), SetupPushNotificationsActivity.this);

                }

            } else {
                switchOver.setChecked(false);
                switchBelow.setChecked(false);
                itemsHours.clear();
                mAdapter.notifyDataSetChanged();
                DialogsHelper.showSnackBar(coordinator, getString(R.string.cant_save_same_currencies), getResources().getColor(R.color.alert_snackbar));
            }
        }
    }


    private void setValuesList() {
        // ------ From currency flag -----
        int id;
        if (positionFrom.toLowerCase().compareTo("try") == 0) {
            id = CurrencyConvertApiModel.idForDrawable(SetupPushNotificationsActivity.this, Utilities.removeCharacters(positionFrom+positionFrom));
        }else
            id = CurrencyConvertApiModel.idForDrawable(SetupPushNotificationsActivity.this, Utilities.removeCharacters(positionFrom));
        if (id== 0) {
            id = getResources().getIdentifier("generic", "drawable", getPackageName());
        }
        imgFlagFrom.setImageResource(id);
        txtCurrentFrom.setText(positionFrom);
        // ------ To currency flag -----
        if (positionTo.toLowerCase().compareTo("try") == 0) {
            id = CurrencyConvertApiModel.idForDrawable(SetupPushNotificationsActivity.this, Utilities.removeCharacters(positionTo+positionTo));
        }else
            id = CurrencyConvertApiModel.idForDrawable(SetupPushNotificationsActivity.this, Utilities.removeCharacters(positionTo));
        if (id== 0) {
            id = getResources().getIdentifier("generic", "drawable", getPackageName());
        }
        imgFlagTo.setImageResource(id);
        txtCurrentTo.setText(positionTo);
    }

    private void makeOperation() {
        if(currenciesFromPreferences!=null)
        resultAmount = Double.valueOf(String.valueOf(Utilities.getNumberDecimal(Utilities.makeOperationWithFormat(1, currenciesFromPreferences.get(CurrencyConvertApiModel.getPositionInList(positionTo, currenciesFromPreferences)).getCurrency(),
                currenciesFromPreferences.get(CurrencyConvertApiModel.getPositionInList(positionFrom, currenciesFromPreferences)).getCurrency()))));

        if(overAlert==0)
            overAlert = Double.valueOf(Double.valueOf(resultAmount) + (Double.valueOf(resultAmount) * 0.05));
        if(belowAlert==0)
            belowAlert = Double.valueOf(Double.valueOf(resultAmount) - (Double.valueOf(resultAmount) * 0.05));
        cleanEditTextAndMakeHint();
        txtAmount.setText(Utilities.getNumberString(resultAmount));
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        boolean haveThisHour = false;
        Utilities.addIntestitial(SetupPushNotificationsActivity.this, "");
        for (int i = 0; i < itemsHours.size(); i++) {
            if (itemsHours.get(i).getHour() == hourOfDay) {
                haveThisHour = true;
                break;
            }
        }
        if (!haveThisHour) {
            String displayHour = "";
            if (hourOfDay >= 12) {
                if (hourOfDay == 12)
                    displayHour = hourOfDay + "PM";
                else
                    displayHour = hourOfDay - 12 + "PM";
            } else {
                if (hourOfDay == 0)
                    displayHour = 12 + "AM";
                else
                    displayHour = hourOfDay + "AM";
            }
            HourModel h = new HourModel(hourOfDay, displayHour);
            itemsHours.add(h);
            Collections.sort(itemsHours, HourModel.ItemByHour);
            mAdapter.notifyDataSetChanged();
            saveNotification();
        } else {
            DialogsHelper.showSnackBar(coordinator, getString(R.string.time_already_scheduled), getResources().getColor(R.color.alert_snackbar));
        }
    }

    private void cleanEditTextAndMakeHint() {
        txtOverAmount.setHint(Utilities.getNumberString(Double.parseDouble(String.valueOf(overAlert))));
        txtBelowAmount.setHint(Utilities.getNumberString(Double.parseDouble(String.valueOf(belowAlert))));
        txtOverAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(overAlert))));
        txtBelowAmount.setText(Utilities.getNumberString(Double.parseDouble(String.valueOf(belowAlert))));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FINISH_PURCHASE) {
            if (resultCode == RESULT_OK) {
                try {
                    isFreeOrPremium = SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM();
                        UDID = CipherAES.decipher(sharedPrefs.getString(getString(R.string.purchaseOrder), ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                enableSwitches();
            }
            if (!SingletonInAppBilling.Instance().getmHelper().handleActivityResult(requestCode,
                    resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void setNotificationModelFromSetup() {
        itemsHours.clear();
        overAlert=0;
        belowAlert=0;
        mAdapter.notifyDataSetChanged();
        switchOver.setChecked(false);
        switchBelow.setChecked(false);
        if (notificationModelList != null) {
            if (notificationModelList.size() > 0) {
                String compareCurrencies = positionFrom + "-" + positionTo;
                for (int i = 0; i < notificationModelList.size(); i++) {
                    if (notificationModelList.get(i).getKey().compareTo(compareCurrencies) == 0) {
                        // --------- For Hours ----------
                        if (notificationModelList.get(i).getHours() != null) {
                            for (int hour = 0; hour < notificationModelList.get(i).getHours().length; hour++) {
                                String display;
                                if (Integer.parseInt(notificationModelList.get(i).getHours()[hour]) == 12)
                                    display = 12 + "PM";
                                else {
                                    if (Integer.parseInt(notificationModelList.get(i).getHours()[hour]) == 0)
                                        display = 12 + "AM";
                                    else {
                                        display = Integer.parseInt(notificationModelList.get(i).getHours()[hour]) > 12 ?
                                                String.valueOf(Integer.parseInt(notificationModelList.get(i).getHours()[hour]) - 12) + "PM" :
                                                notificationModelList.get(i).getHours()[hour] + "AM";
                                    }
                                }
                                HourModel h = new HourModel(Integer.parseInt(notificationModelList.get(i).getHours()[hour]), display);
                                itemsHours.add(h);
                            }
                        }
                        // --------- For Alerts ----------
                        if (notificationModelList.get(i).getAlertsNotification() != null) {
                            overAlert=notificationModelList.get(i).getAlertsNotification().getHigh();
                            belowAlert=notificationModelList.get(i).getAlertsNotification().getLow();
                            if (notificationModelList.get(i).getAlertsNotification().isHigh_active()) {
                                switchOver.setChecked(true);
                            } else {
                                switchOver.setChecked(false);
                            }

                            txtOverAmount.setText(String.valueOf(notificationModelList.get(i).getAlertsNotification().getHigh()));
                            if (notificationModelList.get(i).getAlertsNotification().isLow_active()) {
                                switchBelow.setChecked(true);

                            } else {
                                switchBelow.setChecked(false);
                            }

                            txtBelowAmount.setText(String.valueOf(notificationModelList.get(i).getAlertsNotification().getLow()));
                        }
                    }
                    Collections.sort(itemsHours, HourModel.ItemByHour);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
        makeOperation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setThemeByActivity();
    }

    @Override
    public void setThemeByActivity() {
        if (themeSelected.contains("Night")) {
            relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            txtCurrentTo.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtCurrentFrom.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtAmount.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtCurrent.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtYouWillReceive.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtReceiveNotification.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtVS.setTextColor(getResources().getColor(R.color.primaryTextTheme1));
            txtTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            linDaily.setBackground(getResources().getDrawable(R.drawable.border_theme1));
            linOverAlert.setBackground(getResources().getDrawable(R.drawable.border_theme1));
            txtAddHour.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme1));
            txtOverAmount.setHintTextColor(getResources().getColor(R.color.secundaryTextColorTheme1));
            txtBelowAmount.setHintTextColor(getResources().getColor(R.color.secundaryTextColorTheme1));
            txtLabelBelow.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
            txtLabelOver.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme1));
            imgMore.setColorFilter(getResources().getColor(R.color.secundaryTextColorTheme1));
            linAddHours.setBackground(getResources().getDrawable(R.drawable.dash_border_theme1));
        } else {
            if (themeSelected.contains("Daylight")) {
                toolbarback.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
                txtAmount.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                relContent.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDaylight));
                txtCurrentTo.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtCurrentFrom.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtCurrent.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtYouWillReceive.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtReceiveNotification.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtVS.setTextColor(getResources().getColor(R.color.primaryTextTheme2));
                txtTitle.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
                linDaily.setBackground(getResources().getDrawable(R.drawable.border_theme2));
                linOverAlert.setBackground(getResources().getDrawable(R.drawable.border_theme2));
                txtAddHour.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme2));
                txtOverAmount.setHintTextColor(getResources().getColor(R.color.secundaryTextColorTheme2));
                txtBelowAmount.setHintTextColor(getResources().getColor(R.color.secundaryTextColorTheme2));
                txtLabelBelow.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                txtLabelOver.setTextColor(getResources().getColor(R.color.primaryTextLabelsTheme2));
                imgMore.setColorFilter(getResources().getColor(R.color.secundaryTextColorTheme2));
                linAddHours.setBackground(getResources().getDrawable(R.drawable.dash_border_theme2));
            }
        }
    }
}
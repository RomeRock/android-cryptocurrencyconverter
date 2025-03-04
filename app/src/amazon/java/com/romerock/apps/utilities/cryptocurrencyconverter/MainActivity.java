package com.romerock.apps.utilities.cryptocurrencyconverter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.google.ads.mediation.inmobi.InMobiConsent;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.inmobi.sdk.InMobiSdk;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Popup;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.adapters.RecyclerViewCurrenciesDashboardAdapter;
import com.romerock.apps.utilities.cryptocurrencyconverter.fragments.ShareDialogFragment;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.IabHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.IabResult;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.Inventory;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SimpleItemTouchHelperCallback;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.Wear;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CheckUDIDListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.CurrenciesListInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.FinishVideo;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.OnStartDragListenerInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.PurchaseDialog;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ThemeInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.CurrencyConvertApiModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.NotificationModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.PurchaseModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.PushNotificationModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.UserUdId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES.cipher;
import static com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities.getThemePreferences;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipyRefreshLayout.OnRefreshListener, OnStartDragListenerInterface, ThemeInterface {
    public static final int DISMISS_TIMEOUT = 2000;
    private static final int FINISH_PURCHASE = 10004;
    public boolean isEditActive = false;
    public double baseValue = 1;
    public int basePosition = 0;
    public String baseNameCurrency = "";
    @BindView(R.id.linMenu)
    LinearLayout linMenu;
    ItemLibraryCurrencyModel FromCurrent;
    ItemLibraryCurrencyModel ToCurrent;
    @BindView(R.id.txtUpdateTime)
    TextView txtUpdateTime;
    @BindView(R.id.recyclerCurrencyDashboard)
    RecyclerView recyclerCurrencyDashboard;
    @BindView(R.id.swipyRefreshCurrencies)
    SwipyRefreshLayout swipyRefreshCurrencies;
    @BindView(R.id.txtEditText)
    TextView txtEditText;
    Map<String, String> currenciesMap = new HashMap<String, String>();
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.linSuscribeMe)
    RelativeLayout linSuscribeMe;
    @BindView(R.id.linButtom)
    LinearLayout linButtom;
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    @BindView(R.id.iconFacebook)
    ImageView iconFacebook;
    @BindView(R.id.iconTwitter)
    ImageView iconTwitter;
    @BindView(R.id.iconInstagram)
    ImageView iconInstagram;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtAddCurrencyLabel)
    TextView txtAddCurrencyLabel;
    @BindView(R.id.imgAddCurrency)
    ImageView imgAddCurrency;
    @BindView(R.id.linLanguage)
    LinearLayout linLanguage;
    @BindView(R.id.linWearApp)
    LinearLayout linWearApp;
    @BindView(R.id.imgLanguage)
    ImageView imgLanguage;
    @BindView(R.id.imgFeedback)
    ImageView imgFeedback;
    @BindView(R.id.imgRateUs)
    ImageView imgRateUs;
    @BindView(R.id.imgWear)
    ImageView imgWear;
    @BindView(R.id.imgSharerewarded)
    ImageView imgSharerewarded;
    @BindView(R.id.adView)
    RelativeLayout adView;
    private int TWEET_COMPOSER_REQUEST_CODE = 65642;
    private int FACEBOOK_REQUEST_CODE = 64207;
    private int ACTIVITY_CHANGE_LANGUAGE = 525;
    private int CHANGE_THEME = 601;
    private int SETUP_PUSH = 530;
    private FragmentManager fm;
    private Bundle skuDetailsProducts;
    private RecyclerViewCurrenciesDashboardAdapter dashboardAdapter;
    private FirebaseHelper firebaseHelper;
    private String TAG = "CRYPTO CURRENCY_CONVERTER";
    private int CURRENCY_SELECTED = 210;
    private int TO_CURRENCY = 211;
    private Map<String, JSONObject> valueSnap;
    private List<ItemLibraryCurrencyModel> listAllItems = new ArrayList<ItemLibraryCurrencyModel>();
    private List<ItemLibraryCurrencyModel> libraryCurrencies = new ArrayList<>();
    private List<ItemLibraryCurrencyModel> listDashboardCurrencies = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;
    private double amount;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor ed;
    private String[] splitCurrencies;
    private ItemTouchHelper.Callback callback;
    private DialogsHelper dialogsHelper;
    private Wear wear;
    private GoogleApiClient mGoogleApiClient;
    private String isFree = "";
    private RewardedVideoAd rewardedVideoAd;


    String UDID = "", isFreeOrPremium = "free";

    public TextView getTxtEditText() {
        return txtEditText;
    }

    public RecyclerViewCurrenciesDashboardAdapter getDashboardAdapter() {
        return dashboardAdapter;
    }

    public CoordinatorLayout getCoordinator() {
        return coordinator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.ChangeLanguage(this);
        FirebaseApp.initializeApp(MainActivity.this);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(MainActivity.this);
        SingletonInAppBilling.Instance().setRewardedVideoAd(rewardedVideoAd);
        setTheme(getThemePreferences(getApplication()));
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        ed = sharedPrefs.edit();
        setContentView(R.layout.activity_main);
        wear = new Wear(MainActivity.this);
        Window window = this.getWindow();
        Utilities.colorStatusBar(getApplication(), window);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        dialogsHelper = new DialogsHelper(MainActivity.this);
        SingletonInAppBilling.Instance().setDialogsHelper(dialogsHelper);
        ed = sharedPrefs.edit();
        firebaseHelper = FirebaseHelper.getInstance();
        if (SingletonInAppBilling.getFirebaseHelper() == null) {
            SingletonInAppBilling.setFirebaseHelper(firebaseHelper);
        }
        if (!sharedPrefs.getBoolean(getString(R.string.preferences_rate), false)) {
            int countSomeLove = sharedPrefs.getInt(getString(R.string.preferences_count_some_love), 0);
            if (countSomeLove > 2 || SingletonInAppBilling.Instance().isShowPopUp()) {
                Popup.RatePopup(MainActivity.this);
                SingletonInAppBilling.Instance().setShowPopUp(true);
                ed.putInt(getString(R.string.preferences_count_some_love), 0);
                ed.commit();
            }
        }
        linSuscribeMe.setVisibility(View.GONE);  //delete for amazon, samsung and huawei
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        swipyRefreshCurrencies.setDistanceToTriggerSync(50);
        swipyRefreshCurrencies.setOnRefreshListener(MainActivity.this);

        JSONObject consentObject = new JSONObject();
        try {
            consentObject.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, true);
            consentObject.put("gdpr", "1");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        InMobiConsent.updateGDPRConsent(consentObject);

        try {
            isFree = CipherAES.decipher(sharedPrefs.getString(getString(R.string.purchaseAndroid), ""));
            SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(isFree);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utilities.checkForBigBanner(MainActivity.this, adView);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                shareAndGetRewarded();
            }

            @Override
            public void onCancel() {
                Log.d("", "");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("", "");
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(wear)
                .addOnConnectionFailedListener(wear)
                .build();
        wear.setmGoogleApiClient(mGoogleApiClient);
        linWearApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wear.openPlayStoreOnWearDevicesWithoutApp();
            }
        });


    }

    public void setThemeByActivity() {
        String themeSelected = sharedPrefs.getString(getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            linMenu.setBackground(ContextCompat.getDrawable(this, R.drawable.underline_theme1));
            linButtom.setBackgroundColor(getResources().getColor(R.color.backgroundAccentTheme1));
            iconTwitter.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
            iconFacebook.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
            iconInstagram.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
            txtAddCurrencyLabel.setTextColor(getResources().getColor(R.color.secundaryTextColorTheme1));
            imgAddCurrency.setColorFilter(getResources().getColor(R.color.secundaryTextColorTheme1));
            imgLanguage.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
            imgLanguage.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
            imgFeedback.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
            imgRateUs.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
            imgWear.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
            imgSharerewarded.setColorFilter(getResources().getColor(R.color.primaryTextTheme1));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public ShareDialog getShareDialog() {
        return shareDialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    void getUpdateFirebase() {
        String currenciesSaved = sharedPrefs.getString(getString(R.string.preferences_defaultCurrencies), "").toString();
        if (currenciesSaved.contains("|")) {
            splitCurrencies = currenciesSaved.split("\\|");
        } else {
            splitCurrencies = new String[1];
            splitCurrencies[0] = currenciesSaved;
        }

        if (sharedPrefs.getBoolean(getString(R.string.preferences_defaultCurrencies_first_time), true)) {
            NotificationModel.saveNotification(MainActivity.this, firebaseHelper, splitCurrencies);
        }
        CurrencyConvertApiModel.getListCurrencies(firebaseHelper, new CurrenciesListInterface() {
            @Override
            public void getCurrenciesList(DataSnapshot dataSnapshotLastest, DataSnapshot dataSnapshotLibrary) {
                if (dataSnapshotLastest != null) {
                    Map<String, JSONObject> valueSnapLastest;
                    valueSnapLastest = (Map<String, JSONObject>) dataSnapshotLastest.getValue();
                    Map<String, JSONObject> valueSnapUpdateLibrary;
                    valueSnapUpdateLibrary = (Map<String, JSONObject>) dataSnapshotLibrary.getValue();
                    listAllItems = CurrencyConvertApiModel.matchCurrenciesWithList(valueSnapLastest, valueSnapUpdateLibrary, txtUpdateTime, MainActivity.this);
                    listDashboardCurrencies = CurrencyConvertApiModel.getListCurrenciesForDashboard(listAllItems, splitCurrencies);
                    if (listDashboardCurrencies == null)
                        listDashboardCurrencies = new ArrayList<ItemLibraryCurrencyModel>();
                    if(recyclerCurrencyDashboard!=null) {
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
                    }
                } else {
                    if (listDashboardCurrencies.size() < 1) {
                        DialogsHelper.showSnackBar(coordinator, getString(R.string.error_internet), getResources().getColor(R.color.alert_snackbar));
                    }
                }
            }
        }, MainActivity.this);
        dialogsHelper.hideLoading();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOnline();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        setThemeByActivity();
        PushNotificationModel.CleanBadges(this);
        firebaseHelper.subscribeUnsubscribeTopic(MainActivity.this, true);
        if (!sharedPrefs.contains(getString(R.string.preferences_topic))) {
            ed.putString(getString(R.string.preferences_topic), "general");
            ed.commit();
        }
        UserUdId.checkPremiumState(MainActivity.this, new CheckUDIDListener() {
            @Override
            public void checkUDIDFromFirebase(boolean haveChild) {
            }

            @Override
            public void checkPremiumState(boolean status) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                String isFreeOrPremiumToSharePreferences;
                if (status) {
                    isFreeOrPremiumToSharePreferences = UserUdId.getPREMIUM();
                    SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(isFreeOrPremiumToSharePreferences);
                    params.setMargins(0, 60, 0, 0);
                    try {
                        ed.putString(getString(R.string.purchaseAndroid), cipher(UserUdId.getPREMIUM()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ed.commit();
                    // linSuscribeMe.setVisibility(View.GONE);
                    adView.setVisibility(View.GONE);
                } else {

                    params.setMargins(0, 64, 0, 0);
                   // linSuscribeMe.setVisibility(View.VISIBLE);
                    if (!sharedPrefs.contains(getString(R.string.shareAndRewarded)) && !SingletonInAppBilling.Instance().isInvalidated()) {
                        ed.putString(getString(R.string.preferences_theme_tittle), "Night");
                        ed.putBoolean(getString(R.string.preferences_isInvalidate), true);
                        SingletonInAppBilling.Instance().setIS_FREE_OR_PREMIUM(UserUdId.getFREE());
                        if (sharedPrefs.contains(getString(R.string.purchaseOrder)))
                            ed.remove(getString(R.string.purchaseOrder));
                        try {
                            ed.putString(getString(R.string.purchaseAndroid), cipher(UserUdId.getFREE()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ed.commit();
                        SingletonInAppBilling.Instance().setInvalidated(true);
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);
                    }
                }
                linLanguage.setLayoutParams(params);

                try {
                    ed.putString(getString(R.string.purchaseAndroid), cipher(SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(MainActivity.this)));
                    ed.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UserUdId.verify( isFreeOrPremium,  sharedPrefs, MainActivity.this, firebaseHelper);
            }
        });
        Utilities.addIntestitialWithCount(MainActivity.this, SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(MainActivity.this));
        getUpdateFirebase();
    }

    public List<ItemLibraryCurrencyModel> getListAllItems() {
        return listAllItems;
    }

    @Override
    protected void onPause() {
        Utilities.closeKeyboard(MainActivity.this);
        recyclerCurrencyDashboard.requestFocus();
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
            SingletonInAppBilling.Instance().getFirebaseDatabase().goOffline();
        super.onPause();
        if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {

            Wearable.CapabilityApi.removeCapabilityListener(
                    mGoogleApiClient,
                    wear,
                    wear.getCapabilityWearApp());

            mGoogleApiClient.disconnect();
        }
    }

    @OnClick({R.id.txtEditText, R.id.linAddCurrency, R.id.iconFacebook, R.id.iconTwitter, R.id.iconInstagram, R.id.linXpressVote, R.id.linRateUs, R.id.linTipCalculator, R.id.linPrivacyPolicy,
            R.id.linSharerewarded, R.id.linFeedback, R.id.linLanguage, R.id.linSuscribeMe, R.id.linAddNotify, R.id.linQuickConverter, R.id.linTaxCalculator, R.id.linLoanCalculator,
            R.id.lin52Challenge,  R.id.linCurrency, R.id.linBooks, R.id.linPiggy})
    public void onViewClicked(View view) {
        if (view != null) {
            Intent i;
            switch (view.getId()) {
                case R.id.linPrivacyPolicy:
                    Utilities.goToLinks(MainActivity.this,   getString(R.string.privacy_policy_url));
                    break;
                case R.id.lin52Challenge:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_52Weeks));
                    break;
                case R.id.linCurrency:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_currency));
                    break;
                case R.id.linBooks:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_books));
                    break;
                case R.id.linPiggy:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_piggy));
                    break;
                case R.id.txtEditText:
                    if (dashboardAdapter != null) {
                        dashboardAdapter.hideAllControllsItems();
                        dashboardAdapter.resetMarginRows();
                    }
                    if (isEditActive) {
                       // mItemTouchHelper.attachToRecyclerView(null);
                        isEditActive = false;
                        txtEditText.setText(getString(R.string.edit));
                        swipyRefreshCurrencies.setEnabled(true);
                    } else {
                        if(recyclerCurrencyDashboard!=null)
                            mItemTouchHelper.attachToRecyclerView(recyclerCurrencyDashboard);
                        isEditActive = true;
                        txtEditText.setText(getString(R.string.done));
                        swipyRefreshCurrencies.setEnabled(false);
                        if (dashboardAdapter != null)
                            dashboardAdapter.showAllControllsItems();
                    }
                    break;
                case R.id.linAddCurrency:
                    if (SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(MainActivity.this).compareTo(UserUdId.getFREE()) == 0) {
                        int countCryptoAded= sharedPrefs.getInt(getString(R.string.countCryptoAded), 0);
                        if(countCryptoAded<1){

                            processSetCurrencies();
                        }else {
                            Popup.ShowRewardedPopup(MainActivity.this, new FinishVideo() {
                                @Override
                                public void finish(boolean isFinishSuccess, boolean completeSuccess) {
                                    if (isFinishSuccess)
                                        processSetCurrencies();
                                }
                            });
                        }
                    }else {
                       processSetCurrencies();
                    }
                    break;
                case R.id.iconFacebook:
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_facebook_profile)));
                        startActivity(intent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_facebook))));
                    }
                    break;
                case R.id.iconTwitter:
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_twitter_profile)));
                        startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.follow_us_twitter))));
                    }
                    break;
                case R.id.iconInstagram:
                    Utilities.goToLinks(MainActivity.this, getString(R.string.follow_us_instagram));
                    break;
                case R.id.linQuickConverter:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_quickConverter));
                    break;
                case R.id.linTipCalculator:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_tipCalculator));
                    break;
                case R.id.linXpressVote:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_xpressVote));
                    break;
                case R.id.linTaxCalculator:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_TaxCalculator));
                    break;
                case R.id.linLoanCalculator:
                    Utilities.goToLinks(MainActivity.this, BuildConfig.URL_STORE+getString(R.string.link_LoanCalulator));
                    break;
                case R.id.linLanguage:
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    i = new Intent(this, LanguageSettingsActivity.class);
                    startActivityForResult(i, ACTIVITY_CHANGE_LANGUAGE);
                    break;
                case R.id.linRateUs:
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    Popup.RatePopup(MainActivity.this);
                    break;
                case R.id.linSharerewarded:
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    ShareDialogFragment shareDialogFragment = new ShareDialogFragment();
                    fm = getSupportFragmentManager();
                    shareDialogFragment = ShareDialogFragment.newInstance();
                    shareDialogFragment.show(fm, "share dialog");
                    break;
                case R.id.linFeedback:
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    Popup.Feedback(MainActivity.this);
                    break;

                case R.id.linAddNotify:
                    i = new Intent(MainActivity.this, NotificationCenterActivity.class);
                    i.putExtra("itemsInDashboard", (Serializable) listDashboardCurrencies);
                    i.putExtra("listAllCurrencies", (Serializable) listAllItems);
                    startActivity(i);
                    break;
            }
        }
    }

    private void processSetCurrencies() {
        dialogsHelper.showLoading();
        isEditActive = false;
        txtEditText.setText(getString(R.string.edit));
        swipyRefreshCurrencies.setEnabled(true);
        if (dashboardAdapter != null)
            dashboardAdapter.hideAllControllsItems();
        Intent i = new Intent(MainActivity.this, CurrencyActivity.class);
        i.putExtra("itemsData", (Serializable) listAllItems);
        i.putExtra("itemsInDashboard", (Serializable) listDashboardCurrencies);
        startActivityForResult(i, CURRENCY_SELECTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SingletonInAppBilling.Instance().setShowPopUp(false);
        if (data != null) {
            Bundle res = data.getExtras();
            if (requestCode == CURRENCY_SELECTED) {

                dialogsHelper.hideLoading();
                if (resultCode == RESULT_OK) {
                    ed.putInt(getString(R.string.countCryptoAded), 1);
                    ed.commit();
                    boolean haveCurrency = false;
                    Utilities.countTotalKeys(MainActivity.this);
                    txtEditText.setVisibility(View.VISIBLE);
                    ItemLibraryCurrencyModel itemReturn = (ItemLibraryCurrencyModel) res.get("Current");
                    Utilities.addIntestitial(MainActivity.this, SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(MainActivity.this));
                    if (listDashboardCurrencies != null)
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
                        if(dashboardAdapter!=null)
                            dashboardAdapter.notifyDataSetChanged();
                        SingletonInAppBilling.Instance().setInvalidated(false);
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        startActivity(intent);
                    } else {
                        dialogsHelper.showSnackBar(coordinator, getString(R.string.have_currency), getResources().getColor(R.color.alert_snackbar));
                    }
                }
            }


        }
        if (requestCode == ACTIVITY_CHANGE_LANGUAGE || requestCode == CHANGE_THEME) {
            if (requestCode == ACTIVITY_CHANGE_LANGUAGE)
                firebaseHelper.subscribeUnsubscribeTopic(MainActivity.this, true);
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);
        }
        if ((requestCode == FACEBOOK_REQUEST_CODE || requestCode == TWEET_COMPOSER_REQUEST_CODE) && (resultCode == RESULT_OK || resultCode == RESULT_CANCELED)) {
            shareAndGetRewarded();
        }
    }


    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshCurrencies.setRefreshing(false);
                        getUpdateFirebase();
                    }
                });
            }
        }, DISMISS_TIMEOUT);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (isEditActive)
            mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("listDashboardCurrencies", (Serializable) listDashboardCurrencies);
        savedInstanceState.putSerializable("listAllItems", (Serializable) listAllItems);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        listDashboardCurrencies = (List<ItemLibraryCurrencyModel>) savedInstanceState.getSerializable("listDashboardCurrencies");
        listAllItems = (List<ItemLibraryCurrencyModel>) savedInstanceState.getSerializable("listAllItems");
    }

    private void shareAndGetRewarded() {
        if (!sharedPrefs.contains(getString(R.string.shareAndRewarded)) && SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(MainActivity.this).compareTo(UserUdId.getFREE()) == 0) {
            dialogsHelper.showSnackBar(coordinator, getString(R.string.share_success), getResources().getColor(R.color.colorAccent));
            ed.putBoolean(getString(R.string.shareAndRewarded), true);
            ed.commit();
        }
    }
}
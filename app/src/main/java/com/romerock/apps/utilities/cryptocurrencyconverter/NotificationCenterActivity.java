package com.romerock.apps.utilities.cryptocurrencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.CipherAES;
import com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities;
import com.romerock.apps.utilities.cryptocurrencyconverter.adapters.RecyclerViewCurrenciesNotificationCenterAdapter;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.DialogsHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.FirebaseHelper;
import com.romerock.apps.utilities.cryptocurrencyconverter.helpers.SingletonInAppBilling;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ItemClickLibraryInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.NotificationsListListener;
import com.romerock.apps.utilities.cryptocurrencyconverter.interfaces.ThemeInterface;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.ItemLibraryCurrencyModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.NotificationModel;
import com.romerock.apps.utilities.cryptocurrencyconverter.model.UserUdId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.romerock.apps.utilities.cryptocurrencyconverter.Utilities.Utilities.getThemePreferences;

public class NotificationCenterActivity extends AppCompatActivity implements ThemeInterface {

    @BindView(R.id.recyclerNotificationCenter)
    RecyclerView recyclerNotificationCenter;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.linEmptyNotifications)
    LinearLayout linEmptyNotifications;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.toolbarback)
    Toolbar toolbarback;
    @BindView(R.id.adView)
    AdView adView;

    private SharedPreferences sharedPrefs;
    private FirebaseHelper firebaseHelper;
    private List<ItemLibraryCurrencyModel> listDashboardCurrencies;
    private List<ItemLibraryCurrencyModel> listAllCurrencies;
    private String UDID;
    private String isFreeOrPremium;
    private RecyclerViewCurrenciesNotificationCenterAdapter mAdapter;
    private List<NotificationModel> notificationModelList;
    private boolean isEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Utilities.colorStatusBar(getApplication(), window);
        Utilities.ChangeLanguage(NotificationCenterActivity.this);
        setTheme(getThemePreferences(getApplication()));
        setContentView(R.layout.activity_notification_center);
        ButterKnife.bind(this);
        sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listDashboardCurrencies = (List<ItemLibraryCurrencyModel>) getIntent().getSerializableExtra("itemsInDashboard");
            listAllCurrencies = (List<ItemLibraryCurrencyModel>) getIntent().getSerializableExtra("listAllCurrencies");
        }
        notificationModelList = new ArrayList<>();
        recyclerNotificationCenter.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewCurrenciesNotificationCenterAdapter(notificationModelList, NotificationCenterActivity.this, new ItemClickLibraryInterface() {
            @Override
            public void onItemClicked(View view, ItemLibraryCurrencyModel item, String code) {

            }
        }, listDashboardCurrencies, listAllCurrencies, recyclerNotificationCenter, new NotificationsListListener() {
            @Override
            public void getNotificationList(List<NotificationModel> notificationModelList) {

            }

            @Override
            public void removeNotification(String key) {
                NotificationModel.removeNotification(firebaseHelper, UDID, isFreeOrPremium, key);
                if (notificationModelList.size() < 1) {
                    linEmptyNotifications.setVisibility(View.VISIBLE);
                }
                UserUdId.setDevCancelRecall(firebaseHelper, UDID, isFreeOrPremium);
            }
        }, sharedPrefs);
        recyclerNotificationCenter.setAdapter(mAdapter);
        recyclerNotificationCenter.setItemAnimator(new DefaultItemAnimator());
        AdRequest adRequest = Utilities.addBanner(NotificationCenterActivity.this, "", getString(R.string.banner_ad_unit_id_big));
        if (adRequest != null){
            adView.loadAd(adRequest);
        }
        else
            adView.setVisibility(View.GONE);
    }

    @OnClick({R.id.toolbarback, R.id.linAddNotify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbarback:
                finish();
                break;
            case R.id.linAddNotify:
                if (listDashboardCurrencies.size() > 1) {
                    isEditable = false;
                    Intent i = new Intent(NotificationCenterActivity.this, SetupPushNotificationsActivity.class);
                    i.putExtra("Currencies", (Serializable) listDashboardCurrencies);
                    i.putExtra("AllListCurrencies", (Serializable) listAllCurrencies);
                    i.putExtra("positionFrom", listDashboardCurrencies.get(0).getName());
                    i.putExtra("positionTo", listDashboardCurrencies.get(1).getName());
                    startActivity(i);
                } else {
                    DialogsHelper.showSnackBar(coordinator, getString(R.string.need_at_least_2_currencies), getResources().getColor(R.color.alert_snackbar));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOnline();
        try {
            sharedPrefs = getSharedPreferences(getString(R.string.preferences_name), MODE_PRIVATE);
            setThemeByActivity();
            firebaseHelper = FirebaseHelper.getInstance();
            try {
                isFreeOrPremium = SingletonInAppBilling.Instance().getIS_FREE_OR_PREMIUM(NotificationCenterActivity.this);
                if (isFreeOrPremium.compareTo(UserUdId.getFREE()) == 0) {
                    UDID = CipherAES.decipher(sharedPrefs.getString(getString(R.string.udidAndroid), ""));
                } else {
                    UDID = CipherAES.decipher(sharedPrefs.getString(getString(R.string.purchaseOrder), ""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            NotificationModel.getMyNotifications(NotificationCenterActivity.this, firebaseHelper, UDID, isFreeOrPremium, new NotificationsListListener() {
                @Override
                public void getNotificationList(List<NotificationModel> notificationModelListSend) {
                    if (notificationModelListSend.size() > 0) {
                        linEmptyNotifications.setVisibility(View.GONE);
                        recyclerNotificationCenter.setVisibility(View.VISIBLE);
                        notificationModelList.clear();
                        notificationModelList.addAll(notificationModelListSend);
                    } else {
                        linEmptyNotifications.setVisibility(View.VISIBLE);
                        recyclerNotificationCenter.setVisibility(View.GONE);
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void removeNotification(String key) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onPause() {
        if(SingletonInAppBilling.Instance().getFirebaseDatabase()!=null)
        SingletonInAppBilling.Instance().getFirebaseDatabase().goOffline();
        super.onPause();
    }


    @Override
    public void setThemeByActivity() {
        String themeSelected = sharedPrefs.getString(getString(R.string.preferences_theme_tittle), null);
        if (themeSelected.contains("Night")) {
            toolbarback.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            txtTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            if (themeSelected.contains("Daylight")) {
                toolbarback.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
                txtTitle.setBackgroundColor(getResources().getColor(R.color.colorAccent_Daylight));
            }
        }
    }
}
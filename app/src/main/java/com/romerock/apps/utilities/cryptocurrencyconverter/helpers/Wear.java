package com.romerock.apps.utilities.cryptocurrencyconverter.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.wearable.intent.RemoteIntent;
import com.romerock.apps.utilities.cryptocurrencyconverter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Ebricko on 02/05/2018.
 */
public class Wear  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        CapabilityApi.CapabilityListener{
    private static final String WELCOME_MESSAGE = "Welcome to our Mobile app!\n\n";
    private static final String CHECKING_MESSAGE =
            WELCOME_MESSAGE + "Checking for Wear Devices for app...\n";
    private static final String NO_DEVICES =
            WELCOME_MESSAGE
                    + "You have no Wear devices linked to your phone at this time.\n";
    private static final String MISSING_ALL_MESSAGE =
            WELCOME_MESSAGE
                    + "You are missing the Wear app on all your Wear Devices, please click on the "
                    + "button below to install it on those device(s).\n";
    private static final String INSTALLED_SOME_DEVICES_MESSAGE =
            WELCOME_MESSAGE
                    + "Wear app installed on some your device(s) (%s)!\n\nYou can now use the "
                    + "MessageApi, DataApi, etc.\n\n"
                    + "To install the Wear app on the other devices, please click on the button "
                    + "below.\n";
    private static final String INSTALLED_ALL_DEVICES_MESSAGE =
            WELCOME_MESSAGE
                    + "Wear app installed on all your devices (%s)!\n\nYou can now use the "
                    + "MessageApi, DataApi, etc.";
    // Name of capability listed in Wear app's wear.xml.
    // IMPORTANT NOTE: This should be named differently than your Phone app's capability.
    private static final String CAPABILITY_WEAR_APP = "verify_remote_example_wear_app";
    // Links to Wear app (Play Store).
    // TODO: Replace with your links/packages.
    private static final String PLAY_STORE_APP_URI =
            "market://details?id=com.romerock.apps.utilities.cryptocurrencyconverter";
    private Context context;
    // Result from sending RemoteIntent to wear device(s) to open app in play/app store.
    private final ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == RemoteIntent.RESULT_OK) {
                Toast toast = Toast.makeText(
                        context,
                        context.getResources().getString(R.string.Play_Store_Request_Wear),
                        Toast.LENGTH_LONG);
                toast.show();

            } else if (resultCode == RemoteIntent.RESULT_FAILED) {
                Toast toast = Toast.makeText(
                        context,
                        context.getResources().getString(R.string.play_store_request_fail),
                        Toast.LENGTH_LONG);
                toast.show();

            } else {
                throw new IllegalStateException("Unexpected result " + resultCode);
            }
        }
    };

    public static String getCapabilityWearApp() {
        return CAPABILITY_WEAR_APP;
    }

    private Set<Node> mWearNodesWithApp;
    private List<Node> mAllConnectedNodes;

    private GoogleApiClient mGoogleApiClient;

    public Wear(Context context) {
        this.context = context;

    }

    public void findWearDevicesWithApp() {
        // You can filter this by FILTER_REACHABLE if you only want to open Nodes (Wear Devices)
        // directly connect to your phone.
        PendingResult<CapabilityApi.GetCapabilityResult> pendingResult =
                Wearable.CapabilityApi.getCapability(
                        mGoogleApiClient,
                        CAPABILITY_WEAR_APP,
                        CapabilityApi.FILTER_ALL);

        pendingResult.setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {
            @Override
            public void onResult(@NonNull CapabilityApi.GetCapabilityResult getCapabilityResult) {
                if (getCapabilityResult.getStatus().isSuccess()) {
                    CapabilityInfo capabilityInfo = getCapabilityResult.getCapability();
                    mWearNodesWithApp = capabilityInfo.getNodes();
                    verifyNodeAndUpdateUI();

                }
            }
        });
    }

    public void findAllWearDevices() {

        PendingResult<NodeApi.GetConnectedNodesResult> pendingResult =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);

        pendingResult.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {

                if (getConnectedNodesResult.getStatus().isSuccess()) {
                    mAllConnectedNodes = getConnectedNodesResult.getNodes();
                    verifyNodeAndUpdateUI();

                }
            }
        });
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        mWearNodesWithApp = capabilityInfo.getNodes();
        findAllWearDevices();
        verifyNodeAndUpdateUI();
    }

    private void verifyNodeAndUpdateUI() {
        if ((mWearNodesWithApp == null) || (mAllConnectedNodes == null)) {

        }  else if (mWearNodesWithApp.size() < mAllConnectedNodes.size()) {
            // TODO: Add your code to communicate with the wear app(s) via
            // Wear APIs (MessageApi, DataApi, etc.)

            String installMessage =
                    String.format(INSTALLED_SOME_DEVICES_MESSAGE, mWearNodesWithApp);
            //   mInformationTextView.setText(installMessage);


        } else {
            // TODO: Add your code to communicate with the wear app(s) via
            // Wear APIs (MessageApi, DataApi, etc.)

            String installMessage =
                    String.format(INSTALLED_ALL_DEVICES_MESSAGE, mWearNodesWithApp);
            //  mInformationTextView.setText(installMessage);
        }
    }

    public void openPlayStoreOnWearDevicesWithoutApp() {
        ArrayList<Node> nodesWithoutApp = new ArrayList<>();

        for (Node node : mAllConnectedNodes) {
            if (!mWearNodesWithApp.contains(node)) {
                nodesWithoutApp.add(node);
            }
        }

        if (!nodesWithoutApp.isEmpty()) {
                    Intent intent =
                    new Intent(Intent.ACTION_VIEW)
                            .addCategory(Intent.CATEGORY_BROWSABLE)
                            .setData(Uri.parse(PLAY_STORE_APP_URI));

            for (Node node : nodesWithoutApp) {
                RemoteIntent.startRemoteActivity(
                        context,
                        intent,
                        mResultReceiver,
                        node.getId());
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // Set up listeners for capability changes (install/uninstall of remote app).
        Wearable.CapabilityApi.addCapabilityListener(
                mGoogleApiClient,
                this,
                CAPABILITY_WEAR_APP);

        // Initial request for devices with our capability, aka, our Wear app installed.
        findWearDevicesWithApp();

        // Initial request for all Wear devices connected (with or without our capability).
        // Additional Note: Because there isn't a listener for ALL Nodes added/removed from network
        // that isn't deprecated, we simply update the full list when the Google API Client is
        // connected and when capability changes come through in the onCapabilityChanged() method.
        findAllWearDevices();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }
}

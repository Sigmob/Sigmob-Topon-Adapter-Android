package com.sigmob.sigmobtoponadapterdemo;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATAdStatusInfo;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.ATSDK;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialExListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterstitialAdActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ATSDK.setChannel("customsigmob");
        ATSDK.integrationChecking(getApplicationContext());
        ATSDK.setNetworkLogDebug(true);
        ATSDK.init(getApplicationContext(), "TopOn_AppId", "TopOn_AppKey");

        initInterstitialAd("Topon插屏广告位");
        findViewById(R.id.load_ad_btn).setOnClickListener(this);
        findViewById(R.id.is_ad_ready_btn).setOnClickListener(this);
        findViewById(R.id.show_ad_btn).setOnClickListener(this);
    }

    private static final String TAG = "Sigmob-Custom-AT";

    private void printLogOnUI(String str){
        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();

    }
    private ATInterstitial mInterstitialAd;
    private void initInterstitialAd(String placementId) {
        mInterstitialAd = new ATInterstitial(this, placementId);

        mInterstitialAd.setAdListener(new ATInterstitialExListener() {

            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
                printLogOnUI("onDeeplinkCallback");
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo adInfo, ATNetworkConfirmInfo networkConfirmInfo) {
                Log.i(TAG, "onDownloadConfirm: adInfo=" + adInfo.toString());
                printLogOnUI("onDownloadConfirm");
            }

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                printLogOnUI("onInterstitialAdLoaded");
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Log.i(TAG, "onInterstitialAdLoadFail:\n" + adError.getFullErrorInfo());
                printLogOnUI("onInterstitialAdLoadFail:" + adError.getFullErrorInfo());
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClicked:\n" + entity.toString());
                printLogOnUI("onInterstitialAdClicked");
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdShow:\n" + entity.toString());
                printLogOnUI("onInterstitialAdShow");
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClose:\n" + entity.toString());
                printLogOnUI("onInterstitialAdClose");
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoStart:\n" + entity.toString());
                printLogOnUI("onInterstitialAdVideoStart");
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoEnd:\n" + entity.toString());
                printLogOnUI("onInterstitialAdVideoEnd");
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Log.i(TAG, "onInterstitialAdVideoError:\n" + adError.getFullErrorInfo());
                printLogOnUI("onInterstitialAdVideoError");
            }

        });

    }


    private void loadAd() {
        if (mInterstitialAd == null) {
            printLogOnUI("ATInterstitial is not init.");
            return;
        }

        Map<String, Object> localMap = new HashMap<>();

        mInterstitialAd.setLocalExtra(localMap);
        mInterstitialAd.load();
    }

    private void isAdReady() {
        ATAdStatusInfo atAdStatusInfo = mInterstitialAd.checkAdStatus();
        printLogOnUI("interstitial ad ready status:" + atAdStatusInfo.isReady());
        List<ATAdInfo> atAdInfoList = mInterstitialAd.checkValidAdCaches();
        Log.i(TAG, "Valid Cahce size:" + (atAdInfoList != null ? atAdInfoList.size() : 0));
        if (atAdInfoList != null) {
            for (ATAdInfo adInfo : atAdInfoList) {
                Log.i(TAG, "\nCahce detail:" + adInfo.toString());
            }
        }
    }

    private void showAd() {
        mInterstitialAd.show(InterstitialAdActivity.this, null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mInterstitialAd != null) {
            mInterstitialAd.setAdSourceStatusListener(null);
            mInterstitialAd.setAdDownloadListener(null);
            mInterstitialAd.setAdListener(null);
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v == null) return;


        if (v.getId() == R.id.load_ad_btn) {
            loadAd();
        } else if (v.getId() == R.id. is_ad_ready_btn) {
            isAdReady();
        } else if (v.getId() == R.id.show_ad_btn) {
            showAd();
        }
    }
}

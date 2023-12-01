/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.customsigmobadapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBidRequestInfoListener;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.BaseAd;
import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.newInterstitial.WindNewInterstitialAd;
import com.sigmob.windad.newInterstitial.WindNewInterstitialAdListener;
import com.sigmob.windad.newInterstitial.WindNewInterstitialAdRequest;


import java.util.Map;

public class CustomSigmobATInterstitialAdapter extends CustomInterstitialAdapter {

    private static final String Tag = CustomSigmobATInterstitialAdapter.class.getSimpleName();
    private WindNewInterstitialAdRequest interstitialAdRequest;
    private WindNewInterstitialAd interstitialAd;
    String bidToken = "";
    private volatile boolean isC2SBidding = false;
    private String placementId = "";
    private ATBiddingListener biddingListener;

    static synchronized void loadAd(CustomSigmobATInterstitialAdapter adapter) {
         adapter.startLoadAdForInterstitial();
    }

    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        String appId = ATInitMediation.getStringFromMap(serverExtra, "app_id");
        String appKey = ATInitMediation.getStringFromMap(serverExtra, "app_key");
        this.placementId =  ATInitMediation.getStringFromMap(serverExtra, "placement_id");
        this.bidToken = ATInitMediation.getStringFromMap(serverExtra, "payload");
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(this.placementId)) {
            onAdLoadError("", "app_id、app_key、placement_id could not be null.");
            return;
        }
        postOnMainThread(new Runnable() {
            @Override // java.lang.Runnable
            public final void run() {
                try {

                    WindAds windAds = WindAds.sharedAds();
                    boolean isInit = windAds.isInit();
                    if (!isInit) {
                        WindAdOptions windAdOptions = new WindAdOptions(appId, appKey);
                        isInit = windAds.startWithOptions(context, windAdOptions);
                    }
                    if (isInit){
                        CustomSigmobATInterstitialAdapter.loadAd(CustomSigmobATInterstitialAdapter.this);
                    }else {
                        onAdLoadError("", "Sigmob SDK init failed.");
                    }

                } catch (Throwable th) {
                    onAdLoadError("", th.getMessage());
                }
            }
        });
    }

    private void startLoadAdForInterstitial() {
        this.interstitialAdRequest = new WindNewInterstitialAdRequest(this.placementId, "", (Map) null);
        this.interstitialAd = new WindNewInterstitialAd(this.interstitialAdRequest );
        this.interstitialAd.setWindNewInterstitialAdListener(new InterstitialAdListener());
        if (this.isC2SBidding && this.interstitialAd!= null) {
            this.interstitialAd.setCurrency("CNY");
            this.interstitialAd.loadAd();
        } else if (!TextUtils.isEmpty(this.bidToken)) {
            this.interstitialAd.loadAd(this.bidToken);
        } else {
            this.interstitialAd.loadAd();
        }
    }

    public class InterstitialAdListener implements WindNewInterstitialAdListener {
        InterstitialAdListener() {
        }

        public final void onInterstitialAdLoadSuccess(String placementId) {
            if (TextUtils.equals(placementId, CustomSigmobATInterstitialAdapter.this.placementId)) {
                if (!CustomSigmobATInterstitialAdapter.this.isC2SBidding || CustomSigmobATInterstitialAdapter.this.interstitialAd == null) {
                    if (CustomSigmobATInterstitialAdapter.this.mLoadListener != null) {
                        CustomSigmobATInterstitialAdapter.this.mLoadListener.onAdCacheLoaded(new BaseAd[0]);
                        return;
                    }
                    return;
                }

                double ecpm = 0.0;
                String ecpmStr = CustomSigmobATInterstitialAdapter.this.interstitialAd.getEcpm();

                try {
                    if (!TextUtils.isEmpty(ecpmStr)) {
                        ecpm = Double.parseDouble(ecpmStr) / 100.0;
                    } else {
                        onAdLoadError("", "ecpm is null");
                    }
                } catch (Exception var7) {
                    onAdLoadError("", "ecpm is null");
                }

                biddingListener.onC2SBiddingResultWithCache(ATBiddingResult.success(ecpm, "" + System.currentTimeMillis(), new CustomSigmobATBiddingNotice(CustomSigmobATInterstitialAdapter.this.interstitialAd), ATAdConst.CURRENCY.RMB_CENT), null);


            }
        }

        public final void onInterstitialAdPreLoadSuccess(String placementId) {
        }

        public final void onInterstitialAdPreLoadFail(String placementId) {
        }

        @Override
        public void onInterstitialAdShow(String s) {
            if (TextUtils.equals(placementId,CustomSigmobATInterstitialAdapter.this.placementId ) && CustomSigmobATInterstitialAdapter.this.mImpressListener != null) {
                CustomSigmobATInterstitialAdapter.this.mImpressListener.onInterstitialAdShow();
                CustomSigmobATInterstitialAdapter.this.mImpressListener.onInterstitialAdVideoStart();
            }
        }

        public final void onInterstitialAdClicked(String placementId) {
            if (TextUtils.equals(placementId, CustomSigmobATInterstitialAdapter.this.placementId) && CustomSigmobATInterstitialAdapter.this.mImpressListener != null) {
                CustomSigmobATInterstitialAdapter.this.mImpressListener.onInterstitialAdClicked();
            }
        }

        public final void onInterstitialAdClosed(String placementId) {
            if (TextUtils.equals(placementId, CustomSigmobATInterstitialAdapter.this.placementId) && CustomSigmobATInterstitialAdapter.this.mImpressListener != null) {
                CustomSigmobATInterstitialAdapter.this.mImpressListener.onInterstitialAdClose();
            }
        }

        public final void onInterstitialAdLoadError(WindAdError windAdError, String placementId) {
            if (TextUtils.equals(placementId, CustomSigmobATInterstitialAdapter.this.placementId) && windAdError != null) {
                CustomSigmobATInterstitialAdapter.this.onAdLoadError(String.valueOf(windAdError.getErrorCode()), windAdError.toString());
            }
        }

        @Override
        public void onInterstitialAdShowError(WindAdError windAdError, String s) {
            if (TextUtils.equals(placementId, CustomSigmobATInterstitialAdapter.this.placementId) && CustomSigmobATInterstitialAdapter.this.mImpressListener != null) {
                CustomSigmobATInterstitialAdapter.this.mImpressListener.onInterstitialAdVideoError(new StringBuilder().append(windAdError.getErrorCode()).toString(), windAdError.toString());
            }
        }

    }

    /* JADX WARN: Not initialized variable reg: 0, insn: 0x005a: INVOKE  (r0 I:java.lang.Exception) type: VIRTUAL call: java.lang.Exception.printStackTrace():void, block:B:16:0x005a */
    public void show(Activity activity) {
        Exception printStackTrace;
        try {
            if (isAdReady()) {
                if (this.isC2SBidding) {
                    int ecpm = 0;
                    try {
                        ecpm = Integer.parseInt(this.interstitialAd.getEcpm());
                    }catch (Throwable th){
                        th.printStackTrace();
                    }

                    this.interstitialAd.setBidEcpm(ecpm);
                }
                this.interstitialAd.show(null);
            }
        } catch (Exception unused) {

        }
    }

    public boolean isAdReady() {
       if (this.interstitialAd != null) {
            return this.interstitialAd.isReady();
        } else {
            return false;
        }
    }

    public String getNetworkName() {
        return "Sigmob-Custom";
    }

    public void destory() {
        this.interstitialAdRequest = null;

        if (this.interstitialAd != null) {
            this.interstitialAd.destroy();
            this.interstitialAd = null;
        }
    }

    public String getNetworkPlacementId() {
        return this.placementId;
    }

    public String getNetworkSDKVersion() {
        return WindAds.getVersion();
    }

    public void getBidRequestInfo(Context applicationContext, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBidRequestInfoListener bidRequestInfoListener) {
        Log.e(getNetworkName(),"not support");
    }

    public boolean startBiddingRequest(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBiddingListener biddingListener) {
        this.biddingListener = biddingListener;
        this.isC2SBidding = true;
        loadCustomNetworkAd(context, serverExtra, localExtra);
        return true;
    }

    public void onAdLoadError(String code, String msg) {
        if (this.isC2SBidding) {
            if (this.biddingListener != null) {
                this.biddingListener.onC2SBiddingResultWithCache(ATBiddingResult.fail(msg), (BaseAd)null);
            }

        } else {
            if ( this.mLoadListener != null) {
                this.mLoadListener.onAdLoadError(code, msg);
            }

        }
    }
}
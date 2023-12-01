package com.customsigmobadapter;

import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBiddingNotice;
import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.ATSDK;
import com.anythink.network.sigmob.SigmobATInitManager;
import com.sigmob.windad.Splash.WindSplashAD;
import com.sigmob.windad.WindAdBiddingLossReason;
import com.sigmob.windad.interstitial.WindInterstitialAd;
import com.sigmob.windad.natives.WindNativeUnifiedAd;
import com.sigmob.windad.newInterstitial.WindNewInterstitialAd;
import com.sigmob.windad.rewardVideo.WindRewardVideoAd;

import java.util.HashMap;
import java.util.Map;

public class CustomSigmobATBiddingNotice implements ATBiddingNotice {
    private Object adObject;

    public CustomSigmobATBiddingNotice(Object adObject) {
        this.adObject = adObject;
    }

    public void notifyBidWin(double costPrice, double secondPrice, Map<String, Object> extra) {
        HashMap info = new HashMap();
        info.put("AUCTION_PRICE", SigmobATInitManager.getEcpmInt(this.adObject));
        info.put("HIGHEST_LOSS_PRICE", (int)Math.round(secondPrice));
        info.put("CURRENCY", "CNY");
        if (ATSDK.isNetworkLogDebug()) {
            Log.i("CustomSigmobATBiddingNotice", "notifyBidWin() >>> ".concat(String.valueOf(info)));
        }

        try {
            if (this.adObject instanceof WindNativeUnifiedAd) {
                ((WindNativeUnifiedAd)this.adObject).sendWinNotificationWithInfo(info);
            } else if (this.adObject instanceof WindRewardVideoAd) {
                ((WindRewardVideoAd)this.adObject).sendWinNotificationWithInfo(info);
            } else if (this.adObject instanceof WindInterstitialAd) {
                ((WindInterstitialAd)this.adObject).sendWinNotificationWithInfo(info);
            } else if (this.adObject instanceof WindNewInterstitialAd) {
                ((WindNewInterstitialAd)this.adObject).sendWinNotificationWithInfo(info);
            } else if (this.adObject instanceof WindSplashAD) {
                ((WindSplashAD)this.adObject).sendWinNotificationWithInfo(info);
            }
        } catch (Exception exception) {
        }

        this.adObject = null;
    }

    public void notifyBidLoss(String lossCode, double winPrice, Map<String, Object> extra) {
        WindAdBiddingLossReason reason = WindAdBiddingLossReason.LOSS_REASON_LOW_PRICE;
        if ("2".equals(lossCode)) {
            reason = WindAdBiddingLossReason.LOSS_REASON_RETURN_TIMEOUT;
        }

        HashMap info;
        int winPrice1 = (int)Math.round(winPrice);
        (info = new HashMap()).put("AUCTION_PRICE", (int)Math.round(winPrice1));
        info.put("CURRENCY", "CNY");
        info.put("LOSS_REASON", reason.getCode());
        int extra1;
        String.valueOf(extra1 = ATInitMediation.getIntFromMap(extra, "adn_id", 10001));
        String extra2;
        switch (extra1) {
            case 1:
            case 2:
                extra2 = "1";
                break;
            default:
                extra2 = "10001";
        }

        info.put("ADN_ID", extra2);
        if (ATSDK.isNetworkLogDebug()) {
            Log.i("CustomSigmobATBiddingNotice", "notifyBidLoss() >>> lossCode = " + lossCode + ", lossReason = " + reason.getCode() + " winPrice = $" + winPrice1 + ", " + info);
        }

        try {
            if (this.adObject instanceof WindNativeUnifiedAd) {
                ((WindNativeUnifiedAd)this.adObject).sendLossNotificationWithInfo(info);
            } else if (this.adObject instanceof WindRewardVideoAd) {
                ((WindRewardVideoAd)this.adObject).sendLossNotificationWithInfo(info);
            } else if (this.adObject instanceof WindInterstitialAd) {
                ((WindInterstitialAd)this.adObject).sendLossNotificationWithInfo(info);
            } else if (this.adObject instanceof WindNewInterstitialAd) {
                ((WindNewInterstitialAd)this.adObject).sendLossNotificationWithInfo(info);
            } else if (this.adObject instanceof WindSplashAD) {
                ((WindSplashAD)this.adObject).sendLossNotificationWithInfo(info);
            }
        } catch (Exception exception) {
        }

        this.adObject = null;
    }

    public void notifyBidDisplay(boolean isWinner, double displayPrice) {
    }

    public ATAdConst.CURRENCY getNoticePriceCurrency() {
        return ATAdConst.CURRENCY.RMB_CENT;
    }
}

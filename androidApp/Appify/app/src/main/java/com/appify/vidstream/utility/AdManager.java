package com.appify.vidstream.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.appify.vidstream.constants.ApplicationConstants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.sdk.InMobiSdk;

import java.util.Date;
import java.util.Map;

public class AdManager implements ApplicationConstants{

    private static InterstitialAd AdMob_Interstitial;
    private static InMobiInterstitial InMobi_InterstitialAd;
    private static long NewTime;
    private Context _context;

    public  AdManager(Context context){
        this._context = context;
    }

    public void createAdMobAds()
    {
        AdMob_Interstitial = new InterstitialAd(_context);
        AdMob_Interstitial.setAdUnitId(ADMOB_INTERSTITIAL);
        requestNewAdMobInt();
        AdMob_Interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }
            @Override
            public void onAdClosed() {
                long adNewTime = new Date().getTime() / 1000;
                setNewTime(adNewTime);
                requestNewAdMobInt();
            }
        });
    }

    private void requestNewAdMobInt()
    {
        AdRequest adRequestInt = new AdRequest.Builder().build();
        // Load the interstitial ad.
        AdMob_Interstitial.loadAd(adRequestInt);
    }

    public InterstitialAd getAdMobAd() {
        return AdMob_Interstitial;
    }

    public void setNewTime(long NewTime){
        this.NewTime = NewTime;
    }

    public long getNewTime(){
        return NewTime;
    }

    public void createInMobiInterstitial()
    {
        //Fresco.initialize(this);
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
        InMobiSdk.init(_context, INMOBI_ACCOUNT_ID);

        InMobi_InterstitialAd = new InMobiInterstitial(_context, INMOBI_INTERSTITIAL,
                new InMobiInterstitial.InterstitialAdListener() {
                    @Override
                    public void onAdRewardActionCompleted(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
                    }
                    @Override
                    public void onAdDisplayed(InMobiInterstitial inMobiInterstitial) {
                    }
                    @Override
                    public void onAdDismissed(InMobiInterstitial inMobiInterstitial) {
                        long adNewTime = new Date().getTime() / 1000;
                        setNewTime(adNewTime);
                        createInMobiInterstitial();
                    }
                    @Override
                    public void onAdInteraction(InMobiInterstitial inMobiInterstitial, Map<Object, Object> map) {
                    }
                    @Override
                    public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
                        if (inMobiInterstitial.isReady()) {
                            InMobi_InterstitialAd.load();
                        }
                    }
                    @Override
                    public void onAdLoadFailed(InMobiInterstitial inMobiInterstitial, InMobiAdRequestStatus inMobiAdRequestStatus) {
                    }
                    @Override
                    public void onUserLeftApplication(InMobiInterstitial inMobiInterstitial) {
                    }
                });
    }

    public InMobiInterstitial getInMobiAd() {
        return InMobi_InterstitialAd;
    }

}

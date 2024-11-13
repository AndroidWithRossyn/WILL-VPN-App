package com.willdev.openvpn.fromanother.util.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdkUtils;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.textview.MaterialTextView;
import com.willdev.openvpn.utils.Config;
import com.willdev.openvpn.utils.Util;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAdsShowOptions;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.R;
import com.willdev.openvpn.database.DatabaseHandler;
import com.willdev.openvpn.fromanother.activity.Login;
import com.willdev.openvpn.fromanother.interfaces.FavouriteIF;
import com.willdev.openvpn.fromanother.interfaces.FullScreen;
import com.willdev.openvpn.fromanother.interfaces.OnClick;
import com.willdev.openvpn.fromanother.interfaces.VideoAd;
import com.willdev.openvpn.fromanother.service.DownloadIGService;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.login.LoginManager;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;

import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.adlisteners.VideoListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

public class Method {

    private Activity activity;
    public static boolean loginBack = false;
    public static boolean isUpload = true, isDownload = true;
    public boolean personalization_ad = false;
    private OnClick onClick;
    private VideoAd videoAd;
    private FullScreen fullScreen;

    Util util = new Util();
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "login";
    public String pref_login = "pref_login";
    public String profileId = "profileId";
    public String userImage = "userImage";
    public String loginType = "loginType";
    public String show_login = "show_login";
    public String notification = "notification";
    public String verification_code = "verification_code";
    public String is_verification = "is_verification";
    public String them_setting = "them";

    public String reg_name = "reg_name";
    public String reg_email = "reg_email";
    public String reg_password = "reg_password";
    public String reg_phoneNo = "reg_phoneNo";
    public String reg_reference = "reg_reference";

    public String language_ids = "language_ids";

    private String filename;
    private DatabaseHandler db;

    private RewardedVideoAd rewardedVideoAd;
    private String TAG = "RewarderVideoAd";
    private RewardedAd mRewardedAd;
    private Boolean facebookAd = false, startAd = false;
    private StartAppAd startAppAd;
    private Context mContext;
    private InterstitialAd mInterstitialAdMob;

    private boolean isRewardVideoLoaded = false, isInterstitialLoaded = false, isBannerLoaded = false;

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, VideoAd videoAd) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
        this.videoAd = videoAd;
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, OnClick onClick) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        this.onClick = onClick;
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity, OnClick onClick, VideoAd videoAd, FullScreen fullScreen) {
        this.activity = activity;
        db = new DatabaseHandler(activity);
        this.onClick = onClick;
        this.videoAd = videoAd;
        this.fullScreen = fullScreen;
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
    }

    public void login() {
        String firstTime = "firstTime";
        if (!pref.getBoolean(firstTime, false)) {
            editor.putBoolean(pref_login, false);
            editor.putBoolean(firstTime, true);
            editor.commit();
        }
    }


    public boolean isLogin() {
        return pref.getBoolean(pref_login, false);
    }

    public String getName() {
        return pref.getString("pref_name", "");
    }

    public String getEmail() {
        return pref.getString("pref_email", "");
    }

    public String userId() {
        return pref.getString(profileId, "");
    }


    public String getLoginType() {
        return pref.getString(loginType, "");
    }


    public String getLanguageIds() {
        return pref.getString(language_ids, "");
    }


    public void forceRTLIfSupported() {
        if (activity.getResources().getString(R.string.isRTL).equals("true")) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }


    public boolean isRtl() {
        return activity.getResources().getString(R.string.isRTL).equals("true");
    }


    public boolean isAppInstalledWhatsapp() {
        String packageName = "com.whatsapp";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isAppInstalledInstagram() {
        String packageName = "com.instagram.android";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isAppInstalledFacebook() {
        String packageName = "com.facebook.katana";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isAppInstalledFbMessenger() {
        String packageName = "com.facebook.orca";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isAppInstalledTwitter() {
        String packageName = "com.twitter.android";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }


    public int getScreenHeight() {
        int columnHeight;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnHeight = point.y;
        return columnHeight;
    }



    public void VideoAdDialog(String type, String value, Context context) {

        mContext = context;

        if (Constant.aboutUsList != null) {
            if (Constant.aboutUsList.isRewarded_video_ads()) {
                if (value.equals("view_ad")) {
                    showAdDialog(type);
                } else {
                    Constant.REWARD_VIDEO_AD_COUNT = Constant.REWARD_VIDEO_AD_COUNT + 1;
                    if (Constant.REWARD_VIDEO_AD_COUNT == Constant.REWARD_VIDEO_AD_COUNT_SHOW) {
                        Constant.REWARD_VIDEO_AD_COUNT = 0;
                        showAdDialog(type);
                    } else {
                        callVideoAdData(type);
                    }
                }
            } else {
                callVideoAdData(type);
            }
        } else {
            callVideoAdData(type);
        }

    }

    private void showAdDialog(String type) {
        Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view_ad);
        if (isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
        MaterialButton buttonYes = dialog.findViewById(R.id.button_yes_viewAd);
        MaterialButton buttonNo = dialog.findViewById(R.id.button_no_viewAd);
        MaterialTextView title = dialog.findViewById(R.id.tvTitle);
        MaterialTextView tvMessage = dialog.findViewById(R.id.tvMessage);

        Util util = new Util();
        buttonYes.setText(util.setText("yes", "YES"));
        buttonNo.setText(util.setText("no", "NO"));
        title.setText(util.setText("watch_ad_title", "Watch rewarded video"));
        tvMessage.setText(util.setText("watch_ad_msg", "Do you want to watch rewarded video ad?"));

        buttonYes.setOnClickListener(v -> {
            dialog.dismiss();
            showVideoAd(type);
        });

        buttonNo.setOnClickListener(v -> {
            dialog.dismiss();
            if (Constant.aboutUsList.isInterstitial_ad()) {
                skipVideoAd(type);
            } else {
                callVideoAdData(type);
            }
        });

        dialog.show();
    }

    private void showVideoAd(String type) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        AdRequest adRequest;
        Bundle extras = new Bundle();
        if (!personalization_ad) {
            extras.putString("npa", "1");
        }
        extras.putBoolean("_noRefresh", true);
        adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();

        if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB)) {
                 RewardedAd.load(mContext, WebAPI.ADMOB_REWARD_ID,
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        callVideoAdData(type);
                        progressDialog.dismiss();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;

                        callVideoAdData(type);
                        progressDialog.dismiss();

                        mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                                Log.d("TAG", "The user earned the reward.");
                            }
                        });

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {

                                Log.d(TAG, "Ad was shown.");
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Ad was dismissed.");
                                mRewardedAd = null;
                            }
                        });
                    }
                });
        } else if(WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)) {
                rewardedVideoAd = new RewardedVideoAd(mContext, WebAPI.ADMOB_REWARD_ID);
                RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
                    @Override
                    public void onError(Ad ad, AdError error) {

                        Log.e(TAG, "FB Rewarded video ad failed to load: " + error.getErrorMessage());
                        callVideoAdData(type);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {

                        Log.d(TAG, "FB Rewarded video ad is loaded and ready to be displayed!");
                        facebookAd = true;

                        rewardedVideoAd.show();
                    }

                    @Override
                    public void onAdClicked(Ad ad) {

                        Log.d(TAG, "FB Rewarded video ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                        Log.d(TAG, "FB Rewarded video ad impression logged!");
                    }

                    @Override
                    public void onRewardedVideoCompleted() {

                        Log.d(TAG, "FB Rewarded video completed!");
                        facebookAd = false;
                    }

                    @Override
                    public void onRewardedVideoClosed() {

                        callVideoAdData(type);
                        progressDialog.dismiss();
                        Log.d(TAG, "FB Rewarded video ad closed!");
                    }
                };
                rewardedVideoAd.loadAd(
                        rewardedVideoAd.buildLoadAdConfig()
                                .withAdListener(rewardedVideoAdListener)
                                .build());
        } else if(WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {
            startAppAd = new StartAppAd(mContext);

            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                @Override
                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {

                    startAppAd.showAd();

                    callVideoAdData(type);
                    progressDialog.dismiss();

                    startAppAd.setVideoListener(new VideoListener() {
                        @Override
                        public void onVideoCompleted() {
                            Log.d(TAG, "Rewarded video completed!");

                        }
                    });
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    //Toast.makeText(mContext, "Ad Not Available or Buy Subscription", Toast.LENGTH_SHORT).show();
                    Log.d("StartApp Failed", ad.getErrorMessage());

                    callVideoAdData(type);
                    progressDialog.dismiss();
                }
            });
        } else if(WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {


            UnityAds.initialize(mContext, WebAPI.ADMOB_ID, true, new IUnityAdsInitializationListener() {
                @Override
                public void onInitializationComplete() {
                    Log.v("CHECKUNITY", "Unity Ads initialization complete");
                    UnityAds.load(WebAPI.ADMOB_REWARD_ID, new IUnityAdsLoadListener() {
                        @Override
                        public void onUnityAdsAdLoaded(String placementId) {
                            UnityAds.show((Activity) mContext, WebAPI.ADMOB_REWARD_ID, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                                @Override
                                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                                    Log.e("CHECKUNITY", "failed: " + message);
                                }

                                @Override
                                public void onUnityAdsShowStart(String placementId) {

                                }

                                @Override
                                public void onUnityAdsShowClick(String placementId) {

                                }

                                @Override
                                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                                    callVideoAdData(type);
                                    progressDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                            Log.e("CHECKUNITY", "failed: " + message);
                        }
                    });
                }

                @Override
                public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                    Log.v("CHECKUNITY", "Unity Ads failed");
                }
            });

        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {

            MaxRewardedAd rewardedAd = MaxRewardedAd.getInstance(WebAPI.ADMOB_REWARD_ID, activity);
            rewardedAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onRewardedVideoStarted(MaxAd ad) {

                }

                @Override
                public void onRewardedVideoCompleted(MaxAd ad) {
                    callVideoAdData(type);
                    progressDialog.dismiss();
                }

                @Override
                public void onUserRewarded(MaxAd ad, MaxReward reward) {

                }

                @Override
                public void onAdLoaded(MaxAd ad) {
                    rewardedAd.showAd();
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                }

                @Override
                public void onAdHidden(MaxAd ad) {

                }

                @Override
                public void onAdClicked(MaxAd ad) {

                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    callVideoAdData(type);
                    progressDialog.dismiss();
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    Log.e("REWARDEDVIDEO", "Applovin reward ad error");
                }
            });

            rewardedAd.loadAd();
        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {

            if(!WebAPI.rewardedVideoLoaded) {
                Appodeal.initialize(activity, WebAPI.ADMOB_REWARD_ID, Appodeal.REWARDED_VIDEO);
                WebAPI.rewardedVideoLoaded = true;
                progressDialog.show();
            } else {
                Appodeal.show(activity, Appodeal.REWARDED_VIDEO);
                isRewardVideoLoaded = true;
            }

            Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
                @Override
                public void onRewardedVideoLoaded(boolean isPrecache) {
                    // Called when rewarded video is loaded
                    if(!isRewardVideoLoaded) {
                        Log.v("APPODEAL", "reward loaded");
                        Appodeal.show(activity, Appodeal.REWARDED_VIDEO);
                        isRewardVideoLoaded = true;
                    }

                    progressDialog.dismiss();
                }
                @Override
                public void onRewardedVideoFailedToLoad() {
                    // Called when rewarded video failed to load
                    progressDialog.dismiss();
                    callVideoAdData(type);
                    Log.v("APPODEAL", "reward not loaded");
                }
                @Override
                public void onRewardedVideoShown() {
                    // Called when rewarded video is shown
                    progressDialog.dismiss();
                    Log.v("APPODEAL", "reward shown");
                }
                @Override
                public void onRewardedVideoShowFailed() {
                    // Called when rewarded video show failed
                    progressDialog.dismiss();
                    Log.v("APPODEAL", "reward failed");
                }
                @Override
                public void onRewardedVideoClicked() {
                    // Called when rewarded video is clicked
                }
                @Override
                public void onRewardedVideoFinished(double amount, String name) {
                    // Called when rewarded video is viewed until the end
                    Log.v("APPODEAL", "reward finish");
                }
                @Override
                public void onRewardedVideoClosed(boolean finished) {
                    // Called when rewarded video is closed
                    callVideoAdData(type);

                    Log.v("APPODEAL", "reward closed");
                }
                @Override
                public void onRewardedVideoExpired() {
                    // Called when rewarded video is expired
                }
            });
        } else {
        	callVideoAdData(type);
            progressDialog.dismiss();
        }
    }

    private void skipVideoAd(String type) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB)) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(mContext, WebAPI.ADMOB_INTERSTITIAL, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAdMob = interstitialAd;
                            Log.i("INTERSTITIAL", "onAdLoaded");

                            progressDialog.dismiss();
                            callVideoAdData(type);

                            if (mInterstitialAdMob != null) {

                                mInterstitialAdMob.show(activity);

                                mInterstitialAdMob.setFullScreenContentCallback(new FullScreenContentCallback(){
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        Log.d("TAG", "The ad failed to show.");
                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        mInterstitialAdMob = null;
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });

                            } else {
                                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i("INTERSTITIAL", loadAdError.getMessage());
                            callVideoAdData(type);
                            progressDialog.dismiss();
                            mInterstitialAdMob = null;
                        }
                    });
        } else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)) {

            com.facebook.ads.InterstitialAd mInterstitialAd = new  com.facebook.ads.InterstitialAd(mContext, WebAPI.ADMOB_INTERSTITIAL);
            com.facebook.ads.InterstitialAdListener interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
                @Override
                public void onError(Ad ad, AdError adError)
                {
                    Log.d("TAG", "The interstitial wasn't loaded yet. " + adError.getErrorCode());
                    callVideoAdData(type);
                    progressDialog.dismiss();
                }

                @Override
                public void onAdLoaded(Ad ad)
                {

                    if (mInterstitialAd.isAdLoaded())
                    {
                        mInterstitialAd.show();
                        progressDialog.dismiss();
                    }
                    else
                    {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                        callVideoAdData(type);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onAdClicked(Ad ad)
                {

                }

                @Override
                public void onLoggingImpression(Ad ad)
                {

                }

                @Override
                public void onInterstitialDisplayed(Ad ad)
                {
                }

                @Override
                public void onInterstitialDismissed(Ad ad)
                {
                    callVideoAdData(type);
                    progressDialog.dismiss();
                }
            };

            mInterstitialAd.loadAd(
                    mInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());

        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {

            startAppAd = new StartAppAd(mContext);

            startAppAd.loadAd (StartAppAd.AdMode.OFFERWALL, new AdEventListener() {
                @Override
                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {

                    Log.e("STARTAPP", " interstitial ad received");

                    startAppAd.showAd(new AdDisplayListener() {
                                @Override
                                public void adHidden(com.startapp.sdk.adsbase.Ad ad) {

                                }

                                @Override
                                public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                                    Log.e("STARTAPP", " interstitial ad displayed.");

                                    startAppAd = null;
                                    callVideoAdData(type);
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void adClicked(com.startapp.sdk.adsbase.Ad ad) {

                                }

                                @Override
                                public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                                    Log.e("STARTAPP", " interstitial ad not displayed.");

                                    callVideoAdData(type);
                                    progressDialog.dismiss();
                                }
                            });
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    Log.e("STARTAPP", " failed to receive intertitial ad");

                    callVideoAdData(type);
                    progressDialog.dismiss();
                }
            });
        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {
            UnityAds.initialize(mContext, WebAPI.ADMOB_ID, true, new IUnityAdsInitializationListener() {
                @Override
                public void onInitializationComplete() {
                    Log.v("CHECKUNITY", "Unity Ads initialization complete");
                    UnityAds.load(WebAPI.ADMOB_INTERSTITIAL, new IUnityAdsLoadListener() {
                        @Override
                        public void onUnityAdsAdLoaded(String placementId) {
                            UnityAds.show((Activity) mContext, WebAPI.ADMOB_INTERSTITIAL, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                                @Override
                                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                                    Log.e("CHECKUNITY", "failed: " + message);
                                }

                                @Override
                                public void onUnityAdsShowStart(String placementId) {

                                }

                                @Override
                                public void onUnityAdsShowClick(String placementId) {

                                }

                                @Override
                                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                                    callVideoAdData(type);
                                    progressDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                            Log.e("CHECKUNITY", "failed: " + message);
                        }
                    });
                }

                @Override
                public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                    Log.v("CHECKUNITY", "Unity Ads failed");
                }
            });


        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {

            callVideoAdData(type);
            progressDialog.dismiss();
        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {
            callVideoAdData(type);
            progressDialog.dismiss();

        } else {
            callVideoAdData(type);
            progressDialog.dismiss();
        }
    }

    private void callVideoAdData(String video_ad_type) {
        videoAd.videoAdClick(video_ad_type);
    }


    public void onClickData(int position, String title, String type, String statusType, String id, String tag) {

        ProgressDialog progressDialog = new ProgressDialog(activity);

        progressDialog.show();
        progressDialog.setMessage(util.setText("loading", activity.getResources().getString(R.string.loading)));
        progressDialog.setCancelable(false);

        if (Constant.aboutUsList != null) {

            if (Constant.aboutUsList.isInterstitial_ad()) {

                Constant.AD_COUNT = Constant.AD_COUNT + 1;
                if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                    Constant.AD_COUNT = 0;

                    if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB)) {
                        AdRequest adRequest = new AdRequest.Builder().build();

                        InterstitialAd.load(mContext, WebAPI.ADMOB_INTERSTITIAL, adRequest,
                                new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                                        mInterstitialAdMob = interstitialAd;
                                        Log.i("INTERSTITIAL", "onAdLoaded");

                                        if (mInterstitialAdMob != null) {

                                            mInterstitialAdMob.show(activity);
                                            progressDialog.dismiss();

                                            mInterstitialAdMob.setFullScreenContentCallback(new FullScreenContentCallback(){
                                                @Override
                                                public void onAdDismissedFullScreenContent() {

                                                    Log.d("TAG", "The ad was dismissed.");

                                                    onClick.position(position, title, type, statusType, id, tag);
                                                }

                                                public void onAdFailedToShowFullScreenContent(AdError adError) {

                                                    Log.d("TAG", "The ad failed to show.");

                                                }

                                                @Override
                                                public void onAdShowedFullScreenContent() {

                                                    mInterstitialAdMob = null;
                                                    Log.d("TAG", "The ad was shown.");
                                                }
                                            });

                                        } else {
                                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                        Log.i("INTERSTITIAL", loadAdError.getMessage());
                                        onClick.position(position, title, type, statusType, id, tag);
                                        mInterstitialAdMob = null;
                                    }
                                });
                    } else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)) {

                        com.facebook.ads.InterstitialAd mInterstitialAd = new  com.facebook.ads.InterstitialAd(mContext, WebAPI.ADMOB_INTERSTITIAL);
                        com.facebook.ads.InterstitialAdListener interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
                            @Override
                            public void onError(Ad ad, AdError adError)
                            {
                                Log.d("TAG", "The interstitial wasn't loaded yet. " + adError.getErrorCode());
                                onClick.position(position, title, type, statusType, id, tag);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onAdLoaded(Ad ad)
                            {

                                if (mInterstitialAd.isAdLoaded())
                                {
                                    mInterstitialAd.show();
                                    progressDialog.dismiss();
                                }
                                else
                                {
                                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                                }
                            }

                            @Override
                            public void onAdClicked(Ad ad)
                            {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad)
                            {

                            }

                            @Override
                            public void onInterstitialDisplayed(Ad ad)
                            {
                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad)
                            {
                                progressDialog.dismiss();
                                onClick.position(position, title, type, statusType, id, tag);
                            }
                        };

                        mInterstitialAd.loadAd(
                                mInterstitialAd.buildLoadAdConfig()
                                        .withAdListener(interstitialAdListener)
                                        .build());

                    } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {
                        UnityAds.initialize(mContext, WebAPI.ADMOB_ID, true, new IUnityAdsInitializationListener() {
                            @Override
                            public void onInitializationComplete() {
                                Log.v("CHECKUNITY", "Unity Ads initialization complete");
                                UnityAds.load(WebAPI.ADMOB_INTERSTITIAL, new IUnityAdsLoadListener() {
                                    @Override
                                    public void onUnityAdsAdLoaded(String placementId) {
                                        UnityAds.show((Activity) mContext, WebAPI.ADMOB_INTERSTITIAL, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                                            @Override
                                            public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                                                Log.e("CHECKUNITY", "failed: " + message);
                                            }

                                            @Override
                                            public void onUnityAdsShowStart(String placementId) {

                                            }

                                            @Override
                                            public void onUnityAdsShowClick(String placementId) {

                                            }

                                            @Override
                                            public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                                                onClick.position(position, title, type, statusType, id, tag);
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                                        Log.e("CHECKUNITY", "failed: " + message);
                                    }
                                });
                            }

                            @Override
                            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                                Log.v("CHECKUNITY", "Unity Ads failed");
                            }
                        });
                        onClick.position(position, title, type, statusType, id, tag);
                        progressDialog.dismiss();
                    } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {

                        onClick.position(position, title, type, statusType, id, tag);
                        progressDialog.dismiss();
                    } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {

                        onClick.position(position, title, type, statusType, id, tag);
                        progressDialog.dismiss();
                    }

                } else {
                    progressDialog.dismiss();
                    onClick.position(position, title, type, statusType, id, tag);
                }
            } else {
                progressDialog.dismiss();
                onClick.position(position, title, type, statusType, id, tag);
            }

        } else {
            progressDialog.dismiss();
            onClick.position(position, title, type, statusType, id, tag);
        }

    }



    public void adView(LinearLayout linearLayout) {

        if (Constant.aboutUsList != null && !Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {
            if (Constant.aboutUsList.isBanner_ad()) {
                if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB)) {
                    if (personalization_ad) {
                        showPersonalizedAds(linearLayout);
                    } else {
                        showNonPersonalizedAds(linearLayout);
                    }
                } else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)){
                    FbBannerAd(linearLayout);
                } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {

                    Banner startAppBanner = new Banner(activity);
                    RelativeLayout.LayoutParams bannerParameters =
                            new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                    bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    linearLayout.addView(startAppBanner, bannerParameters);

                } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {

                    BannerView banner = new BannerView(activity, WebAPI.ADMOB_BANNER, new UnityBannerSize(320, 50));
                    banner.load();

                    RelativeLayout.LayoutParams bannerParameters =
                            new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                    bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    linearLayout.addView(banner, bannerParameters);

                } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {

                    MaxAdView adView = new MaxAdView(WebAPI.ADMOB_BANNER, activity);
                    final boolean isTablet = AppLovinSdkUtils.isTablet(activity);
                    final int heightPx = AppLovinSdkUtils.dpToPx(activity, isTablet ? 90 : 50);
                    adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx));
                    adView.setBackgroundColor(Color.TRANSPARENT);
                    adView.loadAd();
                    linearLayout.addView(adView);
                } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {
                    if (!WebAPI.bannerLoaded) {
                        Appodeal.initialize(activity, WebAPI.ADMOB_BANNER, Appodeal.BANNER_BOTTOM);
                        WebAPI.bannerLoaded = true;
                    }
                    Appodeal.show(activity, Appodeal.BANNER_BOTTOM);
                }
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }

    }

    public void FbBannerAd(LinearLayout linearLayout) {
        if (Constant.aboutUsList != null && !Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {
            if (Constant.aboutUsList.isBanner_ad()) {
                com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, WebAPI.ADMOB_BANNER, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                linearLayout.addView(adView);
                adView.loadAd();
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public void showPersonalizedAds(LinearLayout linearLayout) {
        if (Constant.aboutUsList != null && !Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {
            if (Constant.aboutUsList.isBanner_ad()) {
                AdView adView = new AdView(activity);
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                adView.setAdUnitId(WebAPI.ADMOB_BANNER);
                adView.setAdSize(AdSize.BANNER);
                linearLayout.addView(adView);
                adView.loadAd(adRequest);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public void showNonPersonalizedAds(LinearLayout linearLayout) {
        if (Constant.aboutUsList != null && !Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {
            if (Constant.aboutUsList.isBanner_ad()) {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                AdView adView = new AdView(activity);
                AdRequest adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
                adView.setAdUnitId(WebAPI.ADMOB_BANNER);
                adView.setAdSize(AdSize.BANNER);
                linearLayout.addView(adView);
                adView.loadAd(adRequest);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }



    public void ShowFullScreen(boolean isFullScreen) {
        fullScreen.fullscreen(isFullScreen);
    }



    public void download(String id, String status_name, String category, String
            status_image_s, String status_image_b,
                         String video_uri, String layout_type, String status_type, String
                                 watermark_image, String watermark_on_off) {

        String filePath = null;


        if (status_type.equals("image") || status_type.equals("gif")) {

            if (status_type.equals("image")) {
                filename = "filename-" + id + ".jpg";
            } else {
                filename = "filename-" + id + ".gif";
            }

            File root_file = new File(Constant.image_path);
            if (!root_file.exists()) {
                root_file.mkdirs();
            }

            File file = new File(Constant.image_path, filename);
            filePath = file.toString();

            Intent serviceIntent = new Intent(activity, DownloadIGService.class);
            serviceIntent.setAction(DownloadIGService.ACTION_START);
            serviceIntent.putExtra("id", id);
            serviceIntent.putExtra("downloadUrl", status_image_b);
            serviceIntent.putExtra("file_path", root_file.toString());
            serviceIntent.putExtra("file_name", filename);
            serviceIntent.putExtra("status_type", status_type);
            activity.startService(serviceIntent);

        }

        new DownloadImage().execute(status_image_b, id, status_name, category, layout_type, status_type, filePath);

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImage extends AsyncTask<String, String, String> {

        private String filePath = null;
        private String iconsStoragePath;
        private String id, status_name, category, layout_type, status_type, file_path;

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                id = params[1];
                status_name = params[2];
                category = params[3];
                layout_type = params[4];
                status_type = params[5];
                file_path = params[6];

                if (status_type.equals("video")) {

                    iconsStoragePath = Constant.video_path;

                    File sdIconStorageDir = new File(iconsStoragePath);

                    if (!sdIconStorageDir.exists()) {
                        sdIconStorageDir.mkdirs();
                    }

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmapDownload = BitmapFactory.decodeStream(input);

                    String fname = "Image-" + id;
                    filePath = iconsStoragePath + fname + ".jpg";

                    File file = new File(iconsStoragePath, filePath);
                    if (file.exists()) {
                        Log.d("file_exists", "file_exists");
                    } else {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

                            bitmapDownload.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                        } catch (IOException e) {
                            Log.w("TAG", "Error saving image file: " + e.getMessage());
                        }
                    }

                }

            } catch (IOException e) {
                Log.w("error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (status_type.equals("image")) {
                Toast.makeText(activity, activity.getResources().getString(R.string.download), Toast.LENGTH_SHORT).show();
            }


            super.onPostExecute(s);
        }

    }




    public void addToFav(String id, String userId, String statusType, FavouriteIF favouriteIF) {

        ProgressDialog progressDialog = new ProgressDialog(activity);

        progressDialog.show();
        progressDialog.setMessage(util.setText("loading", activity.getResources().getString(R.string.loading)));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("post_id", id);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("type", statusType);
        jsObj.addProperty("method_name", "status_favourite");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.has(Constant.STATUS)) {

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("-2")) {
                            suspend(message);
                        } else {
                            alertBox(message);
                        }

                        favouriteIF.isFavourite("", "");

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);

                        String success = object.getString("success");
                        String msg = object.getString("msg");
                        if (success.equals("1")) {
                            String is_favourite = object.getString("is_favourite");
                            favouriteIF.isFavourite(is_favourite, msg);
                        } else {
                            favouriteIF.isFavourite("", msg);
                        }

                        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    favouriteIF.isFavourite("", activity.getResources().getString(R.string.failed_try_again));
                    alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                favouriteIF.isFavourite("", activity.getResources().getString(R.string.failed_try_again));
                alertBox(activity.getResources().getString(R.string.failed_try_again));
            }
        });

    }


    public void alertBox(String message) {

        if (!activity.isFinishing()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
            builder.setMessage(Html.fromHtml(message));
            builder.setCancelable(false);
            builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                    (arg0, arg1) -> {
                        if (message.equals(
                                    util.setText("you_have_not_login", activity.getResources().getString(R.string.you_have_not_login))
                            )
                        ) {
                            activity.startActivity(new Intent(activity, Login.class));
                            activity.finishAffinity();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }


    public void suspend(String message) {

        if (isLogin()) {

            String type_login = pref.getString(loginType, "");
            if (type_login.equals("google")) {


                GoogleSignInClient mGoogleSignInClient;

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();


                mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
            } else if (type_login.equals("facebook")) {
                LoginManager.getInstance().logOut();
            }

            editor.putBoolean(pref_login, false);
            editor.commit();
            Events.Login loginNotify = new Events.Login("");
            GlobalBus.getBus().post(loginNotify);
        }

        if (!activity.isFinishing()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
            builder.setMessage(Html.fromHtml(message));
            builder.setCancelable(false);
            builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                    (arg0, arg1) -> {

                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }


    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }


    public boolean isDarkMode() {
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:

                return false;
            case Configuration.UI_MODE_NIGHT_YES:

                return true;
            default:
                return false;
        }
    }

    public String webViewText() {
        String color;
        if (isDarkMode()) {
            color = Constant.webTextDark;
        } else {
            color = Constant.webTextLight;
        }
        return color;
    }

    public String webViewLink() {
        String color;
        if (isDarkMode()) {
            color = Constant.webLinkDark;
        } else {
            color = Constant.webLinkLight;
        }
        return color;
    }

    public String imageGalleryToolBar() {
        String color;
        if (isDarkMode()) {
            color = Constant.darkGallery;
        } else {
            color = Constant.lightGallery;
        }
        return color;
    }

    public String imageGalleryProgressBar() {
        String color;
        if (isDarkMode()) {
            color = Constant.progressBarDarkGallery;
        } else {
            color = Constant.progressBarLightGallery;
        }
        return color;
    }

}

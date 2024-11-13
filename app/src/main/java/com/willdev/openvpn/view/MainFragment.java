package com.willdev.openvpn.view;

import com.appodeal.ads.nativead.NativeIconView;
import com.appodeal.ads.nativead.NativeMediaView;
import com.willdev.openvpn.databinding.FragmentMainBinding;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkUtils;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.willdev.openvpn.utils.Util;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.startapp.sdk.adsbase.adlisteners.VideoListener;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener;
//import com.mopub.common.MoPub;
//import com.mopub.mobileads.MoPubErrorCode;
//import com.mopub.mobileads.MoPubInterstitial;
//import com.mopub.mobileads.MoPubView;
//import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
//import com.mopub.nativeads.ViewBinder;
import com.willdev.openvpn.CheckInternetConnection;
import com.willdev.openvpn.R;
import com.willdev.openvpn.SharedPreference;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.interfaces.ChangeServer;
import com.willdev.openvpn.model.Server;
import com.willdev.openvpn.utils.Config;
import com.loopj.android.http.HttpGet;
import com.startapp.sdk.ads.banner.Mrec;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import ph.gemeaux.materialloadingindicator.MaterialCircularIndicator;
import top.oneconnectapi.app.OpenVpnApi;
import top.oneconnectapi.app.core.OpenVPNService;
import top.oneconnectapi.app.core.OpenVPNThread;
import top.oneconnectapi.app.core.VpnStatus;

import static android.app.Activity.RESULT_OK;

import static com.unity3d.services.core.misc.Utilities.runOnUiThread;

public class MainFragment extends Fragment implements View.OnClickListener, ChangeServer, IUnityAdsInitializationListener {
    private Server server = null;

    private boolean connectAfterStop = false;
    private CheckInternetConnection connection;
    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();
    boolean vpnStart = false;
    private SharedPreference preference;
    private FragmentMainBinding binding;
    private View mView;
    private static final int REQUEST_CODE = 101;
    private TextView ipConnection;

    Util util = new Util();

    NativeAdLayout nativeAdLayout;
    FrameLayout frameLayout;
    LinearLayout lytPremium;
    private InterstitialAd mInterstitialAdMob;
    private StartAppAd startAppAd;

    com.facebook.ads.InterstitialAd mInterstitialAd;
    com.facebook.ads.InterstitialAdListener interstitialAdListener;

    private String unityGameID = "1234567";
    private Boolean testMode = true;

    private boolean isLoaded = false;

    private CountDownTimer timer = null;
    private final Long twoHours = 7200000L;
    private Long timeLeft = 0L;
    private BillingClient billingClient;
    private MaterialCircularIndicator progressDialog;

    private String wait, time;

    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
            mView = binding.getRoot();
            nativeAdLayout = mView.findViewById(R.id.native_ad_container);
            frameLayout = binding.flAdplaceholder;
            ipConnection = mView.findViewById(R.id.tv_ip_address);
            progressDialog = new MaterialCircularIndicator(getContext());
            lytPremium = mView.findViewById(R.id.lytPremium);

            billingClient = BillingClient.newBuilder(getContext())
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases()
                    .build();

            billingSetup();

            /*
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (WebAPI.FREE_SERVERS != "") {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            },1000);

             */

        } else {
            if (mView.getParent() != null) {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
        }

        setTextLanguage();
        loadRewardedAds();
        initializeAll();
        showIP();

        binding.btnAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRewardedAdDialog(util.setText("watch_ad_msg", "Watch an ad to add more time to your connection or purchase VIP"));
            }
        });

        binding.btnPing.setOnClickListener(v -> {
            try {
                checkConnectionDialog();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        binding.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) ((MainActivity) getActivity()).openCloseDrawer();

            }
        });

        binding.purchaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PurchaseActivity.class);
                startActivity(intent);
            }
        });

        return mView;
    }

    private void billingSetup() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.v("CHECKBILLING", "ready");
                    checkIfSubscribed();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.v("CHECKBILLING", "disconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.

            }
        });
    }

    private void initializeAll() {

        startAppAd = new StartAppAd(getContext());
        showIP();
        binding.logTv.setText(util.setText("disconnected", "Disconnected"));
        connection = new CheckInternetConnection();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB)) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(getActivity(), WebAPI.ADMOB_INTERSTITIAL, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                            mInterstitialAdMob = interstitialAd;
                            Log.i("INTERSTITIAL", "onAdLoaded");

                            if (mInterstitialAdMob != null) {

                                mInterstitialAdMob.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        Log.d("TAG", "The ad was dismissed.");
                                        prepareVpn();
                                    }

                                    public void onAdFailedToShowFullScreenContent(AdError adError) {

                                        Log.d("TAG", "The ad failed to show.");
                                        prepareVpn();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {

                                        mInterstitialAdMob = null;
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });

                            } else {
                                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                prepareVpn();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            Log.i("INTERSTITIAL", loadAdError.getMessage());
                            mInterstitialAdMob = null;
                        }
                    });

            AdLoader adLoader = new AdLoader.Builder(getActivity(), WebAPI.ADMOB_NATIVE)
                    .forNativeAd(new OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                            frameLayout.setVisibility(View.VISIBLE);
                            Log.v("ADMOBNATIVE", "ad loaded");
                            NativeAdView adView = (NativeAdView) getLayoutInflater()
                                    .inflate(R.layout.ad_unifined, null);
                            if ((!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium)) {
                                populateUnifiedNativeAdView(nativeAd, adView);
                                frameLayout.removeAllViews();
                                frameLayout.addView(adView);
                                frameLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            Log.v("ADMOBNATIVE", "error: " + adError.toString());
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            .build())
                    .build();


            VideoOptions videoOptions = new VideoOptions.Builder()
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            adLoader.loadAd(new AdRequest.Builder()
                    .build());
        } else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)) {
            mInterstitialAd = new com.facebook.ads.InterstitialAd(getActivity(), WebAPI.ADMOB_INTERSTITIAL);
            interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.d("TAG", "The interstitial wasn't loaded yet. " + adError.getErrorCode());
                    prepareVpn();
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (mInterstitialAd.isAdLoaded()) {
                        if (!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium)
                            mInterstitialAd.show();
                    } else {
                        Log.d("FACEBOOKAD", "The interstitial wasn't loaded yet.");
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    prepareVpn();
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                }
            };

            NativeAd nativeAd = new NativeAd(getContext(), WebAPI.ADMOB_NATIVE);
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e("FACEBOOKAD", WebAPI.ADMOB_NATIVE);
                    Log.e("FACEBOOKAD", "onAdFailedToLoad" + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd == null || nativeAd != ad) {
                        return;
                    }
                    nativeAd.unregisterView();

                    if ((!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium)) {
                        nativeAdLayout.setVisibility(View.VISIBLE);
                    }
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_layout, nativeAdLayout, false);
                    nativeAdLayout.addView(adView);

                    LinearLayout adChoicesContainer = getActivity().findViewById(R.id.ad_choices_container);
                    AdOptionsView adOptionsView = new AdOptionsView(getContext(), nativeAd, nativeAdLayout);
                    adChoicesContainer.removeAllViews();
                    adChoicesContainer.addView(adOptionsView, 0);

                    com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                    TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                    com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                    TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                    TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                    TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                    Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                    nativeAdTitle.setText(nativeAd.getAdvertiserName());
                    nativeAdBody.setText(nativeAd.getAdBodyText());
                    nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                    nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                    nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                    sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(nativeAdTitle);
                    clickableViews.add(nativeAdCallToAction);

                    nativeAd.registerViewForInteraction(
                            adView, nativeAdMedia, nativeAdIcon, clickableViews);
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(
                    nativeAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());

        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {

            //START APP NATIVE
            Mrec startAppMrec = new Mrec(getContext());
            RelativeLayout.LayoutParams mrecParameters = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            mrecParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mrecParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            frameLayout.addView(startAppMrec, mrecParameters);
            if (!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {
                frameLayout.setVisibility(View.VISIBLE);
            } else {
                frameLayout.setVisibility(View.GONE);
            }


            //LOAD INTERSTITIAL
            startAppAd = new StartAppAd(getContext());

            startAppAd.loadAd(StartAppAd.AdMode.OFFERWALL, new AdEventListener() {
                @Override
                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    Log.e("STARTAPP", ": ad received");
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    Log.e("STARTAPP", ": failed to receive ad");
                }
            });
        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {

            UnityAds.initialize(getActivity(), WebAPI.ADMOB_ID, true, new IUnityAdsInitializationListener() {
                @Override
                public void onInitializationComplete() {
                    Log.v("CHECKUNITY", "Unity Ads initialization complete");
                    RelativeLayout.LayoutParams bannerParameters =
                            new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                    bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    BannerView bottomBanner = new BannerView((Activity) getContext(), WebAPI.ADMOB_BANNER, new UnityBannerSize(320, 50));
                    bottomBanner.setListener(new BannerView.IListener() {
                        @Override
                        public void onBannerLoaded(BannerView bannerView) {
                            Log.v("CHECKUNITY", " banner ad loaded");
                        }

                        @Override
                        public void onBannerShown(BannerView bannerAdView) {

                        }

                        @Override
                        public void onBannerClick(BannerView bannerView) {

                        }

                        @Override
                        public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                            Log.v("CHECKUNITY", "error " + bannerErrorInfo.errorMessage);
                        }

                        @Override
                        public void onBannerLeftApplication(BannerView bannerView) {

                        }
                    });
                    bottomBanner.load();

                    frameLayout.addView(bottomBanner, bannerParameters);

                    if ((!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium)) {
                        frameLayout.setVisibility(View.VISIBLE);
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                    Log.e("CHECKUNITY", "Unity Ads initialization failed: [" + error + "] " + message);
                }
            });

        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {

            AppLovinSdk.getInstance(getContext()).setMediationProvider("max");
            AppLovinSdk.initializeSdk(getContext(), new AppLovinSdk.SdkInitializationListener() {
                @Override
                public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                    Log.v("APPLOVIN", "true");
                    MaxAdView adView = new MaxAdView(WebAPI.ADMOB_NATIVE, MaxAdFormat.MREC, getActivity());
                    adView.setId(ViewCompat.generateViewId());
                    adView.setListener(new MaxAdViewAdListener() {
                        @Override
                        public void onAdExpanded(MaxAd ad) {

                        }

                        @Override
                        public void onAdCollapsed(MaxAd ad) {

                        }

                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            Log.v("APPLOVIN", "ad loaded");
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
                            Log.e("APPLOVIN", "error: " + error.toString());
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            Log.e("APPLOVIN", "error: " + error.toString());
                        }
                    });

                    final int widthPx = AppLovinSdkUtils.dpToPx(getContext(), 300);
                    final int heightPx = AppLovinSdkUtils.dpToPx(getContext(), 250);

                    adView.setLayoutParams(new ConstraintLayout.LayoutParams(widthPx, heightPx));


                    adView.setBackgroundColor(Color.BLACK);

                    RelativeLayout.LayoutParams bannerParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    bannerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bannerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    adView.loadAd();

                    frameLayout.addView(adView, bannerParams);
                    if ((!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium)) {
                        frameLayout.setVisibility(View.VISIBLE);
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }
                }
            });

        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {

            if (WebAPI.nativeAd == null) {

                Appodeal.initialize(getActivity(), WebAPI.ADMOB_NATIVE, Appodeal.NATIVE);

                Log.v("APPODEAL", "INITIALIZE");

                Appodeal.setNativeCallbacks(new NativeCallbacks() {
                    @Override
                    public void onNativeLoaded() {
                        if (!isLoaded) {
                            Log.v("APPODEAL", "LOADED");

                            List<com.appodeal.ads.NativeAd> loadedNativeAds = Appodeal.getNativeAds(1);
                            if (loadedNativeAds.isEmpty()) {
                                Log.v("APPODEAL", "EMPTY");
                            } else {
                                WebAPI.nativeAd = loadedNativeAds.get(0);
                                if (!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {
                                    showNativeAd();
                                }
                            }
                            isLoaded = true;
                        }

                    }

                    @Override
                    public void onNativeFailedToLoad() {
                        Log.v("APPODEAL", "FAILED");
                    }

                    @Override
                    public void onNativeShown(com.appodeal.ads.NativeAd nativeAd) {

                    }

                    @Override
                    public void onNativeShowFailed(com.appodeal.ads.NativeAd nativeAd) {
                        Log.v("APPODEAL", "NOT SHOWN");
                    }

                    @Override
                    public void onNativeClicked(com.appodeal.ads.NativeAd nativeAd) {

                    }

                    @Override
                    public void onNativeExpired() {
                        //Toast.makeText(getContext(), "onNativeExpired", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {
                    showNativeAd();
                }
            }
        }
    }

    private void onServerSelect(Server currentServer) {
        server = currentServer;
        binding.countryName.setText(server.getCountry());
        updateCurrentVipServerIcon(server.getFlagUrl());

        if (!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium && !WebAPI.ADS_TYPE.equals("")) {

            if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB)) {

                if (mInterstitialAdMob != null) {
                    mInterstitialAdMob.show(getActivity());
                } else {
                    prepareVpn();
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)) {

                mInterstitialAd.loadAd(
                        mInterstitialAd.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build());

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {
                prepareVpn();

                startAppAd.showAd(new AdDisplayListener() {
                    @Override
                    public void adHidden(com.startapp.sdk.adsbase.Ad ad) {

                    }

                    @Override
                    public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                        Log.e("STARTAPP", "interstitial ad displayed.");
                    }

                    @Override
                    public void adClicked(com.startapp.sdk.adsbase.Ad ad) {

                    }

                    @Override
                    public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                        Log.e("STARTAPP", "interstitial ad not displayed.");
                    }
                });

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {

                UnityAds.initialize(getActivity(), WebAPI.ADMOB_ID, true, new IUnityAdsInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        Log.v("CHECKUNITY", "Unity Ads initialization complete 2");
                        UnityAds.load(WebAPI.ADMOB_REWARD_ID, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                UnityAds.show(getActivity(), WebAPI.ADMOB_REWARD_ID, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
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

                prepareVpn();

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {

                prepareVpn();

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {

                prepareVpn();

            }
        } else {
            prepareVpn();
        }
    }

    public void showNativeAd() {

        CardView cardView = (CardView) getLayoutInflater()
                .inflate(R.layout.appodeal_ad_layout, null);

        com.appodeal.ads.nativead.NativeAdView nativeAdView = cardView.findViewById(R.id.native_item);

        TextView tvTitle = (TextView) nativeAdView.findViewById(R.id.tv_title);
        tvTitle.setText(WebAPI.nativeAd.getTitle());
        nativeAdView.setTitleView(tvTitle);

        TextView tvDescription = (TextView) nativeAdView.findViewById(R.id.tv_description);
        tvDescription.setText(WebAPI.nativeAd.getDescription());
        nativeAdView.setDescriptionView(tvDescription);

        RatingBar ratingBar = (RatingBar) nativeAdView.findViewById(R.id.rb_rating);
        if (WebAPI.nativeAd.getRating() == 0) {
            ratingBar.setVisibility(View.INVISIBLE);
        } else {
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(WebAPI.nativeAd.getRating());
            ratingBar.setStepSize(0.1f);
        }
        nativeAdView.setRatingView(ratingBar);

        Button ctaButton = (Button) nativeAdView.findViewById(R.id.b_cta);
        ctaButton.setText(WebAPI.nativeAd.getCallToAction());
        nativeAdView.setCallToActionView(ctaButton);

        TextView tvAgeRestrictions = (TextView) nativeAdView.findViewById(R.id.tv_age_restriction);
        tvAgeRestrictions.setVisibility(View.GONE);

        NativeIconView nativeIconView = nativeAdView.findViewById(R.id.icon);
        nativeAdView.setIconView(nativeIconView);

        NativeMediaView nativeMediaView = (NativeMediaView) nativeAdView.findViewById(R.id.appodeal_media_view_content);
        nativeAdView.setMediaView(nativeMediaView);

        nativeAdView.registerView(WebAPI.nativeAd);
        nativeAdView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams bannerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        bannerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bannerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        frameLayout.addView(cardView, bannerParams);
        frameLayout.setVisibility(View.VISIBLE);
    }

    private void populateUnifiedNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }


        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.vpnBtn.setOnClickListener(this);
        binding.currentConnectionLayout.setOnClickListener(this);
        isServiceRunning();
        VpnStatus.initLogCache(getActivity().getCacheDir());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vpnBtn: {

                if (vpnStart) {
                    confirmDisconnect();
                } else {
                    if (server == null) {
                        showToast(util.setText("select_server_first", getResources().getString(R.string.select_server_first)));
                    } else {
                        prepareVpn();
                    }
                }

                break;
            }

            case R.id.currentConnectionLayout: {

                if (!WebAPI.FREE_SERVERS.equals("")) {
                    Intent mIntent = new Intent(this.getActivity(), Servers.class);
                    try {
                        selectServer.launch(mIntent);
                    } catch (ActivityNotFoundException ex) {
                        Log.e("Error", ex.getLocalizedMessage());
                    }
                } else {
                    Toast.makeText(getContext(), util.setText("loading_servers", getResources().getString(R.string.loading_servers)), Toast.LENGTH_LONG).show();
                }

                break;

            }
        }
    }

    public void confirmDisconnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(util.setText("connection_close_confirm", getActivity().getString(R.string.connection_close_confirm)));

        builder.setPositiveButton(util.setText("yes", getActivity().getString(R.string.yes)), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopVpn();
            }
        });
        builder.setNegativeButton(util.setText("no", getActivity().getString(R.string.no)), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void checkConnectionDialog() throws IOException, InterruptedException {

        AlertDialog alertDialog;
        AlertDialog.Builder dialogBuilder;
        LayoutInflater inflater;
        View dialogView;

        dialogBuilder = new AlertDialog.Builder(getContext());
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.dialog_check_connection, null);
        dialogBuilder.setView(dialogView);

        FrameLayout lytLoading = dialogView.findViewById(R.id.lytLoading);
        LinearLayout lytResult = dialogView.findViewById(R.id.lytResult);
        ImageView ivStatusIcon = dialogView.findViewById(R.id.ivStatusIcon);
        TextView tvStatus = dialogView.findViewById(R.id.tvStatus);
        TextView tvStatusMsg = dialogView.findViewById(R.id.tvStatusMsg);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCancelable(true);

        btnCancel.setText(util.setText("close", getString(R.string.close)));

        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                String ping = ping();

                if (!ping.equals("0")) {
                    lytResult.setVisibility(View.VISIBLE);
                    lytLoading.setVisibility(View.GONE);
                    ivStatusIcon.setImageResource(R.drawable.ic_check_ping);
                    tvStatus.setText(util.setText("ping_success", getString(R.string.ping_success)));
                    tvStatusMsg.setText(util.setText("ping_1", getString(R.string.ping_1)) + ping + util.setText("ping_2", getString(R.string.ping_2)));
                } else {
                    lytResult.setVisibility(View.VISIBLE);
                    lytLoading.setVisibility(View.GONE);
                    ivStatusIcon.setImageResource(R.drawable.ic_failed_ping);
                    tvStatus.setText(util.setText("ping_failed", getString(R.string.ping_failed)));
                    tvStatusMsg.setText(util.setText("not_connected_to_internet", getString(R.string.not_connected_to_internet)));
                }
            }
        });

        btnCancel.setOnClickListener(view -> alertDialog.dismiss());

        alertDialog.show();
    }

    public String ping() {
        String str = "";
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();

            if (exitValue == 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        ipProcess.getInputStream()));
                int i;
                char[] buffer = new char[4096];
                StringBuilder output = new StringBuilder();
                String[] op;
                String[] delay;
                while ((i = reader.read(buffer)) > 0)
                    output.append(buffer, 0, i);
                reader.close();
                op = output.toString().split("\n");
                delay = op[1].split("time=");

                str = delay[1];

                return str;
            } else
                return "0";
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "0";
    }


    private void prepareVpn() {

        if (vpnStart) {
            connectAfterStop = true;
            stopVpn();
        } else {
            if (getInternetStatus()) {

                Intent intent = VpnService.prepare(getContext());

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else startVpn();//have already permission

                status("connecting");

            } else {

                showToast("you have no internet connection !!");
            }
        }
    }


    public boolean stopVpn() {
        try {
            vpnThread.stop();

            status("connect");
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            startVpn();
        } else {
            showToast(util.setText("permission_denied", getResources().getString(R.string.permission_denied)));
        }
    }


    public boolean getInternetStatus() {
        return connection.netCheck(getActivity());
    }


    public void isServiceRunning() {
        setStatus(vpnService.getStatus(), false);
    }


    private void startVpn() {
        try {
            OpenVpnApi.startVpn(getContext(), server.getOvpn(), server.getCountry(), server.getOvpnUserName(), server.getOvpnUserPassword());

            binding.logTv.setText(util.setText("connecting", "Connecting..."));
            vpnStart = true;

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void setStatus(String connectionState, boolean showDialog) {
        if (connectionState != null)
            switch (connectionState) {
                case "DISCONNECTED":
                    status("connect");
                    showIP();
                    vpnStart = false;
                    vpnService.setDefaultStatus();
                    binding.logTv.setText(util.setText("disconnected", "Disconnected"));
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconstart);
                    binding.lytAddTime.setVisibility(View.GONE);
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                        timeLeft = 0L;
                    }

                    // Connect after disconnect because connecting while already connected cause issues sometimes
                    if (connectAfterStop) {
                        connectAfterStop = false;
                        if (getInternetStatus()) {
                            prepareVpn();
                        } else {
                            showToast("you have no internet connection !!");
                        }
                    }

                    break;
                case "CONNECTED":
                    showIP();
                    vpnStart = true;
                    status("connected");
                    binding.logTv.setText(util.setText("connected", "Connected"));
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconstop);

                    if (showDialog) {
                        if (!Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium && !WebAPI.ADS_TYPE.equals("")) {
                            addTimer(twoHours);
                            binding.lytAddTime.setVisibility(View.VISIBLE);
                            showRewardedAdDialog(util.setText("vpn_disconnect_limit", getString(R.string.vpn_disconnect_limit)));
                        }
                    }

                    break;
                case "WAIT":
                    binding.logTv.setText(util.setText("waiting", "Waiting..."));
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconwait);
                    break;
                case "AUTH":
                    binding.logTv.setText(util.setText("please_wait", "Please wait..."));
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconwait);
                    break;
                case "RECONNECTING":
                    status("connecting");
                    binding.logTv.setText(util.setText("reconnecting", "Reconnecting..."));
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconwait);
                    break;
                case "NONETWORK":
                    binding.logTv.setText(util.setText("no_network", "No Network"));
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconstart);
                    break;
            }

    }

    private RewardedVideoAd rewardedVideoAd;
    private String TAG = "RewarderVideoAd";
    private RewardedAd mRewardedAd;
    MaxRewardedAd rewardedAd;
    private Boolean facebookAd = false, startAd = false;
    private boolean isRewardVideoLoaded = false;
    private AlertDialog alertDialog;

    public void showRewardedAdDialog(String msg) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_time, null);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvExtendTimeMsg = dialogView.findViewById(R.id.tvExtendTimeMsg);
        MaterialButton purchase = dialogView.findViewById(R.id.purchase);
        MaterialButton btnAddTime = dialogView.findViewById(R.id.btnAddTime);
        FrameLayout lytDialog = dialogView.findViewById(R.id.lytDialog);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);

        tvTitle.setText(util.setText("extend_connection_time", getString(R.string.extend_connection_time)));
        btnAddTime.setText(util.setText("add_time", getString(R.string.add_time)));
        purchase.setText(util.setText("upgrade", getString(R.string.upgrade)));

        tvExtendTimeMsg.setText(msg);

        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PurchaseActivity.class);
                startActivity(intent);
            }
        });

        lytDialog.setOnClickListener(v -> alertDialog.dismiss());

        btnAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB)) {
                    if (mRewardedAd != null) {
                        Activity activityContext = getActivity();
                        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                                Log.d("TAG", "The user earned the reward.");
                                addTimer(twoHours);
                                alertDialog.dismiss();
                                loadRewardedAds();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), util.setText("ads_not_available", "Ad not available, please consider Subscribing instead"), Toast.LENGTH_SHORT).show();
                    }
                } else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)) {
                    if (facebookAd)
                        rewardedVideoAd.show();
                    else {
                        Toast.makeText(getActivity(), util.setText("ads_not_available", "Ad not available, please consider Subscribing instead"), Toast.LENGTH_SHORT).show();
                    }

                } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {
                    if (startAd) {

                        startAd = false;
                        startAppAd.showAd();

                        addTimer(twoHours);
                        loadRewardedAds();

                        startAppAd.setVideoListener(new VideoListener() {
                            @Override
                            public void onVideoCompleted() {
                                Log.d("STARTAPP", ": Rewarded video completed!");

                                startAppAd = null;
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), util.setText("ads_not_available", "Ad not available, please consider Subscribing instead"), Toast.LENGTH_SHORT).show();
                    }
                } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {

                    UnityAds.initialize(getActivity(), WebAPI.ADMOB_ID, true, new IUnityAdsInitializationListener() {
                        @Override
                        public void onInitializationComplete() {
                            //btnAddTime
                            Log.v("CHECKUNITY", "Unity Ads initialization complete");
                            UnityAds.load(WebAPI.ADMOB_REWARD_ID, new IUnityAdsLoadListener() {
                                @Override
                                public void onUnityAdsAdLoaded(String placementId) {
                                    UnityAds.show(getActivity(), WebAPI.ADMOB_REWARD_ID, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
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
                                            addTimer(twoHours);
                                            loadRewardedAds();
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
                    alertDialog.show();
                    Log.v("APPLOVINADSTATUS", "LOADING APPLOVIN REWARDED");
                    rewardedAd = MaxRewardedAd.getInstance(WebAPI.ADMOB_REWARD_ID, getActivity());
                    rewardedAd.setListener(new MaxRewardedAdListener() {
                        @Override
                        public void onRewardedVideoStarted(MaxAd ad) {

                        }

                        @Override
                        public void onRewardedVideoCompleted(MaxAd ad) {
                            addTimer(twoHours);
                        }

                        @Override
                        public void onUserRewarded(MaxAd ad, MaxReward reward) {

                        }

                        @Override
                        public void onAdLoaded(MaxAd ad) {
                            Log.d("APPLOVINADSTATUS", " reward ad loaded");
                            alertDialog.dismiss();
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
                            Log.e("APPLOVINADSTATUS", " reward ad not loaded");
                            alertDialog.dismiss();
                        }

                        @Override
                        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            alertDialog.dismiss();
                        }
                    });

                    rewardedAd.loadAd();

                } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {
                    if (WebAPI.rewardedVideoLoaded) {
                        Appodeal.show(getActivity(), Appodeal.REWARDED_VIDEO);
                        addTimer(twoHours);
                        alertDialog.dismiss();
                        loadRewardedAds();
                    } else {
                        Toast.makeText(getActivity(), util.setText("ads_not_available", "Ad not available, please consider Subscribing instead"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), util.setText("ads_not_available", "Ad not available, please consider Subscribing instead"), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    private void loadRewardedAds() {
        //ADMOB
        if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB)) {
            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load(getActivity(), WebAPI.ADMOB_REWARD_ID,
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            mRewardedAd = null;
                            alertDialog.dismiss();
                            loadRewardedAds();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;

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
        }

        //FACEBOOK
        else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)) {
            rewardedVideoAd = new RewardedVideoAd(getActivity(), WebAPI.ADMOB_REWARD_ID);
            RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, AdError error) {
                    Log.e(TAG, "FB Rewarded video ad failed to load: " + error.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {

                    Log.d(TAG, "FB Rewarded video ad is loaded and ready to be displayed!");
                    facebookAd = true;
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
                    addTimer(twoHours);
                    alertDialog.dismiss();
                    loadRewardedAds();
                }

                @Override
                public void onRewardedVideoClosed() {

                    Log.d(TAG, "FB Rewarded video ad closed!");
                }
            };
            rewardedVideoAd.loadAd(
                    rewardedVideoAd.buildLoadAdConfig()
                            .withAdListener(rewardedVideoAdListener)
                            .build());

        }

        //START APP
        else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {
            startAppAd = new StartAppAd(getContext());


            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                @Override
                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {

                    startAd = true;
                }

                @Override
                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                    Log.d("StartApp Failed", ad.getErrorMessage());
                }
            });
        }

        //APPLOVIN
        else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {


        } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {
            Appodeal.initialize(getActivity(), WebAPI.ADMOB_REWARD_ID, Appodeal.REWARDED_VIDEO);

            Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
                @Override
                public void onRewardedVideoLoaded(boolean isPrecache) {
                    // Called when rewarded video is loaded
                    Log.v("APPODEAL", "reward loaded");
                    WebAPI.rewardedVideoLoaded = true;

                    progressDialog.dismiss();
                }

                @Override
                public void onRewardedVideoFailedToLoad() {
                    // Called when rewarded video failed to load
                    progressDialog.dismiss();
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
                    Log.v("APPODEAL", "reward closed");
                }

                @Override
                public void onRewardedVideoExpired() {
                    // Called when rewarded video is expired
                }
            });

        }
    }


    public void status(String status) {

        if (status.equals("connect")) {


        } else if (status.equals("connecting")) {

        } else if (status.equals("connected")) {


        } else if (status.equals("tryDifferentServer")) {


        } else if (status.equals("loading")) {
        } else if (status.equals("invalidDevice")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);
        } else if (status.equals("authenticationCheck")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting);
        }

    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.v("CHECKSTATE", intent.getStringExtra("state"));
                setStatus(intent.getStringExtra("state"), true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = wait;
                if (byteOut == null) byteOut = wait;
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    public void updateConnectionStatus(String duration, String lastPacketReceive, String byteIn, String byteOut) {
        binding.durationTv.setText(time + duration);
        String byteinKb = byteIn.split("-")[0];
        String byteoutKb = byteOut.split("-")[0];
        binding.byteInTv.setText(byteinKb);
        binding.byteOutTv.setText(byteoutKb);
    }


    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void updateCurrentVipServerIcon(String serverIcon) {
        if (!server.getCountry().equals("Select Country")) {
            Glide.with(getActivity())
                    .load(serverIcon)
                    .into(binding.selectedServerIcon);
        }
    }


    @Override
    public void newServer(Server server) {
        this.server = server;

        if (vpnStart) {
            stopVpn();
        }

        prepareVpn();
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((Config.vip_subscription && Config.all_subscription) || Config.no_ads || Config.is_premium) {
            lytPremium.setVisibility(View.GONE);
            nativeAdLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
        } else {
            lytPremium.setVisibility(View.VISIBLE);
            nativeAdLayout.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.VISIBLE);
        }

        if (billingClient.isReady())
            checkIfSubscribed();
        else
            billingSetup();
    }

    void showIP() {
        new RequestTask().execute("https://checkip.amazonaws.com/");
    }

    @Override
    public void onInitializationComplete() {
        Log.e("UnityAdsExample", "Initialized");
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.e("UnityAdsExample", "Unity Ads initialization failed with error: [" + error + "] " + message);
    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else {

                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ipConnection.setText(result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //MoPub.onPause(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        //MoPub.onStop(getActivity());
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    private void addTimer(Long time) {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new CountDownTimer(time + timeLeft, 1000) {

            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                binding.tvDuration.setText(formatMilliSecondsToTime(millisUntilFinished));
            }

            public void onFinish() {
                stopVpn();
            }

        }.start();
    }

    private String formatMilliSecondsToTime(long milliseconds) {

        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return "Time Left: " + twoDigitString(hours) + ":" + twoDigitString(minutes) + ":"
                + twoDigitString(seconds);
    }

    private String twoDigitString(long number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    private void checkIfSubscribed() {

        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                new PurchasesResponseListener() {
                    public void onQueryPurchasesResponse(BillingResult billingResult, List purchases) {
                        // check billingResult
                        // process returned purchase list, e.g. display the plans user owns

                        int isAcknowledged = 0;
                        Log.v("CHECKBILLING", "purchases: " + purchases.size());
                        if (purchases.size() > 0) {
                            for (int i = 0; i < purchases.size(); i++) {
                                Log.v("CHECKBILLING", "" + purchases.get(i).toString());
                                isAcknowledged++;
                            }
                        }

                        if (!Config.stripe_status.equals("active")) {
                            Config.vip_subscription = isAcknowledged > 0;
                            Config.all_subscription = isAcknowledged > 0;
                        }

                        if ((Config.vip_subscription && Config.all_subscription) || Config.no_ads || Config.is_premium) {
                            lytPremium.setVisibility(View.GONE);
                            nativeAdLayout.setVisibility(View.GONE);
                            frameLayout.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }

    private final ActivityResultLauncher<Intent> selectServer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                final int resultCode = result.getResultCode();
                final Intent data = result.getData();

                if (result.getResultCode() == Activity.RESULT_OK) {
                    ((MainActivity) getActivity()).setActivate(true);
                    Server server = data.getParcelableExtra("server");
                    onServerSelect(server);
                }
            });

    private void setTextLanguage() {

        wait = util.setText("wait", "Wait");
        time = util.setText("time", "Time: ");
        binding.tvDownload.setText(util.setText("download", "Download"));
        binding.tvUpload.setText(util.setText("upload", "Upload"));
        binding.tvStatus1.setText(util.setText("status", "Status"));
        binding.tvStatus2.setText(util.setText("status", "Status"));
        binding.logTv.setText(util.setText("start", "START"));
        binding.tvIpAddress.setText(util.setText("ip_address", "IP Address"));
        binding.tvUnlock.setText(util.setText("unlock_msg", "Unlock Premium Servers\nWith No Ads"));
        binding.countryName.setText(util.setText("select_country", "Select Country"));
        binding.tvCheckConnection.setText(util.setText("check_connection", getString(R.string.check_connection)));
        binding.btnPing.setText(util.setText("ping", "Ping"));
    }
}
package com.willdev.openvpn.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.willdev.openvpn.utils.Util;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAdsShowOptions;
import com.willdev.openvpn.R;
import com.willdev.openvpn.adapter.FreeServerAdapter;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.model.Server;
import com.willdev.openvpn.utils.Config;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.adlisteners.VideoListener;
import com.unity3d.ads.UnityAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ph.gemeaux.materialloadingindicator.MaterialCircularIndicator;

public class FreeServersFragmentAdMob extends Fragment implements FreeServerAdapter.OnSelectListener {

    RecyclerView rcvServers;
    LinearLayout btnRandom;

    private FreeServerAdapter serverAdapter;
    private ArrayList<Server> servers;
    private ArrayList<Server> randomServers;
    private int arrLength = 0;

    private boolean isBannerLoaded = false, isRewardVideoLoaded = false;
    private MaterialCircularIndicator progressDialog;

    Util util = new Util();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_free_server, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvServers = view.findViewById(R.id.rcv_servers);
        btnRandom = view.findViewById(R.id.btnRandom);

        TextView tvRand = view.findViewById(R.id.randomServer);
        tvRand.setText(util.setText("random_server", "Random Server"));

        progressDialog = new MaterialCircularIndicator(getContext());
        progressDialog.setCancelable(false);

        if(WebAPI.nativeAd == null) {
            Appodeal.initialize(getActivity(), WebAPI.ADMOB_NATIVE, Appodeal.NATIVE);

            if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB) && !Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {

                AdView adView = new AdView(getContext());
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(WebAPI.ADMOB_BANNER);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when the user is about to return
                        // to the app after tapping on an ad.
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Code to be executed when an ad request fails.
                        Log.v("ADMOBBANNER", adError.toString());
                    }

                    @Override
                    public void onAdImpression() {
                        // Code to be executed when an impression is recorded
                        // for an ad.
                    }

                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        Log.v("ADMOBBANNER", "banner loaded");
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }
                });

                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);

                RelativeLayout.LayoutParams bannerParameters =
                        new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                FrameLayout lytMain = view.findViewById(R.id.lytAd);
                lytMain.addView(adView, bannerParameters);
            }

            if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL) && !Config.vip_subscription && !Config.all_subscription && !Config.no_ads && !Config.is_premium) {

                Appodeal.setNativeCallbacks(new NativeCallbacks() {
                    @Override
                    public void onNativeLoaded() {

                        if(!isBannerLoaded) {
                            Log.v("APPODEAL", "list LOADED");
                            List<com.appodeal.ads.NativeAd> loadedNativeAds = Appodeal.getNativeAds(1);
                            if (loadedNativeAds.isEmpty()){
                                Log.v("APPODEAL", "EMPTY");
                            }

                            WebAPI.nativeAd = loadedNativeAds.get(0);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            serverAdapter = new FreeServerAdapter(getActivity(), WebAPI.nativeAd);
                            serverAdapter.setOnSelectListener(server -> selectServer(server));
                            rcvServers.setLayoutManager(layoutManager);
                            rcvServers.setAdapter(serverAdapter);
                            loadServers();
                            isBannerLoaded = true;
                        }
                    }

                    @Override
                    public void onNativeFailedToLoad() {
                        Log.v("APPODEAL", "FAILED");

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        serverAdapter = new FreeServerAdapter(getActivity(), null);
                        serverAdapter.setOnSelectListener(server -> selectServer(server));
                        rcvServers.setLayoutManager(layoutManager);
                        rcvServers.setAdapter(serverAdapter);
                        loadServers();
                        isBannerLoaded = true;
                    }

                    @Override
                    public void onNativeShown(com.appodeal.ads.NativeAd nativeAd) {
                        Log.v("APPODEAL", "shown");
                    }

                    @Override
                    public void onNativeShowFailed(com.appodeal.ads.NativeAd nativeAd) {
                        Log.v("APPODEAL", "NOT SHOWN");
                    }

                    @Override
                    public void onNativeClicked(com.appodeal.ads.NativeAd nativeAd) {
                        Log.v("APPODEAL", "clicked");
                    }

                    @Override
                    public void onNativeExpired() {
                        Log.v("APPODEAL", "expired");
                    }
                });
            } else {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                serverAdapter = new FreeServerAdapter(getActivity(), null);
                serverAdapter.setOnSelectListener(this);
                rcvServers.setLayoutManager(layoutManager);
                rcvServers.setAdapter(serverAdapter);
                loadServers();
            }
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            serverAdapter = new FreeServerAdapter(getActivity(), WebAPI.nativeAd);
            serverAdapter.setOnSelectListener(server -> selectServer(server));
            rcvServers.setLayoutManager(layoutManager);
            rcvServers.setAdapter(serverAdapter);
            loadServers();
            isBannerLoaded = true;
        }

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomServer();
            }
        });

    }

    private void loadServers() {
        servers = new ArrayList<>();
        randomServers = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(WebAPI.FREE_SERVERS);

            arrLength = jsonArray.length() - 1;

            for (int i=0; i < jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Server(object.getString("serverName"),
                        object.getString("flag_url"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));

                randomServers.add(new Server(object.getString("serverName"),
                        object.getString("flag_url"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));

                Log.v("Servers",object.getString("vpnUserName"));
                Log.v("Servers",object.getString("vpnPassword"));
                Log.v("Servers",object.getString("serverName"));
                Log.v("Servers",object.getString("flag_url"));
                Log.v("Servers",object.getString("ovpnConfiguration"));
                if((i % 2 == 0)&&(i > 0)){
                    if (!Config.vip_subscription && !Config.all_subscription && !WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB) && !Config.no_ads && !Config.is_premium && !WebAPI.ADS_TYPE.equals("")) {
                        servers.add(null);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        serverAdapter.setData(servers);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSelected(Server server) {
        selectServer(server);
    }

    public void randomServer(){

        int random = new Random().nextInt(arrLength);

        Intent mIntent = new Intent();
        mIntent.putExtra("server", randomServers.get(random));
        getActivity().setResult(getActivity().RESULT_OK, mIntent);
        getActivity().finish();
    }

    private void selectServer(Server server) {
        if (getActivity() != null)
        {
            if (Config.vip_subscription || Config.all_subscription || Config.no_ads || Config.is_premium) {
                Intent mIntent = new Intent();
                mIntent.putExtra("server", server);
                getActivity().setResult(getActivity().RESULT_OK, mIntent);
                getActivity().finish();

            } else if(WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {

                StartAppAd startAppAd = new StartAppAd(getContext());

                startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                    @Override
                    public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {

                        startAppAd.showAd();

                        Intent mIntent = new Intent();
                        mIntent.putExtra("server", server);
                        getActivity().setResult(getActivity().RESULT_OK, mIntent);
                        getActivity().finish();

                        startAppAd.setVideoListener(new VideoListener() {
                            @Override
                            public void onVideoCompleted() {
                                Log.d("STARTAPP", "Rewarded video completed!");

                            }
                        });
                    }

                    @Override
                    public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                        //Toast.makeText(mContext, "Ad Not Available or Buy Subscription", Toast.LENGTH_SHORT).show();
                        Log.d("STARTAPP", "StartApp Failed, " + ad.getErrorMessage());

                        Intent mIntent = new Intent();
                        mIntent.putExtra("server", server);
                        getActivity().setResult(getActivity().RESULT_OK, mIntent);
                        getActivity().finish();
                    }
                });
            } else if(WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {
                progressDialog.show();
                UnityAds.initialize(getActivity(), WebAPI.ADMOB_ID, true, new IUnityAdsInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        Log.v("CHECKUNITY", "Unity Ads initialization complete");
                        UnityAds.load(WebAPI.ADMOB_INTERSTITIAL, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                UnityAds.show(getActivity(), WebAPI.ADMOB_INTERSTITIAL, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                                    @Override
                                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                                        Log.e("CHECKUNITY", "failed: " + message);
                                        progressDialog.dismiss();
                                        Intent mIntent = new Intent();
                                        mIntent.putExtra("server", server);
                                        getActivity().setResult(getActivity().RESULT_OK, mIntent);
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onUnityAdsShowStart(String placementId) {

                                    }

                                    @Override
                                    public void onUnityAdsShowClick(String placementId) {

                                    }

                                    @Override
                                    public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
                                        progressDialog.dismiss();
                                        Intent mIntent = new Intent();
                                        mIntent.putExtra("server", server);
                                        getActivity().setResult(getActivity().RESULT_OK, mIntent);
                                        getActivity().finish();
                                    }
                                });
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                                Log.e("CHECKUNITY", "failed: " + message);

                                progressDialog.dismiss();
                                Intent mIntent = new Intent();
                                mIntent.putExtra("server", server);
                                getActivity().setResult(getActivity().RESULT_OK, mIntent);
                                getActivity().finish();
                            }
                        });
                    }

                    @Override
                    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                        Log.v("CHECKUNITY", "Unity Ads failed");
                        progressDialog.dismiss();
                        Intent mIntent = new Intent();
                        mIntent.putExtra("server", server);
                        getActivity().setResult(getActivity().RESULT_OK, mIntent);
                        getActivity().finish();
                    }
                });

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {

                MaxRewardedAd rewardedAd = MaxRewardedAd.getInstance(WebAPI.ADMOB_REWARD_ID, getActivity());
                rewardedAd.setListener(new MaxRewardedAdListener() {
                    @Override
                    public void onRewardedVideoStarted(MaxAd ad) {

                    }

                    @Override
                    public void onRewardedVideoCompleted(MaxAd ad) {
                        Intent mIntent = new Intent();
                        mIntent.putExtra("server", server);
                        getActivity().setResult(getActivity().RESULT_OK, mIntent);
                        getActivity().finish();
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
                        Intent mIntent = new Intent();
                        mIntent.putExtra("server", server);
                        getActivity().setResult(getActivity().RESULT_OK, mIntent);
                        getActivity().finish();
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                    }
                });

                rewardedAd.loadAd();

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {

                if(!WebAPI.rewardedVideoLoaded) {
                    Appodeal.initialize(getActivity(), WebAPI.ADMOB_REWARD_ID, Appodeal.REWARDED_VIDEO);
                    WebAPI.rewardedVideoLoaded = true;
                    progressDialog.show();
                } else {
                    Appodeal.show(getActivity(), Appodeal.REWARDED_VIDEO);
                    isRewardVideoLoaded = true;
                }

                Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
                    @Override
                    public void onRewardedVideoLoaded(boolean isPrecache) {

                        if(!isRewardVideoLoaded) {
                            Log.v("APPODEAL", "reward loaded");
                            Appodeal.show(getActivity(), Appodeal.REWARDED_VIDEO);
                            isRewardVideoLoaded = true;
                        }

                        progressDialog.dismiss();
                    }
                    @Override
                    public void onRewardedVideoFailedToLoad() {

                        progressDialog.dismiss();
                        Log.v("APPODEAL", "reward not loaded");
                    }
                    @Override
                    public void onRewardedVideoShown() {

                        progressDialog.dismiss();
                        Log.v("APPODEAL", "reward shown");
                    }
                    @Override
                    public void onRewardedVideoShowFailed() {

                        progressDialog.dismiss();
                        Log.v("APPODEAL", "reward failed");
                    }
                    @Override
                    public void onRewardedVideoClicked() {

                    }
                    @Override
                    public void onRewardedVideoFinished(double amount, String name) {

                        Log.v("APPODEAL", "reward finish");
                    }
                    @Override
                    public void onRewardedVideoClosed(boolean finished) {

                        Intent mIntent = new Intent();
                        mIntent.putExtra("server", server);
                        getActivity().setResult(getActivity().RESULT_OK, mIntent);
                        getActivity().finish();

                        Log.v("APPODEAL", "reward closed");
                    }
                    @Override
                    public void onRewardedVideoExpired() {

                    }
                });

            } else {

                Intent mIntent = new Intent();
                mIntent.putExtra("server", server);
                getActivity().setResult(getActivity().RESULT_OK, mIntent);
                getActivity().finish();
            }
        }
    }
}
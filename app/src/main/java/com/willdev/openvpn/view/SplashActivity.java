package com.willdev.openvpn.view;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.R;
import com.startapp.sdk.adsbase.StartAppAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.oneconnectapi.app.api.OneConnect;

public class SplashActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        StartAppAd.disableSplash();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OneConnect oneConnect = new OneConnect();
                                oneConnect.initialize(SplashActivity.this, "ONE_CONNECT_SDK");
                                OkHttpClient okHttpClient = new OkHttpClient();
                                Request request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?frs").build();
                                Response response = okHttpClient.newCall(request).execute();
                                WebAPI.FREE_SERVERS = response.body().string();

                                request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?prs").build();
                                response = okHttpClient.newCall(request).execute();
                                WebAPI.PREMIUM_SERVERS = response.body().string();

                                request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?ads").build();
                                response = okHttpClient.newCall(request).execute();
                                String body = response.body().string();
                                try {
                                    JSONArray jsonArray = new JSONArray(body);
                                    for (int i=0; i < jsonArray.length();i++){
                                        JSONObject object = (JSONObject) jsonArray.get(0);
                                        WebAPI.ADMOB_ID = object.getString("admobID");
                                        WebAPI.ADMOB_BANNER = object.getString("bannerID");
                                        WebAPI.ADMOB_INTERSTITIAL = object.getString("interstitialID");
                                        WebAPI.ADMOB_REWARD_ID = object.getString("rewardID");
                                        WebAPI.ADMOB_NATIVE = object.getString("nativeID");
                                        WebAPI.ADS_TYPE = object.getString("adType");
                                    }
                                    try {
                                        ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                        Bundle bundle = applicationInfo.metaData;
                                        applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", WebAPI.ADMOB_ID);
                                        String apiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                        Log.d("AppID","The saved id is "+ WebAPI.ADMOB_ID);
                                        Log.d("AppID","The saved id is "+apiKey);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } catch (IOException e) {
                                Log.v("Kabila",e.toString());
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            },1000);

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

        }.start();

    }
}

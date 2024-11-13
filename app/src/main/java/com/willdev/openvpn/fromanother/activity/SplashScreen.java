package com.willdev.openvpn.fromanother.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.willdev.openvpn.fromanother.util.util.PrefManager;
import com.willdev.openvpn.utils.Config;
import com.willdev.openvpn.utils.Util;
import com.willdev.openvpn.view.MainActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
//import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.startapp.sdk.adsbase.StartAppAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.oneconnectapi.app.api.OneConnect;

public class SplashScreen extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 800;
    private Boolean isCancelled = false;
    private Method method;
    private ProgressBar progressBar;
    private String id = "", type = "", status_type = "", title = "";

    private GoogleSignInClient mGoogleSignInClient;
    Util util = new Util();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppAd.disableSplash();

        method = new Method(SplashScreen.this);
        method.login();
        method.forceRTLIfSupported();

        String them = method.pref.getString(method.them_setting, "system");
        assert them != null;
        switch (them) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }


        setContentView(R.layout.activity_splace_screen);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        changeStatusBarColor();

        progressBar = findViewById(R.id.progressBar_splash_screen);
        progressBar.setVisibility(View.GONE);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fetchSettings();
        fetchServerData();
    }


    public void fetchServerData() {
        Handler mHandler = new Handler();;
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OneConnect oneConnect = new OneConnect();
                    //oneConnect.initialize(SplashScreen.this, "ONE_CONNECT_SDK");
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request;
                    Response response;

                    request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?oneConnect").build();
                    response = okHttpClient.newCall(request).execute();


                    String oneConnectData = response.peekBody(2048).string();

                    try {
                        JSONObject jsonObject = new JSONObject(oneConnectData);
                        String oneConnectEnabled = jsonObject.getString("one_connect");
                        String oneConnectKey = jsonObject.getString("one_connect_key");

                        if (oneConnectEnabled.equals("1")) {
                            try  {
                                oneConnect.initialize(SplashScreen.this, oneConnectKey);
                                try {
                                    WebAPI.FREE_SERVERS = oneConnect.fetch(true);
                                    WebAPI.PREMIUM_SERVERS = oneConnect.fetch(false);
                                    Log.d("SERVER","FREE -"+ WebAPI.FREE_SERVERS.toString());
                                    Log.d("SERVER","FREE -"+ WebAPI.PREMIUM_SERVERS.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?frs").build();
                            response = okHttpClient.newCall(request).execute();
                            WebAPI.FREE_SERVERS = response.body().string();

                            request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?prs").build();
                            response = okHttpClient.newCall(request).execute();
                            WebAPI.PREMIUM_SERVERS = response.body().string();
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

    public void fetchSettings() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(this));
        jsObj.addProperty("api_key", "BTG8B8758765bV4356XC6");
        jsObj.addProperty("user_id", (method.isLogin()) ? method.userId() : "");
        jsObj.addProperty("method_name", "ad_settings");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    JSONObject object = jsonObject.getJSONObject(Constant.tag);

                    String success = object.getString("success");
                    String json_ad = object.getString("json_ad");
                    String onesignal_app_id = object.getString("onesignal_app_id");

//                   // String stripeJson = object.getString("stripe");
//
//                    if //(!stripeJson.equals("")) {
//                       // JSONObject stripeObject = new JSONObject(stripeJson);
//                        //Log.v("CHECKSTRIPE", object.getString("stripe"));
//                       // Config.stripe_json = object.getString("stripe");
//
//                        //Note: App checks google play subscription in mainfragment
//                        if (stripeObject.getString("status").equals("active")) {
//                            Config.stripe_renew_date = stripeObject.getString("current_period_end");
//                            Config.vip_subscription = true;
//                            Config.all_subscription = true;
//                            Config.stripe_status = "active";
//                        }
//                    }

                    if (success.equals("1")) {
                        try {
                            JSONArray jsonArray = new JSONArray(json_ad);

                            JSONObject adObj = (JSONObject) jsonArray.get(0);
                            WebAPI.ADMOB_ID = adObj.getString("admobID");
                            WebAPI.ADMOB_BANNER = adObj.getString("bannerID");
                            WebAPI.ADMOB_INTERSTITIAL = adObj.getString("interstitialID");
                            WebAPI.ADMOB_NATIVE = adObj.getString("nativeID");
                            WebAPI.ADMOB_REWARD_ID = adObj.getString("rewardID");
                            WebAPI.ADS_TYPE = adObj.getString("adType");

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

                        Config.ONESIGNAL_APP_ID = onesignal_app_id;

                        Log.v("CHECKJSON", onesignal_app_id);
                        Log.v("CHECKJSON", WebAPI.ADMOB_NATIVE);
                        Log.v("CHECKJSON", "TEST");

                        runOnUiThread(() -> {
                            PrefManager prefManager = new PrefManager(SplashScreen.this);
                            if (prefManager.isFirstOpen()) {
                                prefManager.setFirstOpen(false);
                                startActivity(new Intent(SplashScreen.this, WelcomeActivity.class));
                                finish();
                            } else {
                                splashScreen();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JsonError", e.toString());
                    method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("JsonError", statusCode + "");
                method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
            }
        });

    }


    public void splashScreen() {

        if (method.isNetworkAvailable()) {

            if (!isCancelled) {
                switch (type) {
                    case "payment_withdraw":
                        call_Activity();
                        break;
                    case "account_verification":

                        break;
                    case "account_status":

                        finishAffinity();
                        break;
                    default:
                        if (method.isLogin()) {
                            login();
                        } else {
                            if (method.pref.getBoolean(method.is_verification, false)) {
                                startActivity(new Intent(SplashScreen.this, Verification.class));
                                finishAffinity();
                            } else {
                                if (method.pref.getBoolean(method.show_login, true)) {
                                    method.editor.putBoolean(method.show_login, false);
                                    method.editor.commit();
                                    Intent i = new Intent(SplashScreen.this, Login.class);
                                    startActivity(i);
                                    finishAffinity();
                                } else {
                                    call_Activity();
                                }
                            }
                        }
                        break;
                }
            }

        } else {
            call_Activity();
        }
    }


    public void login() {

        progressBar.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("method_name", "user_login");
        jsObj.addProperty("user_id", method.userId());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                String loginType = method.getLoginType();

                try {
                    JSONObject jsonObject = new JSONObject(res);

                    if (jsonObject.has(Constant.STATUS)) {

                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("-2")) {
                            method.suspend(util.setText(message, message));
                        } else {
                            method.alertBox(util.setText(message, message));
                        }

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);
                        String success = object.getString("success");
                        String msg = object.getString("msg");
//                        int no_ads = object.getInt("no_ads");
//                        int premium_servers = object.getInt("premium_servers");
//                        int is_premium = object.getInt("is_premium");
//                        String perks = object.getString("perks");
//                        String exp = object.getString("exp");

//                        Config.no_ads = no_ads == 1;
//                        Config.premium_servers_access = premium_servers == 1;
//                        Config.is_premium = is_premium == 1;
//                        Config.perks = perks;
//                        Config.expiration = exp;

                        // Log.e("Error", "JSON ERROR " + is_premium);

                        if (success.equals("1")) {

                            OneSignal.sendTag("user_id", method.userId());
                            //OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
                            //status.getPermissionStatus().getEnabled();
                            OneSignal.sendTag("player_id", OneSignal.getDeviceState().getUserId());

                            if (loginType.equals("google")) {
                                if (GoogleSignIn.getLastSignedInAccount(SplashScreen.this) != null) {
                                    call_Activity();
                                } else {
                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();
                                }
                            } else if (loginType.equals("facebook")) {

                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                                if (isLoggedIn) {
                                    call_Activity();
                                } else {

                                    LoginManager.getInstance().logOut();

                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();

                                }

                            } else {
                                call_Activity();
                            }
                        } else {
                            OneSignal.sendTag("user_id", method.userId());

                            if (loginType.equals("google")) {

                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(SplashScreen.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });


                            } else if (loginType.equals("facebook")) {
                                LoginManager.getInstance().logOut();
                            }

                            method.editor.putBoolean(method.pref_login, false);
                            method.editor.commit();
                            startActivity(new Intent(SplashScreen.this, Login.class));
                            finishAffinity();
                        }
                    }

                } catch (JSONException e) {
                    Log.e("Error", "JSON ERROR " + e);
                    e.printStackTrace();
                    method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
            }
        });
    }


    public void call_Activity() {
        startActivity(new Intent(SplashScreen.this, MainActivity.class)
                .putExtra("type", type)
                .putExtra("id", id)
                .putExtra("status_type", status_type)
                .putExtra("title", title));
        finishAffinity();
    }


    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}




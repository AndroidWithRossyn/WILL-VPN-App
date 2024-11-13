package com.willdev.openvpn.view;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.R;
import com.willdev.openvpn.adapter.TabAdapter;
import com.willdev.openvpn.utils.Util;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ph.gemeaux.materialloadingindicator.MaterialCircularIndicator;
import top.oneconnectapi.app.api.OneConnect;

public class Servers extends AppCompatActivity
{

    private MaterialCircularIndicator progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        Util util = new Util();
        progressDialog = new MaterialCircularIndicator(this);
        progressDialog.setCancelable(false);
        Toolbar toolbar = findViewById(R.id.toolbarold);
        toolbar.setTitle(util.setText("free_servers", getString(R.string.free_servers)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());

        adapter.addFragment(new FreeServersFragmentAdMob(), util.setText("free_servers", getString(R.string.free_servers)));

        adapter.addFragment(new VipServersFragment(), util.setText("premium_servers", getString(R.string.premium_servers)));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
                toolbar.setTitle(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Servers.super.onBackPressed();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.servers_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            WebAPI.FREE_SERVERS = "";
            fetchServerData();
            progressDialog.setLoadingMessage("Refreshing servers");
            progressDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void fetchServerData() {
        Handler mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request;
                    Response response;

                    request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API + "includes/api.php?oneConnect").build();
                    response = okHttpClient.newCall(request).execute();

                    String oneConnectData = response.peekBody(2048).string();

                    try {
                        JSONObject jsonObject = new JSONObject(oneConnectData);
                        String oneConnectEnabled = jsonObject.getString("one_connect");
                        String oneConnectKey = jsonObject.getString("one_connect_key");

                        if (oneConnectEnabled.equals("1")) {
                            try {
                                OneConnect oneConnect = new OneConnect();
                                oneConnect.initialize(Servers.this, oneConnectKey);
                                try {
                                    WebAPI.FREE_SERVERS = oneConnect.fetch(true);
                                    WebAPI.PREMIUM_SERVERS = oneConnect.fetch(false);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API + "includes/api.php?frs").build();
                            response = okHttpClient.newCall(request).execute();
                            WebAPI.FREE_SERVERS = response.body().string();

                            request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API + "includes/api.php?prs").build();
                            response = okHttpClient.newCall(request).execute();
                            WebAPI.PREMIUM_SERVERS = response.body().string();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    Log.v("Kabila", e.toString());
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!WebAPI.FREE_SERVERS.equals("")) {
                            finish();
                            startActivity(getIntent());
                        }
                    }
                });

            }
        }).start();
}}

package com.willdev.openvpn.fromanother.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.willdev.openvpn.utils.Config;
import com.willdev.openvpn.utils.Util;
import com.willdev.openvpn.view.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RedeemActivity extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private String user_id;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    private TextInputEditText editText;
    private MaterialButton button_continue, button_skip;
    Util util = new Util();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        method = new Method(RedeemActivity.this);
        method.forceRTLIfSupported();

        user_id = getIntent().getStringExtra("user_id");

        toolbar = findViewById(R.id.toolbar_erc);
        toolbar.setTitle(util.setText("redeem", getString(R.string.redeem)));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(RedeemActivity.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        editText = findViewById(R.id.etCode);
        button_continue = findViewById(R.id.button_continue_erc);
        button_skip = findViewById(R.id.button_skip_erc);

        MaterialTextView tvEnterRedemptionCode = findViewById(R.id.tvEnterRedemptionCode);
        MaterialTextView tvEnterRedemptionCodeMsg = findViewById(R.id.tvEnterRedemptionCodeMsg);
        TextInputLayout tilEnterCode = findViewById(R.id.tilEnterCode);
        MaterialButton btnRedeemStatus = findViewById(R.id.btnRedeemStatus);

        tvEnterRedemptionCode.setText(util.setText("enter_redemption_code", getString(R.string.enter_redemption_code)));
        tvEnterRedemptionCodeMsg.setText(util.setText("enter_redemption_code_msg", getString(R.string.enter_redemption_code_msg)));
        tilEnterCode.setHint(util.setText("enter_code", getString(R.string.enter_code)));

        button_continue.setText(util.setText("redeem", getString(R.string.redeem)));
        button_skip.setText(util.setText("back", getString(R.string.back)));
        btnRedeemStatus.setText(util.setText("redeem_status", getString(R.string.redeem_status)));

        button_continue.setOnClickListener(v -> {

            editText.setError(null);

            String code = editText.getText().toString();

            if (code.equals("") || code.isEmpty()) {
                editText.requestFocus();
                editText.setError(util.setText("please_enter_code", getString(R.string.please_enter_code)));
            } else {

                editText.clearFocus();
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                if (method.isNetworkAvailable()) {
                    redeem(code);
                } else {
                    method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
                }

            }

        });

        button_skip.setOnClickListener(v -> {
            startActivity(new Intent(RedeemActivity.this, MainActivity.class));
            finishAffinity();
        });


        btnRedeemStatus.setOnClickListener(v -> {
                if (Objects.equals(Config.perks, ""))
                    method.alertBox(util.setText("no_code_redeemed", getString(R.string.no_code_redeemed)));
                else
                    showRedeemDialog(util.setText("redeem_status", getString(R.string.redeem_status)), Config.perks, Config.expiration, util.setText("close", getString(R.string.close)), false);
            }
        );

    }

    private void showRedeemDialog(String title, String perks, String exp, String btn, boolean isFinish) {
        Dialog dialog = new Dialog(RedeemActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_redeem_status);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        MaterialTextView tvTitle = dialog.findViewById(R.id.tvTitle);
        MaterialTextView tvPerks = dialog.findViewById(R.id.tvPerks);
        MaterialTextView tvExpire = dialog.findViewById(R.id.tvExpire);
        MaterialButton btnClose = dialog.findViewById(R.id.btnClose);
        MaterialTextView tvPerksTitle = dialog.findViewById(R.id.tvPerksTitle);
        MaterialTextView tvExpireTitle = dialog.findViewById(R.id.tvExpireTitle);

        tvPerksTitle.setText(util.setText("perks", getString(R.string.perks)));
        tvExpireTitle.setText(util.setText("valid_until", getString(R.string.valid_until)));

        tvTitle.setText(title);
        tvPerks.setText(perks);
        tvExpire.setText(exp);
        btnClose.setText(btn);

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            if (isFinish) {
                finishAffinity();
                startActivity(new Intent(this, MainActivity.class));
            }
        });

        dialog.show();
    }


    public void redeem(String code) {

        progressDialog.show();
        progressDialog.setMessage(util.setText("loading", getResources().getString(R.string.loading)));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RedeemActivity.this));
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("code", code);
        jsObj.addProperty("method_name", "redeem_code");
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
                            method.suspend(util.setText(message, message));
                        } else {
                            method.alertBox(util.setText(message, message));
                        }

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);

                        String success = object.getString("success");
                        String msg = object.getString("msg");
                        String exp = object.getString("exp");
                        String perks = object.getString("perks");
                        int no_ads = object.getInt("no_ads");
                        int premium_servers = object.getInt("premium_servers");

                        Config.no_ads = no_ads == 1;
                        Config.premium_servers_access = premium_servers == 1;
                        Config.perks = perks;
                        Config.expiration = exp;

                        if (success.equals("1")) {
                            showRedeemDialog(util.setText("success", getString(R.string.success)), perks, exp, util.setText("back_to_home", getString(R.string.back_to_home)), true);
                        } else {
                            method.alertBox(util.setText(msg, msg));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

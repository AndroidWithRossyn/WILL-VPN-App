package com.willdev.openvpn.fromanother.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.willdev.openvpn.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ForgetPassword extends AppCompatActivity {

    private Method method;
    public MaterialToolbar toolbar;
    private TextInputEditText editText_fp;
    private MaterialButton button;
    private ProgressDialog progressDialog;
    private InputMethodManager imm;
    Util util = new Util();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        method = new Method(ForgetPassword.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(ForgetPassword.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        toolbar = findViewById(R.id.toolbar_fp);
        toolbar.setTitle(util.setText("forgot_password", getString(R.string.forgot_password)));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        MaterialTextView tvTitle = findViewById(R.id.tvTitle);
        MaterialTextView tvSubTitle = findViewById(R.id.tvSubTitle);
        editText_fp = findViewById(R.id.editText_fp);
        button = findViewById(R.id.button_fp);

        tvTitle.setText(util.setText("forgot_password", getString(R.string.forgot_password)));
        tvSubTitle.setText(util.setText("forget_password_text", getString(R.string.forget_password_text)));
        editText_fp.setHint(util.setText("enter_email", getString(R.string.enter_email)));
        button.setText(util.setText("submit", getString(R.string.submit)));

        button.setOnClickListener(v -> {

            String string_fp = editText_fp.getText().toString();
            editText_fp.setError(null);

            if (!isValidMail(string_fp) || string_fp.isEmpty()) {
                editText_fp.requestFocus();
                editText_fp.setError(getResources().getString(R.string.please_enter_email));
            } else {

                editText_fp.clearFocus();
                imm.hideSoftInputFromWindow(editText_fp.getWindowToken(), 0);

                if (method.isNetworkAvailable()) {
                    forgetPassword(string_fp);
                } else {
                    method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
                }
            }

        });

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void forgetPassword(String sendEmail_forget_password) {

        progressDialog.show();
        progressDialog.setMessage(util.setText("loading", getResources().getString(R.string.loading)));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ForgetPassword.this));
        jsObj.addProperty("method_name", "forgot_pass");
        jsObj.addProperty("email", sendEmail_forget_password);
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
                        method.alertBox(util.setText(message, message));

                    } else {

                        JSONObject object = jsonObject.getJSONObject(Constant.tag);

                        String msg = object.getString("msg");
                        String success = object.getString("success");

                        if (success.equals("1")) {
                            editText_fp.setText("");
                        }

                        method.alertBox(util.setText(msg, msg));

                    }

                } catch (JSONException e) {
                    method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                    e.printStackTrace();
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

    @Override
    public void onBackPressed() {
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.onBackPressed();
    }

}

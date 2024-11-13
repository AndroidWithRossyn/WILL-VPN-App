package com.willdev.openvpn.fromanother.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.willdev.openvpn.R;
import com.willdev.openvpn.SharedPreference;
import com.willdev.openvpn.fromanother.LanguageAdapter;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.willdev.openvpn.model.Language;
import com.willdev.openvpn.model.Words;
import com.willdev.openvpn.utils.Global;
import com.willdev.openvpn.utils.Util;
import com.willdev.openvpn.view.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class LanguageActivity  extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;
    Util util = new Util();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        method = new Method(LanguageActivity.this);
        method.forceRTLIfSupported();


        TextView tvTitle = findViewById(R.id.tvTitle);
        ImageView btnClose = findViewById(R.id.btnClose);

        tvTitle.setText(new Util().setText("select_language", getString(R.string.select_language)));
        btnClose.setOnClickListener(v -> {
            finish();
        });

        progressDialog = new ProgressDialog(LanguageActivity.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getLanguageList();
    }

    public void getLanguageList() {

        progressDialog.show();
        progressDialog.setMessage(util.setText("loading", getResources().getString(R.string.loading)));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(LanguageActivity.this));
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("method_name", "language");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONObject object = jsonObject.getJSONObject(Constant.tag);

                    Type listType = new TypeToken<List<Language>>() {}.getType();
                    List<Language> languageList = new Gson().fromJson(object.getString("list"), listType);

                    languageList.add(0, new Language(-1, "English", "", "", ""));

                    setRecyclerview(languageList);

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

    private void setRecyclerview(List<Language> list) {
        LanguageAdapter adapter = new LanguageAdapter(this, list, data -> {
            if (data.id != -1) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> map = gson.fromJson(data.wordJson, type);

                List<Words> wordList = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    Words obj = new Words(entry.getKey(), entry.getValue());
                    wordList.add(obj);
                }

                if (data.direction.equals("RTL")) {
                    getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }

                Global.WORD_LIST = wordList;

                Intent intent;
                if (new SharedPreference(this).getDirection().equals("") && method.pref.getBoolean(method.show_login, true)) {
                    method.editor.putBoolean(method.show_login, false);
                    method.editor.commit();
                    intent = new Intent(this, Login.class);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }

                new SharedPreference(this).saveWords(data.wordJson, data.direction);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            } else {

                Intent intent;
                if (new SharedPreference(this).getDirection().equals("") && method.pref.getBoolean(method.show_login, true)) {
                    method.editor.putBoolean(method.show_login, false);
                    method.editor.commit();
                    intent = new Intent(this, Login.class);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }

                new SharedPreference(this).saveWords("", "LTR");
                Global.WORD_LIST.clear();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        RecyclerView rvLanguage = findViewById(R.id.rvLanguage);
        rvLanguage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvLanguage.setAdapter(adapter);
    }
}

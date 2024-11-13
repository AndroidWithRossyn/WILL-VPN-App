package com.willdev.openvpn.fromanother.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.willdev.openvpn.utils.FilePath;
import com.willdev.openvpn.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AccountVerification extends AppCompatActivity {

    private Method method;
    public MaterialToolbar toolbar;
    private ProgressDialog progressDialog;
    private String name;
    private Uri document_image;
    private MaterialButton button;
    private ImageView imageView;
    private LinearLayout linearLayout_image;
    //private ArrayList<Image> galleryImages;
    private InputMethodManager imm;
    private int REQUEST_GALLERY_PICKER = 100;
    private MaterialTextView textView_noData;
    private MaterialTextView textView_title;
    private MaterialTextView textView_image;
    private TextInputEditText editText_userName;
    private TextInputEditText editText_full_name;
    private TextInputEditText editText_msg;

    private static final int PICK_IMAGE_REQUEST = 165;
    private static final int PERMISSION_REQUEST_STORAGE = 234;
    Util util = new Util();
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification);

        method = new Method(AccountVerification.this);
        method.forceRTLIfSupported();

        name = getIntent().getStringExtra("name");

        //galleryImages = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_av);
        toolbar.setTitle(getResources().getString(R.string.request_verification));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(AccountVerification.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        textView_noData = findViewById(R.id.textView_av);
        textView_title = findViewById(R.id.textView_title_av);
        textView_image = findViewById(R.id.textView_image_av);
        editText_userName = findViewById(R.id.editText_userName_av);
        editText_full_name = findViewById(R.id.editText_full_name_av);
        editText_msg = findViewById(R.id.editText_msg_av);
        imageView = findViewById(R.id.imageView_av);
        linearLayout_image = findViewById(R.id.linearLayout_image_av);
        button = findViewById(R.id.button_av);

        textView_noData.setVisibility(View.GONE);

        editText_userName.clearFocus();
        editText_userName.setCursorVisible(false);
        editText_userName.setFocusable(false);

        textView_title.setText(getResources().getString(R.string.apply_for)
                + " " + getResources().getString(R.string.app_name)
                + " " + getResources().getString(R.string.verification));

        LinearLayout linearLayout = findViewById(R.id.linearLayout_av);
        textView_noData.setText(util.setText("no_data_found", getResources().getString(R.string.no_data_found)));
        method.adView(linearLayout);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                getData();
            } else {
                method.alertBox(util.setText("you_have_not_login", getResources().getString(R.string.you_have_not_login)));
                textView_noData.setVisibility(View.VISIBLE);
                textView_noData.setText(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
            textView_noData.setVisibility(View.VISIBLE);
            textView_noData.setText(util.setText("no_data_found", getResources().getString(R.string.no_data_found)));
        }

        try {
            requestPermissionForReadExternalStorage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            document_image = data.getData();
            imageView.setImageURI(document_image);
        }
    }

    private void openGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);

        //Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void getData() {

        editText_userName.setText(name);

        button.setOnClickListener(view -> {

            String name = editText_userName.getText().toString();
            String full_name = editText_full_name.getText().toString();
            String msg = editText_msg.getText().toString();

            form(name, full_name, msg, document_image);

        });

        linearLayout_image.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launchPickerSingleMode();
            } else {
                openGallery();
            }

        });

    }

    private void form(String name, String full_name, String msg, Uri document) {

        editText_userName.setError(null);
        editText_full_name.setError(null);
        editText_msg.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editText_userName.requestFocus();
            editText_userName.setError(getResources().getString(R.string.please_enter_name));
        } else if (full_name.equals("") || full_name.isEmpty()) {
            editText_full_name.requestFocus();
            editText_full_name.setError(getResources().getString(R.string.please_enter_full_name));
        } else if (msg.equals("") || msg.isEmpty()) {
            editText_msg.requestFocus();
            editText_msg.setError(getResources().getString(R.string.please_enter_message));
        } else if (document == null || document.equals("")) {
            method.alertBox(getResources().getString(R.string.please_select_image));
        } else {

            editText_full_name.clearFocus();
            editText_msg.clearFocus();
            imm.hideSoftInputFromWindow(editText_full_name.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editText_msg.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                submit(method.userId(), full_name, msg, document);
            } else {
                method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void launchPickerSingleMode() {
        // Launches photo picker in single-select mode.
        // This means that the user can select one photo or video.
        final Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        try {
            startForSingleModeResult.launch(intent);
        } catch (ActivityNotFoundException ex) {
            Log.e("ImagePickerError", ex.getLocalizedMessage());
        }
    }


    private final ActivityResultLauncher<Intent> startForSingleModeResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                final int resultCode = result.getResultCode();
                final Intent data = result.getData();

                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (data == null) return;
                    if (data.getData() != null) {
                        document_image = data.getData();
                        imageView.setImageURI(document_image);
                    }
                }
            });
    public void submit(String user_id, String sendFullName, String sendMessage, Uri document) {

        progressDialog.show();
        progressDialog.setMessage(util.setText("loading", getResources().getString(R.string.loading)));
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AccountVerification.this));
        jsObj.addProperty("method_name", "profile_verify");
        jsObj.addProperty("user_id", user_id);
        jsObj.addProperty("full_name", sendFullName);
        jsObj.addProperty("message", sendMessage);
        params.put("data", API.toBase64(jsObj.toString()));
        try {
            String filePath = FilePath.getPath(this, document_image);
            params.put("document", new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
                        String msg = object.getString("msg");
                        String success = object.getString("success");

                        if (success.equals("1")) {

                            editText_full_name.setText("");
                            editText_msg.setText("");
                            textView_image.setText(getResources().getString(R.string.add_thumbnail_file));
                            Glide.with(AccountVerification.this)
                                    .load(R.drawable.logo)
                                    .placeholder(R.drawable.logo).into(imageView);

                            onBackPressed();
                        }

                        Toast.makeText(AccountVerification.this, msg, Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.onBackPressed();
    }

    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}

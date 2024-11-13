package com.willdev.openvpn.fromanother.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class EditProfileFragment extends Fragment {

    private Method method;
    private String profileId;
    private ProgressBar progressBar;
    private String imageProfile;

    private Uri imageUri2;
    private boolean isProfile;
    private MaterialButton saveButton;
    private InputMethodManager imm;
    private ImageView circleImageView;
    private int REQUEST_GALLERY_PICKER = 100;
    //private ArrayList<Image> galleryImages;
    private TextInputLayout textInputEmail, textInputPass, textInputConformPass;
    private TextInputEditText editTextName, editTextEmail, editTextPassword,
            editTextConfirmPassword, editTextPhoneNo, editTextInstagram, editTextYoutube;

    private static final int PICK_IMAGE_REQUEST = 165;
    private static final int PERMISSION_REQUEST_STORAGE = 234;
    View view2;

    Util util = new Util();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_fragment, container, false);
        view2 = view;

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ImageView goBack = view.findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        method = new Method(getActivity());
        //galleryImages = new ArrayList<>();

        assert getArguments() != null;
        String set_name = getArguments().getString("name");
        String set_email = getArguments().getString("email");
        String set_phone = getArguments().getString("phone");
        String instagram = getArguments().getString("instagram");
        String youtube = getArguments().getString("youtube");
        String user_image = getArguments().getString("user_image");
        profileId = getArguments().getString("profileId");

        saveButton = view.findViewById(R.id.saveButton);
        progressBar = view.findViewById(R.id.progressbar_editPro);
        circleImageView = view.findViewById(R.id.imageView_user_editPro);
        editTextName = view.findViewById(R.id.editText_name_editPro);
        editTextEmail = view.findViewById(R.id.editText_email_editPro);
        editTextPassword = view.findViewById(R.id.editText_password_editPro);
        editTextConfirmPassword = view.findViewById(R.id.editText_confirm_pass_editPro);
        editTextPhoneNo = view.findViewById(R.id.editText_phone_editPro);
        editTextInstagram = view.findViewById(R.id.editText_instagram_editPro);
        editTextYoutube = view.findViewById(R.id.editText_youtube_editPro);
        textInputEmail = view.findViewById(R.id.textInput_email_editPro);
        textInputPass = view.findViewById(R.id.textInput_password_editPro);
        textInputConformPass = view.findViewById(R.id.textInput_confirm_password_editPro);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextInputLayout tilName = view.findViewById(R.id.tilName);
        TextInputLayout tilPhone = view.findViewById(R.id.tilPhone);
        MaterialButton saveButton = view.findViewById(R.id.saveButton);

        tvTitle.setText(util.setText("edit_profile", "Edit Profile"));
        tilName.setHint(util.setText("enter_name", "Enter Name"));
        tilPhone.setHint(util.setText("enter_phone_number", "Enter Phone Number"));
        saveButton.setText(util.setText("save", "Save"));
        textInputEmail.setHint(util.setText("enter_email", "Enter Email"));
        textInputPass.setHint(util.setText("enter_password", "Enter Password"));
        textInputConformPass.setHint(util.setText("confirm_password", "Confirm Password"));

        if (method.getLoginType().equals("google") || method.getLoginType().equals("facebook")) {
            textInputPass.setVisibility(View.GONE);
            textInputConformPass.setVisibility(View.GONE);
            editTextName.setCursorVisible(false);
            editTextName.setFocusable(false);
        } else {
            textInputPass.setVisibility(View.VISIBLE);
            textInputConformPass.setVisibility(View.VISIBLE);
        }
        if (set_email.equals("")) {
            textInputEmail.setVisibility(View.GONE);
        } else {
            textInputEmail.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.GONE);

        editTextName.setText(set_name);
        editTextEmail.setText(set_email);
        editTextPhoneNo.setText(set_phone);
        editTextInstagram.setText(instagram);
        editTextYoutube.setText(youtube);

        assert user_image != null;
        if (!user_image.equals("")) {
            Glide.with(getActivity().getApplicationContext()).load(user_image).placeholder(R.drawable.user_profile).into(circleImageView);
        }

        imageProfile = user_image;

        circleImageView.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launchPickerSingleMode();
            } else {
                openGallery();
            }

        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirm_password = editTextConfirmPassword.getText().toString();
                String phoneNo = editTextPhoneNo.getText().toString();
                String instagram = editTextInstagram.getText().toString();
                String youtube = editTextYoutube.getText().toString();

                editTextName.clearFocus();
                editTextEmail.clearFocus();
                editTextPassword.clearFocus();
                editTextConfirmPassword.clearFocus();
                editTextPhoneNo.clearFocus();
                editTextInstagram.clearFocus();
                editTextYoutube.clearFocus();
                imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextConfirmPassword.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextInstagram.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextYoutube.getWindowToken(), 0);

                editTextName.setError(null);
                editTextEmail.setError(null);
                editTextPhoneNo.setError(null);

                if (name.equals("") || name.isEmpty()) {
                    editTextName.requestFocus();
                    editTextName.setError(getResources().getString(R.string.please_enter_name));
                } else if (method.getLoginType().equals("normal") && (!isValidMail(email) || email.isEmpty())) {
                    editTextEmail.requestFocus();
                    editTextEmail.setError(getResources().getString(R.string.please_enter_email));
                } else if (!password.equals(confirm_password)) {
                    method.alertBox(getResources().getString(R.string.pass_confPass_match));
                } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
                    editTextPhoneNo.requestFocus();
                    editTextPhoneNo.setError(getResources().getString(R.string.please_enter_phone));
                } else {
                    if (getActivity() != null) {
                        if (method.isNetworkAvailable()) {
                            profileUpdate(profileId, name, email, password, phoneNo, youtube, instagram, imageProfile);
                        } else {
                            method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
                        }
                    } else {
                        method.alertBox(getResources().getString(R.string.wrong));
                    }
                }
            }
        });

        try {
            requestPermissionForReadExternalStorage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        setHasOptionsMenu(true);
        return view;

    }

    private final ActivityResultLauncher<Intent> startForSingleModeResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                final int resultCode = result.getResultCode();
                final Intent data = result.getData();

                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (data == null) return;
                    Uri imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        isProfile = true;
                        circleImageView.setImageURI(imageUri);
                        imageUri2 = data.getData();

                        Uri selectedFileUri = data.getData();
                        String selectedFilePath = FilePath.getPath(getContext(), selectedFileUri);
                        Log.i("CHECKFILE", "Selected File Path:" + selectedFilePath);

                        //Log.v("CHECKURI", "CHECKURI " + data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

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

    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                isProfile = true;
                circleImageView.setImageURI(imageUri);
                imageUri2 = data.getData();

                Uri selectedFileUri = data.getData();
                String selectedFilePath = FilePath.getPath(getContext(), selectedFileUri);
                Log.i("CHECKFILE", "Selected File Path:" + selectedFilePath);

                //Log.v("CHECKURI", "CHECKURI " + data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save) {
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirm_password = editTextConfirmPassword.getText().toString();
            String phoneNo = editTextPhoneNo.getText().toString();
            String instagram = editTextInstagram.getText().toString();
            String youtube = editTextYoutube.getText().toString();

            editTextName.clearFocus();
            editTextEmail.clearFocus();
            editTextPassword.clearFocus();
            editTextConfirmPassword.clearFocus();
            editTextPhoneNo.clearFocus();
            editTextInstagram.clearFocus();
            editTextYoutube.clearFocus();
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextConfirmPassword.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextInstagram.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextYoutube.getWindowToken(), 0);

            editTextName.setError(null);
            editTextEmail.setError(null);
            editTextPhoneNo.setError(null);

            if (name.equals("") || name.isEmpty()) {
                editTextName.requestFocus();
                editTextName.setError(getResources().getString(R.string.please_enter_name));
            } else if (method.getLoginType().equals("normal") && (!isValidMail(email) || email.isEmpty())) {
                editTextEmail.requestFocus();
                editTextEmail.setError(getResources().getString(R.string.please_enter_email));
            } else if (!password.equals(confirm_password)) {
                method.alertBox(getResources().getString(R.string.pass_confPass_match));
            } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
                editTextPhoneNo.requestFocus();
                editTextPhoneNo.setError(getResources().getString(R.string.please_enter_phone));
            } else {
                if (getActivity() != null) {
                    if (method.isNetworkAvailable()) {
                        profileUpdate(profileId, name, email, password, phoneNo, youtube, instagram, imageProfile);
                    } else {
                        method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
                    }
                } else {
                    method.alertBox(getResources().getString(R.string.wrong));
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void profileUpdate(String id, String sendName, String sendEmail, String sendPassword,
                               String sendPhone, String user_youtube, String user_instagram, String profile_image) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_profile_update");
            jsObj.addProperty("user_id", id);
            jsObj.addProperty("name", sendName);
            jsObj.addProperty("email", sendEmail);
            jsObj.addProperty("password", sendPassword);
            jsObj.addProperty("phone", sendPhone);
            jsObj.addProperty("user_youtube", user_youtube);
            jsObj.addProperty("user_instagram", user_instagram);
            try {
                if (isProfile) {
                    String filePath = FilePath.getPath(getContext(), imageUri2);
                    File imageFile = new File(filePath);
                    Log.v("CHECKFILE", filePath);
                    params.put("user_image", imageFile);
                } else {
                    params.put("user_image", profile_image);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant.url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

                        String res = new String(responseBody);

                        try {
                            JSONObject jsonObject = new JSONObject(res);

                            if (jsonObject.has(Constant.STATUS)) {

                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                if (status.equals("-2")) {
                                    Log.v("CHECKFILE", "fail");
                                    method.suspend(util.setText(message, message));
                                } else {
                                    Log.v("CHECKFILE", "success");
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                    method.alertBox(util.setText(message, message));
                                }

                            } else {

                                JSONObject object = jsonObject.getJSONObject(Constant.tag);
                                String msg = object.getString("msg");
                                String success = object.getString("success");

                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                Log.v("CHECKFILE", "success 245");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.v("CHECKFILE", e.toString());
                            method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                    Log.v("CHECKFILE", statusCode + "");
                }
            });

        }

    }
}

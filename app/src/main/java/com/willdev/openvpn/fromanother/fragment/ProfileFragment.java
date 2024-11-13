package com.willdev.openvpn.fromanother.fragment;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.reflect.TypeToken;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.PurchaseHistoryAdapter;
import com.willdev.openvpn.fromanother.util.util.API;
import com.willdev.openvpn.fromanother.util.util.Constant;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.willdev.openvpn.model.PurchaseHistory;
import com.willdev.openvpn.utils.Config;
import com.willdev.openvpn.utils.Util;
import com.willdev.openvpn.view.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import ph.gemeaux.materialloadingindicator.MaterialCircularIndicator;

public class ProfileFragment extends Fragment {

    private Method method;
    private AppCompatImageButton btnEdit;
    private ImageView imageView_profile, imageView_loginType;
    private String name, email, phone, user_image;
    private MaterialTextView textViewUserName;
    TextView tvRenewDate, tvEmail, tvAccountType, tvNoData, tvPurchaseHistory, tvAccountTypeTitle, tvRenew;
    RecyclerView rvPurchaseHistory;

    CircularProgressIndicator pgPurchase;

    ConstraintLayout lytRenew;

    MaterialButton btnCancel;

    MaterialCircularIndicator progressDialog;
    Util util = new Util();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment, container, false);


        progressDialog = new MaterialCircularIndicator(getActivity());

        ImageView goBack = view.findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });

        assert getArguments() != null;

        method = new Method(getActivity());

        textViewUserName = view.findViewById(R.id.textView_name_pro);
        imageView_profile = view.findViewById(R.id.imageView_pro);
        imageView_loginType = view.findViewById(R.id.imageView_loginType_pro);
        tvRenewDate = view.findViewById(R.id.tvRenewDate);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAccountType = view.findViewById(R.id.tvAccountType);
        rvPurchaseHistory = view.findViewById(R.id.rvPurchaseHistory);
        lytRenew = view.findViewById(R.id.lytRenew);
        btnCancel = view.findViewById(R.id.btnCancel);
        tvNoData = view.findViewById(R.id.tvNoData);
        pgPurchase = view.findViewById(R.id.pgPurchase);
        tvPurchaseHistory = view.findViewById(R.id.tvPurchaseHistory);
        tvAccountTypeTitle = view.findViewById(R.id.tvAccountTypeTitle);
        tvRenew = view.findViewById(R.id.tvRenew);

        tvPurchaseHistory.setText(util.setText("purchase_history", getContext().getString(R.string.purchase_history)));
        tvAccountTypeTitle.setText(util.setText("account_type", getContext().getString(R.string.account_type)));
        tvRenew.setText(util.setText("renews_on", getContext().getString(R.string.renews_on)));
        btnCancel.setText(util.setText("cancel_subscription", getContext().getString(R.string.cancel_subscription)));
        tvNoData.setText(util.setText("no_purchase_history", getContext().getString(R.string.no_purchase_history)));

        btnEdit = view.findViewById(R.id.btnEdit);
        TextView tvTitle = view.findViewById(R.id.tvTitle);

        tvTitle.setText(util.setText("profile", "Profile"));

        imageView_loginType.setVisibility(View.GONE);

        textViewUserName.setText(method.getName());
        tvEmail.setText(method.getEmail());

        if (Config.vip_subscription && Config.all_subscription) {
            tvAccountType.setText(util.setText("premium", getContext().getString(R.string.premium)));
        } else {
            tvAccountType.setText(util.setText("free", getContext().getString(R.string.free)));
            lytRenew.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }

        btnCancel.setOnClickListener(v -> {

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle(util.setText("confirm_cancellation", getContext().getString(R.string.confirm_cancellation)));
            builder.setMessage(util.setText("confirm_cancellation_msg", getContext().getString(R.string.confirm_cancellation_msg)));
            builder.setPositiveButton(util.setText("yes", getContext().getString(R.string.yes)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelStripeSubscription();
                }
            });
            builder.setNegativeButton(util.setText("no", getContext().getString(R.string.no)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when Cancel is clicked
                }
            });
            builder.show();
        });

        //Google Play Billing doesn't apply to this feature
        if (!Config.stripe_status.equals("active")) {
            lytRenew.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }

        fetchPurchaseHistory();

        setRenewDate();

        if (!Config.stripe_renew_date.equals(""))
            lytRenew.setVisibility(View.VISIBLE);

        callData();

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.ic_searchView);
        if (method.isLogin()) {
            searchItem.setVisible(true);
        } else {
            searchItem.setVisible(false);
        }
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener((new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (method.isNetworkAvailable()) {
                    if (method.isLogin()) {
                        UserFollowFragment userFollowFragment = new UserFollowFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "search_user");
                        bundle.putString("user_id", method.userId());
                        bundle.putString("search", query);
                        userFollowFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, userFollowFragment, query).addToBackStack(query).commitAllowingStateLoss();
                    } else {
                        method.alertBox(util.setText("you_have_not_login", getResources().getString(R.string.you_have_not_login)));
                    }
                    return false;
                } else {
                    method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        }));

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void callData() {
        if (getActivity() != null) {
            if (method.isNetworkAvailable()) {
                if (method.isLogin()) {
                    profile();
                }
            } else {
                method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
            }
        }
    }

    public void profile() {

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "user_profile");
            jsObj.addProperty("user_id", method.userId());
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant.url, params, new AsyncHttpResponseHandler() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (getActivity() != null) {

                        String res = new String(responseBody);

                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            Log.v("CHECKRES", res);
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

                                if (success.equals("1")) {

                                    name = object.getString("name");
                                    email = object.getString("email");
                                    phone = object.getString("phone");
                                    user_image = object.getString("user_image");

                                    if (!user_image.equals("")) {
                                        Glide.with(getActivity().getApplicationContext()).load(user_image)
                                                .placeholder(R.drawable.user_profile).into(imageView_profile);
                                    }

                                    textViewUserName.setText(name);
                                    tvEmail.setText(email);

                                    String stripeJson = object.getString("stripe");

                                    if (!stripeJson.equals("")) {
                                        JSONObject stripeObject = new JSONObject(stripeJson);
                                        Config.stripe_json = object.getString("stripe");

                                        //Note: App checks google play subscription in mainfragment
                                        if (stripeObject.getString("status").equals("active")) {
                                            Config.stripe_renew_date = stripeObject.getString("current_period_end");
                                            Config.stripe_status = "active";
                                            tvAccountType.setText("Premium");
                                            setRenewDate();
                                            lytRenew.setVisibility(View.VISIBLE);
                                        } else {
                                            tvAccountType.setText("Free");
                                            lytRenew.setVisibility(View.GONE);
                                            btnCancel.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                btnEdit.setOnClickListener(v -> {
                                    if (method.isNetworkAvailable()) {
                                        if (method.isLogin()) {
                                            editProfile();
                                        } else {
                                            method.alertBox(util.setText("you_have_not_login", getResources().getString(R.string.you_have_not_login)));
                                        }
                                    } else {
                                        method.alertBox(new Util().setText("no_internet_connection", getString(R.string.no_internet_connection)));
                                    }
                                });
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                        }

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                }
            });

        }

    }

    private void editProfile() {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("email", email);
        args.putString("phone", phone);
        args.putString("user_image", user_image);
        args.putString("profileId", method.userId());
        editProfileFragment.setArguments(args);
        //getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, editProfileFragment, getResources().getString(R.string.profile)).addToBackStack(getResources().getString(R.string.profile)).commit();
        ((MainActivity)getActivity()).openScreen(editProfileFragment, true);
    }

    private void setRenewDate() {

        if (Config.stripe_renew_date.equals(""))
            return;

        long unixSeconds = Long.parseLong(Config.stripe_renew_date);
        Date date = new java.util.Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);

        tvRenewDate.setText(formattedDate);
    }

    private void fetchPurchaseHistory() {
        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "fetch_purchase_history");
            jsObj.addProperty("user_id", method.userId());
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant.url, params, new AsyncHttpResponseHandler() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String res = new String(responseBody);

                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        JSONObject object = jsonObject.getJSONObject(Constant.tag);

                        Type listType = new TypeToken<List<PurchaseHistory>>() {}.getType();
                        List<PurchaseHistory> phList = new Gson().fromJson(object.getString("list"), listType);

                        setRecyclerview(phList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                }
            });
        }
    }

    private void cancelStripeSubscription() {

        progressDialog.show();

        if (getActivity() != null) {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "cancel_stripe_subscription");
            jsObj.addProperty("user_id", method.userId());
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant.url, params, new AsyncHttpResponseHandler() {
                @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String res = new String(responseBody);

                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        JSONObject object = jsonObject.getJSONObject(Constant.tag);

                        Toast.makeText(getContext(), util.setText("unsubscribe_success", getContext().getString(R.string.unsubscribe_success)), Toast.LENGTH_SHORT).show();

                        Config.stripe_status = "";
                        Config.stripe_json = "";
                        Config.stripe_renew_date = "";
                        Config.vip_subscription = false;
                        Config.all_subscription = false;

                        tvAccountType.setText("Free");
                        lytRenew.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);

                        Log.v("CHECKSTRIPECANCEL", res);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    method.alertBox(util.setText("failed_try_again", getString(R.string.failed_try_again)));
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void setRecyclerview(List<PurchaseHistory> list) {

        pgPurchase.setVisibility(View.GONE);

        if (list.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
            return;
        }

        PurchaseHistoryAdapter adapter = new PurchaseHistoryAdapter(getContext(), list);
        rvPurchaseHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvPurchaseHistory.setAdapter(adapter);
    }
}

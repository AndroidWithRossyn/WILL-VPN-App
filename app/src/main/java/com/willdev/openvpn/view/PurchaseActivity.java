package com.willdev.openvpn.view;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.willdev.openvpn.R;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.model.PaymentMethod;
import com.willdev.openvpn.model.SubscriptionPlans;
import com.willdev.openvpn.utils.Config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.Pair;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ph.gemeaux.materialloadingindicator.MaterialCircularIndicator;

import com.google.common.collect.ImmutableList;
import com.willdev.openvpn.utils.Util;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseActivity extends AppCompatActivity {


    String vpn1 = Config.all_month_id;
    String vpn2 = Config.all_threemonths_id;
    String vpn3 = Config.all_sixmonths_id;
    String vpn4 = Config.all_yearly_id;

    String stripeId1 = Config.monthly_stripe_id;
    String stripeId2 = Config.months_3_stripe_id;
    String stripeId3 = Config.months_6_stripe_id;
    String stripeId4 = Config.yearly_stripe_id;

    String subscription = "";

    TextView btnStripe;
    TextView btnGooglePlay ;
    private MutableLiveData<Integer> all_check = new MutableLiveData<>();

    RadioButton oneMonth;
    RadioButton threeMonth;
    RadioButton sixMonth;
    RadioButton oneYear;

    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;

    private String paymentClientSecret;

    private static final String TAG = "CheckoutActivity";

    private int selectedSub = 0;
    private String stripeSubId = "";

    private CardView lytMonthly, lyt3Months, lyt6Months, lytYearly;

    com.willdev.openvpn.fromanother.util.util.Method method;

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (purchases != null) {

                    if (purchases.get(0) != null) {
                        Log.v("CHECKBILLING", purchases.get(0).toString());
                        handlePurchase(purchases.get(0).getPurchaseToken());
                    }
                } else {
                    Toast.makeText(PurchaseActivity.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private BillingClient billingClient;
    private List<SubscriptionPlans> subscriptionPlans;
    Util util = new Util();
    MaterialCircularIndicator progressDialog;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_all);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_payment_method);

        btnStripe = bottomSheetDialog.findViewById(R.id.btnStripe);
        btnGooglePlay = bottomSheetDialog.findViewById(R.id.btnGooglePlay);

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvDesc = findViewById(R.id.tvDesc);
        TextView button = findViewById(R.id.button);
        TextView tvFineText = findViewById(R.id.tvFineText);
        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);

        tvTitle.setText(util.setText("subscription", getString(R.string.subscription)));
        tvDesc.setText(util.setText("banner_description", getString(R.string.banner_description)));
        button.setText(util.setText("become_premium", getString(R.string.become_premium)));
        tvFineText.setText(util.setText("purchase_reminder", getString(R.string.purchase_reminder)));
        tv1.setText(util.setText("unlock_premium_servers", getString(R.string.unlock_premium_servers)));
        tv2.setText(util.setText("with_no_ads", getString(R.string.with_no_ads)));

        oneMonth = findViewById(R.id.one_month);
        threeMonth = findViewById(R.id.three_month);
        sixMonth = findViewById(R.id.six_month);
        oneYear = findViewById(R.id.one_year);

        lytMonthly = findViewById(R.id.lytMonthly);
        lyt3Months = findViewById(R.id.lyt3Months);
        lyt6Months = findViewById(R.id.lyt6Months);
        lytYearly = findViewById(R.id.lytYearly);

        progressDialog = new MaterialCircularIndicator(this);
        progressDialog.show();

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        method = new com.willdev.openvpn.fromanother.util.util.Method(PurchaseActivity.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request request = new Request.Builder().url(WebAPI.ADMIN_PANEL_API+"includes/api.php?get_subscription").build();
                            Response response = okHttpClient.newCall(request).execute();
                            subscription = response.body().string();

                            JSONObject jsonArray = new JSONObject(subscription);
                            JSONObject object = jsonArray.getJSONObject("set");
                            JSONObject subObject = object.getJSONObject("sub");
                            JSONObject paymentObject = object.getJSONObject("pm");

                            Type type = new TypeToken<Map<String, SubscriptionPlans>>(){}.getType();
                            Map<String, SubscriptionPlans> map = new Gson().fromJson(subObject.toString(), type);

                            subscriptionPlans = new ArrayList<>(map.values());

                            Type type2 = new TypeToken<Map<String, PaymentMethod>>(){}.getType();
                            Map<String, PaymentMethod> map2 = new Gson().fromJson(paymentObject.toString(), type2);

                            List<PaymentMethod> paymentMethodList = new ArrayList<>(map2.values());

                            boolean activePaymentMethod = false;

                            for (int i=0; i<paymentMethodList.size(); i++ ) {

                                if (paymentMethodList.get(i).status.equals("1"))
                                    activePaymentMethod = true;

                                if (paymentMethodList.get(i).name.equals("Stripe") && paymentMethodList.get(i).status.equals("1")) {
                                    btnStripe.setVisibility(View.VISIBLE);
                                }
                                else if (paymentMethodList.get(i).name.equals("Google Play") && paymentMethodList.get(i).status.equals("1"))
                                    btnGooglePlay.setVisibility(View.VISIBLE);
                            }

                            if (!activePaymentMethod) {
                                TextView tvNoData = bottomSheetDialog.findViewById(R.id.tvNoData);
                                tvNoData.setVisibility(View.VISIBLE);
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView tvMonthly = findViewById(R.id.tvMonthly);
                                    TextView tvMonthlyPrice = findViewById(R.id.tvMonthlyPrice);
                                    TextView tv3Months = findViewById(R.id.tv3Months);
                                    TextView tv3MonthsPrice = findViewById(R.id.tv3MonthsPrice);
                                    TextView tv6Months = findViewById(R.id.tv6Months);
                                    TextView tv6MonthsPrice = findViewById(R.id.tv6MonthsPrice);
                                    TextView tvYearly = findViewById(R.id.tvYearly);
                                    TextView tvYearlyPrice = findViewById(R.id.tvYearlyPrice);

                                    LinearLayout lytSubscriptions = findViewById(R.id.lytSubscriptions);

                                    tvMonthly.setText(util.setText(subscriptionPlans.get(0).getName(), subscriptionPlans.get(0).getName()));
                                    tvMonthlyPrice.setText(subscriptionPlans.get(0).getCurrency() + subscriptionPlans.get(0).getPrice());
                                    tv3Months.setText(util.setText(subscriptionPlans.get(1).getName(), subscriptionPlans.get(1).getName()));
                                    tv3MonthsPrice.setText(subscriptionPlans.get(1).getCurrency() + subscriptionPlans.get(1).getPrice());
                                    tv6Months.setText(util.setText(subscriptionPlans.get(2).getName(), subscriptionPlans.get(2).getName()));
                                    tv6MonthsPrice.setText(subscriptionPlans.get(2).getCurrency() + subscriptionPlans.get(2).getPrice());
                                    tvYearly.setText(util.setText(subscriptionPlans.get(3).getName(), subscriptionPlans.get(3).getName()));
                                    tvYearlyPrice.setText(subscriptionPlans.get(3).getCurrency() + subscriptionPlans.get(3).getPrice());

                                    lytSubscriptions.setVisibility(View.VISIBLE);

                                    vpn1 = subscriptionPlans.get(0).getProduct_id();
                                    vpn2 = subscriptionPlans.get(1).getProduct_id();
                                    vpn3 = subscriptionPlans.get(2).getProduct_id();
                                    vpn4 = subscriptionPlans.get(3).getProduct_id();

                                    stripeId1 = subscriptionPlans.get(0).getStripeProductId();
                                    stripeId2 = subscriptionPlans.get(1).getStripeProductId();
                                    stripeId3 = subscriptionPlans.get(2).getStripeProductId();
                                    stripeId4 = subscriptionPlans.get(3).getStripeProductId();

                                    lytMonthly.setVisibility((subscriptionPlans.get(0).getStatus().equals("1")) ? View.VISIBLE : View.GONE);
                                    lyt3Months.setVisibility((subscriptionPlans.get(1).getStatus().equals("1")) ? View.VISIBLE : View.GONE);
                                    lyt6Months.setVisibility((subscriptionPlans.get(2).getStatus().equals("1")) ? View.VISIBLE : View.GONE);
                                    lytYearly.setVisibility((subscriptionPlans.get(3).getStatus().equals("1")) ? View.VISIBLE : View.GONE);

                                    progressDialog.dismiss();
                                }
                            });
                        } catch (IOException e) {
                            Log.v("Kabila",e.toString());
                            e.printStackTrace();
                        } catch (JSONException e) {
                            Log.v("CHECKJSON", e.toString());
                            e.printStackTrace();
                        }
                    }
                }).start();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },1000);

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        all_check.setValue(0);
        all_check.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case 0:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 1:
                        oneMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 2:
                        threeMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 3:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        break;

                }
            }
        });

        oneMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedSub = 0;
                if(isChecked) all_check.postValue(0);
            }
        });
        threeMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedSub = 1;
                if(isChecked) all_check.postValue(1);
            }
        });
        sixMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedSub = 2;
                if(isChecked) all_check.postValue(2);
            }
        });
        oneYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedSub = 3;
                if(isChecked) all_check.postValue(3);
            }
        });

        billingSetup();

        CardView all_pur = findViewById(R.id.all_pur);

        all_pur.setOnClickListener(v -> {
            if(all_check.getValue() != null)
                showPaymentMethod();
        });

        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void billingSetup() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    Log.v("CHECKBILLING", "ready");
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.v("CHECKBILLING", "disconnected");
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                finish();
                Toast.makeText(PurchaseActivity.this, "Service temporarily unavailable. Please check your Google Play account or try again after some time.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void queryProduct(String productId) {
        Log.v("CHECKBILLING", "clicked");
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(productId)
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {

                        Log.v("CHECKBILLING", billingResult.toString());
                        Log.e("CHECKBILLING", productId + ": " + productDetailsList.toString());
                        if (productDetailsList.size() > 0) {

                            makePurchase(productDetailsList.get(0));

                        } else {
                            Log.e("CHECKBILLING", "onProductDetailsResponse: No products");

                            finish();
                            Toast.makeText(PurchaseActivity.this, "Sorry, this subscription is currently unavailable", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void makePurchase(ProductDetails productDetails) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                        ImmutableList.of(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(productDetails)
                                        .setOfferToken("")
                                        .build()
                        )
                )
                .build();

        Log.v("CHECKBILLING", "makePurchase");
        // Launch the billing flow
        BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void handlePurchase(String purchaseToken) {

        AcknowledgePurchaseParams acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchaseToken)
                        .build();

        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Log.v("CHECKBILLING", "acknowledged");
                        Config.vip_subscription = true;
                        Config.all_subscription = true;
                        savePayment("Google Play");
                    }
                };

                thread.start();
            }
        };

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
    }

    private void unlock_all(int i) {

        switch (i) {
            case 0:
                queryProduct(vpn1);
                break;

            case 1:
                queryProduct(vpn2);
                break;

            case 2:
                queryProduct(vpn3);
                break;

            case 3:
                queryProduct(vpn4);
                break;
        }

    }

    private void stripeSelectId(int i) {

        switch (i) {
            case 0:
                startStripePayment(stripeId1);
                break;

            case 1:
                startStripePayment(stripeId2);
                break;

            case 2:
                startStripePayment(stripeId3);
                break;

            case 3:
                startStripePayment(stripeId4);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        billingClient.endConnection();
    }

    private void showPaymentMethod() {

        TextView tvSelectPaymentMethod = bottomSheetDialog.findViewById(R.id.tvSelectPaymentMethod);

        tvSelectPaymentMethod.setText(new Util().setText("select_payment_method", getString(R.string.select_payment_method)));

        btnGooglePlay.setOnClickListener(v -> {
            unlock_all(all_check.getValue());
            bottomSheetDialog.dismiss();
        });

        btnStripe.setOnClickListener(v -> {
            if (!method.isLogin()) {
                method.alertBox(util.setText("you_have_not_login", getResources().getString(R.string.you_have_not_login)));
            } else {
                stripeSelectId(all_check.getValue());
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void startStripePayment(String id) {

        progressDialog.show();

        List<Pair<String, String>> params = new ArrayList<>();
        params.add(new Pair<>("name", method.getName()));
        params.add(new Pair<>("email", method.getEmail()));
        params.add(new Pair<>("product_id", id));
        params.add(new Pair<>("product_name", subscriptionPlans.get(selectedSub).getName()));

        Fuel.INSTANCE.post(WebAPI.ADMIN_PANEL_API + "includes/stripe_api.php", params).responseString(new com.github.kittinunf.fuel.core.Handler<String>() {
            @Override
            public void success(String s) {
                try {
                    final JSONObject result = new JSONObject(s);
                    Log.v("CHECKRES", s);
                    progressDialog.dismiss();

                    stripeSubId = result.getString("subscriptionId");
                    customerConfig = new PaymentSheet.CustomerConfiguration(
                            result.getString("customer"),
                            result.getString("ephemeralKey")
                    );
                    paymentIntentClientSecret = result.getString("paymentIntent");
                    PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));

                    presentPaymentSheet();
                } catch (JSONException e) { Log.v("CHECKRES", e.toString()); }
            }

            @Override
            public void failure(@NonNull FuelError fuelError) {
                progressDialog.dismiss();
                Log.v("CHECKRES", fuelError.toString());
            }
        });
    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d(TAG, "Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e(TAG, "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            Log.d(TAG, "Completed");
            Config.vip_subscription = true;
            Config.all_subscription = true;
            Config.stripe_status = "active";
            savePayment("Stripe");
        }

        //Log.d(TAG, paymentSheetResult.toString());

    }

    private void presentPaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Example, Inc.")
                .customer(customerConfig)
                // Set `allowsDelayedPaymentMethods` to true if your business handles payment methods
                // delayed notification payment methods like US bank accounts.
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );
    }

    private void savePayment(String type) {

        progressDialog.show();

        List<Pair<String, String>> params = new ArrayList<>();
        params.add(new Pair<>("payment_method", "Stripe"));
        params.add(new Pair<>("product_id", subscriptionPlans.get(selectedSub).getProduct_id()));
        params.add(new Pair<>("user_id", method.userId()));
        params.add(new Pair<>("method_name", "savePayment"));

        if (type.equals("Stripe"))
            params.add(new Pair<>("stripe_subscription_id", stripeSubId));

        Fuel.INSTANCE.get(WebAPI.ADMIN_PANEL_API + "includes/api.php", params).responseString(new com.github.kittinunf.fuel.core.Handler<String>() {
            @Override
            public void success(String s) {
                Log.v("CHECKRES", s);
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void failure(@NonNull FuelError fuelError) {
                progressDialog.dismiss();
                finish();
                Log.v("CHECKRES", fuelError.toString());
            }
        });
    }
}

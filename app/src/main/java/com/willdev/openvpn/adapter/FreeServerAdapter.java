package com.willdev.openvpn.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.appodeal.ads.nativead.NativeAdView;
import com.appodeal.ads.nativead.NativeIconView;
import com.appodeal.ads.nativead.NativeMediaView;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.willdev.openvpn.R;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.model.Server;
import com.startapp.sdk.ads.banner.Banner;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.util.ArrayList;
import java.util.List;

public class FreeServerAdapter extends RecyclerView.Adapter<FreeServerAdapter.MyViewHolder> {

    private ArrayList<Server> serverLists;
    private Context mContext;
    private OnSelectListener selectListener;
    private int AD_TYPE = 0;
    private int CONTENT_TYPE = 1;
    private com.appodeal.ads.NativeAd nativeAd;
    private com.facebook.ads.AdView facebookAdview;

    public FreeServerAdapter(Context context, com.appodeal.ads.NativeAd nativeAd) {
        this.mContext = context;
        serverLists = new ArrayList<>();
        this.nativeAd = nativeAd;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdView adview;
        RelativeLayout mainLayout;

        if (viewType == AD_TYPE) {

            if(false) {
                Log.v("ADMOBAD", "true");
                adview = new AdView(mContext);
                adview.setAdSize(AdSize.BANNER);
                adview.setAdUnitId(WebAPI.ADMOB_BANNER);
                adview.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);

                        Log.e("ADMOBAD", "error: " + loadAdError);
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.v("ADMOBAD", "ad loaded");
                    }
                });
                float density = mContext.getResources().getDisplayMetrics().density;
                int height = Math.round(AdSize.BANNER.getHeight() * density);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
                adview.setLayoutParams(params);
                AdRequest request = new AdRequest.Builder().build();
                adview.loadAd(request);
                return new MyViewHolder(adview);

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS)) {
                Log.v("FACEBOOKAD", "true");
                mainLayout = new RelativeLayout(mContext);
                AdSettings.addTestDevice("06518f22-9f3f-40f4-97bc-f91ae11087e2");
                AudienceNetworkAds.initialize(mContext);
                facebookAdview = new com.facebook.ads.AdView(mContext, WebAPI.ADMOB_BANNER, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                mainLayout.addView(facebookAdview);

                com.facebook.ads.AdListener adListener = new
                        com.facebook.ads.AdListener() {
                            public void onError(Ad ad, AdError adError) {
                                Log.d("FACEBOOKAD", adError.getErrorMessage());
                            }

                            public void onAdLoaded(Ad ad) {

                            }

                            public void onAdClicked(Ad ad) {

                            }

                            public void onLoggingImpression(Ad ad) {

                            }
                        };

                facebookAdview.loadAd(facebookAdview.buildLoadAdConfig().withAdListener(adListener).build());
                return new MyViewHolder(mainLayout);

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_STARTAPP)) {

                mainLayout = new RelativeLayout(mContext);
                Banner startAppBanner = new Banner(mContext);
                RelativeLayout.LayoutParams bannerParameters =
                        new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mainLayout.addView(startAppBanner, bannerParameters);
                return new MyViewHolder(mainLayout);

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_UNITY)) {

                mainLayout = new RelativeLayout(mContext);

                RelativeLayout.LayoutParams bannerParameters =
                        new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                BannerView bottomBanner = new BannerView((Activity) mContext, WebAPI.ADMOB_BANNER, new UnityBannerSize(320, 50));
                bottomBanner.setListener(new BannerView.IListener() {
                    @Override
                    public void onBannerLoaded(BannerView bannerView) {
                        Log.v("CHECKUNITY", " banner ad loaded");
                    }

                    @Override
                    public void onBannerShown(BannerView bannerAdView) {

                    }

                    @Override
                    public void onBannerClick(BannerView bannerView) {

                    }

                    @Override
                    public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                        Log.v("CHECKUNITY", "error " + bannerErrorInfo.errorMessage);
                    }

                    @Override
                    public void onBannerLeftApplication(BannerView bannerView) {

                    }
                });
                bottomBanner.load();
                mainLayout.addView(bottomBanner, bannerParameters);


                return new MyViewHolder(mainLayout);

            } else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APV)) {

                mainLayout = new RelativeLayout(mContext);

                MaxAdView adView = new MaxAdView(WebAPI.ADMOB_BANNER, (Activity) mContext);

                adView.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(MaxAd ad) {

                    }

                    @Override
                    public void onAdCollapsed(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        Log.v("APPLOVINADSTATUS", " banner ad loaded");
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {

                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {

                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        Log.v("APPLOVINADSTATUS", " banner ad not loaded");
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                        Log.v("APPLOVINADSTATUS", " banner ad not displayed");
                    }
                });
                //adView.setRevenueListener( this );

                // Set the height of the banner ad based on the device type.
                final boolean isTablet = AppLovinSdkUtils.isTablet(mContext);
                final int heightPx = AppLovinSdkUtils.dpToPx(mContext, isTablet ? 90 : 50);
                adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightPx));

                // Need to set the background or background color for banners to be fully functional.
                adView.setBackgroundColor(Color.TRANSPARENT);

                // Load the first ad.
                adView.loadAd();

                mainLayout.addView(adView);

                return new MyViewHolder(mainLayout);
            }
            else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_APPODEAL)) {

                mainLayout = new RelativeLayout(mContext);

                if(nativeAd != null) {
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    CardView cardView = (CardView) inflater.inflate(R.layout.appodeal_ad_layout, null);
                    NativeAdView nativeAdView = cardView.findViewById(R.id.native_item);

                    TextView tvTitle = (TextView) nativeAdView.findViewById(R.id.tv_title);
                    tvTitle.setText(nativeAd.getTitle());
                    nativeAdView.setTitleView(tvTitle);

                    TextView tvDescription = (TextView) nativeAdView.findViewById(R.id.tv_description);
                    tvDescription.setText(nativeAd.getDescription());
                    nativeAdView.setDescriptionView(tvDescription);

                    RatingBar ratingBar = (RatingBar) nativeAdView.findViewById(R.id.rb_rating);
                    if (nativeAd.getRating() == 0) {
                        ratingBar.setVisibility(View.INVISIBLE);
                    } else {
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingBar.setRating(nativeAd.getRating());
                        ratingBar.setStepSize(0.1f);
                    }
                    nativeAdView.setRatingView(ratingBar);

                    Button ctaButton = (Button) nativeAdView.findViewById(R.id.b_cta);
                    ctaButton.setText(nativeAd.getCallToAction());
                    nativeAdView.setCallToActionView(ctaButton);


                    TextView tvAgeRestrictions = (TextView) nativeAdView.findViewById(R.id.tv_age_restriction);
                    tvAgeRestrictions.setVisibility(View.GONE);

                    NativeIconView nativeIconView = nativeAdView.findViewById(R.id.icon);
                    nativeAdView.setIconView(nativeIconView);

                    NativeMediaView nativeMediaView = (NativeMediaView) nativeAdView.findViewById(R.id.appodeal_media_view_content);
                    nativeAdView.setMediaView(nativeMediaView);

                    nativeAdView.registerView(nativeAd);
                    nativeAdView.setVisibility(View.VISIBLE);

                    RelativeLayout.LayoutParams bannerParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    bannerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bannerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    mainLayout.addView(cardView, bannerParams);
                }


                return new MyViewHolder(mainLayout);
            }
            /*
            else if (WebAPI.ADS_TYPE.equals(WebAPI.TYPE_MP)) {

                mainLayout = new RelativeLayout(mContext);
                com.mopub.mobileads.MoPubView moPubView = new com.mopub.mobileads.MoPubView(mContext);
                moPubView.setAdUnitId(WebAPI.ADMOB_BANNER);
                moPubView.loadAd();
                RelativeLayout.LayoutParams bannerParameters =
                        new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mainLayout.addView(moPubView, bannerParameters);
                return new MyViewHolder(mainLayout);

            }
 */
            else {

                View view = LayoutInflater.from(mContext).inflate(R.layout.item_server, parent, false);
                return new MyViewHolder(view);
            }
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_server, parent, false);
            return new MyViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Log.v("CHECKCOUNT", position + " " + serverLists.size());

        if(getItemViewType(position) == CONTENT_TYPE){
            holder.serverCountry.setText(serverLists.get(position).getCountry());
            Glide.with(mContext)
                    .load(serverLists.get(position).getFlagUrl())
                    .into(holder.serverIcon);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectListener.onSelected(serverLists.get(position));
                    Log.v("Kabila",serverLists.get(position).getCountry());
                }
            });
        }else {

        }

    }

    @Override
    public int getItemCount() {
        return serverLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView serverIcon;
        TextView serverCountry;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            serverIcon = itemView.findViewById(R.id.flag);
            serverCountry = itemView.findViewById(R.id.countryName);
        }
    }

    public void setData(List<Server> servers) {
        serverLists.clear();
        serverLists.addAll(servers);
        notifyDataSetChanged();
    }

    public interface OnSelectListener {
        void onSelected(Server server);
    }

    public void setOnSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    @Override
    public int getItemViewType(int position) {
        return serverLists.get(position) ==null? AD_TYPE:CONTENT_TYPE;
    }

    public interface ServerSelected {
        void onServerSelected(Server server);
    }
}

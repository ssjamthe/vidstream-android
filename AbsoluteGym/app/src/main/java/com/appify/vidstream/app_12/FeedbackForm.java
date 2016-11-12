package com.appify.vidstream.app_12;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appify.vidstream.constants.ApplicationConstants;
import com.appify.vidstream.control.AppController;
import com.appify.vidstream.utility.CheckInternetConnection;
import com.appify.vidstream.utility.SSLManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;

public class FeedbackForm extends AppCompatActivity implements ApplicationConstants {

    private Random random = new Random();
    private Double randomNo = random.nextDouble() * 1.0;
    private CheckInternetConnection cic;
    private Boolean isInternetPresent = false;
    private EditText ed_feedback;
    private TextView tv_edit_counter, thank_u_text;
    private ImageButton bt_submit;
    private boolean flag;
    private long minIntervalInterstitial;
    private RelativeLayout inMobiAdContainer, feedback_linear;
    private int ActivityNo;
    private String BackGround_Image, deviceID, showBanner, showInmobiAdWeightage;
    private LinearLayout feedbackInnerLayout;
    private NetworkResponse networkResponse;
    private ProgressDialog progressDialog;
    private InterstitialAd banner;
    private AdView adMobAdView;
    private InMobiBanner mBannerAd;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_form);

        //For ActionBar Visible BackArrow 77>82
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SSLManager.handleSSLHandshake(); //For SSL Request

        cic = new CheckInternetConnection(getApplicationContext());
        isInternetPresent = cic.isConnectingToInternet();
        if (!isInternetPresent) {
            Intent intent = new Intent(FeedbackForm.this,
                    NoInternetScreen.class);
            intent.putExtra("flag",flag);
            startActivity(intent);
            FeedbackForm.this.finish();
        }

        //Intent values from CategorizationScreen.
        try {
            Intent adIntent = getIntent();
            BackGround_Image = adIntent.getStringExtra("back_img");
            flag = adIntent.getExtras().getBoolean("flag");
            deviceID = adIntent.getStringExtra("deviceID");
            ActivityNo = getIntent().getIntExtra("ActivityNo", 0);
            showBanner = adIntent.getStringExtra("showBanner");
            showInmobiAdWeightage = adIntent.getStringExtra("showInmobiAdWeightage");
            minIntervalInterstitial = getIntent().getLongExtra("minIntervalInterstitial", 0);
        }catch (Exception e){e.printStackTrace();}

        //Initialize Views
        feedback_linear = (RelativeLayout) findViewById(R.id.feedback_linear);
        feedbackInnerLayout = (LinearLayout) findViewById(R.id.feedbackInnerLayout);
        ed_feedback = (EditText) findViewById(R.id.feefback_form_edit_text);
        adMobAdView = (AdView) this.findViewById(R.id.adViewFeedback);
        inMobiAdContainer = (RelativeLayout) findViewById(R.id.feedback_ad_container);
        tv_edit_counter = (TextView) findViewById(R.id.text_counter);
        thank_u_text = (TextView) findViewById(R.id.thank_u_text);
        bt_submit = (ImageButton) findViewById(R.id.submit_feedback);

        //for Showing Ads
        try
        {
            double showAdWeight = Double.parseDouble(showInmobiAdWeightage);
            if (showAdWeight > randomNo) {
                if (showBanner.equalsIgnoreCase("true")) {
                    showInMobiAdBanner();
                }
            } else {
                if (showBanner.equalsIgnoreCase("true")) {
                    showAdMobBanner();
                }
            }
        }
        catch (Exception e){e.printStackTrace();}

        //Counter
        tv_edit_counter.setText("0 / 500");
        ed_feedback.addTextChangedListener(mTextEditorWatcher);

        feedback_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        feedbackInnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        tv_edit_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        thank_u_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        try {
            if(BackGround_Image != null)
            {
                Picasso.with(FeedbackForm.this).load(BackGround_Image).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        feedback_linear.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }catch (Exception e){e.printStackTrace();}

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    try {
                        hideKeyboard();
                        cic = new CheckInternetConnection(getApplicationContext());
                        isInternetPresent = cic.isConnectingToInternet();
                        if (!isInternetPresent) {
                            hideKeyboard();
                            AlertDialog.Builder builderError = new AlertDialog.Builder(
                                    FeedbackForm.this);
                            builderError
                                    .setTitle(Html.fromHtml("<font color='" + APP_THEME_COLOR + "'>" + "Please Check Your Internet Connection!" + "</font>"))
                                    .setCancelable(false)
                                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface arg0,
                                                int arg1) {
                                            arg0.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builderError.create();
                            dialog.show();
                        }else {
                            progressDialogCall();
                            String Comment = ed_feedback.getText().toString();
                            final String SendFeedbckForm = URL_IP_ADDRESS + URL_FEEDBACKFORM + "?appId=" + URLEncoder.encode(APP_ID) + "&deviceId=" + URLEncoder.encode(deviceID) + "&user_comment=" + URLEncoder.encode(Comment);
                            System.out.println("SendFeedbckForm= " + SendFeedbckForm);
                            JsonObjectRequest SendFeedbckFormRequest = new JsonObjectRequest(SendFeedbckForm, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject childCategoryVideoResponse) {
                                    ed_feedback.setText("");
                                    midToast("Thank You!", Toast.LENGTH_LONG);
                                    onBackPressed();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError errorResponse) {
                                    try {
                                        System.out.println("SendFeedbckForm= " + errorResponse.getMessage());
                                        ed_feedback.setText("");
                                        midToast("Thank You!", Toast.LENGTH_LONG);
                                        onBackPressed();
                                        networkResponse = errorResponse.networkResponse;
                                        if (networkResponse != null) {
                                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            // Adding request to request queue
                            AppController.getInstance().addToRequestQueue(SendFeedbckFormRequest);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            String Counter_text = "" + String.valueOf(s.length()) + " / 500";
            tv_edit_counter.setText(Counter_text);
            int Limit = Integer.parseInt(String.valueOf(s.length()));
            if(Limit == 500){
                midToast("Limit is exceeded, you can write character upto 500 only.",Toast.LENGTH_LONG);
            }
        }
        public void afterTextChanged(Editable s) {
        }
    };

    void midToast(String str, int showTime) {
        Toast toast = Toast.makeText(FeedbackForm.this, str, showTime);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,
                0, 0);
        TextView v = (TextView) toast.getView().findViewById(
                android.R.id.message);
        v.setTextColor(Color.YELLOW);
        toast.show();
    }

    private void progressDialogCall() {
        progressDialog = new ProgressDialog(FeedbackForm.this);
        progressDialog.setMessage("Sending...");
        progressDialog.setCancelable(false);
        try {
            progressDialog.show();
        }catch (Exception e){e.printStackTrace();}
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private boolean isValid(){
        if(ed_feedback.getText().length() < 1)
        {
            ed_feedback.requestFocus();
            ed_feedback.setError("Field is empty!");
            return false;
        }
        else
        {
            return true;
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void showKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void showAdMobBanner(){
        try{
            adMobAdView.setVisibility(View.VISIBLE);
            //adMobAdView.setAdSize(AdSize.SMART_BANNER);
            banner = new InterstitialAd(FeedbackForm.this);
            banner.setAdUnitId(ADMOB_BANNER);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(APP_NAME).build();
            adMobAdView.loadAd(adRequest);
            banner.loadAd(adRequest);
            banner.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    if (banner.isLoaded()) {
                    }else {
                        Log.d("","Banner ad failed to load");
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }

    private void showInMobiAdBanner() {
        try{
            mBannerAd = new InMobiBanner(FeedbackForm.this, INMOBI_BANNER);
            inMobiAdContainer.setVisibility(View.VISIBLE);
            if (inMobiAdContainer != null) {

                mBannerAd.setAnimationType(InMobiBanner.AnimationType.ROTATE_HORIZONTAL_AXIS);
                mBannerAd.setListener(new InMobiBanner.BannerAdListener() {
                    @Override
                    public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                    }

                    @Override
                    public void onAdLoadFailed(InMobiBanner inMobiBanner,
                                               InMobiAdRequestStatus inMobiAdRequestStatus) {
                    }

                    @Override
                    public void onAdDisplayed(InMobiBanner inMobiBanner) {
                    }

                    @Override
                    public void onAdDismissed(InMobiBanner inMobiBanner) {
                    }

                    @Override
                    public void onAdInteraction(InMobiBanner inMobiBanner, Map<Object, Object> map) {
                    }

                    @Override
                    public void onUserLeftApplication(InMobiBanner inMobiBanner) {
                    }

                    @Override
                    public void onAdRewardActionCompleted(InMobiBanner inMobiBanner, Map<Object, Object> map) {
                    }
                });

                int width = toPixelUnits(320);
                int height = toPixelUnits(50);
                RelativeLayout.LayoutParams bannerLayoutParams =
                        new RelativeLayout.LayoutParams(width, height);
                bannerLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                bannerLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                inMobiAdContainer.addView(mBannerAd, bannerLayoutParams);
                mBannerAd.load();
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private int toPixelUnits(int dipUnit) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dipUnit * density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {}
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {}
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return super.dispatchTouchEvent(event);
        }
        catch (Exception ignored){
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        ActivityNo = ActivityNo+1;
        Intent intentCatzation = new Intent(FeedbackForm.this,CategorizationScreen.class);
        intentCatzation.putExtra("flag",flag);
        intentCatzation.putExtra("ActivityNo",ActivityNo);
        intentCatzation.putExtra("showBanner", showBanner);
        intentCatzation.putExtra("showInmobiAdWeightage",showInmobiAdWeightage);
        intentCatzation.putExtra("minIntervalInterstitial",minIntervalInterstitial);
        startActivity(intentCatzation);
        FeedbackForm.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}

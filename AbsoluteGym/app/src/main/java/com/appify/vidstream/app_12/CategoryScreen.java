package com.appify.vidstream.app_12;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appify.vidstream.adapter.CategoryGridBaseAdapter;
import com.appify.vidstream.adapter.CategoryListBaseAdapter;
import com.appify.vidstream.adapter.VideoThumbnailGridBaseAdapter;
import com.appify.vidstream.adapter.VideoThumbnailListBaseAdapter;
import com.appify.vidstream.adapter.OrderByAdapter;
import com.appify.vidstream.constants.ApplicationConstants;
import com.appify.vidstream.control.AppController;
import com.appify.vidstream.model.CategoriesModel;
import com.appify.vidstream.model.OrderByModel;
import com.appify.vidstream.model.VideoModel;
import com.appify.vidstream.utility.AdManager;
import com.appify.vidstream.utility.CheckInternetConnection;
import com.appify.vidstream.utility.SSLManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.sdk.InMobiSdk;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryScreen extends AppCompatActivity implements ApplicationConstants {

    private InterstitialAd banner, interstitial;
    private String getshowBanner, getshowInmobiAdWeightage, getshowAdMovingInside, getdeviceID, BackGround_Image, videoORcategory;
    private boolean flag;
    private static final String TAG = CategoryScreen.class.getSimpleName();
    private ProgressDialog progressDialog;
    private List<VideoModel> videoModeList = new ArrayList<VideoModel>();
    private VideoThumbnailGridBaseAdapter VideoThumbnailGridBaseAdapter;
    private VideoThumbnailListBaseAdapter VideoThumbnailListBaseAdapter;
    private List<CategoriesModel> categoriesModeList = new ArrayList<CategoriesModel>();
    private CategoryGridBaseAdapter childCategoryGridBaseAdapter;
    private CategoryListBaseAdapter childCategoryListBaseAdapter;
    private List<OrderByModel> orderByModeList = new ArrayList<OrderByModel>();
    private OrderByAdapter orderByAdapter;
    private String OrderAttributeCategoryValue;
    private Spinner orderBySpin, entriesPerPageSpin;
    private GridView videoGridList, childCategoriesGridList;
    private RelativeLayout orderByRelativeLayout,relative_category_background;
    private MenuItem listGridConvertor;
    private AdView adMobAdView;
    private InMobiBanner mBannerAd;
    private InMobiInterstitial mInterstitialAd;
    private RelativeLayout inMobiAdContainer;
    private NetworkResponse networkResponse;
    private String SelectedOrderValue, EntriesPerPage_Position;
    private int PAGE_NO, ActivityNo, PrevActivityNo;
    private ArrayAdapter<String> EntriesAdapter;
    private ArrayList HierarchyList = new ArrayList();
    private String Category_ID, LAST_CAT_VALUE;
    private CheckInternetConnection cic;
    private Boolean isInternetPresent = false;
    private boolean userScrolled = false;
    private long getminIntervalInterstitial;
    private static final String PREFS_NAME = "CAT_PREF";
    private static final String PREFS_KEY = "CAT_PREF_PREV_ACTNO";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //AdManager
    private AdManager adManager;
    private ProgressBar progressBar;
    private Random random = new Random();
    private Double randomNo = random.nextDouble() * 1.0;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_screen);

        //For ActionBar Background and Visible BackArrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            //Fresco.initialize(this);
            InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
            InMobiSdk.init(this, INMOBI_ACCOUNT_ID);
            //Sheared Preferences
            preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            adManager = new AdManager(CategoryScreen.this);
            SSLManager.handleSSLHandshake(); //For SSL Request
        }catch (Exception e) {e.printStackTrace();}

        //Initialize Views
        orderByRelativeLayout = (RelativeLayout) findViewById(R.id.orderByRelativeLayout);
        videoGridList = (GridView) findViewById(R.id.gridViewVideosCategories);
        childCategoriesGridList = (GridView) findViewById(R.id.gridViewChildCategories);
        orderBySpin = (Spinner) findViewById(R.id.orderbySpin);
        progressBar = (ProgressBar) findViewById(R.id.cat_progressbar);
        entriesPerPageSpin = (Spinner) findViewById(R.id.entriesPerPageSpin);
        adMobAdView = (AdView) this.findViewById(R.id.adViewCategori);
        inMobiAdContainer = (RelativeLayout) findViewById(R.id.category_ad_container);
        relative_category_background = (RelativeLayout) findViewById(R.id.relative_category);
        videoGridList.setVisibility(View.GONE);
        orderByRelativeLayout.setVisibility(View.GONE);

        //Intent values from CategorizationScreen or YoutubeScreen.
        try {
            Intent adIntent = getIntent();
            HierarchyList = adIntent.getStringArrayListExtra("categoryID");
            getshowBanner = adIntent.getStringExtra("showBanner");
            getshowInmobiAdWeightage = adIntent.getStringExtra("showInmobiAdWeightage");
            getminIntervalInterstitial = getIntent().getLongExtra("minIntervalInterstitial", 0);
            getshowAdMovingInside = adIntent.getStringExtra("showAdMovingInside");
            getdeviceID = adIntent.getStringExtra("deviceID");
            BackGround_Image = adIntent.getStringExtra("back_img");
            flag = adIntent.getExtras().getBoolean("flag");
            ActivityNo = getIntent().getIntExtra("ActivityNo", 0);
            System.out.println("Get Categorization ActivityNo from Intent>>> And set ActivityNo = " + ActivityNo + ";");
            PrevActivityNo = preferences.getInt(PREFS_KEY, 0);
        }catch (Exception e){e.printStackTrace();onBackPressed();}

        cic = new CheckInternetConnection(getApplicationContext());
        isInternetPresent = cic.isConnectingToInternet();
        if (!isInternetPresent) {
            noInternetPresent();
        }

        //for Showing Ads
        try
        {
            //for getting() Interstitial Ad.
            interstitial = adManager.getAdMobAd();
            System.out.println("Cat interstitial="+interstitial);
            mInterstitialAd = adManager.getInMobiAd();
            System.out.println("Cat InMobi interstitial="+mInterstitialAd);

            double showAdWeight = Double.parseDouble(getshowInmobiAdWeightage);
            //For Interstitial Ads
            long CurrentTime = new Date().getTime() / 1000;
            long GetAdShowTime = adManager.getNewTime(); //Get New Time From AdManager
            long Duration = CurrentTime - GetAdShowTime;
            //Toast.makeText(CategoryScreen.this, "Cat Duration = " + Duration + ", showAdMovingInside = " + getshowAdMovingInside, Toast.LENGTH_SHORT).show();
            System.out.println("Cat Duration = " + Duration + ", showAdMovingInside = " + getshowAdMovingInside);
            if (Duration >= getminIntervalInterstitial && PrevActivityNo != ActivityNo )
            {
                if (getshowAdMovingInside.equalsIgnoreCase("true")) {
                    if (showAdWeight > randomNo && (!(mInterstitialAd.equals(null))))
                    {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(mInterstitialAd != null)
                                {
                                    mInterstitialAd.load();
                                    mInterstitialAd.show();
                                }
                                System.out.println("Cat interstitial="+mInterstitialAd);
                                PrevActivityNo = ActivityNo;
                                editor = preferences.edit();
                                editor.putInt(PREFS_KEY, PrevActivityNo);
                                editor.commit();
                            }
                        }, 1);

                    } else {
                        //Call AdMob Interstitial Ad
                        if (interstitial.isLoaded()) {
                            interstitial.show();
                            PrevActivityNo = ActivityNo;
                            editor = preferences.edit();
                            editor.putInt(PREFS_KEY, PrevActivityNo);
                            editor.commit();
                        }else {
                            Log.d("","Interstitial ad failed to load");
                        }
                    }
                }
            }
            if (!interstitial.isLoaded()) {
                adManager.createAdMobAds(); //Request for creating Ad.
            }
            if(!mInterstitialAd.isReady()){
                adManager.createInMobiInterstitial(); //Request for creating Ad.
            }

        }catch (Exception e){e.printStackTrace();}

        try {
            //For Banner
            double showAdWeight = Double.parseDouble(getshowInmobiAdWeightage);
            if (showAdWeight > randomNo) {
                if (getshowBanner.equalsIgnoreCase("true")) {
                    showInMobiAdBanner();
                }
            }else{
                if (getshowBanner.equalsIgnoreCase("true")) {
                    showAdmobBanner();
                }
            }
        }catch (Exception e){e.printStackTrace();}

        try {
            PAGE_NO = 1;
            EntriesPerPage_Position = FIRST;
            System.out.println("HierarchyList = " + HierarchyList);
            Object getArrayLastValue = HierarchyList.get(HierarchyList.size() - 1);  //Get Last Value
            LAST_CAT_VALUE = String.valueOf(getArrayLastValue.toString());
            System.out.println("Last Value = " + LAST_CAT_VALUE);
            //Toast.makeText(CategoryScreen.this,"HierarchyList = "+HierarchyList,Toast.LENGTH_LONG).show();

            if (flag == false) {
                childCategoriesGridList.setNumColumns(2);
                videoGridList.setNumColumns(2);
                flag = false;
            } else {
                childCategoriesGridList.setNumColumns(1);
                videoGridList.setNumColumns(1);
                flag = true;
            }
        }catch (Exception e) {e.printStackTrace();}

        //For Background Image
        try {
            if(BackGround_Image != null)
            {
                Picasso.with(CategoryScreen.this).load(BackGround_Image).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        relative_category_background.setBackgroundDrawable(new BitmapDrawable(bitmap));
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

//Entries Per Page Adapter
        try {
            implementScrollListener();  //Implement Video Scroll Listener
            EntriesAdapter = new ArrayAdapter<String>(CategoryScreen.this, R.layout.entries_per_page, getEntriesPerPageArray());
            entriesPerPageSpin.setAdapter(EntriesAdapter);
            entriesPerPageSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String display = parent.getSelectedItem().toString();
                    EntriesPerPage_Position = ((TextView) view).getText().toString();
                    System.out.println("Page No IN = " + PAGE_NO);
                    System.out.println("Entries IN= " + EntriesPerPage_Position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }catch (Exception e){e.printStackTrace();}

//Initialize Adapters and WebService Method call
        try {
            //For Adapters
            childCategoryGridBaseAdapter = new CategoryGridBaseAdapter(CategoryScreen.this, categoriesModeList);
            childCategoryListBaseAdapter = new CategoryListBaseAdapter(CategoryScreen.this, categoriesModeList);
            VideoThumbnailGridBaseAdapter = new VideoThumbnailGridBaseAdapter(CategoryScreen.this, videoModeList);
            VideoThumbnailListBaseAdapter = new VideoThumbnailListBaseAdapter(CategoryScreen.this, videoModeList);
            orderByAdapter = new OrderByAdapter(CategoryScreen.this, orderByModeList);
            categoryVideoWebServiceCall(LAST_CAT_VALUE);  //WebService call

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**********
     * End OnCreate()
     *****************/

    //TODO Web Service Call for Categories and Videos
    private void categoryVideoWebServiceCall(String CategoryID){
        try{
            progressDialogCall();
            System.out.println("1 progressDialogCall");
            Category_ID = CategoryID;
            OrderAttributeCategoryValue = Category_ID;
            //Toast.makeText(CategoryScreen.this,"Category_ID = "+Category_ID,Toast.LENGTH_SHORT).show();
            childCategoryGridBaseAdapter.clearCategoryGrid();
            childCategoryListBaseAdapter.clearCategoryList();
            VideoThumbnailGridBaseAdapter.clearVideoThumbnailGrid();
            VideoThumbnailListBaseAdapter.clearVideoThumbnailList();
            orderByAdapter.clearOrderByList();
            orderBySpin.setAdapter(orderByAdapter);
            if(flag==false){
                childCategoriesGridList.setAdapter(childCategoryGridBaseAdapter);
                videoGridList.setAdapter(VideoThumbnailGridBaseAdapter);
                childCategoriesGridList.setNumColumns(2);
                videoGridList.setNumColumns(2);
                flag = false;
            }else{
                childCategoriesGridList.setAdapter(childCategoryListBaseAdapter);
                videoGridList.setAdapter(VideoThumbnailListBaseAdapter);
                childCategoriesGridList.setNumColumns(1);
                videoGridList.setNumColumns(1);
                flag = true;
            }

            String SendOrderAttribute = null;
            if(Category_ID.equalsIgnoreCase(RecentlyViewedID)){
                SendOrderAttribute = LOADSORTEDVIDEOSURL_VIEWTIMEID;
            }else{
                SendOrderAttribute = LOADSORTEDVIDEOSURL_UPLOADTIMEID;
            }

            System.out.println("Page No = "+PAGE_NO);
            System.out.println("Entries = "+EntriesPerPage_Position);
            final String Cat1PageNo = String.valueOf(PAGE_NO);
            final String loadChildCatURL = URL_IP_ADDRESS + URL_LOADCHILDCATEGORIES+"?appId="+URLEncoder.encode(APP_ID)+"&catId="+URLEncoder.encode(Category_ID)+"&orderAttr="+URLEncoder.encode(SendOrderAttribute)+"&page_no="+URLEncoder.encode(Cat1PageNo)+"&entries_per_page="+URLEncoder.encode(EntriesPerPage_Position)+"&deviceId="+URLEncoder.encode(getdeviceID);
            System.out.println("loadChildCatURL = "+loadChildCatURL);
            JsonObjectRequest childCategoryVideoRequest = new JsonObjectRequest(Request.Method.POST, loadChildCatURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject childCategoryVideoResponse) {
                    try { //If response for Categories
                        childCategoriesGridList.setVisibility(View.VISIBLE);
                        orderByRelativeLayout.setVisibility(View.GONE);
                        videoORcategory = "category";
                        JSONArray childCatArray = childCategoryVideoResponse.getJSONArray("categories");
                        for (int j = 0; j < childCatArray.length(); j++) {
                            JSONObject childCatObject = childCatArray.getJSONObject(j);
                            CategoriesModel categoriesModel = new CategoriesModel();
                            categoriesModel.setCatTitle(childCatObject.getString("name"));
                            categoriesModel.setCatID(childCatObject.getString("id"));
                            categoriesModel.setCatImage(childCatObject.getString("image"));
                            categoriesModeList.add(categoriesModel);
                        }
                    } catch (Exception e) { //If response for Videos
                        e.printStackTrace();
                        childCategoriesGridList.setVisibility(View.GONE);
                        videoORcategory = "video";
                        try {//Response for Order By Attributes
                            orderByRelativeLayout.setVisibility(View.VISIBLE);
                            JSONArray orderArray = childCategoryVideoResponse.getJSONArray("orderAttributes");
                            for (int i = 0; i <= childCategoryVideoResponse.length(); i++) {
                                OrderByModel byModel = new OrderByModel();
                                byModel.setOrderTitle(orderArray.getString(i));
                                orderByModeList.add(byModel);
                            }
                        } catch (Exception exe) {
                            exe.printStackTrace();
                        }
                        System.out.println("Set Outer VideoThumbnailGridBaseAdapter ----------------->");
                        orderByAdapter.notifyDataSetChanged();
                        System.out.println("orderByModeList>>>>>>"+orderByModeList);
                    }
                    childCategoryGridBaseAdapter.notifyDataSetChanged();
                    hidePDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError errorResponse) {
                    VolleyLog.d(TAG, "Child Caregories Did Not Fetch: " + errorResponse.getMessage());
                    System.out.println("Child Caregories url = "+errorResponse.getMessage());

                    try{
                        String ErrorMessage = errorResponse.getMessage();
                        String Keyword = "Authentication Failed";
                        if(ErrorMessage != null){
                            if (ErrorMessage.contains(Keyword)) {
                                authenticationErrorDialog();
                            } else {
                                cantReachedDialog();
                            }
                        }else {
                            cantReachedDialog();
                        }

                        hidePDialog();
                        networkResponse = errorResponse.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Status code", String.valueOf(networkResponse.statusCode));
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put(TOKEN_KEY, TOKEN_VALUE);
                    return headers;
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(childCategoryVideoRequest);
            // On Click Listener of Video List and Intent to YoutubePlayer.java
            videoGridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) { //TODO ClickListener
                    // Getting the Container Layout of the ListView
                    LinearLayout linearLayoutParent = (LinearLayout) view;
                    TextView tvVidName = (TextView) linearLayoutParent.getChildAt(1);
                    TextView tvVidId = (TextView) linearLayoutParent.getChildAt(2);

                    PrevActivityNo = ActivityNo;
                    editor = preferences.edit();
                    editor.putInt(PREFS_KEY, PrevActivityNo);
                    editor.commit();
                    PrevActivityNo  = preferences.getInt(PREFS_KEY, 0);
                    ActivityNo = ActivityNo+1;
                    releaseYoutubeThumbnailView();

                    Intent intentcat = new Intent(CategoryScreen.this, YoutubePlayer.class);
                    intentcat.putExtra("VIDEO_ID", tvVidId.getText().toString());
                    intentcat.putExtra("VIDEO_NAME", tvVidName.getText().toString());
                    intentcat.putExtra("categoryID", HierarchyList);
                    intentcat.putExtra("showBanner", getshowBanner);
                    intentcat.putExtra("showInmobiAdWeightage", getshowInmobiAdWeightage);
                    intentcat.putExtra("minIntervalInterstitial", getminIntervalInterstitial);
                    intentcat.putExtra("showAdMovingInside", getshowAdMovingInside);
                    intentcat.putExtra("deviceID", getdeviceID);
                    intentcat.putExtra("back_img", BackGround_Image);
                    intentcat.putExtra("flag",flag);
                    intentcat.putExtra("ActivityNo",ActivityNo);
                    startActivity(intentcat);
                    CategoryScreen.this.finish();
                }
            });
            //On Click Listener childCategoriesGridList
            childCategoriesGridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                   try {
                       // Getting the Container Layout of the ListView
                       LinearLayout linearLayoutParent = (LinearLayout) view;
                       TextView tvCategory = (TextView) linearLayoutParent.getChildAt(2);
                       String selectedChildCategory = tvCategory.getText().toString();
                       Category_ID = selectedChildCategory;
                       OrderAttributeCategoryValue = Category_ID;
                       HierarchyList.add(Category_ID);
                       PrevActivityNo = ActivityNo;
                       editor = preferences.edit();
                       editor.putInt(PREFS_KEY, PrevActivityNo);
                       editor.commit();
                       PrevActivityNo = preferences.getInt(PREFS_KEY, 0);
                       ActivityNo = ActivityNo + 1;
                       System.out.println("After Add 1 ActivityNo from Intent>>> And set ActivityNo = " + ActivityNo + ";");
                       RefreshCatVid();
                   }catch (Exception e){e.printStackTrace();}
                }
            });

// OrderBy Spinner
            orderBySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View container, int position, long id) {
                    try {
                        LinearLayout orderLayout = (LinearLayout) container;
                        TextView orderText = (TextView) orderLayout.getChildAt(0);
                        //Toast.makeText(CategoryScreen.this,orderText.getText().toString(),Toast.LENGTH_SHORT).show();
                        if (flag == false) {
                            videoGridList.setAdapter(VideoThumbnailGridBaseAdapter);
                            videoGridList.setNumColumns(2);
                            flag = false;
                        } else {
                            videoGridList.setAdapter(VideoThumbnailListBaseAdapter);
                            videoGridList.setNumColumns(1);
                            flag = true;
                        }
                        String selectedOrder = orderText.getText().toString();
                        if (selectedOrder.equals(LOADSORTEDVIDEOSURL_MOSTVIEWED)) {
                            SelectedOrderValue = LOADSORTEDVIDEOSURL_MOSTVIEWEDID;
                            PAGE_NO = 1;
                        } else if(selectedOrder.equals(LOADSORTEDVIDEOSURL_VIEWTIME)){
                            SelectedOrderValue = LOADSORTEDVIDEOSURL_VIEWTIMEID;
                            PAGE_NO = 1;
                        } else{
                            SelectedOrderValue = LOADSORTEDVIDEOSURL_UPLOADTIMEID;
                            PAGE_NO = 1;
                        }

                        System.out.println("Page No Order = " + PAGE_NO);
                        System.out.println("Entries Order = " + EntriesPerPage_Position);
                        String CatPageNo = String.valueOf(PAGE_NO);
                        System.out.println("Requesting and calling method for getting Videos");

                        //Requesting and calling method for getting Videos
                        LoadVideos(APP_ID, OrderAttributeCategoryValue, SelectedOrderValue, CatPageNo, EntriesPerPage_Position, getdeviceID);
                    }catch (Exception e){e.printStackTrace();}
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//End WebServices

    private void LoadVideos(String getappId, String getcatId, String getorderAttr, String getpage_no, String getentries_per_page, String getdeviceId)
    {
        final String setappId = getappId;
        final String setcatId = getcatId;
        final String setorderAttr = getorderAttr;
        final String setpage_no = getpage_no;
        final String setentries_per_page = getentries_per_page;
        final String setdeviceId = getdeviceId;

        progressDialogCall();
        // Creating volley request obj getting videos
        final String loadChildCatURL = URL_IP_ADDRESS + URL_LOADCHILDCATEGORIES + "?appId=" + URLEncoder.encode(setappId) + "&catId=" + URLEncoder.encode(setcatId) + "&orderAttr=" + URLEncoder.encode(setorderAttr) + "&page_no=" + URLEncoder.encode(setpage_no) + "&entries_per_page=" + URLEncoder.encode(setentries_per_page) + "&deviceId=" + URLEncoder.encode(setdeviceId);
        System.out.println("loadChildCatURL = "+loadChildCatURL);
        JsonObjectRequest orderByVideoRequest = new JsonObjectRequest(Request.Method.POST, loadChildCatURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject childCategoryVideoResponse) {
                try {
                    childCategoryGridBaseAdapter.clearCategoryGrid();
                    childCategoryListBaseAdapter.clearCategoryList();
                    VideoThumbnailGridBaseAdapter.clearVideoThumbnailGrid();
                    VideoThumbnailListBaseAdapter.clearVideoThumbnailList();
                    videoGridList.setVisibility(View.VISIBLE);
                    JSONArray videoCatArray = childCategoryVideoResponse.getJSONArray("videos");
                    for (int k = 0; k < videoCatArray.length(); k++) {
                        JSONObject videoCatObject = videoCatArray.getJSONObject(k);
                        VideoModel videoModel = new VideoModel();
                        videoModel.setVideoName(videoCatObject.getString("name"));
                        videoModel.setVideoId(videoCatObject.getString("id"));
                        videoModeList.add(videoModel);
                    }
                    hidePDialog();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    hidePDialog();
                }
                VideoThumbnailGridBaseAdapter.notifyDataSetChanged();
                System.out.println("Set OrderBy VideoThumbnailGridBaseAdapter ----------------->");
                VideoThumbnailListBaseAdapter.notifyDataSetChanged();
                orderByAdapter.notifyDataSetChanged();
                hidePDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError errorResponse) {
                VolleyLog.d(TAG, "Movie Error: " + errorResponse.getMessage());
                System.out.println("loadChildCatURL error= "+errorResponse.getMessage());

                try {
                    String ErrorMessage = errorResponse.getMessage();
                    String Keyword = "Authentication Failed";
                    if(ErrorMessage != null){
                        if (ErrorMessage.contains(Keyword)) {
                            authenticationErrorDialog();
                        } else {
                            cantReachedDialog();
                        }
                    }else {
                        cantReachedDialog();
                    }

                    hidePDialog();
                    networkResponse = errorResponse.networkResponse;
                    if (networkResponse != null) {
                        Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put(TOKEN_KEY, TOKEN_VALUE);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(orderByVideoRequest);
    }

    //ArrayList for EntriesPerPage
    private ArrayList<String> getEntriesPerPageArray()
    {
        ArrayList<String> list=new ArrayList<String>();
        list.add(FIRST);
        list.add(SECOND);
        list.add(THIRD);
        return list;
    }

    // Implement scroll listener
    private void implementScrollListener() {
        try {
            videoGridList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                    // If scroll state is touch scroll then set userScrolled
                    // true
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        userScrolled = true;
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    if (userScrolled
                            && firstVisibleItem + visibleItemCount == totalItemCount) {
                        userScrolled = false;
                        PAGE_NO = PAGE_NO + 1;
                        updateListView();
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }

    // Method for repopulating recycler view
    private void updateListView() {
        String Cat4PageNo = String.valueOf(PAGE_NO);
        final String loadChildCatURL = URL_IP_ADDRESS + URL_LOADCHILDCATEGORIES + "?appId=" + URLEncoder.encode(APP_ID) + "&catId=" + URLEncoder.encode(OrderAttributeCategoryValue) + "&orderAttr=" + URLEncoder.encode(SelectedOrderValue) + "&page_no=" + URLEncoder.encode(Cat4PageNo) + "&entries_per_page=" + URLEncoder.encode(EntriesPerPage_Position) + "&deviceId=" + URLEncoder.encode(getdeviceID);
        JsonObjectRequest orderByVideoRequest = new JsonObjectRequest(Request.Method.POST, loadChildCatURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject childCategoryVideoResponse) {
                try {
                    videoGridList.setVisibility(View.VISIBLE);
                    JSONArray videoCatArray = childCategoryVideoResponse.getJSONArray("videos");
                    for (int k = 0; k < videoCatArray.length(); k++) {
                        JSONObject videoCatObject = videoCatArray.getJSONObject(k);
                        VideoModel videoModel = new VideoModel();
                        videoModel.setVideoName(videoCatObject.getString("name"));
                        videoModel.setVideoId(videoCatObject.getString("id"));
                        videoModeList.add(videoModel);
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                VideoThumbnailGridBaseAdapter.notifyDataSetChanged();
                VideoThumbnailListBaseAdapter.notifyDataSetChanged();
                orderByAdapter.notifyDataSetChanged();
                hidePDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError errorResponse) {
                VolleyLog.d(TAG, "Movie Error: " + errorResponse.getMessage());
                hidePDialog();
                networkResponse = errorResponse.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put(TOKEN_KEY, TOKEN_VALUE);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(orderByVideoRequest);

        // After adding new data hide the view.
        //bottomLayout.setVisibility(View.GONE);
        hidePDialog();
    }

    private void showInMobiAdBanner() {
    try{
        mBannerAd = new InMobiBanner(CategoryScreen.this, INMOBI_BANNER);
        inMobiAdContainer.setVisibility(View.VISIBLE);
        if (inMobiAdContainer != null) {

            mBannerAd.setAnimationType(InMobiBanner.AnimationType.ROTATE_HORIZONTAL_AXIS);
            mBannerAd.setListener(new InMobiBanner.BannerAdListener() {
                @Override
                public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoadFailed(InMobiBanner inMobiBanner,
                                           InMobiAdRequestStatus inMobiAdRequestStatus) {
                    Log.w(TAG, "Banner ad failed to load with error: " +
                            inMobiAdRequestStatus.getMessage());
                }

                @Override
                public void onAdDisplayed(InMobiBanner inMobiBanner) {
                    progressBar.setVisibility(View.GONE);
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

    private void showAdmobBanner(){
        try{
            adMobAdView.setVisibility(View.VISIBLE);
            //adMobAdView.setAdSize(AdSize.SMART_BANNER);
            banner = new InterstitialAd(CategoryScreen.this);
            banner.setAdUnitId(ADMOB_BANNER);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(APP_NAME).build();
            adMobAdView.loadAd(adRequest);
            progressBar.setVisibility(View.GONE);
            banner.loadAd(adRequest);
            banner.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    if (banner.isLoaded()) {
                        progressBar.setVisibility(View.GONE);
                    }else {
                        Log.d("","Banner ad failed to load");
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void progressDialogCall() {
        progressDialog = new ProgressDialog(CategoryScreen.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void releaseYoutubeThumbnailView(){
        try {
            VideoThumbnailGridBaseAdapter.releaseGridThumbnailView();
            VideoThumbnailListBaseAdapter.releaseListThumbnailView();
        }catch (Exception e){e.printStackTrace();}
    }

    private void authenticationErrorDialog(){
        AlertDialog.Builder builderError = new AlertDialog.Builder(CategoryScreen.this);
        builderError
                .setTitle(Html.fromHtml("<font color='" + APP_THEME_COLOR + "'>" + AUTHENTICATION_ERROR + "</font>"))
                .setMessage(UPDATING_APP)
                .setCancelable(false)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface arg0,
                            int arg1) {
                        arg0.dismiss();
                        CategoryScreen.this.finish();
                    }
                });
        AlertDialog dialog = builderError.create();
        dialog.show();
    }

    private void cantReachedDialog(){
        AlertDialog.Builder builderError = new AlertDialog.Builder(CategoryScreen.this);
        builderError
                .setTitle(Html.fromHtml("<font color='" + APP_THEME_COLOR + "'>" + CANTBEREACHED + "</font>"))
                .setMessage(TRYAGAIN)
                .setCancelable(false)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface arg0,
                            int arg1) {
                        arg0.dismiss();
                        onBackPressed();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent ref = new Intent(CategoryScreen.this, CategoryScreen.class);
                        ref.putExtra("categoryID", HierarchyList);
                        ref.putExtra("showBanner", getshowBanner);
                        ref.putExtra("showInmobiAdWeightage", getshowInmobiAdWeightage);
                        ref.putExtra("minIntervalInterstitial", getminIntervalInterstitial);
                        ref.putExtra("showAdMovingInside", getshowAdMovingInside);
                        ref.putExtra("deviceID", getdeviceID);
                        ref.putExtra("back_img", BackGround_Image);
                        ref.putExtra("flag",flag);
                        ActivityNo = ActivityNo-1;
                        ref.putExtra("ActivityNo",ActivityNo);
                        startActivity(ref);
                        CategoryScreen.this.finish();
                    }
                });
        AlertDialog dialog = builderError.create();
        dialog.show();
    }

    private void RefreshCatVid(){
        try {
            Intent ref = new Intent(CategoryScreen.this, CategoryScreen.class);
            ref.putExtra("categoryID", HierarchyList);
            ref.putExtra("showBanner", getshowBanner);
            ref.putExtra("showInmobiAdWeightage", getshowInmobiAdWeightage);
            ref.putExtra("minIntervalInterstitial", getminIntervalInterstitial);
            ref.putExtra("showAdMovingInside", getshowAdMovingInside);
            ref.putExtra("deviceID", getdeviceID);
            ref.putExtra("back_img", BackGround_Image);
            ref.putExtra("flag", flag);
            ref.putExtra("ActivityNo", ActivityNo);
            startActivity(ref);
            CategoryScreen.this.finish();
        }catch (Exception e){e.printStackTrace();}
    }

    private void noInternetPresent(){
        Intent intent = new Intent(CategoryScreen.this,
                NoInternetScreen.class);
        intent.putExtra("flag",flag);
        startActivity(intent);
        CategoryScreen.this.finish();
    }

    @Override
    public void onBackPressed() {
        try {
            PrevActivityNo = ActivityNo;
            editor = preferences.edit();
            editor.putInt(PREFS_KEY, PrevActivityNo);
            editor.commit();
            PrevActivityNo = preferences.getInt(PREFS_KEY, 0);
            ActivityNo = ActivityNo + 1;
            System.out.println("B4 HierarchyList = " + HierarchyList);
            releaseYoutubeThumbnailView();
            if (HierarchyList.isEmpty()) {
                Intent intentCatzation = new Intent(CategoryScreen.this, CategorizationScreen.class);
                intentCatzation.putExtra("flag", flag);
                intentCatzation.putExtra("ActivityNo", ActivityNo);
                intentCatzation.putExtra("showBanner", getshowBanner);
                intentCatzation.putExtra("showInmobiAdWeightage", getshowInmobiAdWeightage);
                intentCatzation.putExtra("minIntervalInterstitial", getminIntervalInterstitial);
                startActivity(intentCatzation);
                CategoryScreen.this.finish();

            } else if (!HierarchyList.isEmpty()) {
                System.out.println("HierarchyList = " + HierarchyList);
                //Toast.makeText(CategoryScreen.this,"HierarchyList = "+HierarchyList,Toast.LENGTH_SHORT).show();
                HierarchyList.remove(HierarchyList.size() - 1);  //Remove Last Value
                if (HierarchyList.isEmpty()) {
                    Intent intentCatzation = new Intent(CategoryScreen.this, CategorizationScreen.class);
                    intentCatzation.putExtra("flag", flag);
                    intentCatzation.putExtra("ActivityNo", ActivityNo);
                    intentCatzation.putExtra("showBanner", getshowBanner);
                    intentCatzation.putExtra("showInmobiAdWeightage", getshowInmobiAdWeightage);
                    intentCatzation.putExtra("minIntervalInterstitial", getminIntervalInterstitial);
                    startActivity(intentCatzation);
                    CategoryScreen.this.finish();
                } else {
                    Object getLastValue = HierarchyList.get(HierarchyList.size() - 1);  //Get Last Value
                    String LAST_VALUE = String.valueOf(getLastValue.toString());
                    System.out.println("Last Value = " + LAST_VALUE);
                    PAGE_NO = 1;
                    RefreshCatVid();  //Refresh
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_screen, menu);

        listGridConvertor = menu.findItem(R.id.list_grid_convertor);
        if(flag==false){
            listGridConvertor.setIcon(R.drawable.ic_action_view_as_list);
        }else{
            listGridConvertor.setIcon(R.drawable.ic_action_view_as_grid);
        }
        listGridConvertor.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                try {
                    if (flag==false) {
                        if (videoORcategory.equalsIgnoreCase("category")) {
                            childCategoriesGridList.setAdapter(childCategoryListBaseAdapter);
                            childCategoriesGridList.setNumColumns(1);
                        } else {
                            videoGridList.setAdapter(VideoThumbnailListBaseAdapter);
                            videoGridList.setNumColumns(1);
                        }
                        listGridConvertor.setIcon(R.drawable.ic_action_view_as_grid);
                        flag = true;
                    } else {
                        if (videoORcategory.equalsIgnoreCase("category")) {
                            childCategoriesGridList.setAdapter(childCategoryGridBaseAdapter);
                            childCategoriesGridList.setNumColumns(2);
                        } else {
                            videoGridList.setAdapter(VideoThumbnailGridBaseAdapter);
                            videoGridList.setNumColumns(2);
                        }
                        listGridConvertor.setIcon(R.drawable.ic_action_view_as_list);
                        flag = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.child_category_home:
                releaseYoutubeThumbnailView();
                Intent intentHome = new Intent(CategoryScreen.this, CategorizationScreen.class);
                intentHome.putExtra("flag",flag);
                intentHome.putExtra("ActivityNo",ActivityNo);
                intentHome.putExtra("showBanner", getshowBanner);
                intentHome.putExtra("showInmobiAdWeightage",getshowInmobiAdWeightage);
                intentHome.putExtra("minIntervalInterstitial",getminIntervalInterstitial);
                startActivity(intentHome);
                CategoryScreen.this.finish();
                break;

            case R.id.child_category_refresh:
                RefreshCatVid();
        }
        return true;
    }

}

package com.appify.vidstream.app_12;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appify.vidstream.constants.ApplicationConstants;
import com.appify.vidstream.utility.CheckInternetConnection;
import com.appify.vidstream.utility.SSLManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
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
import android.content.Intent;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class YoutubePlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener,ApplicationConstants {

	private static final int RECOVERY_REQUEST = 1;
	private static final String TAG = YoutubePlayer.class.getSimpleName();
	private YouTubePlayer youTubePlayer;
	private boolean flag;
	private YouTubePlayerView youTubePlayerView;
	private String VIDEO_ID, VIDEO_NAME, deviceID, showBanner, youtubeshowInmobiAdWeightage, showAdMovingInside, back_image;
	private TextView videoName;
	private LinearLayout home_but, back_but;
	private RelativeLayout player_background;
	private CheckInternetConnection cic;
	private Boolean isInternetPresent = false;
	private ArrayList CategoryHierarchyList = new ArrayList();
	private long showMinIntervalInterstitial;
	private int ActivityNo, PrevActivityNo;
	private static final String PREFS_NAME = "VID_PREF";
	private static final String PREFS_KEY = "VID_PREF_PREV_ACTNO";
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	//PowerManager
	protected PowerManager.WakeLock mWakeLock;

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_player);

		try{
			//Fresco.initialize(this);
			InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
			InMobiSdk.init(this, INMOBI_ACCOUNT_ID);
			//Sheared Preferences
			preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			SSLManager.handleSSLHandshake(); //For SSL Request
		}catch (Exception e){e.printStackTrace();}

		player_background = (RelativeLayout) findViewById(R.id.player_background);
		videoName = (TextView) findViewById(R.id.youtubeVidText);

		//Intent values from CategoryScreen.
		try {
			final Intent intentget = getIntent();
			VIDEO_ID = intentget.getStringExtra("VIDEO_ID");
			VIDEO_NAME = intentget.getStringExtra("VIDEO_NAME");
			CategoryHierarchyList = intentget.getStringArrayListExtra("categoryID");
			showBanner = intentget.getStringExtra("showBanner");
			youtubeshowInmobiAdWeightage = intentget.getStringExtra("showInmobiAdWeightage");
			showMinIntervalInterstitial = getIntent().getLongExtra("minIntervalInterstitial", 0);
			showAdMovingInside = intentget.getStringExtra("showAdMovingInside");
			deviceID = intentget.getStringExtra("deviceID");
			back_image = intentget.getStringExtra("back_img");
			flag = intentget.getExtras().getBoolean("flag");
			ActivityNo = getIntent().getIntExtra("ActivityNo", 0);
			System.out.println("Get Categorization ActivityNo from Intent>>>And set ActivityNo = " + ActivityNo + ";");
		}catch(Exception e){e.printStackTrace();}

		cic = new CheckInternetConnection(getApplicationContext());
		isInternetPresent = cic.isConnectingToInternet();
		if (!isInternetPresent) {
            noInternetPresent();
		}

		//Update ActivityNo and set video Name
        try {
            ActivityNo = ActivityNo + 1;
			System.out.println("After Add 1 ActivityNo from Intent>>> And set ActivityNo = " + ActivityNo + ";");
            videoName.setText(VIDEO_NAME);

            if (!(PrevActivityNo == 0)) {
                if (ActivityNo != PrevActivityNo) {
                    PrevActivityNo = ActivityNo;
                    editor = preferences.edit();
                    editor.putInt(PREFS_KEY, PrevActivityNo);
                    editor.commit();
                    PrevActivityNo = preferences.getInt(PREFS_KEY, 0);
                }
            }
        }catch (Exception e){e.printStackTrace();onBackPressed();}

		/* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
		final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		this.mWakeLock.acquire();

		// For Buttons
			back_but = (LinearLayout) findViewById(R.id.back_to_cat);
			back_but.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
			home_but = (LinearLayout) findViewById(R.id.back_to_home);
			home_but.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                    try {
                        Intent intentHome = new Intent(YoutubePlayer.this, CategorizationScreen.class);
                        intentHome.putExtra("flag", flag);
                        intentHome.putExtra("ActivityNo", ActivityNo);
                        intentHome.putExtra("showBanner", showBanner);
                        intentHome.putExtra("showInmobiAdWeightage", youtubeshowInmobiAdWeightage);
                        intentHome.putExtra("minIntervalInterstitial", showMinIntervalInterstitial);
                        startActivity(intentHome);
                        YoutubePlayer.this.finish();
                    }catch (Exception e){e.printStackTrace();}
				}
			});

		//For initializing YouTubePlayerView
		try {
			youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeplayerview);
			youTubePlayerView.initialize(API_KEY, this);
		}catch (Exception e){e.printStackTrace();}

		try {
			if(youTubePlayer != null){
				youTubePlayer.play();
			}
		}catch (Exception e){e.printStackTrace();}

		//Background Image
		try{
			if(back_image != null)
			{
				Picasso.with(YoutubePlayer.this).load(back_image).into(new Target() {
					@Override
					public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
						player_background.setBackgroundDrawable(new BitmapDrawable(bitmap));
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

		//For Video Beacons
		try {

			final String VideoViewedUser_URL = URL_IP_ADDRESS + URL_YOUTUBEVIDEO + "?appId=" + URLEncoder.encode(APP_ID) + "&videoId=" + URLEncoder.encode(VIDEO_ID) + "&deviceId=" + URLEncoder.encode(deviceID.toString());
			System.out.println("VideoViewedUser_URL = "+VideoViewedUser_URL);
			JsonObjectRequest videoViewedRequest = new JsonObjectRequest(Request.Method.POST, VideoViewedUser_URL, null, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {

					try {
						String videoMessage = response.getString("allData");
						System.out.println("VideoViewedUser_URL videoMessage= "+videoMessage);
					}catch (Exception e){e.printStackTrace();}

				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError volleyError) {
					VolleyLog.d(TAG, "Spin Error: " + volleyError.getMessage());
					System.out.println("VideoViewedUser_URL errorMessage= "+volleyError.getMessage());
				}
			});
			RequestQueue videoviewedrequestQueue = Volley.newRequestQueue(YoutubePlayer.this);
			videoviewedrequestQueue.add(videoViewedRequest);

		}catch (Exception e){
			e.printStackTrace();
		}

	}
	/************************ End OnCreate() **************************************/

	@Override
	public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
		try {
			if (errorReason.isUserRecoverableError()) {
				errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
			} else {
				String error = String.format(getString(R.string.player_error), errorReason.toString());
				Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			}
		}catch (Exception e){e.printStackTrace();}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == RECOVERY_REQUEST) {
				// Retry initialization if user performed a recovery action
				getYouTubePlayerProvider().initialize(API_KEY, this);
			}
		}catch (Exception e){e.printStackTrace();}
	}

	protected Provider getYouTubePlayerProvider() {
		return youTubePlayerView;
	}

	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
		/** add listeners to YouTubePlayer instance **/
		try {
			youTubePlayer = player;
			player.setPlayerStateChangeListener(playerStateChangeListener);
			player.setPlaybackEventListener(playbackEventListener);
			/** Start buffering **/
			if (!wasRestored) {
				player.loadVideo(VIDEO_ID);
				player.cueVideo(VIDEO_ID);
			}else {
				Toast.makeText(this, R.string.player_error, Toast.LENGTH_LONG).show();
			}
		}catch (Exception e){e.printStackTrace();Toast.makeText(this, R.string.player_error, Toast.LENGTH_LONG).show();}
	}

	private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {

		@Override
		public void onBuffering(boolean arg0) {
		}

		@Override
		public void onPaused() {
		}

		@Override
		public void onPlaying() {
		}

		@Override
		public void onSeekTo(int arg0) {
		}

		@Override
		public void onStopped() {
		}

	};

	private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {

		@Override
		public void onAdStarted() {
		}

		@Override
		public void onError(ErrorReason arg0) {
		}

		@Override
		public void onLoaded(String arg0) {
		}

		@Override
		public void onLoading() {
		}

		@Override
		public void onVideoEnded() {
		}

		@Override
		public void onVideoStarted() {
		}
	};

	@Override
	protected void onDestroy() {
        try {
            if (youTubePlayer != null) {
                youTubePlayer.release();
            }
        }catch (Exception e){e.printStackTrace();}
		super.onDestroy();
		this.mWakeLock.release();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
        try {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            }
        }catch (Exception e){e.printStackTrace();}
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

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}

    private void noInternetPresent(){
        Intent intent = new Intent(YoutubePlayer.this,
                NoInternetScreen.class);
		intent.putExtra("flag",flag);
        startActivity(intent);
        YoutubePlayer.this.finish();
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
			System.out.println("After Add 1 ActivityNo from Intent>>> And set ActivityNo = " + ActivityNo + ";");
            try {
                trimCache(this);
                if (youTubePlayer != null) {
                    youTubePlayer.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intentplayer = new Intent(YoutubePlayer.this, CategoryScreen.class);
            intentplayer.putExtra("categoryID", CategoryHierarchyList);
            intentplayer.putExtra("showBanner", showBanner);
            intentplayer.putExtra("showInmobiAdWeightage", youtubeshowInmobiAdWeightage);
            intentplayer.putExtra("minIntervalInterstitial", showMinIntervalInterstitial);
            intentplayer.putExtra("showAdMovingInside", showAdMovingInside);
            intentplayer.putExtra("deviceID", deviceID);
            intentplayer.putExtra("back_img", back_image);
            intentplayer.putExtra("flag", flag);
            intentplayer.putExtra("ActivityNo", ActivityNo);
            startActivity(intentplayer);
            YoutubePlayer.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
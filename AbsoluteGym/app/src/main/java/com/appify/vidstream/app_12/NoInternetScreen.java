package com.appify.vidstream.app_12;

import android.R.anim;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NoInternetScreen extends AppCompatActivity {
	
	private ImageView netImg;
	private TextView netText,retry;
	private boolean flag;

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_internet_screen);

		//For ActionBar Background
		//getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_rect));

		try {
			flag = getIntent().getExtras().getBoolean("flag");
			Log.e("Get flag from Intent>>>","And set flag = "+ flag +";");
		}catch (Exception e){
			e.printStackTrace();
			Log.e("No Values For Intent>>>","And set flag = false;");
			flag = false;
		}

		try{
			CategorizationScreen categorizationScreen = new CategorizationScreen();
			categorizationScreen.finish();
			CategoryScreen categoryScreen = new CategoryScreen();
			categoryScreen.finish();
			FeedbackForm feedbackForm = new FeedbackForm();
			feedbackForm.finish();
			YoutubePlayer youtubePlayer = new YoutubePlayer();
			youtubePlayer.finish();
		}catch (Exception e){e.printStackTrace();}

        netImg = (ImageView) findViewById(R.id.netImage);
        netImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					Intent intent = new Intent(NoInternetScreen.this, CategorizationScreen.class);
					intent.putExtra("flag", flag);
					startActivity(intent);
					NoInternetScreen.this.finish();
				}catch (Exception e){e.printStackTrace();NoInternetScreen.this.finish();}
			}
		});
        netText = (TextView) findViewById(R.id.netText);
        netText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try{
					Intent intent = new Intent(NoInternetScreen.this,CategorizationScreen.class);
					intent.putExtra("flag",flag);
					startActivity(intent);
					NoInternetScreen.this.finish();
				}catch (Exception e){e.printStackTrace();NoInternetScreen.this.finish();}
			}
		});
        retry = (TextView) findViewById(R.id.sub_net_text);
        retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					Intent intent = new Intent(NoInternetScreen.this, CategorizationScreen.class);
					intent.putExtra("flag", flag);
					startActivity(intent);
					NoInternetScreen.this.finish();
				}catch (Exception e){e.printStackTrace();NoInternetScreen.this.finish();}
			}
		});
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_no_internet_screen, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		NoInternetScreen.this.finish();
	}

}

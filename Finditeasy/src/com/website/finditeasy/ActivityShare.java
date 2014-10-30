/**
    * File        : ActivityShare.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 01/19/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdView;
import com.website.finditeasy.R;
import com.website.finditeasy.ads.Ads;
import com.website.finditeasy.facebook.AsyncFacebookRunner;
import com.website.finditeasy.facebook.BaseRequestListener;
import com.website.finditeasy.facebook.DialogError;
import com.website.finditeasy.facebook.Facebook;
import com.website.finditeasy.facebook.FacebookError;
import com.website.finditeasy.facebook.SessionStore;
import com.website.finditeasy.facebook.Facebook.DialogListener;
import com.website.finditeasy.libraries.UserFunctions;
import com.website.finditeasy.twitter.TwitterApp;
import com.website.finditeasy.twitter.TwitterApp.TwDialogListener;
import com.website.finditeasy.utils.Utils;

public class ActivityShare extends SherlockFragmentActivity implements OnClickListener{
	
	// Create an instance of ActionBar
	ActionBar actionbar;
	
	// Declare object of AdView class
	AdView adView;
	
	// Declare objects for Facebook and TwitterApp class
	Facebook mFacebook;
	TwitterApp mTwitter;
	
	private boolean postToTwitter = false;
	private ProgressDialog mProgress;
	private Handler mRunOnUi = new Handler();
	private static final String[] PERMISSIONS = new String[] {"publish_stream", "read_stream", "offline_access"};
		
	// Create object of views
	EditText txtWhatDoYouThink;
	CheckBox chkFacebook, chkTwitter;
	Button btnOtherApps, btnShare;
	
	// Declare variables to store data
	private String mLocationId, mLocationName, mDesc, mImgLocation;
	private String mAppName;
	private String URLLocationPage;

	// Declare object of utils and userFunctions class
	Utils utils;
	UserFunctions userFunction;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		// Declare object of Utils and UserFunctions class
		utils = new Utils(this);
		userFunction 	= new UserFunctions();
		
		// Change actionbar title
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
		if ( 0 == titleId ) titleId = com.actionbarsherlock.R.id.abs__action_bar_title; 
					
		// Change the title color to white
		TextView txtActionbarTitle = (TextView)findViewById(titleId);
		txtActionbarTitle.setTextColor(getResources().getColor(R.color.actionbar_title_color));
		
		// Get ActionBar and set back button on actionbar
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		// Connect view objects and xml ids
		txtWhatDoYouThink 	= (EditText) findViewById(R.id.txtWhatDoYouThink);
		chkFacebook 		= (CheckBox) findViewById(R.id.chkFacebook);
		chkTwitter 			= (CheckBox) findViewById(R.id.chkTwitter);
		btnOtherApps 		= (Button) findViewById(R.id.btnOtherApps);
		btnShare 			= (Button) findViewById(R.id.btnShare);
		adView 				= (AdView) findViewById(R.id.adView);
		
		chkFacebook.setOnClickListener(this);
		chkTwitter.setOnClickListener(this);
		btnOtherApps.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		
		// Get intent Data from ActivityDetailPlace
		Intent i = getIntent();
		mLocationId 	= i.getStringExtra(utils.EXTRA_LOCATION_ID);
		mLocationName 	= i.getStringExtra(utils.EXTRA_LOCATION_NAME);
		mDesc 			= i.getStringExtra(utils.EXTRA_LOCATION_DESC);
		mImgLocation 	= i.getStringExtra(utils.EXTRA_LOCATION_IMG);
		
		URLLocationPage = userFunction.URLAdmin+
				userFunction.service_view_location+
				userFunction.key_location_id+
				"="+mLocationId;
		mAppName = getString(R.string.app_name);
		

		// Set objects of Facebook and TwitterAp class
		mProgress = new ProgressDialog(this);
        mFacebook = new Facebook(getString(R.string.facebook_app_id));
        mTwitter  = new TwitterApp(this, 
        		getString(R.string.twitter_consumer_key),
        		getString(R.string.twitter_secret_key));
        
        // Restore facebook session
        SessionStore.restore(mFacebook, this);
		
        // Checking facebook session
        if (mFacebook.isSessionValid()) {
			chkFacebook.setChecked(true);
			String name = SessionStore.getName(this);
		}
        
        // Checking twitter token
        mTwitter.setListener(mTwLoginDialogListener);
		
		if (mTwitter.hasAccessToken()) {
			chkTwitter.setChecked(true);
			
		}
		
 		// load ads
        Ads.loadAds(adView);
        
	}
	
	// Check the value of review, if more than 110 show toast message
	private void postReview(String review) {
		Toast.makeText(this, getString(R.string.post_review), Toast.LENGTH_SHORT).show();
		
		if(review.length()>110){
			Toast.makeText(this, getString(R.string.post_limit), Toast.LENGTH_SHORT).show();
		}else{
			postToTwitter = true;
			
		}	
	}
	
	// Method to check if twitter token not available, authorize user
	private void onTwitterClick() {
		if (!mTwitter.hasAccessToken()) {
			chkTwitter.setChecked(false);
			mTwitter.authorize();
		}
		
	}
	
	// Method to post status to twitter
	private void postToTwitter(final String review) {
		new Thread() {
			@Override
			public void run() {
				int what = 0;
				
				try {
					mTwitter.updateStatus(review+URLLocationPage);
					
				} catch (Exception e) {
					what = 1;
				}
				
				mHandlerTwitter.sendMessage(mHandlerTwitter.obtainMessage(what));
			}
		}.start();
	}

	// Event handler for reading twitter posting result
	private Handler mHandlerTwitter = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String text = (msg.what == 0) ? getString(R.string.post_to_twitter) : getString(R.string.post_to_twitter_failed);
			
			Toast.makeText(ActivityShare.this, text, Toast.LENGTH_SHORT).show();
			
			if(!(chkFacebook.isChecked() && chkTwitter.isChecked())) {
				finish();
			}
		}
	};
	
	// Event listener to read twitter username from twitter dialog
	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		
		public void onComplete(String value) {
			String username = mTwitter.getUsername();
			username		= (username.equals("")) ? getString(R.string.no_name) : username;
		
			chkTwitter.setChecked(true);
			
			Toast.makeText(ActivityShare.this, getString(R.string.connect_twitter)+" "+ username, Toast.LENGTH_LONG).show();
		}
		
		public void onError(String value) {
			chkTwitter.setChecked(false);
			
			Toast.makeText(ActivityShare.this, getString(R.string.connect_twitter_failed), Toast.LENGTH_LONG).show();
		}
	};
	
	// Method to post status to facebook
	private void postToFacebook(String review) {	
		mProgress.setMessage("Posting ...");
		mProgress.show();
		
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
		
		Bundle params = new Bundle();
    		
		params.putString("message", review);
		params.putString("name", mLocationName);
		params.putString("caption", mAppName);
		params.putString("link", URLLocationPage);
		params.putString("description", mDesc);
		params.putString("picture", userFunction.URLAdmin+mImgLocation);
		
		
		mAsyncFbRunner.request("me/feed", params, "POST", new WallPostListener());
	}

	// Event listener to read facebook posting result
	private final class WallPostListener extends BaseRequestListener {
        public void onComplete(final String response) {
        	mRunOnUi.post(new Runnable() {
        		
        		public void run() {
        			mProgress.cancel();
        			
        			Toast.makeText(ActivityShare.this, getString(R.string.post_to_facebook), Toast.LENGTH_SHORT).show();
        			finish();
        		}
        	});
        }

		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
    }
	
	// Method to check facebook session, if not valid, authorize user
	private void onFacebookClick() {
		if (!mFacebook.isSessionValid()) {
			chkFacebook.setChecked(false);
			mFacebook.authorize(this, PERMISSIONS, -1, new FbLoginDialogListener());
		}
	}
    
	// Event listener to get facebook name from facebook dialog
    private final class FbLoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionStore.save(mFacebook, ActivityShare.this);
           
            chkFacebook.setChecked(true);
			 
            getFbName();
        }

        public void onFacebookError(FacebookError error) {
           Toast.makeText(ActivityShare.this, "Facebook connection failed", Toast.LENGTH_SHORT).show();
           
           chkFacebook.setChecked(false);
        }
        
        public void onError(DialogError error) {
        	Toast.makeText(ActivityShare.this, "Facebook connection failed", Toast.LENGTH_SHORT).show(); 
        	
        	chkFacebook.setChecked(false);
        }

        public void onCancel() {
        	chkFacebook.setChecked(false);
        }
    }
    
    // Method to get facebook name
	private void getFbName() {
		mProgress.setMessage("Finalizing ...");
		mProgress.show();
		
		new Thread() {
			@Override
			public void run() {
		        String name = "";
		        int what = 1;
		        
		        try {
		        	String me = mFacebook.request("me");
		        	
		        	JSONObject jsonObj = (JSONObject) new JSONTokener(me).nextValue();
		        	name = jsonObj.getString("name");
		        	what = 0;
		        } catch (Exception ex) {
		        	ex.printStackTrace();
		        }
		        
		        mFbHandler.sendMessage(mFbHandler.obtainMessage(what, name));
			}
		}.start();
	}
	
	// Event handler to read facebook authentication
	private Handler mFbHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();
			
			if (msg.what == 0) {
				String username = (String) msg.obj;
		        username = (username.equals("")) ? getString(R.string.no_name) : username;
		            
		        SessionStore.saveName(username, ActivityShare.this);
		        
		         
		        Toast.makeText(ActivityShare.this, getString(R.string.connect_facebook)+" "+ username, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ActivityShare.this, getString(R.string.connect_facebook_2), Toast.LENGTH_SHORT).show();
			}
		}
	};

	
	// Listener for option menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		// previous page or exit
	    		finish();
	    		return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	// Listener on Click
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnShare:
			
			// Get text from edittext and store to string variable
			String review = txtWhatDoYouThink.getText().toString();
			
			// Check to value of review
			if(review.equals("")){
				review = mLocationName+" at "+getString(R.string.app_name) +" ";
			}else{
				review +=" - "+mLocationName+" at "+getString(R.string.app_name) +" ";
			}
			
			// Check the checkbox and post to both facebook and twitter or one of them
			if(chkFacebook.isChecked() && chkTwitter.isChecked()) {
				postReview(review);
				if (postToTwitter) {
					postToFacebook(review);
					postToTwitter(review);
				}
			}else if(chkFacebook.isChecked() && !chkTwitter.isChecked()){
				postToFacebook(review);
			}else if(!chkFacebook.isChecked() && chkTwitter.isChecked()){
				postReview(review);
				if (postToTwitter) postToTwitter(review);
			}else{
				Toast.makeText(ActivityShare.this, getString(R.string.select_share), Toast.LENGTH_SHORT).show();
			}
			
			break;

		case R.id.btnOtherApps:
			// Share this app to other application
			Intent iShare = new Intent(Intent.ACTION_SEND);
			iShare.setType("text/plain");
			iShare.putExtra(Intent.EXTRA_SUBJECT, mLocationName);
			iShare.putExtra(Intent.EXTRA_TEXT, "\nDetail: "+URLLocationPage);
			startActivity(Intent.createChooser(iShare, getString(R.string.share_via)));
			break;
			
		case R.id.chkFacebook:
			// When checkbox facebook click
			onFacebookClick();
			break;
		
		case R.id.chkTwitter:
			// When checkbox twitter click
			onTwitterClick();
			break;
		default:
			break;
		}
		
	}

}

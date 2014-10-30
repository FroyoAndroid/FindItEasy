/**
    * File        : ActivityDetailPlace.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 01/19/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.website.finditeasy.R;
import com.website.finditeasy.ads.Ads;
import com.website.finditeasy.lazylist.ImageLoader;
import com.website.finditeasy.libraries.UserFunctions;
import com.website.finditeasy.utils.Utils;

public class ActivityDetailPlace extends SherlockFragmentActivity implements OnClickListener{
	
	// Create an instance of ActionBar
	ActionBar actionbar;
	
	// Declare object of AdView class
	AdView adView;
	
	// Declare object of userFunction, JSONObject, and Utils class
	UserFunctions userFunction;
	JSONObject json;
	Utils utils;
	ImageLoader imageLoader;
	WebView webDesc;
	
	// Declare variables to store data
	private String mGetLocationId;
	private String mLocationName;
	private String mAddress;
	private String mPhone;
	private String mWebsite;
	private String mDesc;
	private String mIcMarkerLocation;
	private String mImgLocation;
	private Double mDblLatitude;
	private Double mDblLongitude;
	private int mSelectedMapType;
	private float mSelectedMapZoom;

	// Declare view objects
	TextView lblPlaceName, lblAddress, lblPhone, lblWebsite;
	ImageView imgThumbnail;
	LinearLayout lytMedia, lytRetry, lytDetail;
	ProgressBar prgLoading;
	Button btnRetry;
	ImageButton imgBtnShare, imgBtnDirection;

	// Declare object to handle map
    GoogleMap map;
	SupportMapFragment fragMap;
	GoogleMapOptions options = new GoogleMapOptions();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);
        
        // Declare object of userFunction and utils class
		userFunction 	 = new UserFunctions();
		utils			 = new Utils(this);
		imageLoader 	 = new ImageLoader(this);
		
		// Get intent Data from ActivityHome, ActivityPlaceAroundYou, ActivityPlaceList OR ActivitySearch
        Intent i = getIntent();
        mGetLocationId = i.getStringExtra(utils.EXTRA_LOCATION_ID);
        
    	mSelectedMapType = utils.loadPreferences(getString(R.string.preferences_type));
    	mSelectedMapZoom = utils.loadPreferences(getString(R.string.preferences_zoom));
    	
    	// Condition if Map Zoom is 0
    	if(mSelectedMapZoom == 0){
    		mSelectedMapZoom = 15;
    	}
        
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
		lblPlaceName	= (TextView) findViewById(R.id.lblPlaceName);
		lblAddress		= (TextView) findViewById(R.id.lblAddress);
		lblPhone		= (TextView) findViewById(R.id.lblPhone);
		lblWebsite		= (TextView) findViewById(R.id.lblWebsite);
		lytMedia		= (LinearLayout) findViewById(R.id.lytMedia);
		lytRetry		= (LinearLayout) findViewById(R.id.lytRetry);
		lytDetail		= (LinearLayout) findViewById(R.id.lytDetail);
		prgLoading		= (ProgressBar) findViewById(R.id.prgLoading);
		btnRetry		= (Button) findViewById(R.id.btnRetry);
		imgBtnShare 	= (ImageButton) findViewById(R.id.imgShare);
		imgBtnDirection = (ImageButton) findViewById(R.id.imgDirection);
		imgThumbnail 	= (ImageView) findViewById(R.id.imgLocation);
		fragMap 		= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		adView 			= (AdView)this.findViewById(R.id.adView);
		webDesc 		= (WebView) findViewById(R.id.webDesc);
		
		lblWebsite.setOnClickListener(this);
		imgBtnShare.setOnClickListener(this);
		imgBtnDirection.setOnClickListener(this);
     	btnRetry.setOnClickListener(this);
     	
		// Get screen device width and height
        DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int wPix = dm.widthPixels;
		int hPix = wPix / 2 + 50;
		
		// Change cover image width and height
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wPix, hPix);
        lytMedia.setLayoutParams(lp);
		
        // Call Method setUpMapIfNeeded
        setUpMapIfNeeded();
        
        // Call asynctask class
     	new getDataAsync().execute();
     	
     	// load ads
        Ads.loadAds(adView);
     	        
    }
		
	// AsyncTask to Get Data from Server and put it on View Object
	public class getDataAsync extends AsyncTask<Void, Void, Void>{

		ProgressDialog progress;
    	
    	@Override
		 protected void onPreExecute() {
    		/////////////////
		  // TODO Auto-generated method stub
    		
    		// Show progress dialog when fetching data from database
    		progress= ProgressDialog.show(
    				ActivityDetailPlace.this, 
    				"", 
    				getString(R.string.loading_data), 
    				true);
    		
    	}

    	@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
    		
    		// Method to get Data from Server
			getDataFromServer();
			return null;
		}
    	
    	@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			progress.dismiss();
			
			if(json != null) {
				// Display Data
				lytDetail.setVisibility(View.VISIBLE);
				lytRetry.setVisibility(View.GONE);
				lblPlaceName.setText(mLocationName);
				lblAddress.setText(mAddress);
				lblPhone.setText(mPhone);
				lblWebsite.setText(mWebsite);
				
				// Load data from url 
				webDesc.loadUrl(userFunction.varLoadURL+mGetLocationId);
	
				// Set Image thumbnail from Server
				//setImageInThread(imgThumbnail, userFunction.URLAdmin+mImgLocation);
				imageLoader.DisplayImage(userFunction.URLAdmin+mImgLocation, imgThumbnail);
				
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDblLatitude, mDblLongitude), mSelectedMapZoom));
		    	
				// Call AsynTask class
		    	new loadMarkerFromServer().execute();
		    	
			} else {
				lytDetail.setVisibility(View.GONE);
				lytRetry.setVisibility(View.VISIBLE);
            	Toast.makeText(ActivityDetailPlace.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();

			}

		}
		
	}
	
	// AsyncTask class to load marker from server in UI background
	public class loadMarkerFromServer extends AsyncTask<Void, Void, Void>{
		Bitmap bmImg;
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		    Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
		    Canvas canvas = new Canvas(bmp);

		    // Paint defines the text color,
		    // Stroke width, size
		    Paint color = new Paint();
		    color.setTextSize(35);
		    color.setColor(Color.BLACK);
		    
		    URL url;
			try {
				url = new URL(userFunction.URLAdmin+mIcMarkerLocation);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setDoInput(true);
	            conn.connect();
	            InputStream is = conn.getInputStream();
	            bmImg = BitmapFactory.decodeStream(is);

			    //modify canvas
			    canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
			            R.drawable.ic_launcher), 0,0, color);
			    canvas.drawText("User Name!", 30, 40, color);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);			

		    // Add marker to Map
		    map.addMarker(new MarkerOptions().position(new LatLng(mDblLatitude, mDblLongitude))
		    .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
		    .anchor(0.5f, 1)); //Specifies the anchor to be
		               //at a particular point in the marker image.
		}
	
	}

	// Method to check map
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
        	map = fragMap.getMap();
        	
             if (map != null) setUpMap();
                 
        }
    }
	
	// Method to Set Map Type Selected
    private void setUpMap() {
    	// Call previous map type setting
    	setMapType(mSelectedMapType);
	}
    
    // Method to set map type based on dialog map type setting
 	void setMapType(int position){
 		switch(position){
 		case 0:
 			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
 			break;
 		case 1:
 			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
 			break;
 		case 2:
 			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
 			break;
 		case 3:
 			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
 			break;
 		}
 	}
 	
 	// Method to get Data from Server
	public void getDataFromServer(){
	       
        try {
            json = userFunction.placeDetailInfo(mGetLocationId);
            
            if(json != null) {
	            JSONArray dataLocationArray = json.getJSONArray(userFunction.array_place_detail);
	            JSONObject locationObject 	= dataLocationArray.getJSONObject(0);
	            
	            // Store Data to Variables
	            mLocationName 	 = locationObject.getString(userFunction.key_location_name);
	            mAddress 		 = locationObject.getString(userFunction.key_location_address);
	            mPhone 			 = locationObject.getString(userFunction.key_location_phone);
	            mWebsite 		 = locationObject.getString(userFunction.key_location_website);
	            mDesc 			 = locationObject.getString(userFunction.key_location_desc);
	            mDblLatitude 	 = locationObject.getDouble(userFunction.key_location_lat);
	            mDblLongitude 	 = locationObject.getDouble(userFunction.key_location_lng);
	            mImgLocation 	 = locationObject.getString(userFunction.key_location_image);
	            mIcMarkerLocation= locationObject.getString(userFunction.key_category_marker);
				
            }      
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }      
    }
	
	public void setImageInThread(final ImageView imageView, final String url) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Drawable result = (Drawable) message.obj;
                imageView.setImageDrawable(result);
            }
        };

        new Thread() {
            @Override
            public void run() {
                Drawable drawable = LoadImageFromWebOperations(url);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        }.start();
    }
    
    private Drawable LoadImageFromWebOperations(String url) {
    	try {
    	    InputStream is = (InputStream) new URL(url).getContent();
    	    Drawable d = Drawable.createFromStream(is, "image.png");
    	    return d;
    	} catch (Exception e) {
    	    System.out.println("Exc=" + e);
    	    return null;
    	}
    } 
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    
	// Listener for option menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		
	    		// Previous page or exit
	    		finish();
	    		return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// Listener for on click
	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.imgShare:
			 // Share google play link of this app to other app such as facebook, twitter etc
			 i = new Intent(this, ActivityShare.class);
			 i.putExtra(utils.EXTRA_LOCATION_ID, mGetLocationId);
			 i.putExtra(utils.EXTRA_LOCATION_NAME, mLocationName);
			 i.putExtra(utils.EXTRA_LOCATION_DESC, mDesc);
			 i.putExtra(utils.EXTRA_LOCATION_IMG, mImgLocation);
			 startActivity(i); 
			 break;
			 
		case R.id.btnRetry:
			// Retry to get Data
			new getDataAsync().execute();
			break;
			
		case R.id.imgDirection:
			// Open ActivityDirection to get Direction
			i = new Intent(this, ActivityDirection.class);
			i.putExtra(utils.EXTRA_DEST_LAT, mDblLatitude);
			i.putExtra(utils.EXTRA_DEST_LNG, mDblLongitude);
			i.putExtra(utils.EXTRA_DEST_MARKER, mIcMarkerLocation);
			startActivity(i);
			break;	
		
		case R.id.lblWebsite:
			// Open Built-in browser
			i = new Intent(this, ActivityBrowser.class);
			i.putExtra(utils.EXTRA_LOCATION_URL, mWebsite);
			i.putExtra(utils.EXTRA_LOCATION_NAME, mLocationName);
			startActivity(i);
			
		default:
			break;
		}
	}
}
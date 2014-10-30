/**
    * File        : ActivityPlaceAroundYou.java
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.website.finditeasy.R;
import com.website.finditeasy.ads.Ads;
import com.website.finditeasy.lazylist.ImageLoader;
import com.website.finditeasy.libraries.UserFunctions;
import com.website.finditeasy.utils.LocationUtils;
import com.website.finditeasy.utils.Utils;

public class ActivityPlaceAroundYou extends SherlockFragmentActivity implements 
	OnInfoWindowClickListener, LocationListener, LocationSource,
	GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener{

	// Create an instance of ActionBar
	ActionBar actionbar;
	
	// Declare object of AdView class
	AdView adView;
	
	// Declare object to handle map
    GoogleMap map;
	GoogleMapOptions options = new GoogleMapOptions();
	OnLocationChangedListener mListener;
	LocationManager locationManager;
	
	// Declare view objects
	SupportMapFragment fragMap;
	TextView lblPosition;
	ProgressDialog dialog;

    private int mSelectedMapType;
    private float mSelectedMapZoom;
	
	// Declare object of Utils, ImageLoader, JSONObject and UserFuntions class
	Utils utils;
	ImageLoader imageLoader;
	UserFunctions userFunction;
	JSONObject json;
	
	// A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
	
	// Declare variable Long lat
    private double mUserLat;
    private double mUserLng;
    private int mLengthData;
	private String mSelectedLocationId;
	
    // Declare variable array to store data
    private String mLocationName[];
    private String mAddress[];
    private double mDblLatitude[];
    private double mDblLongitude[];
    private String mLocationId[];
    private String mIcMarkerLocation[];
    private String mCategoryId[];
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_you);
        
        // connect view objects and xml ids
  		adView = (AdView)this.findViewById(R.id.adView1);

        // Change actionbar title
     	int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
     	if ( 0 == titleId ) titleId = com.actionbarsherlock.R.id.abs__action_bar_title; 
     	
     	// Change the title color to white
     	TextView txtActionbarTitle = (TextView)findViewById(titleId);
     	txtActionbarTitle.setTextColor(getResources().getColor(R.color.actionbar_title_color));
     		
     	// Get ActionBar and set back button on actionbar
     	actionbar = getSupportActionBar();
     	actionbar.setDisplayHomeAsUpEnabled(true);

        // Connect view objects and ids on xml
        fragMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        lblPosition = (TextView) findViewById(R.id.lblPosition);
        
        // Declare object of Utils, UserFunction dan ImageLaoder class
        utils 		 = new Utils(this);
    	userFunction = new UserFunctions();
    	imageLoader  = new ImageLoader(this);
    	
    	// Store data from preference
		mSelectedMapType = utils.loadPreferences(getString(R.string.preferences_type));
    	mSelectedMapZoom = utils.loadPreferences(getString(R.string.preferences_zoom));
    	
    	// Condition if Map Zoom is 0
    	if(mSelectedMapZoom == 0){
    		mSelectedMapZoom = 15;
    	}
    	
        // Checking internet connection. if not enable, show toast alert
		if(!utils.isNetworkAvailable()){
			Toast.makeText(this, getString(R.string.internet_alert), Toast.LENGTH_SHORT).show();
		}

        // Call this method to set up map
        setUpMapIfNeeded();
        
        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        // Set the update interval
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        /*
         * create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        
        // load ads
        Ads.loadAds(adView);
        
    }
    
	// Method to show gps alert dialog
    private void createGpsDisabledAlert(int FLAG){
    	final int flag = FLAG;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setTitle(R.string.notice);
		
		// Set dialog message
		builder.setMessage(getString(R.string.direction_alert));
		
		// Set positive button
		builder.setPositiveButton(getString(R.string.settings),
			new DialogInterface.OnClickListener(){
			    public void onClick(DialogInterface dialog, int id){
			      	  showGpsOptions(flag);
			    }
			});
		
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener(){
				    public void onClick(DialogInterface dialog, int id){
				    	finish();
				    }
				});
		
		// Set negative button
		builder.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// Show dialog
		AlertDialog alert = builder.create();
		alert.show();
	}
	
    // Method to display Location Settings page
	private void showGpsOptions(int result){
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(gpsOptionsIntent, result);
	}
    	
    /*
     * handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * LocationUpdateRemover and LocationUpdateRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	Log.d(LocationUtils.APPTAG, "onActivityResult");

        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:

                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.resolved));
                        Log.d(LocationUtils.APPTAG, getString(R.string.connected));
                        Log.d(LocationUtils.APPTAG, getString(R.string.resolved));
                    break;

                    // If any other result was returned by Google Play services
                    default:
                        // Log the result
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));
                        Log.d(LocationUtils.APPTAG, getString(R.string.disconnected));
                        Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

                    break;
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(LocationUtils.APPTAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }

    /**
     * verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {
    	Log.d(LocationUtils.APPTAG, "servicesConnected");

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }
    

    /**
     * Get the address of the current location, using reverse geocoding. This only works if
     * a geocoding service is available.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    // For Eclipse with ADT, suppress warnings about Geocoder.isPresent()
    @SuppressLint("NewApi")
    public void getAddress(Location location) {
    	Log.d(LocationUtils.APPTAG, "getAddress");

        // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
            // No geocoder is present. Issue an error message
            Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }

            // Start the background task
        (new ActivityPlaceAroundYou.getAddressTask(this)).execute(location);

    }

    /**
     * sends a request to start location updates
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void startUpdates() {
    	Log.d(LocationUtils.APPTAG, "startUpdates");

        if (servicesConnected()) {
            startPeriodicUpdates();
        }
    }

    /**
     * sends a request to remove location updates
     * request them.
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void stopUpdates() {
    	Log.d(LocationUtils.APPTAG, "stopUpdates");

        if (servicesConnected()) {
            stopPeriodicUpdates();
        }
    }

    /*
     * called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gpsIsEnabled){
        	startUpdates();
        }else{
			createGpsDisabledAlert(1);
		}

    }

    /*
     * called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Log.d(LocationUtils.APPTAG+":onDisconnected",getString(R.string.disconnected));
    }

    /*
     * called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    	Log.d(LocationUtils.APPTAG, "onConnectionFailed");

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }
    
    /**
     * in response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        Log.d(LocationUtils.APPTAG+":startPeriodicUpdates",getString(R.string.location_requested));
    }

    /**
     * in response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
        Log.d(LocationUtils.APPTAG+":stopPeriodicUpdates",getString(R.string.location_updates_stopped));
    }

    /**
     * show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }

    /**
     * define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * this method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    	
	// Method to check map
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
        	map = fragMap.getMap();
        	
             if (map != null) 
             {
                 setUpMap();
             }
         
        }
    }
	
    
	// Method to set up map
    void setUpMap(){
    	setMapType(mSelectedMapType);
    	
    	// Enable compass, zoom control, rotate and tilt gesture to the map
		options.compassEnabled(true);
		options.rotateGesturesEnabled(true);
		options.tiltGesturesEnabled(true);
		options.zoomControlsEnabled(true);
		SupportMapFragment.newInstance(options);	
		
		map.setLocationSource(this);
		map.setMyLocationEnabled(true);
		map.setOnInfoWindowClickListener(this);
		
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
    
	// Use to show detail page when info window on the marker clicked
	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
		
		String title = marker.getTitle();
		for(int i=0;i<mLocationId.length;i++){
			if(mLocationName[i].equals(title)){
				mSelectedLocationId = mLocationId[i];
				break;
			}
		}
		Log.d("ActivityPlaceAroundYou", "selectedLocationId: "+mSelectedLocationId);
		Intent i = new Intent(this, ActivityDetailPlace.class);
		i.putExtra(utils.EXTRA_LOCATION_ID, mSelectedLocationId);
		startActivity(i);
		
	}

	// Checking user location
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
			Log.d(LocationUtils.APPTAG, "onLocationChanged");
			
			if(mListener != null){
				mListener.onLocationChanged(location);
						
				getAddress(location);
				mUserLat = (double) location.getLatitude();
				mUserLng = (double) location.getLongitude();

				map.setMyLocationEnabled(true);
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),mSelectedMapZoom));
			
				stopPeriodicUpdates();
				map.clear();
				new getLocationTask().execute();
			}
		
	}

    
	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
		
	}
	
	// Method to get Data from Server
	public void getDataFromServer(){
	       
        try {
            json = userFunction.aroundYou(mUserLat, mUserLng);
            if(json != null){
	            JSONArray dataLocationArray = json.getJSONArray(userFunction.array_around_you);
	            mLengthData  	 = dataLocationArray.length();
	            mLocationId  	 = new String[mLengthData];
	            mLocationName	 = new String[mLengthData];
	            mAddress 	 	 = new String[mLengthData];
	            mDblLatitude 	 = new double[mLengthData];
	            mDblLongitude	 = new double[mLengthData];
	            mIcMarkerLocation= new String[mLengthData];
	            mCategoryId		 = new String[mLengthData];
	
				for (int i = 0; i < mLengthData; i++) {
					JSONObject locationObject = dataLocationArray.getJSONObject(i);
					mLocationId[i] 		= locationObject.getString(userFunction.key_location_id);
					mLocationName[i] 	= locationObject.getString(userFunction.key_location_name);
					mAddress[i] 		= locationObject.getString(userFunction.key_location_address);
					mDblLatitude[i]		= locationObject.getDouble(userFunction.key_location_lat);
					mDblLongitude[i] 	= locationObject.getDouble(userFunction.key_location_lng);
					mIcMarkerLocation[i]= locationObject.getString(userFunction.key_category_marker);
					mCategoryId[i] 		= locationObject.getString(userFunction.key_category_id);
	            }
               
            }
                               
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }      
    }
	
	/*
     * called when the Activity is no longer visible at all.
     * stop updates and disconnect.
     */
    @Override
    public void onStop() {
    	Log.d(LocationUtils.APPTAG, "onStop");
    	
        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
    }

    /*
     * called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {
    	Log.d(LocationUtils.APPTAG, "onStart");

        super.onStart();

        /*
         * connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();

    }
    /*
     * Called when the system detects that this Activity is now visible.
     */
    @Override
    public void onResume() {
    	Log.d(LocationUtils.APPTAG, "onResume");
    	
        super.onResume();
		setUpMapIfNeeded();
    }

    /**
     * an AsyncTask that calls getFromLocation() in the background.
     * The class uses the following generic types:
     * Location - A {@link android.location.Location} object containing the current location,
     *            passed as the input parameter to doInBackground()
     * Void     - indicates that progress units are not used by this subclass
     * String   - An address passed to onPostExecute()
     */
    protected class getAddressTask extends AsyncTask<Location, Void, String> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public getAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /**
         * get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Location... params) {
            /*
             * get a new geocoding service instance, set for localized addresses. This example uses
             * android.location.Geocoder, but other geocoders that conform to address standards
             * can also be used.
             */
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

            // Get the current location from the input parameter list
            Location location = params[0];

            // Create a list to contain the result address
            List <Address> addresses = null;

            // Try to get an address for the current location. Catch IO or network problems.
            try {

                /*
                 * call the synchronous getFromLocation() method with the latitude and
                 * longitude of the current location. Return at most 1 address.
                 */
                addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1
                );

                // Catch network or other I/O problems.
                } catch (IOException exception1) {

                    // Log an error and return an error message
                    Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

                    // Print the stack trace
                    exception1.printStackTrace();

                    // Return an error message
                    return (getString(R.string.cannot_get_address));

                // Catch incorrect latitude or longitude values
                } catch (IllegalArgumentException exception2) {

                    // Construct a message containing the invalid arguments
                    String errorString = getString(
                            R.string.illegal_argument_exception,
                            location.getLatitude(),
                            location.getLongitude()
                    );
                    // Log the error and print the stack trace
                    Log.e(LocationUtils.APPTAG, errorString);
                    exception2.printStackTrace();

                    //
                    return errorString;
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {

                    // Get the first address
                    Address address = addresses.get(0);

                    // Format the first line of address
                    String addressText = getString(R.string.address_output_string,

                            // If there's a street address, add it
                            address.getMaxAddressLineIndex() > 0 ?
                                    address.getAddressLine(0) : "",

                            // Locality is usually a city
                            address.getLocality(),

                            // The country of the address
                            address.getCountryName()
                    );

                    return addressText;

                // If there aren't any addresses, post a message
                } else {
                  return getString(R.string.no_address_found);
                }
        }

        /**
         * A method that's called once doInBackground() completes. Set the text of the
         * UI element that displays the address. This method runs on the UI thread.
         */
        @Override
        protected void onPostExecute(String address) {

            // Set the address in the UI
        	lblPosition.setText(address);
        	
    	}
        
    }
    
	// Asynctask class to handle getting location in background
	public class getLocationTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		 protected void onPreExecute() {
		  // TODO Auto-generated method stub
			// Show progress dialog when start
			dialog = ProgressDialog.show(ActivityPlaceAroundYou.this,"", getString(R.string.loading_data), true);
			
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// Call method to get location data form database
			getDataFromServer();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			if(json != null){
				new loadMarkerFromServer().execute();
			}else{
				Toast.makeText(ActivityPlaceAroundYou.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		}
	}
	
	public class loadMarkerFromServer extends AsyncTask<Void, Void, Void>{
		ArrayList<Bitmap> bmImg = new ArrayList<Bitmap>();
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.setMessage(getString(R.string.getting_marker));
		}
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
		    
		    for(int i=0;i<mLengthData;i++){
	    		URL url;
				try {
					url = new URL(userFunction.URLAdmin+mIcMarkerLocation[i]);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            conn.setDoInput(true);
		            conn.connect();
		            InputStream is = conn.getInputStream();
		            bmImg.add(BitmapFactory.decodeStream(is));
	
				    // Modify canvas
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
	    	}
			    
		    
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			for(int i=0;i<mLengthData;i++){
				// Add marker to Map
			    map.addMarker(new MarkerOptions().position(new LatLng(mDblLatitude[i], mDblLongitude[i]))
			    .icon(BitmapDescriptorFactory.fromBitmap(bmImg.get(i)))
			    .anchor(0.5f, 1)
			    .title(mLocationName[i])
			    .snippet(mAddress[i])); //Specifies the anchor to be
			               // At a particular point in the marker image.
			
			}
			
			// Close progress dialog when finish getting data and show data to marker if available
			dialog.dismiss();
		}
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
	

}
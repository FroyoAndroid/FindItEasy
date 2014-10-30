package com.website.finditeasy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	
	// Declare object of Contecxt class
	Context ctx;
	
	// Declare Variables for Intent Put Extra
	public final String EXTRA_LOCATION_ID 	= "locationId";
	public final String EXTRA_LOCATION_NAME	= "locationName";
	public final String EXTRA_LOCATION_URL	= "locationUrl";
	public final String EXTRA_LOCATION_DESC	= "locationDesc";
	public final String EXTRA_LOCATION_IMG	= "locationImg";	
	public final String EXTRA_DEST_LAT		= "destLatitude";
	public final String EXTRA_DEST_LNG		= "destLongitude";
	public final String EXTRA_DEST_MARKER	= "destMarker";
	public final String EXTRA_CATEGORY_ID	= "categoryId";
	public final String EXTRA_CATEGORY_NAME	= "categoryName";
	
	// Declare variables for setting preference
	public final String UTILS_OVERLAY = "utilOverlay";
	public String UTILS_PARAM_NOTIF	= "0";
	public final String UTILS_NOTIF = "notif";
	public Utils(Context c){
		ctx = c;
	}
	
	// Method to check internet connection
	public boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
		
	// Method to save map type setting to SharedPreferences
	public void savePreferences(String param, int value){
	    SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_data", 0);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putInt(param, value);
	    editor.commit();
	}
	
	// Method to save map type setting to SharedPreferences
	public void saveString(String param, String value){
	    SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_data1", 0);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putString(param, value);
	    editor.commit();
	}
	
	// Method to load map type setting 
	public int loadPreferences(String param){
	    SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_data", 0);
	    int selectedPosition = sharedPreferences.getInt(param, 0);
	    
	    return selectedPosition;
	}
	
	// Method to load map type setting 
	public String loadString(String param){
	    SharedPreferences sharedPreferences = ctx.getSharedPreferences("user_data1", 0);
	    String selectedPosition = sharedPreferences.getString(param, "unknown");
	    
	    return selectedPosition;
	}
}

package com.website.finditeasy.ads;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Ads {
	public static void loadAds(AdView ads){
		
        
		/**
      	 * code below is used to test admob on device during development process.
      	 */
		AdRequest adRequest = new AdRequest.Builder().addTestDevice("YOUR_DEVICE_ID").build();
		//AdRequest adRequest = new AdRequest.Builder().build();
		/**
      	 * code below is used to publish admob when the app launched.
      	 * remove the comment tag below and delete block of code 
      	 * that used for testing admob.
      	 */
		//AdRequest adRequest = new AdRequest.Builder().build();
		
		ads.loadAd(adRequest);
      	
      	/**
      	 * the end of admob code
      	 */
	}
}

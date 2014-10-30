/**
    * File        : ActivityPlaceList.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 01/19/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdView;
import com.website.finditeasy.R;
import com.website.finditeasy.ads.Ads;
import com.website.finditeasy.fragments.FragmentPlaceList;
import com.website.finditeasy.utils.Utils;

public class ActivityPlaceList extends SherlockFragmentActivity implements FragmentPlaceList.OnListSelectedListener{
	
	// Create an instance of ActionBar
	ActionBar actionbar;
	
	// Declare object of AdView class
	AdView adView;
	
	// Declare object of Utils class
	Utils utils;
	
	// Declare variable to store data
	private String mCategoryId, mCategoryName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// Declare object of Utils class
		utils = new Utils(this);
		
		// connect view objects and xml ids
		adView = (AdView)this.findViewById(R.id.adView);
				
		// Get intent Data from ActivityCategory
        Intent i = getIntent();
        mCategoryId   = i.getStringExtra(utils.EXTRA_CATEGORY_ID);
        mCategoryName = i.getStringExtra(utils.EXTRA_CATEGORY_NAME);
        
     	// Change actionbar title
     	int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
     	if ( 0 == titleId ) titleId = com.actionbarsherlock.R.id.abs__action_bar_title; 
     			
     	// Change the title color to white
     	TextView txtActionbarTitle = (TextView)findViewById(titleId);
     	txtActionbarTitle.setTextColor(getResources().getColor(R.color.actionbar_title_color));
     	
     	// Get ActionBar and set back button on actionbar
 		actionbar = getSupportActionBar();
 		actionbar.setDisplayHomeAsUpEnabled(true);	
 		actionbar.setTitle(mCategoryName);

     	// If app run in single pane layout
     	if (findViewById(R.id.frame_content) != null) {

     		/* however, if we're being restored from a previous state,
     		 * then we don't need to do anything and should return or else
     		 * we could end up with overlapping fragments. */   
            if (savedInstanceState != null) {
            	return;
            }
            
            /* In case this activity was started with special instructions from an Intent,
             Pass the Intent's extras to the fragment as arguments */
            Bundle bundle = new Bundle();
	        bundle.putString(utils.EXTRA_CATEGORY_ID, mCategoryId);
	        bundle.putString(utils.EXTRA_CATEGORY_NAME, mCategoryName);
	        FragmentPlaceList fragObjPlaceList = new FragmentPlaceList();
	        fragObjPlaceList.setArguments(bundle);

	        // Add the fragment to the 'fragment_container' FrameLayout
	        getSupportFragmentManager().beginTransaction()
	        	.add(R.id.frame_content, fragObjPlaceList).commit();
     	}
     	
 		// load ads
        Ads.loadAds(adView);
	
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

	// Listener For list selected
	@Override
	public void onListSelected(String mIdSelected) {
		// TODO Auto-generated method stub
		
		// Call ActivityDetailPlace
		Intent i = new Intent(this, ActivityDetailPlace.class);
		i.putExtra(utils.EXTRA_LOCATION_ID, mIdSelected);
		startActivity(i);
		
	}
	


}

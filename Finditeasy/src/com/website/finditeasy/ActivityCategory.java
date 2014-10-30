/**
    * File        : ActivityCategory.java
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdView;
import com.website.finditeasy.R;
import com.website.finditeasy.ads.Ads;
import com.website.finditeasy.fragments.FragmentCategoryList;
import com.website.finditeasy.utils.Utils;

public class ActivityCategory extends SherlockFragmentActivity implements FragmentCategoryList.OnCategoryListSelectedListener{

	// Create an instance of ActionBar
	ActionBar actionbar;
	
	// Declare object of AdView class
	AdView adView;
		
	// Declare object of Utils class
	Utils utils;
	
	protected Fragment mFrag;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		// Declare object of Utils class
		utils = new Utils(this);
		
		// connect view objects and xml ids
		adView = (AdView)this.findViewById(R.id.adView);
		
		// Change actionbar title
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
		if ( 0 == titleId ) titleId = com.actionbarsherlock.R.id.abs__action_bar_title; 
							
		// Change the title color to white
		TextView txtActionbarTitle = (TextView)findViewById(titleId);
		txtActionbarTitle.setTextColor(getResources().getColor(R.color.actionbar_title_color));
								
		// Get ActionBar and set back button on actionbar
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
						
		FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
		mFrag = new FragmentCategoryList();
		t.replace(R.id.frame_content, mFrag);
		t.commit();
		
 		// load ads
        Ads.loadAds(adView);
	
	}

	@Override
	public void onCategoryListSelected(String mCategoryId, String mCategoryName) {
		// TODO Auto-generated method stub
		
		// Call ActivityPlaceList
		Intent i = new Intent(this, ActivityPlaceList.class);
		i.putExtra(utils.EXTRA_CATEGORY_ID, mCategoryId);
		i.putExtra(utils.EXTRA_CATEGORY_NAME, mCategoryName);
		startActivity(i);	
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

/**
    * File        : ActivitySearch.java
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
import com.website.finditeasy.fragments.FragmentSearchList;
import com.website.finditeasy.utils.Utils;

public class ActivitySearch extends SherlockFragmentActivity implements FragmentSearchList.OnListSelectedListener{
	
	// Create an instance of ActionBar
	ActionBar actionbar;
	
	// Declare object of AdView class
	AdView adView;
	
	// Declare object of Utils class
	Utils utils;
	protected Fragment mFrag;
	
	protected void onCreate(Bundle savedInstanceState) {
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
		mFrag = new FragmentSearchList();
		t.replace(R.id.frame_content, mFrag);
		t.commit();
		
 		// load ads
        Ads.loadAds(adView);
	
	}

	// Listener for List Selected
	@Override
	public void onListSelected(String mIdSelected) {
		// TODO Auto-generated method stub
		// Call ActivityDetailPlace
		Intent i = new Intent(this, ActivityDetailPlace.class);
		i.putExtra(utils.EXTRA_LOCATION_ID, mIdSelected);
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

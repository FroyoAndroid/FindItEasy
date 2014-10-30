/**
    * File        : ActivityHome.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 01/19/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.website.finditeasy.R;
import com.website.finditeasy.ads.Ads;
import com.website.finditeasy.fragments.FragmentHomeLatestMapsList;
import com.website.finditeasy.fragments.FragmentMenuList;
import com.website.finditeasy.utils.Utils;

public class ActivityHome extends ActivityBase implements FragmentMenuList.OnMenuListSelectedListener, 
FragmentHomeLatestMapsList.OnDataListSelectedListener, OnClickListener{	
	
	Dialog dialog; 
	
	// Declare object of AdView class
	AdView adView;
		
	// Set argument
	final static String ARG_ID = "id";
	
	// Declare object of Context and Utils class
	Context ctx;
	Utils utils;
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.actionbar_menu, menu);
		return true;      
    }
	
	public ActivityHome() {
		super(R.string.app_name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ctx = this;
		setContentView(R.layout.activity_home);
		
		// Declare object of Utils class;
		utils = new Utils(this);
		
		// connect view objects and xml ids
		adView = (AdView)this.findViewById(R.id.adView);
				
		// Check paramter overlays
		int paramOverlay = utils.loadPreferences(utils.UTILS_OVERLAY);
		
		// Condition if app start in the first time overlay will show 
		if(paramOverlay!=1) showOverLay();
				
		// Sliding menu
		SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        
     	// Change actionbar title
     	int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
     	if ( 0 == titleId ) titleId = com.actionbarsherlock.R.id.abs__action_bar_title; 
     			
     	// Change the title color to white
     	TextView txtActionbarTitle = (TextView)findViewById(titleId);
     	txtActionbarTitle.setTextColor(getResources().getColor(R.color.actionbar_title_color));			
     		
     	// Create an instance of LatestMapListFragment
        FragmentHomeLatestMapsList MapsListFrag = new FragmentHomeLatestMapsList();

        // add the fragment to the 'fragment_container' FrameLayout
             getSupportFragmentManager().beginTransaction()
                     .add(R.id.frame_content, MapsListFrag).commit();
             
  		// load ads
         Ads.loadAds(adView);      

	}	
	
	// Call Activity when Menu Seleceted
	@Override
	public void onMenuListSelected(int mSelectedMenu) {
		// TODO Auto-generated method stub
		Intent i;
		switch (mSelectedMenu) {
		
		case 0:
			// Refresh data
			// Create an instance of LatestMapListFragment
	        FragmentHomeLatestMapsList MapsListFrag = new FragmentHomeLatestMapsList();

	        // add the fragment to the 'fragment_container' FrameLayout
             getSupportFragmentManager().beginTransaction()
                     .replace(R.id.frame_content, MapsListFrag).commit();
             
             getSlidingMenu().toggle(true);
             break;
             
		case 1:
			// Call ActivityCategory
			i = new Intent(this, ActivityCategory.class);
			startActivity(i);
			break;
	
		case 2:
			// Call ActivitySearch
			i = new Intent(this, ActivitySearch.class);
			startActivity(i);
			break;	
		
		case 3:
			// Call ActivitySetting
			i = new Intent(this, ActivitySetting.class);
			startActivity(i);
			break;	
			
		case 4:
			// Call ActivityAbout
			i = new Intent(this, ActivityAbout.class);
			startActivity(i);
			break;	
			
		default:
			break;
		}
		
	}
	
	// Listener when item selected 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.abAroundYou:
	        	// Call ActivityPlaceAroundYou
	        	Intent iCart = new Intent(this, ActivityPlaceAroundYou.class);
				startActivity(iCart);
	            break;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
		return true;
	}

	// Listener when list selected
	@Override
	public void onListSelected(String id) {
		// TODO Auto-generated method stub
		
		// Call ActivityDetailPlace
		Intent i = new Intent(this, ActivityDetailPlace.class);
		i.putExtra(utils.EXTRA_LOCATION_ID, id);
		startActivity(i);
	}
	
	// Method is used to show overlay when apps starting in the first time.
	private void showOverLay(){

		dialog = new Dialog(ctx, android.R.style.Theme_Translucent_NoTitleBar);

		dialog.setContentView(R.layout.overlay_view);

		LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
		layout.setOnClickListener(this);
		dialog.show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.overlayLayout:
			utils.savePreferences(utils.UTILS_OVERLAY, 1);
			dialog.dismiss();
			break;

		default:
			break;
		}
	}





}
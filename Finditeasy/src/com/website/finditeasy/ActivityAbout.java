/**
    * File        : ActivityAbout.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 25/05/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.website.finditeasy.R;

public class ActivityAbout extends SherlockFragmentActivity implements OnClickListener{
   
	// Create an instance of ActionBar
	ActionBar actionbar;
	
	// Declare view objects
	Button btnRetry, btnShare, btnRate;
			
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
            
        // Change actionbar title
     	int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
     	if ( 0 == titleId ) titleId = com.actionbarsherlock.R.id.abs__action_bar_title; 
     					
     	// Change the title color to white
     	TextView txtActionbarTitle = (TextView)findViewById(titleId);
     	txtActionbarTitle.setTextColor(getResources().getColor(R.color.actionbar_title_color));
     	
     	// Connect view objects and xml ids
		btnShare		= (Button) findViewById(R.id.btnShare);
		btnRate			= (Button) findViewById(R.id.btnRate);
		
		btnShare.setOnClickListener(this);
		btnRate.setOnClickListener(this);
	    
     	// Get ActionBar and set back button on actionbar
     	actionbar = getSupportActionBar();
     	actionbar.setDisplayHomeAsUpEnabled(true);
     	        
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
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btnShare:
			// Share this app to other application
			Intent iShare = new Intent(Intent.ACTION_SEND);
			iShare.setType("text/plain");
			iShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
			iShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.gplay_url));
			startActivity(Intent.createChooser(iShare, getString(R.string.share_via)));
			break;
			
		case R.id.btnRate:
			// Rate this app in Play Store
			Intent iRate = new Intent(Intent.ACTION_VIEW);
			iRate.setData(Uri.parse(getString(R.string.gplay_url)));
			startActivity(iRate);
			break;
			
		default:
			break;
		}
		// TODO Auto-generated method stub
		
	}	

}
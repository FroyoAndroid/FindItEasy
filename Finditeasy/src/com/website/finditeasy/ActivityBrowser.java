/**
    * File        : ActivityBrowser.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 25/05/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.website.finditeasy.R;
import com.website.finditeasy.libraries.UserFunctions;
import com.website.finditeasy.utils.Utils;

public class ActivityBrowser extends SherlockFragmentActivity {
	
	// Create an instance of ActionBar
	ActionBar actionbar;
		
    WebView web;
    ProgressBar prgPageLoading;
    String mGetDealId, mGetDealTitle;
    String url;
    
    // Declare object of Utils and UserFunctions class
    Utils utils;
    UserFunctions userFunction;
    
    MenuItem miPrev, miNext, miRefresh, miStop;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.actionbar_browser, menu);
		miPrev = (MenuItem) menu.findItem(R.id.abPrevious);
		miNext = (MenuItem) menu.findItem(R.id.abNext);
		miRefresh = (MenuItem) menu.findItem(R.id.abRefresh);
		miStop = (MenuItem) menu.findItem(R.id.abStop);
		
		return true;      
    }
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_browser);
        
        // Declare object of Utils and userFunction class
        utils			 = new Utils(this);
        userFunction 	 = new UserFunctions();
        
        // Get intent Data from ActivityDetail
        Intent i = getIntent();

    	url = i.getStringExtra(utils.EXTRA_LOCATION_URL);
        mGetDealTitle = i.getStringExtra(utils.EXTRA_LOCATION_NAME);

        // Change actionbar title
     	int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
     	if ( 0 == titleId ) titleId = com.actionbarsherlock.R.id.abs__action_bar_title; 
     					
     	// Change the title color
     	TextView txtActionbarTitle = (TextView)findViewById(titleId);
     	txtActionbarTitle.setTextColor(getResources().getColor(R.color.actionbar_title_color));
     						
     	// Get ActionBar and set back button on actionbar
     	actionbar = getSupportActionBar();
     	actionbar.setDisplayHomeAsUpEnabled(true);  
     	actionbar.setTitle(Html.fromHtml(mGetDealTitle));  

     	// Connecct view object and xml ids
        web = (WebView) findViewById(R.id.web);
        prgPageLoading = (ProgressBar) findViewById(R.id.prgPageLoading);
        
        web.setHorizontalScrollBarEnabled(true); 
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setJavaScriptEnabled(true);
        setProgressBarVisibility(true);

        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setUseWideViewPort(true);
        web.setInitialScale(1);
        
        web.loadUrl(url);
        
        final Activity act = this;
        web.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView webview, int progress){
        		
        		act.setProgress(progress*100);
        		prgPageLoading.setProgress(progress);
        		
        	}
        	
        	
        });
        
        web.setWebViewClient(new WebViewClient() {
        	@Override
            public void onPageStarted( WebView view, String url, Bitmap favicon ) {

                super.onPageStarted( web, url, favicon );
                prgPageLoading.setVisibility(View.VISIBLE);
                
            }

            @Override
            public void onPageFinished( WebView view, String url ) {

                super.onPageFinished( web, url );
                
                prgPageLoading.setProgress(0);
                prgPageLoading.setVisibility(View.GONE);
                
                if(web.canGoBack()){
                	miPrev.setEnabled(true);
                	miPrev.setIcon(R.drawable.ic_action_previous_item);
                }else{
                	miPrev.setEnabled(false);
                	miPrev.setIcon(R.drawable.ic_action_previous_item_disabled);
                }
                
                if(web.canGoForward()){
                	miNext.setEnabled(true);
                	miNext.setIcon(R.drawable.ic_action_next_item);
                }else{
                	miNext.setEnabled(false);
                	miNext.setIcon(R.drawable.ic_action_next_item_disabled);
                }
            }   
        	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        	     Toast.makeText(act, description, Toast.LENGTH_SHORT).show();
        	   }
        	
        	
        	});
        		
    }
    
 // Listener when item selected Menu in action bar
 	@Override
 	public boolean onOptionsItemSelected(MenuItem item) {
 	    // Handle presses on the action bar items
 	    switch (item.getItemId()) {
 	    case android.R.id.home:
     		// Previous page or exit
     		finish();
     		return true;
     		
 	    case R.id.abPrevious:
 	    	if(web.canGoBack()){
				web.goBack();
			}
 	        break;
 	    case R.id.abNext:
 	    	if(web.canGoForward()){
				web.goForward();
			}
 	    	break;
 	    case R.id.abRefresh:
 	    	web.reload();
 	    	break;
 	    case R.id.abStop:
 	    	web.stopLoading();
 	    	break;
 	    case R.id.abBrowser:
 	    	Intent iBrowser = new Intent(Intent.ACTION_VIEW);
			iBrowser.setData(Uri.parse(url));
			startActivity(iBrowser);
			break;
        default:
            return super.onOptionsItemSelected(item);
 	    }
 		return true;
 	}

    
}
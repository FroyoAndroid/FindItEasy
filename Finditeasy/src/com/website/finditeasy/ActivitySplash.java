/**
    * File        : ActivitySplash.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 01/19/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy;

import com.actionbarsherlock.app.SherlockActivity;
import com.website.finditeasy.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

public class ActivitySplash extends SherlockActivity {

	// Create an instance of ProgressBar
	ProgressBar prgLoading;
		
	private int progress = 0;
    /** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
    	prgLoading.setProgress(progress);

    	// Run progressbar via asynctask
		new Loading().execute();
	}
	
	public class Loading extends AsyncTask<Void, Void, Void>{
    	@Override
		 protected void onPreExecute() {}
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			// Create progress bar loading
			while(progress < 100){
				try {
					Thread.sleep(1000);
					progress += 30;
					prgLoading.setProgress(progress);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			// When progressbar finish call HomeActivity class
			Intent i = new Intent(ActivitySplash.this, ActivityHome.class);
			startActivity(i);
		}
    }

}

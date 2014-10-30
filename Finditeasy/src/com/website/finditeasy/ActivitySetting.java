/**
    * File        : ActivitySetting.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 01/19/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.website.finditeasy.R;
import com.website.finditeasy.facebook.DialogError;
import com.website.finditeasy.facebook.Facebook;
import com.website.finditeasy.facebook.FacebookError;
import com.website.finditeasy.facebook.SessionStore;
import com.website.finditeasy.facebook.Facebook.DialogListener;
import com.website.finditeasy.twitter.TwitterApp;
import com.website.finditeasy.twitter.TwitterApp.TwDialogListener;
import com.website.finditeasy.utils.Utils;

public class ActivitySetting extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceClickListener{
	
	private ListPreference listPreferenceViewType;
	
	// Create an instance of ActionBar
	ActionBar actionbar;
		
	// Declare object of Utils class;
	Utils utils;
	
	// Declare facebook and twitter objects
	Facebook mFacebook;
	TwitterApp mTwitter;
	PackageInfo pInfo;
	
	// Declare view objects
	ProgressDialog mProgress;
	ImageButton imgNavBack;
	CheckBoxPreference chkFacebook, chkTwitter;

	// Create array variable for permission info
	private static final String[] PERMISSIONS = new String[] {"publish_stream", "read_stream", "offline_access"};
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);
        
        // Register for changes
    	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        
		// Change actionbar title
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
		if ( 0 == titleId ) titleId = com.actionbarsherlock.R.id.abs__action_bar_title; 
					
		// Change the title color to white
		TextView txtActionbarTitle = (TextView)findViewById(titleId);
		txtActionbarTitle.setTextColor(getResources().getColor(R.color.actionbar_title_color));
						
		// Get ActionBar and set back button on actionbar
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		// Declare object to Utils Class
		utils = new Utils(this);
		listPreferenceViewType 	= (ListPreference) findPreference(getString(R.string.preferences_type));   
		
        if(listPreferenceViewType.getValue()==null) {
            // to ensure we don't get a null value
            // set first value by default
            listPreferenceViewType.setValueIndex(0);
        }
		
        listPreferenceViewType.setSummary(listPreferenceViewType.getValue().toString());
        
        // Listener when PreferenceViewType change
        listPreferenceViewType.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        	
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            	String textValue = newValue.toString();
            	ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(textValue);
                listPreferenceViewType.setDefaultValue(index);
                listPreferenceViewType.setValueIndex(index);
                utils.savePreferences(getString(R.string.preferences_type), index);               
                preference.setSummary(textValue);
                return false;
            }
        });
		        
		// Connect view objects and xml id
		chkFacebook = (CheckBoxPreference) findPreference(getString(R.string.preferences_facebook));
		chkTwitter  = (CheckBoxPreference) findPreference(getString(R.string.preferences_twitter));
		
		chkFacebook.setOnPreferenceClickListener(this);
	    // Set progress dialog, facebook id, and twitter key
		mProgress = new ProgressDialog(this);
        mFacebook = new Facebook(getString(R.string.facebook_app_id));
        mTwitter  = new TwitterApp(this, 
        		getString(R.string.twitter_consumer_key),
        		getString(R.string.twitter_secret_key));

        SessionStore.restore(mFacebook, this);
        
        // Check facebook session
        if (mFacebook.isSessionValid()) {
			chkFacebook.setChecked(true);
		}
        
        // Event listener for twitter
        mTwitter.setListener(mTwLoginDialogListener);
		
		if (mTwitter.hasAccessToken()) {
			chkTwitter.setChecked(true);	
		}
		
		// Event listener to handle checkbox button when pressed
		chkFacebook.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				onFacebookClick();
				return false;
			}
		});
		
		// Event listener to handle checkbox button when pressed
		chkTwitter.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				onTwitterClick();
				return false;
			}
		});
	}
    
    // Event listener to read twitter username from twitter dialog
 	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
 		
 		public void onComplete(String value) {
 			String username = mTwitter.getUsername();
 			username		= (username.equals("")) ? getString(R.string.no_name) : username;
 		
 			chkTwitter.setChecked(true);
 			
 			Toast.makeText(ActivitySetting.this, getString(R.string.connect_twitter)+" "+ username, Toast.LENGTH_LONG).show();
 		}
 		
 		public void onError(String value) {
 			chkTwitter.setChecked(false);
 			
 			Toast.makeText(ActivitySetting.this, getString(R.string.connect_twitter_failed), Toast.LENGTH_LONG).show();
 		}
 	};
 	
 	// Method to check whether twitter token is available or not
 	private void onTwitterClick() {
 		if (mTwitter.hasAccessToken()) {
 			
 			// If available, show alert dialog and confirm to delete twitter account
 			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
 			builder.setTitle(getString(R.string.confirm));
 			builder.setMessage(getString(R.string.delete_twitter_connection))
 			       .setCancelable(false)
 			       .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
 			           public void onClick(DialogInterface dialog, int id) {
 			        	   mTwitter.resetAccessToken();
 			        	   
 			        	   chkTwitter.setChecked(false);
 			           }
 			       })
 			       .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
 			           public void onClick(DialogInterface dialog, int id) {
 			                dialog.cancel();
 			                
 			                chkTwitter.setChecked(true);
 			           }
 			       });
 			final AlertDialog alert = builder.create();
 			
 			alert.show();
 			
 		} else {
 			// Otherwise, authorize user
 			chkTwitter.setChecked(false);
 			
 			mTwitter.authorize();
 		}
 	}

 	// Method to check whether facebook session is valid or not
 	private void onFacebookClick() {
 		if (mFacebook.isSessionValid()) {
 			
 			// If valid, show alert dialog and confirm to delete facebook account
 			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
 			builder.setTitle(getString(R.string.confirm));
 			builder.setMessage(getString(R.string.delete_facebook_connection))
 			       .setCancelable(false)
 			       .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
 			           public void onClick(DialogInterface dialog, int id) {
 			        	   fbLogout();
 			           }
 			       })
 			       .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
 			           public void onClick(DialogInterface dialog, int id) {
 			                dialog.cancel();
 			                
 			                chkFacebook.setChecked(true);
 			           }
 			       });
 			
 			final AlertDialog alert = builder.create();
 			
 			alert.show();
 			
 		} else {
 			// Otherwise, authorize user
 			chkFacebook.setChecked(false);
 			
 			mFacebook.authorize(this, PERMISSIONS, -1, new FbLoginDialogListener());
 		}
 	}
     
 	// Event listener to get facebook name from facebook dialog
     private final class FbLoginDialogListener implements DialogListener {
         public void onComplete(Bundle values) {
             SessionStore.save(mFacebook, ActivitySetting.this);
            
             chkFacebook.setChecked(true);
 			 
             getFbName();
         }

         public void onFacebookError(FacebookError error) {
            Toast.makeText(ActivitySetting.this, getString(R.string.connect_facebook_failed), Toast.LENGTH_SHORT).show();
            
            chkFacebook.setChecked(false);
         }
         
         public void onError(DialogError error) {
         	Toast.makeText(ActivitySetting.this, getString(R.string.connect_facebook_failed), Toast.LENGTH_SHORT).show(); 
         	
         	chkFacebook.setChecked(false);
         }

         public void onCancel() {
         	chkFacebook.setChecked(false);
         }
     }
     
     // Method to get facebook name
 	private void getFbName() {
 		mProgress.setMessage("Finalizing ...");
 		mProgress.show();
 		
 		new Thread() {
 			@Override
 			public void run() {
 		        String name = "";
 		        int what = 1;
 		        
 		        try {
 		        	String me = mFacebook.request("me");
 		        	
 		        	JSONObject jsonObj = (JSONObject) new JSONTokener(me).nextValue();
 		        	name = jsonObj.getString("name");
 		        	what = 0;
 		        } catch (Exception ex) {
 		        	ex.printStackTrace();
 		        }
 		        
 		        mFbHandler.sendMessage(mFbHandler.obtainMessage(what, name));
 			}
 		}.start();
 	}
 	
 	// Method to logout facebook account
 	private void fbLogout() {
 		mProgress.setMessage(getString(R.string.disconnect_facebook));
 		mProgress.show();
 			
 		new Thread() {
 			@Override
 			public void run() {
 				SessionStore.clear(ActivitySetting.this);
 		        	   
 				int what = 1;
 					
 		        try {
 		        	mFacebook.logout(ActivitySetting.this);
 		        		 
 		        	what = 0;
 		        } catch (Exception ex) {
 		        	ex.printStackTrace();
 		        }
 		        	
 		        mHandler.sendMessage(mHandler.obtainMessage(what));
 			}
 		}.start();
 	}
 	
 	// Event handler to read facebook authentication
 	private Handler mFbHandler = new Handler() {
 			@Override
 			public void handleMessage(Message msg) {
 				mProgress.dismiss();
 				
 				if (msg.what == 0) {
 					String username = (String) msg.obj;
 			        username = (username.equals("")) ? getString(R.string.no_name) : username;
 			            
 			        SessionStore.saveName(username, ActivitySetting.this);
 			        
 			         
 			        Toast.makeText(ActivitySetting.this, getString(R.string.connect_facebook)+" "+ username, Toast.LENGTH_SHORT).show();
 				} else {
 					Toast.makeText(ActivitySetting.this, getString(R.string.connect_facebook_2), Toast.LENGTH_SHORT).show();
 				}
 			}
 		};
 	
 	// Event handler to read facebook logout
 	private Handler mHandler = new Handler() {
 			@Override
 			public void handleMessage(Message msg) {
 				mProgress.dismiss();
 				
 				if (msg.what == 1) {
 					Toast.makeText(ActivitySetting.this, getString(R.string.logout_facebook_failed), Toast.LENGTH_SHORT).show();
 				} else {
 					chkFacebook.setChecked(false);
 					
 		        	   
 					Toast.makeText(ActivitySetting.this, getString(R.string.disconnect_facebook_2), Toast.LENGTH_SHORT).show();
 				}
 			}
 		};
 		

 		// Listener for option menu
 		@Override
 		public boolean onOptionsItemSelected(MenuItem item) {
 		    switch (item.getItemId()) {
 		    	case android.R.id.home:
 		    		// previous page or exit
 		    		finish();
 		    		return true;
 				default:
 					return super.onOptionsItemSelected(item);
 			}
 		}
 		
 		// Listener when Preference zoom Change
 		@Override
 		 public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
 			if (key.equals(getString(R.string.preferences_zoom))) {
 			    // Notify that value was really changed
 			    int value = sharedPreferences.getInt(getString(R.string.preferences_zoom), 0);
 			    utils.savePreferences(getString(R.string.preferences_zoom),value);			    
 			}
 		 }
 		
 	 	@Override
 	    protected void onDestroy() {
 		// Unregister from changes
 		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
 		super.onDestroy();
 	    }
 	 	@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
			
			return false;
		}
/*
		@Override
		public boolean onPreferenceClick(Preference preference) {
			// TODO Auto-generated method stub
			switch (preference.getKey()) {
			case "key_facebook":
				
				break;

			default:
				break;
			}
			return false;
		}
 */	 	 

 }

package com.website.finditeasy.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.website.finditeasy.R;
import com.website.finditeasy.adapters.AdapterLatestMapsList;
import com.website.finditeasy.libraries.ServiceNotification;
import com.website.finditeasy.libraries.UserFunctions;
import com.website.finditeasy.utils.Utils;
public class FragmentHomeLatestMapsList extends SherlockFragment implements OnClickListener {
	
	
	// Create interface for MapsListFragment
	OnDataListSelectedListener mCallback;
	
	boolean loadingMore = false;
	
    ArrayList<HashMap<String, String>> menuItems;
    ProgressDialog pDialog;

    // Declare object of userFunctions and Utils class
	UserFunctions userFunction;
	Utils utils;
	
	// Create instance of list and ListAdapter
	ListView list;
	TextView lblNoResult;
	AdapterLatestMapsList mla;
	
	Button btnLoadMore, btnRetry; 
	LinearLayout lytRetry;
	
    // flag for current page
	JSONObject json;
    int mCurrentPage = 0;
    int mPreviousPage;
    
	// create array variables to store data
	public String[] mLocationId;
	public String[] mLocationName;
	public String[] mAddress;
	public String[] mIcMarkerLocation;
	public String[] mImgLocation;
	
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "location_name";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_IMAGE = "image";
	public static final String KEY_MARKER = "marker";
	
	private int intLengthData;
	
	// Declare OnListSelected interface
	public interface OnDataListSelectedListener{
		public void onListSelected(String idSelected);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v = inflater.inflate(R.layout.fragment_home_list, container, false);
		
		list 	 	= (ListView) v.findViewById(R.id.list);
		lblNoResult	= (TextView) v.findViewById(R.id.lblNoResult);
		lytRetry 	= (LinearLayout) v.findViewById(R.id.lytRetry);
		btnRetry 	= (Button) v.findViewById(R.id.btnRetry);

		btnRetry.setOnClickListener(this);
		
		// Declare object of userFunctions and Utils class
		userFunction = new UserFunctions();
		utils = new Utils(getActivity());
		
		menuItems = new ArrayList<HashMap<String, String>>();
		
		// Create LoadMore button
        btnLoadMore = new Button(getActivity());
        btnLoadMore.setBackgroundResource(R.drawable.apptheme_btn_default_holo_light);
        btnLoadMore.setText(getString(R.string.btn_load_more));
        btnLoadMore.setTextColor(getResources().getColor(R.color.btn_txt));
 
		new loadFirstListView().execute();
				
		// Listener to handle load more buttton when clicked
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
                // Starting a new async task
				json = null;
                new loadMoreListView().execute();
            }
        });
        
		// Listener to get selected id when list item clicked
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> item = new HashMap<String, String>();
		        item = menuItems.get(position);
				
				// Pass id to onListSelected method on HomeActivity
				mCallback.onListSelected(item.get("id"));

				// Set the item as checked to be highlighted when in two-pane layout
				list.setItemChecked(position, true);
			}
		});
		
		return v;
	}
	
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // The callback interface. If not, it throws an exception.
        try {
            mCallback = (OnDataListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	
	// Load first 10 videos
	private class loadFirstListView extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    getActivity());
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        protected Void doInBackground(Void... unused) {
        	getDataFromServer();
        	return (null);
        }
 
        protected void onPostExecute(Void unused) {
            // Condition if data length uder 10 button loadMore is remove  
        	if(intLengthData<userFunction.valueItemsPerPage){
        		list.removeFooterView(btnLoadMore);
        	} else {
        		list.addFooterView(btnLoadMore);
        	}
            
        	if(isAdded()){
	            if(intLengthData != 0){
	            	
	            	// Check paramter notif
	        		int paramNotif = utils.loadPreferences(utils.UTILS_PARAM_NOTIF);
	        		
	        		// Condition if app start in the first time notif will run in background 
	        		if(paramNotif!=1) {
	        			utils.saveString(utils.UTILS_NOTIF, mLocationId[0]);
		            	utils.savePreferences(utils.UTILS_PARAM_NOTIF, 1);
		            	startService();
	        		}
	        		
	                // Adding load more button to lisview at bottom
	            	lytRetry.setVisibility(View.GONE);
	            	list.setVisibility(View.VISIBLE);
	            	lblNoResult.setVisibility(View.GONE);
	            	// Getting adapter
	            	mla = new AdapterLatestMapsList(getActivity(), menuItems);
	            	list.setAdapter(mla);
	            	
	            } else {
	                list.removeFooterView(btnLoadMore);
	            	if(json != null){
						lblNoResult.setVisibility(View.VISIBLE);
	            		lytRetry.setVisibility(View.GONE);
	            		
		            } else {
						lblNoResult.setVisibility(View.GONE);
	            		lytRetry.setVisibility(View.VISIBLE);
		            	Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
	            	}
	            }
        	}
        	/*
        	if(isAdded()){
	            if(json != null){
	            	
	            	
	            }else{
	            	lytRetry.setVisibility(View.VISIBLE);
	            	Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
	            }
        	}*/
        	// Closing progress dialog
        	pDialog.dismiss();
        }
    }
	
	// Load more videos
    private class loadMoreListView extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
        	pDialog = new ProgressDialog(
                    getActivity());
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        protected Void doInBackground(Void... unused) {
        	
			// Store previous value of current page
			mPreviousPage = mCurrentPage;
            // Increment current page
			mCurrentPage += 10;
			getDataFromServer();
            return (null);
        }
 
        protected void onPostExecute(Void unused) {
        	// Condition if data length uder 10 button loadMore is remove
        	if(intLengthData<userFunction.valueItemsPerPage) list.removeFooterView(btnLoadMore);
            if(json != null){
            	// Get listview current position - used to maintain scroll position
	            int currentPosition = list.getFirstVisiblePosition();
	

            	lytRetry.setVisibility(View.GONE);
	            // Appending new data to menuItems ArrayList
	            mla = new AdapterLatestMapsList(
	                    getActivity(),
	                    menuItems);
	            list.setAdapter(mla);
	            // Setting new scroll position
	            list.setSelectionFromTop(currentPosition + 1, 0);

            }else{
            	if(menuItems != null){
            		mCurrentPage = mPreviousPage;
                	lytRetry.setVisibility(View.GONE);
            	}else{
            		lytRetry.setVisibility(View.VISIBLE);
            	}
            	Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
            // Closing progress dialog
            pDialog.dismiss();
        }
    }
	
	public void getDataFromServer(){
	       
        try {
        	
    		json = userFunction.latestPlace(mCurrentPage);
            if(json != null){
	            JSONArray dataLocationArray = json.getJSONArray(userFunction.array_latest_place);
	            intLengthData = dataLocationArray.length();
	            mLocationId 		= new String[intLengthData];
	            mLocationName 		= new String[intLengthData];
	            mAddress 			= new String[intLengthData];
	            mIcMarkerLocation 	= new String[intLengthData];
	            mImgLocation 		= new String[intLengthData];
	            for (int i = 0; i < intLengthData; i++) {
	            	// Store data from server to variable
	            	JSONObject locationObject = dataLocationArray.getJSONObject(i);
	
	            	HashMap<String, String> map = new HashMap<String, String>();
				    mLocationId[i] 		= locationObject.getString(userFunction.key_location_id);
	                mLocationName[i] 	= locationObject.getString(userFunction.key_location_name);
	                mAddress[i] 		= locationObject.getString(userFunction.key_location_address);
	                mImgLocation[i] 	= locationObject.getString(userFunction.key_location_image);
	                mIcMarkerLocation[i]= locationObject.getString(userFunction.key_category_marker);
	                
				    map.put(KEY_ID, mLocationId[i]); // id not using any where
		            map.put(KEY_NAME, mLocationName[i]);
		            map.put(KEY_ADDRESS, mAddress[i]);
		            map.put(KEY_IMAGE, mImgLocation[i]);
		            map.put(KEY_MARKER, mIcMarkerLocation[i]);
		            
		            // Adding HashList to ArrayList
		            menuItems.add(map);
	            }
            }
                       
                               
        } catch (JSONException e) {
            // TODO Auto-generated catch block
          e.printStackTrace();
        }      
    }
	
	public void startService(){
		
		Intent myIntent = new Intent(getActivity(), ServiceNotification.class);
		  
		PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, myIntent, 0);
		
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(getActivity().ALARM_SERVICE);
		
        Calendar calendar = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30*1000, pendingIntent);
		            
	}
	
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	list.setAdapter(null);
    	super.onDestroy();
    	
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnRetry:
			json = null;
			new loadFirstListView().execute();
			break;

		default:
			break;
		}
	}
}

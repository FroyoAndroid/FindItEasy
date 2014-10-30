package com.website.finditeasy.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.website.finditeasy.R;
import com.website.finditeasy.adapters.AdapterSearchList;
import com.website.finditeasy.libraries.UserFunctions;
public class FragmentSearchList extends SherlockFragment implements OnClickListener{
	
	// Create interface for MapsListFragment
	OnListSelectedListener mCallback;

	boolean loadingMore = false;

    ArrayList<HashMap<String, String>> menuItems;
    ProgressDialog pDialog;
    
    // Declare object of userFunctions class
	UserFunctions userFunction;
	
	// Create instance of list and ListAdapter
	ListView list;
	AdapterSearchList sla;
	LinearLayout lytRetry;
	
	Button btnLoadMore, btnRetry;
	ImageButton btnSearch;
	EditText txtSearch;
	TextView lblNoResult;
	
	// Flag for current page
	JSONObject json;
    private int mCurrentPage = 0;
    private int mPreviousPage;
	
	private String mSearch;
	private int intLengthData;
	
	// Create array variables to store data
	public String[] mLocationId;
	public String[] mLocationName;
	public String[] mAddress;
	public String[] mIcMarkerLocation;
	public String[] mImgLocation;
	
	public final String KEY_ID 		= "id";
	public final String KEY_NAME 	= "location_name";
	public final String KEY_ADDRESS = "address";
	public final String KEY_IMAGE 	= "image";
	public final String KEY_MARKER 	= "marker";
	
	// Declare OnListSelected interface
	public interface OnListSelectedListener{
		public void onListSelected(String idSelected);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v = inflater.inflate(R.layout.fragment_search_list, container, false);
		
		list 		= (ListView) v.findViewById(R.id.list);
		lblNoResult	= (TextView) v.findViewById(R.id.lblNoResult);
		txtSearch	= (EditText) v.findViewById(R.id.txtSearch);
		btnSearch	= (ImageButton) v.findViewById(R.id.btnSearch);
		lytRetry 	= (LinearLayout) v.findViewById(R.id.lytRetry);
		btnRetry 	= (Button) v.findViewById(R.id.btnRetry);
		
		// Declare object of userFunctions class
		userFunction = new UserFunctions();

		menuItems = new ArrayList<HashMap<String, String>>();
		
		// Create LoadMore button
        btnLoadMore = new Button(getActivity());
        btnLoadMore.setBackgroundResource(R.drawable.apptheme_btn_default_holo_light);
        btnLoadMore.setText(getString(R.string.btn_load_more));
        btnLoadMore.setTextColor(getResources().getColor(R.color.btn_txt));
 				
		btnSearch.setOnClickListener(this);
		
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
            mCallback = (OnListSelectedListener) activity;
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
        	// Call method getDataFromServer
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
	            if(menuItems.size() != 0){

	                // Adding load more button to lisview at bottom
	            	lytRetry.setVisibility(View.GONE);
	            	list.setVisibility(View.VISIBLE);
	            	// Getting adapter
	            	sla = new AdapterSearchList(getActivity(), menuItems);
	            	list.setAdapter(sla);
	            	
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
            	if(menuItems != null){
	            	// Get listview current position - used to maintain scroll position
		            int currentPosition = list.getFirstVisiblePosition();
		
	
	            	lytRetry.setVisibility(View.GONE);
		            // Appending new data to menuItems ArrayList
		            sla = new AdapterSearchList(
		                    getActivity(),
		                    menuItems);
		            list.setAdapter(sla);
		            // Setting new scroll position
		            list.setSelectionFromTop(currentPosition + 1, 0);
            	}else{
            		list.removeFooterView(btnLoadMore);
            	}
            }else{
            	Log.d("json", "json not null");
            	if(menuItems != null){
                	Log.d("menuItems", "menuItems not null");
            		mCurrentPage = mPreviousPage;
                	lytRetry.setVisibility(View.GONE);
            	}else{
                	Log.d("menuItems", "menuItems null");
            		lytRetry.setVisibility(View.VISIBLE);
            	}
            	Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
            // Closing progress dialog
            pDialog.dismiss();
        }
    }
    
	
	// Method get data from server
	public void getDataFromServer(){
	       
        try {
        	mSearch = mSearch.replace(" ", "%20");
        	json = userFunction.searchByName(mSearch, mCurrentPage);
            mSearch = mSearch.replace("%20", " ");
            
            if(json != null){
	            JSONArray dataLocationArray = json.getJSONArray(userFunction.array_place_by_search);
	            intLengthData = dataLocationArray.length();
	            mLocationId 		= new String[intLengthData];
	            mLocationName 		= new String[intLengthData];
	            mAddress 			= new String[intLengthData];
	            mIcMarkerLocation 	= new String[intLengthData];
	            mImgLocation 		= new String[intLengthData];
	            
	            // Store data to variable array
	            for (int i = 0; i < intLengthData; i++) {
	            	JSONObject locationObject = dataLocationArray.getJSONObject(i);
	
	            	HashMap<String, String> map = new HashMap<String, String>();
				    
	                mLocationId[i] 		= locationObject.getString(userFunction.key_location_id);
	                mLocationName[i] 	= locationObject.getString(userFunction.key_location_name);
	                mAddress[i] 		= locationObject.getString(userFunction.key_location_address);
	                mIcMarkerLocation[i]= locationObject.getString(userFunction.key_category_marker);
	                mImgLocation[i] 	= locationObject.getString(userFunction.key_location_image);
	                
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
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSearch:
			mSearch = txtSearch.getText().toString().trim();
			if(mSearch.length()>0){
				lblNoResult.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
				
				json = null;
				menuItems.clear();
		    	list.setAdapter(null);
		    	
				new loadFirstListView().execute();
			}
			break;

		case R.id.btnRetry:
			json = null;
	        // Adding load more button to lisview at bottom
			new loadFirstListView().execute();
			break;
		default:
			break;
		}
	}
	
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	
    }
	    
}

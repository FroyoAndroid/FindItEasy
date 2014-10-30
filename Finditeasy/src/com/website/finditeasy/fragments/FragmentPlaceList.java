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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.website.finditeasy.R;
import com.website.finditeasy.adapters.AdapterPlaceList;
import com.website.finditeasy.libraries.UserFunctions;
import com.website.finditeasy.utils.Utils;
public class FragmentPlaceList extends SherlockFragment implements OnClickListener {
	
	// Create interface for MapsListFragment
	OnListSelectedListener mCallback;

	boolean loadingMore = false;
	
    ArrayList<HashMap<String, String>> menuItems;
    ProgressDialog pDialog;
    
    // Declare object of userFunctions and Utils class
	UserFunctions userFunction;
	Utils utils;
	
	// Create instance of list and ListAdapter
	ListView list;
	AdapterPlaceList pla;
	
	Button btnLoadMore, btnRetry;
	LinearLayout lytRetry;
	
    // Declare object of JSONObject class
	JSONObject json;
	
	// Flag for current page
    int currentPage = 0;
    int previousPage;
    int itemsPerPage=10;
    
	// Location_id, location_name, address, category_marker, location_image
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
	
	// Declare variable 
	String mCategoryId, mCategoryName;
	private int intLengthData;
	
	public interface OnListSelectedListener{
		public void onListSelected(String idSelected);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		// Declare object of userFunctions and Utils class
		userFunction = new UserFunctions();
		utils = new Utils(getActivity());
				
		View v = inflater.inflate(R.layout.fragment_home_list, container, false);
		
		list 	 = (ListView) v.findViewById(R.id.list);
		lytRetry = (LinearLayout) v.findViewById(R.id.lytRetry);
		btnRetry = (Button) v.findViewById(R.id.btnRetry);

		btnRetry.setOnClickListener(this);
		
		// Get Bundle data from ActivityPlaceList
		Bundle bundle = this.getArguments();
		mCategoryId 	= bundle.getString(utils.EXTRA_CATEGORY_ID);    
		mCategoryName	= bundle.getString(utils.EXTRA_CATEGORY_NAME);
		
		
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
	            if(json != null){
	            	lytRetry.setVisibility(View.GONE);
	            	// Getting adapter
	            	pla = new AdapterPlaceList(getActivity(), menuItems);
	            	list.setAdapter(pla);
	            	
	            }else{
	            	lytRetry.setVisibility(View.VISIBLE);
	            	Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
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
			previousPage = currentPage;
            // Increment current page
			currentPage += 10;
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
	            pla = new AdapterPlaceList(
	                    getActivity(),
	                    menuItems);
	            list.setAdapter(pla);
	            // Setting new scroll position
	            list.setSelectionFromTop(currentPosition + 1, 0);

            }else{
            	if(menuItems != null){
            		currentPage = previousPage;
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
	
    // Method get data from server
	public void getDataFromServer(){
	       
        try {
        	
            json = userFunction.placeByCategory(mCategoryId, currentPage, itemsPerPage);
            if(json != null){
	            JSONArray dataLocationArray = json.getJSONArray(userFunction.array_place_by_category);
	            intLengthData 	  = dataLocationArray.length();
	            mLocationId 	  = new String[intLengthData];
	            mLocationName 	  = new String[intLengthData];
	            mAddress 		  = new String[intLengthData];
	            mIcMarkerLocation = new String[intLengthData];
	            mImgLocation 	  = new String[intLengthData];
	                       
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
		            
		            // Aading HashList to ArrayList
		            menuItems.add(map);
	            }
            }
                       
                               
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }      
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

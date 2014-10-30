package com.website.finditeasy.fragments;

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
import com.website.finditeasy.adapters.AdapterCategoryList;
import com.website.finditeasy.libraries.UserFunctions;

public class FragmentCategoryList extends SherlockFragment implements OnClickListener {
	OnCategoryListSelectedListener mCallback;
	
	ListView list;
	
	// Declare object of UserFunctions, AdapterCategory and JSONObject class
	AdapterCategoryList la;
	UserFunctions userFunction;
	JSONObject json;
	
	// Declare view objects
	Button btnRetry; 
	LinearLayout lytRetry;
		
	public static String[] Categories;
	public int mCurrentPosition;

	// Get length array from server
	private int intLengthData;
	
	// Create array variables to store data
	public static String[] sCategoryId;
	public static String[] sCategoryName;
	public static String[] sIcMarkerLocation;
	
	public interface OnCategoryListSelectedListener{
		
		public void onCategoryListSelected(String mCategoryId, String mCategoryName);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_home_list, null);
		
		list = (ListView) v.findViewById(R.id.list);
		lytRetry = (LinearLayout) v.findViewById(R.id.lytRetry);
		btnRetry = (Button) v.findViewById(R.id.btnRetry);

		btnRetry.setOnClickListener(this);
		
		la = new AdapterCategoryList(getActivity());
		
		new getCategoryList().execute();
		
		// Declare object of UserFuntions class
		userFunction 	= new UserFunctions();
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				mCallback.onCategoryListSelected(sCategoryId[position], sCategoryName[position]);
				mCurrentPosition = position;
				list.setItemChecked(position, true);
				
			}
		});
		
        return v;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnCategoryListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategoryListSelectedListener");
        }
    }
	
	// AsynTask to get list Category data 
	public class getCategoryList extends AsyncTask<Void, Void, Void>{

		ProgressDialog progress;
    	
    	@Override
		 protected void onPreExecute() {
    		/////////////////
		  // TODO Auto-generated method stub
    		
    		// Show progress dialog when fetching data from database
    		progress= ProgressDialog.show(
    				getActivity(), 
    				"", 
    				getString(R.string.loading_data), 
    				true);
    		
    	}

    	@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
    		// Method to get data from server
			getDataFromServer();
			return null;
		}
    	
    	@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
    		
    		if(isAdded()){
	            if(json != null){
	            	lytRetry.setVisibility(View.GONE);
	            	list.setAdapter(la);
	            	
	            }else{
	            	lytRetry.setVisibility(View.VISIBLE);
	            	Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
	            }
        	}    		
			progress.dismiss();				
		}
	}
	
	// Method to get data from server
	public void getDataFromServer(){
	       
        try {
            json = userFunction.categoryList();
            if(json != null){
		        JSONArray dataLocationArray = json.getJSONArray(userFunction.array_category_list);
		        
		        intLengthData = dataLocationArray.length();
		        sCategoryId 		= new String[intLengthData];
		        sCategoryName 		= new String[intLengthData];
		        sIcMarkerLocation 	= new String[intLengthData];
		                   
		        // Store data to variable array
		        for (int i = 0; i < intLengthData; i++) {
		        	JSONObject locationObject = dataLocationArray.getJSONObject(i);
		
		            sCategoryId[i] 		= locationObject.getString(userFunction.key_category_id);
		            sCategoryName[i] 	= locationObject.getString(userFunction.key_category_name);
		            sIcMarkerLocation[i]= locationObject.getString(userFunction.key_category_marker);
		            
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
			case R.id.btnRetry:
				json = null;
				new getCategoryList().execute();
				break;

			default:
				break;
			}
		
	}
}


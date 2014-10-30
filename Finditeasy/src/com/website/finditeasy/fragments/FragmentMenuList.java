package com.website.finditeasy.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.website.finditeasy.R;
import com.website.finditeasy.adapters.AdapterMenuList;

public class FragmentMenuList extends SherlockFragment {
	OnMenuListSelectedListener mCallback;
	
	ListView list;
	
	// Declare object of AdapterMenuList class
	AdapterMenuList la;
	
	public static String[] listMenu;
	public static int[] imageMenu= new int[]{
		R.drawable.ic_latest, R.drawable.ic_collection, R.drawable.ic_search, 
		R.drawable.ic_settings, R.drawable.ic_about
	};
	public int mCurrentPosition;

	public interface OnMenuListSelectedListener{
		
		public void onMenuListSelected(int selectedIdMenu);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_menu_list, null);
		list   = (ListView) v.findViewById(R.id.list);

		la = new AdapterMenuList(getActivity());
		
		new getMenuList().execute();
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				mCallback.onMenuListSelected(position);
				
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
            mCallback = (OnMenuListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMenuListSelectedListener");
        }
    }
	
	// AsyncTask to get Menu list
	public class getMenuList extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			Resources res = getResources();
			listMenu = res.getStringArray(R.array.menu_list);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			list.setAdapter(la);
		}
	}
}

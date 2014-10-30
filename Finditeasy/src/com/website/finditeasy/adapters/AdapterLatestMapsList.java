package com.website.finditeasy.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.website.finditeasy.R;
import com.website.finditeasy.fragments.FragmentHomeLatestMapsList;
import com.website.finditeasy.lazylist.ImageLoader;
import com.website.finditeasy.libraries.UserFunctions;

public class AdapterLatestMapsList extends BaseAdapter {	
	
	public ImageLoader imageLoader;
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater=null;
	
	// Declare object of userFunctions class
	UserFunctions userFunction;
	
	public AdapterLatestMapsList(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
		imageLoader = new ImageLoader(a.getApplicationContext());
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		
		// Declare object of UserFuntion class
		userFunction = new UserFunctions();
		if(convertView == null){
			convertView = inflater.inflate(R.layout.adapter_row_home, null);
			holder = new ViewHolder();
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		// Connect views object and views id on xml
		holder.lblAddress 	= (TextView) convertView.findViewById(R.id.lblAddress);
		holder.lblTitle 	= (TextView) convertView.findViewById(R.id.lblTitle);
		holder.imgThumbnail = (ImageView) convertView.findViewById(R.id.imgThumbnail);
		holder.icMarker 	= (ImageView) convertView.findViewById(R.id.ic_marker);
		
		HashMap<String, String> item = new HashMap<String, String>();
        item = data.get(position);
        
		// Set data to textview and imageview
		imageLoader.DisplayImage(userFunction.URLAdmin+item.get(FragmentHomeLatestMapsList.KEY_IMAGE), holder.imgThumbnail);
		imageLoader.DisplayMarker(userFunction.URLAdmin+item.get(FragmentHomeLatestMapsList.KEY_MARKER), holder.icMarker);
		
		holder.lblTitle.setText(item.get(FragmentHomeLatestMapsList.KEY_NAME));
		holder.lblAddress.setText(item.get(FragmentHomeLatestMapsList.KEY_ADDRESS));
		
		return convertView;
	}

	// Method to create instance of views
	static class ViewHolder {
		ImageView imgThumbnail, icMarker;
		TextView lblTitle, lblAddress;
	}
	
	
}
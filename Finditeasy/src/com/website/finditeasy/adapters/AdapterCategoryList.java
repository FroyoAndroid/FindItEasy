package com.website.finditeasy.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.website.finditeasy.R;
import com.website.finditeasy.fragments.FragmentCategoryList;
import com.website.finditeasy.lazylist.ImageLoader;
import com.website.finditeasy.libraries.UserFunctions;
public class AdapterCategoryList extends BaseAdapter {

		private Activity activity;
		
		ImageLoader imageLoader;
		
		//declare object of userFunctions class
		UserFunctions userFunction;
		
		public AdapterCategoryList(Activity act) {
			activity = act;
			imageLoader=new ImageLoader(activity.getApplicationContext());
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return FragmentCategoryList.sCategoryId.length;
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
			
			//declare object of userFunctions class
			userFunction = new UserFunctions();
			
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.adapter_row_category, null);
				holder = new ViewHolder();	
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.lblNameCategory = (TextView) convertView.findViewById(R.id.lblNameCategory);
			holder.icMarker 	= (ImageView) convertView.findViewById(R.id.ic_marker);
			
			holder.lblNameCategory.setText(FragmentCategoryList.sCategoryName[position]);
			
			// set data to textview and imageview
			imageLoader.DisplayMarker(userFunction.URLAdmin+FragmentCategoryList.sIcMarkerLocation[position], holder.icMarker);
			
			
			return convertView;
		}
		
		static class ViewHolder {
			TextView lblNameCategory;
			ImageView icMarker;
		}
		
		
	}
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
import com.website.finditeasy.fragments.FragmentMenuList;
public class AdapterMenuList extends BaseAdapter {

		private Activity activity;		
		
		public AdapterMenuList(Activity act) {
			this.activity = act;

		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return FragmentMenuList.listMenu.length;
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
			
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.adapter_row_menu, null);
				holder = new ViewHolder();
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.txtCategory = (TextView) convertView.findViewById(R.id.txtCategory);
			holder.imgMenu = (ImageView) convertView.findViewById(R.id.ic_img);
			holder.txtCategory.setText(FragmentMenuList.listMenu[position]);
			holder.imgMenu.setBackgroundResource(FragmentMenuList.imageMenu[position]);
			return convertView;
		}
		
		static class ViewHolder {
			TextView txtCategory;
			ImageView	imgMenu;
		}
		
		
	}
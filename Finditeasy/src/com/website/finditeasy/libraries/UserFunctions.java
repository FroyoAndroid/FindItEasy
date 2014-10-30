/**
    * File        : UserFuntions.java
    * App name    : Perkutut
    * Version     : 1.1.4
    * Created     : 25/05/14

    * Created by Taufan Erfiyanto & Cahaya Pangripta Alam on 11/24/13.
    * Copyright (c) 2013 pongodev. All rights reserved.
    */

package com.website.finditeasy.libraries;

import org.json.JSONObject;

public class UserFunctions {
	
	private JSONParser jsonParser;

	// Web Service
	private final String Server = "http://croatia-booking-online.com/";
	
	// Folder
	private final String folderMain = "admin/";
	private final String folderApi  = "api/";
	
	// Url
	private final String URLApi  = Server+folderMain+folderApi; 
	public final String URLAdmin = Server+folderMain;
	
	// Service
	private final String service_notif 	  		  = "notify_new_place?";
	private final String service_latest_place  	  = "latest_place?";
	private final String service_around_you 	  = "around_you?";
	private final String service_place_detail 	  = "place_detail?";
	private final String service_category_list 	  = "category_list?";
	private final String service_place_by_category= "place_by_category?";
	private final String service_place_by_search  = "place_by_search_name?";
	public final String service_view_location 	  = "view-location.php?";
	private final String service_desc 	  	  	  = "description.php?";
	// Param
	private final String param_start_index   	= "start_index=";
	private final String param_items_per_page	= "items_per_page=";
	private final String param_user_lat  		= "user_lat=";
	private final String param_user_lng  		= "user_lng=";
	private final String param_location_id  	= "location_id=";
	private final String param_category_id  	= "category_id=";
	private final String param_key_name  		= "key_name=";
    
	// Key object name to get value
	public final String key_location_id		= "location_id";
	public final String key_location_name	= "location_name";
	public final String key_location_address= "address";
	public final String key_location_phone	= "phone";
	public final String key_location_website= "website";
	public final String key_location_desc	= "description";
	public final String key_location_image	= "location_image";
	public final String key_location_lat	= "latitude";
	public final String key_location_lng	= "longitude";
	public final String key_category_id		= "category_id";
	public final String key_category_name	= "category_name";
	public final String key_category_marker	= "category_marker";
	
	// Array
	public final String array_latest_place 	   = "latestPlace";
	public final String array_around_you 	   = "aroundYou";
	public final String array_place_detail 	   = "placeDetail";
	public final String array_category_list	   = "categoryList";
	public final String array_place_by_category= "placeByCategory";
	public final String array_place_by_search  = "placeBySearchName";
	
	// LoadUrl
	public final String varLoadURL = Server+folderMain+service_desc+param_location_id;
	
	private String webService;
	public final int valueItemsPerPage=7;
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	
	public JSONObject getNotif(){		
		webService = URLApi+service_notif;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject latestPlace(int valueStartIndex){
		
		webService = URLApi+service_latest_place+param_start_index+valueStartIndex+"&"+param_items_per_page+valueItemsPerPage;
		
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject aroundYou(double userLat, double userLng){
		webService = URLApi+service_around_you+param_user_lat+userLat+"&"+param_user_lng+userLng;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}

	public JSONObject placeDetailInfo(String locationId){
		webService = URLApi+service_place_detail+param_location_id+locationId;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject categoryList(){
		webService = URLApi+service_category_list;
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject placeByCategory(String valueCategoryId, int valueStartIndex, int valueItemsPerPage){
		
		webService = URLApi+service_place_by_category+param_category_id+valueCategoryId+"&"+param_start_index+valueStartIndex+"&"+param_items_per_page+valueItemsPerPage;
		
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
	public JSONObject searchByName(String valueKeyName, int valueStartIndex){
		
		webService = URLApi+service_place_by_search+param_key_name+valueKeyName+"&"+param_start_index+valueStartIndex+"&"+param_items_per_page+valueItemsPerPage;
		
		JSONObject json = jsonParser.getJSONFromUrl(webService);
		return json;
	}
	
}
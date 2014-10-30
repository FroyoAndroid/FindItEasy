package com.website.finditeasy.libraries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.website.finditeasy.R;
import com.website.finditeasy.ActivityHome;
import com.website.finditeasy.utils.Utils;

public class ServiceNotification extends Service {

	//private static final int MY_NOTIFICATION_ID=1;
	NotifyServiceReceiver notifyServiceReceiver;
	
	// create array variables to store data
	public String mLocationId;
	final static String ACTION = "NotifyServiceAction";
	final static String STOP_SERVICE = "";
	final static int RQS_STOP_SERVICE = 2;
	
	private int intLengthData;
	
	JSONObject json;
	
    // Declare object of userFunctions and Utils class
	UserFunctions userFunctions;
	Utils utils;
	
	@Override
	public void onCreate() {
	 // TODO Auto-generated method stub
		Log.d("onCreate", "onCreate");
		notifyServiceReceiver = new NotifyServiceReceiver();
		
		// Declare object of userFunctions class
		userFunctions = new UserFunctions();
		utils = new Utils(this);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
	 // TODO Auto-generated method stub
		Log.d("onBind", "onBind");
	 return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		 // TODO Auto-generated method stub
		 super.onStart(intent, startId);
		 
		// utils.savePreferences(utils.UTILS_NOTIF, 1);
		 Log.d("onStart", "onStart");
		 
		// Call asynctask class
     	new getDataAsync().execute();


	}

	@Override
	public boolean onUnbind(Intent intent) {
		 // TODO Auto-generated method stub
		Log.d("onUnbind", "onUnbind");
		 Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
		 return super.onUnbind(intent);
	}


	public void notifikasi(){
		Log.d("notifikasi", "notifikasi");
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION);
		registerReceiver(notifyServiceReceiver, intentFilter);

		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.logo)
		        .setContentTitle(getString(R.string.app_name))
		        .setSound(alarmSound)
		        .setContentText(getString(R.string.app_name)+"! "+getString(R.string.alert_notification));
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, ActivityHome.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ActivityHome.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());

	}
	
	
	public class NotifyServiceReceiver extends BroadcastReceiver{
	
		@Override
		public void onReceive(Context arg0, Intent arg1) {
		 // TODO Auto-generated method stub
			 int rqs = arg1.getIntExtra("RQS", 0);
			 if (rqs == RQS_STOP_SERVICE){
				 stopSelf();
			 }
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		this.unregisterReceiver(notifyServiceReceiver);
		super.onDestroy();
	}
	
	// AsyncTask to Get Data from Server and put it on View Object
	public class getDataAsync extends AsyncTask<Void, Void, Void>{


    	
    	@Override
		 protected void onPreExecute() {
		  // TODO Auto-generated method stub
    		
    		
    	}

    	@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
    		
    		// Method to get Data from Server
			getDataFromServer();
			return null;
		}
    	
    	@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
    		if(json != null){

        		// Change id from string to int and get just the number
    			int mUtilsNotif= Integer.valueOf(utils.loadString(utils.UTILS_NOTIF).substring(1, 5));
    			int mIntDealId = Integer.valueOf(mLocationId.substring(1, 5));

    			if(intLengthData!=0 && (mUtilsNotif < mIntDealId)){
             		utils.saveString(utils.UTILS_NOTIF, mLocationId);
             		notifikasi();	
             	} else if (intLengthData!=0 && (mUtilsNotif > mIntDealId)) {
             		utils.saveString(utils.UTILS_NOTIF, mLocationId);
				}
    			
    		}
         	
		}
		
	}

	public void getDataFromServer(){
		try {
			json = userFunctions.getNotif();

			if(json != null){
				JSONArray jsonArray = json.getJSONArray(userFunctions.array_latest_place);
				intLengthData = jsonArray.length();
				
				if(intLengthData!=0){
					JSONObject jsonObj	= jsonArray.getJSONObject(0);
					mLocationId = jsonObj.getString(userFunctions.key_location_id);
				}
			}
		} catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
		}
		
	}
	

}
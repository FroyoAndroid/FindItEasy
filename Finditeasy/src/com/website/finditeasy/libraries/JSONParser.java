package com.website.finditeasy.libraries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
	
	private static JSONObject jObj = null;

	public JSONObject getJSONFromUrl(String url) {

		try {
	        HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
	        // Perform a GET request to Server for a JSON list of all the latest place
	        HttpUriRequest request = new HttpGet(url);
	        // Get the response that Server sends back
	        HttpResponse response = client.execute(request);
	        // Convert this response into an inputstream for the parser to use
	        InputStream atomInputStream = response.getEntity().getContent();
	               
	        BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
	        
	        String line;
	        String str = "";
            while ((line = in.readLine()) != null){
                str += line;
            }
            
            jObj = new JSONObject(str);
            
                               
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        	return null;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		// return JSON String
		return jObj;

	}

}

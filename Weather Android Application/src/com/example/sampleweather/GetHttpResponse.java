package com.example.sampleweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class GetHttpResponse {

	
	public static JSONObject getjson(Context context , String city, String requestaddress){
		Log.d("abhishek","The value of city is: "+ city);
		URL url = null;
		JSONObject json = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(String.format(requestaddress,city));
		} catch (MalformedURLException e) {
			Log.d("abhishek","Code jumped to catch probably because the url requested is malformed. The value of URL is: "+ url);
			return null;
		}
		
		if(url != null){
			Log.d("abhishek","The value of object url is: "+ url);
			try {
				connection = (HttpURLConnection) url.openConnection();
				/*x-api-key: An API key is required to access OpenWeatherMap API. This key can be obtained 
							 either by registering on their website or by using a common key x-api-key.*/
				if(connection!=null){
					connection.addRequestProperty("x-api-key", context.getString(R.string.app_id));
				}
				else{
					Log.d("abhishek","Check the value of connection: "+ connection);
				}
				BufferedReader inputreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer buf = null;
				buf = new StringBuffer(2048);
				String temp="";
				while((temp=inputreader.readLine())!=null){
					buf.append(temp).append("\n");
				}
				inputreader.close();
				
				try {
					json = new JSONObject(buf.toString());
					if(json!=null){
						Log.d("abhishek", "After creating the JsonObject and inside if: The value of json is" + json);
						if(json.getInt("cod") != 200){
							Log.d("abhishek", "Checking the value of cod: The value of cod in the response received is: " + json.getInt("cod"));
							return null;
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

}

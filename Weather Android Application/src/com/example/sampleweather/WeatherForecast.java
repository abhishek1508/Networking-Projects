package com.example.sampleweather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WeatherForecast extends Activity{

	private String city;
	private String forecast_url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&units=metric&cnt=3";
	private static String mDebug = WeatherForecast.class.getName();
	TextView dayname1;
	TextView date1;
	TextView temp1;
	TextView min_temp1;
	TextView max_temp1;
	TextView cond1;
	TextView windspeed1;
	TextView humid1;
	TextView dayname2;
	TextView date2;
	TextView temp2;
	TextView min_temp2;
	TextView max_temp2;
	TextView cond2;
	TextView windspeed2;
	TextView humid2;
	Calculate_Values val = null;
	ProgressDialog progress = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_forecast);
		Bundle bundle = getIntent().getExtras();
		city = bundle.getString("cityname");
		dayname1 = (TextView) findViewById(R.id.forecast_day1);
		date1 = (TextView) findViewById(R.id.date1);
		temp1 = (TextView) findViewById(R.id.temp_value_1);
		min_temp1 = (TextView) findViewById(R.id.temp_min_value_1);
		max_temp1 = (TextView) findViewById(R.id.temp_max_value_1);
		cond1 = (TextView) findViewById(R.id.cond_value_1);
		windspeed1 = (TextView) findViewById(R.id.wind_speed_value_day1);
		humid1 = (TextView) findViewById(R.id.humidity_value_day1);
		
		dayname2 = (TextView) findViewById(R.id.forecast_day2);
		date2 = (TextView) findViewById(R.id.date2);
		temp2 = (TextView) findViewById(R.id.temp_value_2);
		min_temp2 = (TextView) findViewById(R.id.temp_min_value_2);
		max_temp2 = (TextView) findViewById(R.id.temp_max_value_2);
		cond2 = (TextView) findViewById(R.id.cond_value_2);
		windspeed2 = (TextView) findViewById(R.id.wind_speed_value_day2);
		humid2 = (TextView) findViewById(R.id.humidity_value_day2);
		forecast_url = String.format(forecast_url, city);
		val = new Calculate_Values();
		DownloadWeatherData data = new DownloadWeatherData();
		data.execute(new String[] {forecast_url});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.weather_forecast, R.anim.current_conditions);
	}
	
	private class DownloadWeatherData extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = ProgressDialog.show(WeatherForecast.this, "Wait", "Your weather is downloading");
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			URL url = null;
			JSONObject json = null;
			HttpURLConnection connection = null;
			for(String weather_forecast: params){
			try {
				url = new URL(weather_forecast);
			} catch (MalformedURLException e) {
				Log.d(mDebug,"Code jumped to catch probably because the url requested is malformed. The value of URL is: "+ url);
				return null;
			}
			
			if(url != null){
				Log.d(mDebug,"The value of object url is: "+ url);
				try {
					connection = (HttpURLConnection) url.openConnection();
					/*x-api-key: An API key is required to access OpenWeatherMap API. This key can be obtained 
								 either by registering on their website or by using a common key x-api-key.*/
					if(connection!=null){
						connection.addRequestProperty("x-api-key", "2222");
					}
					else{
						Log.d(mDebug,"Check the value of connection: "+ connection);
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
							Log.d(mDebug, "After creating the JsonObject and inside if: The value of json is" + json);
							if(json.getInt("cod") != 200){
								Log.d(mDebug, "Checking the value of cod: The value of cod in the response received is: " + json.getInt("cod"));
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
			}
			return json;
			
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			long date;
			try {
				JSONArray jsonarr = null;
				if(result != null){
					jsonarr = result.getJSONArray("list");
					JSONObject json_one = jsonarr.getJSONObject(1);
					JSONObject json_two = jsonarr.getJSONObject(2);
					date = json_one.getLong("dt");
					if(val!=null){
						dayname1.setText(val.dayname(date));
						date1.setText(val.date(date));
						temp1.setText(val.convert_dble_to_string(val.getObjectJSONDouble(json_one, "temp", "day"))+(char)0x00b0+"C");
						min_temp1.setText(val.convert_dble_to_string(val.getObjectJSONDouble(json_one, "temp", "min"))+(char)0x00b0+"C");
						max_temp1.setText(val.convert_dble_to_string(val.getObjectJSONDouble(json_one, "temp", "max"))+(char)0x00b0+"C");
						cond1.setText(val.getArrayJSON(json_one, "weather", "description"));
						windspeed1.setText(json_one.getString("speed")+" mps");
						humid1.setText(json_one.getString("humidity")+" %");
					
						date = json_two.getLong("dt");
						dayname2.setText(val.dayname(date));
						date2.setText(val.date(date));
						temp2.setText(val.convert_dble_to_string(val.getObjectJSONDouble(json_two, "temp", "day"))+(char)0x00b0+"C");
						min_temp2.setText(val.convert_dble_to_string(val.getObjectJSONDouble(json_two, "temp", "min"))+(char)0x00b0+"C");
						max_temp2.setText(val.convert_dble_to_string(val.getObjectJSONDouble(json_two, "temp", "max"))+(char)0x00b0+"C");
						cond2.setText(val.getArrayJSON(json_two, "weather", "description"));
						windspeed2.setText(json_two.getString("speed")+" mps");
						humid2.setText(json_two.getString("humidity")+" %");
					}
					progress.dismiss();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}


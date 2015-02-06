package com.example.sampleweather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

public class Calculate_Values extends MainActivity{
	
	private static final int clearsky = 800;
	private static final int few_clouds = 801;
	private static final int scattered_clouds = 802;
	private static final int broken_clouds = 803;
	private static final int overcast_clouds = 804;
	private static String mInfo = Calculate_Values.class.getName();
	ImageView image;
	Context context;
	
	
	public Calculate_Values(){}
	/**
	 * Constructor
	 * The context and object of image view is initialized in the constructor which is passed from the
	 * parent class - MainActivity.class
	 */
	public Calculate_Values(Context context,ImageView image){
		this.context = context;
		this.image = image;
	}

	protected String dayname(long date){
		String day;
		Log.d(mInfo,"The value of date is: "+ date);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		Date d = new Date(date*1000);
		day = sdf.format(d);
		Log.d(mInfo,"The final dayname calculated is: "+ day);
		return day;
	}
	/**
	 * The method takes a variable of type long as the parameter and returns the date using the built
	 * is libraries SimpleDateformat and Calendar.
	 * 
	 * @param value	Epoch time passed from the parent
	 * @return String Actual date returned to the parent
	 */
	public String date(long value) {
		Log.d(mInfo,"The value of value is: "+ value);
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(value*1000);
	    Log.d(mInfo,"The final date calculated is: "+ date.format(calendar.getTime()));
	    return date.format(calendar.getTime());	    
	}
	/**
	 * 
	 * @param temperature Converts and Stores the value of temperature in Farenheit and Celsius. 
	 * @param ch This is a character which validates the check for Celsius/Farenheit
	 * @return integer It returns the rounded off temperature integer value to the parent. 
	 */
	protected int calculate_temp(double temperature,char ch){
		int temp = 0;
		double flag = 0.0;
		Log.d(mInfo,"The value of ch is: "+ ch);
		if(ch == 'c')
			temperature = temperature * 9/5 + 32;
		else
			temperature = (temperature-32)*5/9;
		Log.d(mInfo,"The value of temperature is: "+ temperature);
		flag = temperature;
		if(!Double.toString(flag).contains("-"))
			temperature+=0.5;		
		temp = (int) (temperature);
		Log.d(mInfo,"The value of temp is: "+ temp);
		return temp;
	}
	/**
	 * Converts value of type double to type integer.
	 * @return integer
	 */
	protected int convert_dble_to_integ(double temp){
		int tem;
		tem = (int) temp;
		return tem;
	}
	/**
	 * Converts value of type String to Double.
	 * @return String
	 */
	protected String convert_dble_to_string(double temp){
		return Double.toString(temp);
	}
	
	/**
	 * The method takes a JSONObject of type JSONArray and two strings as the parameter and returns the corresponding value from
	 * the json file to the parent as a String.
	 * @param json 
	 * @param str1
	 * @param str2
	 * @return String
	 * @throws JSONException
	 */
	protected String getArrayJSON(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONArray(str1).getJSONObject(0).getString(str2).toUpperCase(Locale.US);
		
	}
	/**
	 * The method takes a JSONObject of type JSONArray and two strings as the parameter and returns the corresponding value from
	 * the json file to the parent as a integer.
	 * @throws JSONException
	 */
	protected int getArray_JSON(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONArray(str1).getJSONObject(0).getInt(str2);	
	}
	/**
	 * The method takes a JSONObject and two strings as the parameter and returns the corresponding value from
	 * the json file to the parent as a long
	 * @throws JSONException
	 */
	protected long getObjectJSONLong(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONObject(str1).getLong(str2)*1000;
	}
	/**
	 * The method takes a JSONObject and two strings as the parameter and returns the corresponding value from
	 * the json file to the parent as a double
	 * @throws JSONException
	 */
	protected double getObjectJSONDouble(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONObject(str1).getDouble(str2);
	}
	/**
	 * The method takes a JSONObject and two strings as the parameter and returns the corresponding value from
	 * the json file to the parent as a String
	 * @throws JSONException
	 */
	protected String getObjectJSONString(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONObject(str1).getString(str2);
		
	}
	/**
	 * Sets the image for different weather based on the descriptions and timing parsed from the JSON File.
	 * @return int
	 */
	protected int weathericon(int id , long sunrise , long sunset){
		if(id == clearsky){
			long curtime = new Date().getTime();
			if(curtime >= sunrise && curtime < sunset)
				image.setImageResource(R.drawable.clear_sky);
			else
				image.setImageResource(R.drawable.clear_sky_night);
			return 1;
		}
		if(id > clearsky){
			switch(id){
				case few_clouds:
					image.setImageResource(R.drawable.few_clouds);
					break;
				case scattered_clouds:
					image.setImageResource(R.drawable.scattered);
					break;
				case broken_clouds:
					image.setImageResource(R.drawable.broken);
					break;
				case overcast_clouds: 
					image.setImageResource(R.drawable.overcast);
					break;
				default:
					image.setImageResource(R.drawable.clear_sky);
					break;
			}
			return 1;
		}
		else{
			int tempid = id/100;
			switch(tempid){
				case 2:
					image.setImageResource(R.drawable.thunder);
					break;
				case 3:
					image.setImageResource(R.drawable.drizzle);
					break;
				case 5:
					image.setImageResource(R.drawable.rain);
					break;
				case 6:
					image.setImageResource(R.drawable.snow);
					break;
				case 7:
					image.setImageResource(R.drawable.atmosphere);
					break;
				default:
					image.setImageResource(R.drawable.clear_sky);
					break;
			}
			return 1;
		}

	}
}

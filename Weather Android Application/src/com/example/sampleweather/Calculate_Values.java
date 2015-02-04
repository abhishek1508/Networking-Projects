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
	ImageView image;
	Context context;
	
	
	public Calculate_Values(){}
	public Calculate_Values(Context context,ImageView image){
		this.context = context;
		this.image = image;
	}

	protected String dayname(long date){
		String day;
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		Date d = new Date(date*1000);
		day = sdf.format(d);
		Log.d("asynctask" , "dayname: "+ day);
		return day;
	}
	
	public String date(long value) {
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy");
		Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(value*1000);
	    return date.format(calendar.getTime());

	    
	}

	
	protected int calculate_temp(double temperature,char ch){
		int temp = 0;
		double flag = 0.0;
		if(ch == 'c')
			temperature = temperature * 9/5 + 32;
		else
			temperature = (temperature-32)*5/9;
		flag = temperature;
		if(!Double.toString(flag).contains("-"))
			temperature+=0.5;		
		temp = (int) (temperature);
		return temp;
	}
	
	protected int convert_dble_to_integ(double temp){
		int tem;
		tem = (int) temp;
		return tem;
	}
	
	protected String convert_dble_to_string(double temp){
		return Double.toString(temp);
	}
	
	protected String getArrayJSON(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONArray(str1).getJSONObject(0).getString(str2).toUpperCase(Locale.US);
		
	}
	
	protected int getArray_JSON(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONArray(str1).getJSONObject(0).getInt(str2);	
	}
	
	protected long getObjectJSONLong(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONObject(str1).getLong(str2)*1000;
	}
	
	protected double getObjectJSONDouble(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONObject(str1).getDouble(str2);
	}
	
	protected String getObjectJSONString(JSONObject json, String str1 , String str2) throws JSONException{
		return json.getJSONObject(str1).getString(str2);
		
	}

	protected int weathericon(int id , long sunrise , long sunset){
		if(id == clearsky){
			long curtime = new Date().getTime();
			if(curtime >= sunrise && curtime < sunset)
				image.setImageResource(R.drawable.sunny);
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
					image.setImageResource(R.drawable.example);
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
					image.setImageResource(R.drawable.example);
					break;
			}
			return 1;
		}

	}
}

package com.example.sampleweather;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener, Display{

	Handler handler;
	Button ok;
	Button forecast;
	ImageView image;
	EditText edittext;
	Switch toggleswitch;
	TextView cityname;
	TextView countryname;
	TextView description;
	TextView current_temp;
	TextView current_min_temp;
	TextView current_max_temp;
	TextView humidity;
	TextView windspeed;
	TextView pressure;
	RelativeLayout relative;

	String city = null;
	String currentweather = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
	private double farenhiet_curr;
	private double celcius_curr;
	private double far_min;
	private double far_max;
	private double cel_min;
	private double cel_max;
	private int cal_temp;
	Calculate_Values val = null;
	CountryAbbreviation abbreviate = null;
	ProgressDialog progress = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_conditions);
		handler = new Handler();
		relative = (RelativeLayout) findViewById(R.id.relativelayout_parent);
		ok = (Button) findViewById(R.id.button_go);
		forecast = (Button) findViewById(R.id.button1);
		toggleswitch = (Switch) findViewById(R.id.switch1);
		cityname = (TextView) findViewById(R.id.cityname);
		countryname = (TextView) findViewById(R.id.countryname);
		description = (TextView) findViewById(R.id.description);
		current_temp = (TextView) findViewById(R.id.current_temp);
		current_min_temp = (TextView) findViewById(R.id.curr_min_temp);
		current_max_temp = (TextView) findViewById(R.id.curr_max_temp);
		humidity = (TextView) findViewById(R.id.humid_value);
		windspeed = (TextView) findViewById(R.id.windspeed_value);
		pressure = (TextView) findViewById(R.id.pressure_value);
		image = (ImageView) findViewById(R.id.weather_icon);
		edittext = (EditText) findViewById(R.id.entercity);
		val = new Calculate_Values(this.getApplicationContext(),image);
		abbreviate = new CountryAbbreviation();
		ok.setOnClickListener(this);
		toggleswitch.setChecked(true);
		toggleswitch.setOnCheckedChangeListener(this);
		forecast.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				city = edittext.getText().toString();
				if(!city.equals("")){
					Intent intent = new Intent(MainActivity.this , WeatherForecast.class);
					intent.putExtra("cityname", city);
					startActivity(intent);
					overridePendingTransition(R.anim.weather_forecast, R.anim.current_conditions);
				}
				else
					Toast.makeText(MainActivity.this, R.string.nocity , Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		String curr = current_temp.getText().toString();
		String min = current_min_temp.getText().toString();
		String max = current_max_temp.getText().toString();
		if(!isChecked){
			celcius_curr = val.calculate_temp(Double.parseDouble(curr), 'f');
			cal_temp = val.convert_dble_to_integ(celcius_curr);
			current_temp.setText(Integer.toString(cal_temp));
			
			cel_min = val.calculate_temp(Double.parseDouble(min), 'f');
			cal_temp = val.convert_dble_to_integ(cel_min);
			current_min_temp.setText(Integer.toString(cal_temp));
			
			cel_max = val.calculate_temp(Double.parseDouble(max), 'f');
			cal_temp = val.convert_dble_to_integ(cel_max);
			current_max_temp.setText(Integer.toString(cal_temp));
		}
		else{
			farenhiet_curr = val.calculate_temp(Double.parseDouble(curr), 'c');
			cal_temp = val.convert_dble_to_integ(farenhiet_curr);
			current_temp.setText(Integer.toString(cal_temp));
			
			far_min = val.calculate_temp(Double.parseDouble(min), 'c');
			cal_temp = val.convert_dble_to_integ(far_min);
			current_min_temp.setText(Integer.toString(cal_temp));
			
			far_max = val.calculate_temp(Double.parseDouble(max), 'c');
			cal_temp = val.convert_dble_to_integ(far_max);
			current_max_temp.setText(Integer.toString(cal_temp));
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		city = edittext.getText().toString();
		
		Log.d("abhishek" , "inside onClick: The value of city is  "+ city);
		if(!city.equals("")){
			progress = ProgressDialog.show(MainActivity.this, "Wait", "Downloading your weather");
			displayresult(city);
		}
		else
			Toast.makeText(MainActivity.this, R.string.nocity , Toast.LENGTH_SHORT).show();
	}

	public void displayresult(final String city) {
		// TODO Auto-generated method stub
		new Thread(){
			public void run(){
				final JSONObject json = GetHttpResponse.getjson(getApplicationContext(), city, currentweather);
				if(json == null){
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress.dismiss();
							Toast.makeText(getApplicationContext(), R.string.app_warning, Toast.LENGTH_SHORT).show();
						}
					});
				}
				else{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							relative.getChildAt(1).setVisibility(View.GONE);
							relative.getChildAt(2).setVisibility(View.VISIBLE);
							reportweather(json,city);
						}
					});
				}
			}
			
		}.start();
		
	}

	
	public void reportweather(JSONObject json , String city) {
		try {
			if(json.getString("name").equals(""))
				cityname.setText(city.toUpperCase(Locale.US));
			else
				cityname.setText(json.getString("name").toUpperCase(Locale.US));
			String str = val.getObjectJSONString(json, "sys", "country");
			if(str.length() <= 2)
				countryname.setText(abbreviate.getCountryname(str));
			else
				countryname.setText(str);
			//countryname.setText(val.getObjectJSONString(json, "sys", "country"));
			description.setText(val.getArrayJSON(json, "weather", "description"));
			celcius_curr = val.getObjectJSONDouble(json, "main", "temp");
			cel_min = val.getObjectJSONDouble(json, "main", "temp_min");
			cel_max = val.getObjectJSONDouble(json, "main", "temp_max");
			if(toggleswitch.isChecked()){
				farenhiet_curr = val.calculate_temp(celcius_curr , 'c');
				far_min = val.calculate_temp(cel_min , 'c');
				far_max = val.calculate_temp(cel_max , 'c');
				cal_temp = val.convert_dble_to_integ(farenhiet_curr);
				current_temp.setText(Integer.toString(cal_temp));
				cal_temp = val.convert_dble_to_integ(far_min);
				current_min_temp.setText(Integer.toString(cal_temp));
				cal_temp = val.convert_dble_to_integ(far_max);
				current_max_temp.setText(Integer.toString(cal_temp));
			}
			else{
				celcius_curr+=0.5;
				cel_min+=0.5;
				cel_max+=0.5;
				cal_temp = val.convert_dble_to_integ(celcius_curr);
				current_temp.setText(Integer.toString(cal_temp));
				cal_temp = val.convert_dble_to_integ(cel_min);
				current_min_temp.setText(Integer.toString(cal_temp));
				cal_temp = val.convert_dble_to_integ(cel_max);
				current_max_temp.setText(Integer.toString(cal_temp));
			}
			humidity.setText(val.getObjectJSONString(json, "main", "humidity"));
			pressure.setText(val.getObjectJSONString(json, "main", "pressure"));
			windspeed.setText(val.getObjectJSONString(json, "wind", "speed"));
			val.weathericon(val.getArray_JSON(json, "weather", "id") , val.getObjectJSONLong(json, "sys", "sunrise"),
					val.getObjectJSONLong(json, "sys", "sunset"));
			progress.dismiss();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void displayresult(JSONObject json) {
		// TODO Auto-generated method stub
		
	}
	
}

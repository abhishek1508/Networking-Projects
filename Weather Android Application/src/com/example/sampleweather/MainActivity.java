package com.example.sampleweather;

import java.lang.reflect.Method;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author ABHISHEK
 * 
 */
public class MainActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener, Display {

	Handler handler;
	Button ok;
	Button forecast;
	ImageView image;
	AutoCompleteTextView mAuto;
	// EditText edittext;
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
	RelativeLayout layout1;
	RelativeLayout layout2;

	private String city = null;
	private static String mDebug = MainActivity.class.getName();
	private String currentweather = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
	private double farenhiet_curr;
	private double celcius_curr;
	private double far_min;
	private double far_max;
	private double cel_min;
	private double cel_max;
	private int cal_temp;
	private boolean isvisible = false;
	private boolean mDisablebutton = false;
	private boolean mActivescreen = false;
	Calculate_Values val = null;
	CountryAbbreviation abbreviate = null;
	ProgressDialog progress = null;
	Context context = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {

		super.onResume();
		boolean mMobile = false;
		/*
		 * The method is called as per the activity lifecycle. When the
		 * application is started the method checks to see if the network state
		 * of the device is turned on/off and guides the user accordingly.
		 */
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			/*
			 * Reflections to use the method of the class
			 * android.net.ConnectivityManager to access private/protected
			 * methods.
			 */
			Class mClass = Class.forName(manager.getClass().getName());
			Method mMethod = mClass.getDeclaredMethod("getMobileDataEnabled");
			mMethod.setAccessible(true);
			mMobile = (Boolean) mMethod.invoke(manager);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WifiManager mWifi = (WifiManager) getSystemService(WIFI_SERVICE);
		if (mWifi != null && !mWifi.isWifiEnabled() && !mMobile) {
			Toast.makeText(MainActivity.this, R.string.network_message,
					Toast.LENGTH_LONG).show();
			mDisablebutton = true;
		} else
			mDisablebutton = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_conditions);
		context = MainActivity.this;
		handler = new Handler();
		layout1 = (RelativeLayout) findViewById(R.id.relativelayout_child1);
		layout2 = (RelativeLayout) findViewById(R.id.relativelayout_child2);
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
		mAuto = (AutoCompleteTextView) findViewById(R.id.entercity);
		val = new Calculate_Values(this.getApplicationContext(), image);
		abbreviate = new CountryAbbreviation();
		ok.setOnClickListener(this);
		toggleswitch.setChecked(true);
		toggleswitch.setOnCheckedChangeListener(this);
		/*
		 * AutoCompleteTextView is used to enable the user to choose from a list
		 * of cities that popup once the user starts in typing the city name. An
		 * array of strings is created and the android provided list view is
		 * inflated when clicked on TextView.
		 */
		String[] mCities = getResources()
				.getStringArray(R.array.list_of_cities);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, mCities);
		mAuto.setAdapter(mAdapter);
		mAuto.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				/*
				 * Display the weather conditions when user presses the Done
				 * button on the soft keyboard.
				 */
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					city = mAuto.getText().toString();
					check_network_onPress(city, mDisablebutton);					
				}
				return false;
			}
		});
		forecast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				city = mAuto.getText().toString();
				/*
				 * Conditions to check if the network connection is on/off in
				 * middle of the application and displays appropriate message
				 * depending on the results obtained.
				 */
				if (city.equals("") && mDisablebutton)
					Toast.makeText(MainActivity.this,
							R.string.city_network_message, Toast.LENGTH_SHORT)
							.show();
				else if (city.equals("") && !mDisablebutton)
					Toast.makeText(MainActivity.this, R.string.nocity,
							Toast.LENGTH_SHORT).show();
				else if (!city.equals("") && mDisablebutton)
					Toast.makeText(MainActivity.this, R.string.network_message,
							Toast.LENGTH_SHORT).show();
				else {
					Intent intent = new Intent(MainActivity.this,
							WeatherForecast.class);
					intent.putExtra("cityname", city);
					startActivity(intent);
					overridePendingTransition(R.anim.weather_forecast,
							R.anim.current_conditions);
				}
			}
		});
	}

	
	private void check_network_onPress(String city, boolean value){
		if (city.equals("") && value)
			Toast.makeText(MainActivity.this,
					R.string.city_network_message, Toast.LENGTH_SHORT)
					.show();
		else if (city.equals("") && !value)
			Toast.makeText(MainActivity.this, R.string.nocity,
					Toast.LENGTH_SHORT).show();
		else if (!city.equals("") && value)
			Toast.makeText(MainActivity.this, R.string.network_message,
					Toast.LENGTH_SHORT).show();
		else {
			progress = ProgressDialog.show(MainActivity.this, "Wait",
					"Downloading your weather");
			displayresult(city);
		}
	}
	/**
	 * The method is written to convert the temperature from Celsius to
	 * Fahrenheit and vice versa by clicking on the switch.
	 * 
	 * @param buttonView
	 *            An object of the widget class CompoundButton which is used for
	 *            all specific types of two state buttons. Example: Radio
	 *            button, Checkbox, Switch
	 * @param isChecked
	 *            A boolean variable which states if the button/switch is in a
	 *            selected state or an unselected state
	 * @return No return type.
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		String curr = current_temp.getText().toString();
		String min = current_min_temp.getText().toString();
		String max = current_max_temp.getText().toString();
		if (!isChecked) {
			celcius_curr = val.calculate_temp(Double.parseDouble(curr), 'f');
			cal_temp = val.convert_dble_to_integ(celcius_curr);
			current_temp.setText(Integer.toString(cal_temp));

			cel_min = val.calculate_temp(Double.parseDouble(min), 'f');
			cal_temp = val.convert_dble_to_integ(cel_min);
			current_min_temp.setText(Integer.toString(cal_temp));

			cel_max = val.calculate_temp(Double.parseDouble(max), 'f');
			cal_temp = val.convert_dble_to_integ(cel_max);
			current_max_temp.setText(Integer.toString(cal_temp));
		} else {
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

	/**
	 * This method is the onClick of the button OK. The method gives instruction
	 * to the program to carry out specific functions when clicked on the
	 * button.
	 * 
	 * @param v
	 *            Object of the class View.
	 * @return No return type.
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		city = mAuto.getText().toString();
		Log.d(mDebug, "inside onClick(). The value of city is: " + city);
		if (context != null) {
			/*
			 * InputMethodManager is used to hide the soft keyboard
			 * automatically when the user presses the OK button after typin in
			 * the name of the city.
			 */
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(
					this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		check_network_onPress(city, mDisablebutton);
	/*	if (city.equals("") && mDisablebutton)
			Toast.makeText(MainActivity.this,
					R.string.city_network_message, Toast.LENGTH_SHORT)
					.show();
		else if (city.equals("") && !mDisablebutton)
			Toast.makeText(MainActivity.this, R.string.nocity,
					Toast.LENGTH_SHORT).show();
		else if (!city.equals("") && mDisablebutton)
			Toast.makeText(MainActivity.this, R.string.network_message,
					Toast.LENGTH_SHORT).show();
		else {
			progress = ProgressDialog.show(MainActivity.this, "Wait",
					"Downloading your weather");
			displayresult(city);
		}*/
		/*if (!city.equals("") && !mDisablebutton) {
			progress = ProgressDialog.show(MainActivity.this, "Wait",
					"Downloading your weather");
			displayresult(city);
		} else
			Toast.makeText(MainActivity.this, R.string.nocity,
					Toast.LENGTH_SHORT).show();*/
	}

	/**
	 * The method is invoked to send a HttpRequest to the server with the name
	 * of the city entered by the user. The HttpRequest is sent in a separate
	 * thread and the result is used to set the values for the various fields
	 * using Handler.
	 * 
	 * @param city
	 *            A String variable entered by the user for which the weather
	 *            details needs to be found.
	 * @return No return type
	 */
	public void displayresult(final String city) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				final JSONObject json = GetHttpResponse.getjson(
						getApplicationContext(), city, currentweather);
				if (json == null) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress.dismiss();
							Toast.makeText(getApplicationContext(),
									R.string.app_warning, Toast.LENGTH_SHORT)
									.show();
						}
					});
				} else {
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							/*
							 * if(!mActivescreen) showvisibility();
							 */
							reportweather(json, city);
						}
					});
				}
			}

		}.start();

	}

	/**
	 * This method is used to fill the values for weather conditions as obtained
	 * by the Http request.
	 * 
	 * @param json
	 *            This is an object of the class JSONObject.
	 * @param city
	 *            A String variable entered by the user for which the weather
	 *            details needs to be found.
	 */
	public void reportweather(JSONObject json, String city) {
		try {
			if (json != null && val != null) {
				Log.d(mDebug,
						"inside the method reportweather(). The value of json is: "
								+ json + " and val is: " + val);
				if (json.getString("name").equals(""))
					cityname.setText(city.toUpperCase(Locale.US));
				else
					cityname.setText(json.getString("name").toUpperCase(
							Locale.US));
				String str = val.getObjectJSONString(json, "sys", "country");
				if (str.length() <= 2)
					countryname.setText(abbreviate.getCountryname(str));
				else
					countryname.setText(str);
				description.setText(val.getArrayJSON(json, "weather",
						"description"));
				celcius_curr = val.getObjectJSONDouble(json, "main", "temp");
				cel_min = val.getObjectJSONDouble(json, "main", "temp_min");
				cel_max = val.getObjectJSONDouble(json, "main", "temp_max");

				/*
				 * Reads the value of temperature from the Json file received as
				 * a result of HttpRequest and rounds the decimal values into
				 * integers.
				 */
				if (toggleswitch.isChecked()) {
					farenhiet_curr = val.calculate_temp(celcius_curr, 'c');
					far_min = val.calculate_temp(cel_min, 'c');
					far_max = val.calculate_temp(cel_max, 'c');
					cal_temp = val.convert_dble_to_integ(farenhiet_curr);
					current_temp.setText(Integer.toString(cal_temp));
					cal_temp = val.convert_dble_to_integ(far_min);
					current_min_temp.setText(Integer.toString(cal_temp));
					cal_temp = val.convert_dble_to_integ(far_max);
					current_max_temp.setText(Integer.toString(cal_temp));
				} else {
					celcius_curr += 0.5;
					cel_min += 0.5;
					cel_max += 0.5;
					cal_temp = val.convert_dble_to_integ(celcius_curr);
					current_temp.setText(Integer.toString(cal_temp));
					cal_temp = val.convert_dble_to_integ(cel_min);
					current_min_temp.setText(Integer.toString(cal_temp));
					cal_temp = val.convert_dble_to_integ(cel_max);
					current_max_temp.setText(Integer.toString(cal_temp));
				}
				humidity.setText(val.getObjectJSONString(json, "main",
						"humidity"));
				pressure.setText(val.getObjectJSONString(json, "main",
						"pressure"));
				windspeed.setText(val
						.getObjectJSONString(json, "wind", "speed"));
				val.weathericon(val.getArray_JSON(json, "weather", "id"),
						val.getObjectJSONLong(json, "sys", "sunrise"),
						val.getObjectJSONLong(json, "sys", "sunset"));
				progress.dismiss();
				if (!mActivescreen)
					showvisibility();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * The method overrides the onBackPressed method of an Activity. It allows
	 * the user to navigate.
	 * 
	 * @return No return type
	 */
	@Override
	public void onBackPressed() {
		Log.d(mDebug,
				"inside the method onBackPressed(). The value of isVisible is: "
						+ isvisible);
		if (isvisible)
			showvisibility();
		else
			/*
			 * A final call to close the application.
			 */
			finish();
	}

	/**
	 * The method controls the visibility of the layouts used in the app and
	 * displays them accordingly.
	 * 
	 * @return No return type
	 */
	public void showvisibility() {
		Log.d(mDebug,
				"inside the method showvisibility(). The value of isVisible is: "
						+ isvisible + " and mActivescreen is: " + mActivescreen);
		if (!isvisible) {
			/*
			 * Set the visibility of the child relative layouts withh id
			 * relativelayout_child1 and relativelayout_child2.
			 */
			layout1.setVisibility(View.GONE);
			layout2.setVisibility(View.VISIBLE);
			isvisible = true;
			mActivescreen = true;
		} else {
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.GONE);
			isvisible = false;
			mActivescreen = false;
		}
	}

}

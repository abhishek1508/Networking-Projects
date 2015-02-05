package com.example.sampleweather;

import org.json.JSONObject;

public interface Display {

	public void displayresult(final String city);
	public void reportweather(JSONObject json,String city);
}

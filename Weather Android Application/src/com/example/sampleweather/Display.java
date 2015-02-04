package com.example.sampleweather;

import org.json.JSONObject;

public interface Display {

	public void displayresult(final String city);
	public void displayresult(final JSONObject json);
	public void reportweather(JSONObject json,String city);
}

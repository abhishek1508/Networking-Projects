package com.example.sampleweather;

import java.util.Locale;

public class CountryAbbreviation {

	
	protected String getCountryname(String str){
		Locale locale = new Locale("en", str);
		return locale.getDisplayCountry();
	}
}

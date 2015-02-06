package com.example.sampleweather;

import java.util.Locale;

public class CountryAbbreviation {

	/**
	 * The api provides a two letter abbreviation for many countries. The library Locale is used to convert the 
	 * abbreviation to full Country name.
	 * @param str
	 * @return
	 */
	protected String getCountryname(String str){
		Locale locale = new Locale("en", str);
		return locale.getDisplayCountry();
	}
}

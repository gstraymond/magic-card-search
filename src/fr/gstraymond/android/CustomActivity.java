package fr.gstraymond.android;

import android.app.Activity;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class CustomActivity extends Activity {

	public CustomApplication getCustomApplication() {
		return (CustomApplication) getApplication();
	}

	public boolean isTablet() {
		return getCustomApplication().isTablet();
	}

	public boolean isSmartphone() {
		return !isTablet();
	}
	
	protected ObjectMapper getObjectMapper() {
		return getCustomApplication().getObjectMapper();
	}
}

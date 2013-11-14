package fr.gstraymond.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import fr.gstraymond.R;
import fr.gstraymond.biz.ApplicationLoader;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new ApplicationLoader(this).execute();
	}
	
	public void startNextActivity() {
		// This method will be executed once the timer is over
		// Start your app main activity
		Intent i = new Intent(SplashScreen.this,
				MagicCardListActivity.class);
		startActivity(i);

		// close this activity
		finish();
	}
}

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
		Intent intent = new Intent(this, MagicCardListActivity.class);
		startActivity(intent);

		finish();
	}
}

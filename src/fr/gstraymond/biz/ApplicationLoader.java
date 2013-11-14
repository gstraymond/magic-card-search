package fr.gstraymond.biz;

import android.os.AsyncTask;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.SplashScreen;

public class ApplicationLoader extends AsyncTask<Void, Void, Void> {

	private SplashScreen splashScreen;
	
	public ApplicationLoader(SplashScreen splashScreen) {
		super();
		this.splashScreen = splashScreen;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		getCustomApplication().init();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		splashScreen.startNextActivity();
	}


	private CustomApplication getCustomApplication() {
		return (CustomApplication) splashScreen.getApplicationContext();
	}
}

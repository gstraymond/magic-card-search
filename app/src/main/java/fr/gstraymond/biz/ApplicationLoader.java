package fr.gstraymond.biz;

import android.os.AsyncTask;

import fr.gstraymond.android.SplashScreenActivity;

public class ApplicationLoader extends AsyncTask<Void, Void, Void> {

    private SplashScreenActivity splashScreenActivity;

    public ApplicationLoader(SplashScreenActivity splashScreenActivity) {
        super();
        this.splashScreenActivity = splashScreenActivity;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        splashScreenActivity.getCustomApplication().init();
        return null;
    }
}

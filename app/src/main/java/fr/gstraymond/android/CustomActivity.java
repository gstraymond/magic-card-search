package fr.gstraymond.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

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

    protected void replaceFragment(Fragment fragment, int id) {
        replaceFragment(fragment, id, null);
    }

    protected void replaceFragment(Fragment fragment, int id, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        getFragmentManager().beginTransaction().replace(id, fragment).commit();
    }
}

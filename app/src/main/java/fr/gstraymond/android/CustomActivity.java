package fr.gstraymond.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gstraymond.db.json.JsonHistoryDataSource;

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

    public ObjectMapper getObjectMapper() {
        return getCustomApplication().getObjectMapper();
    }

    public JsonHistoryDataSource getJsonHistoryDataSource() { return getCustomApplication().getJsonHistoryDataSource(); }

    public void replaceFragment(Fragment fragment, int id) {
        replaceFragment(fragment, id, null);
    }

    public void replaceFragment(Fragment fragment, int id, Bundle bundle) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        getFragmentManager().beginTransaction().replace(id, fragment).commitAllowingStateLoss();
    }

    protected void actionBarSetDisplayHomeAsUpEnabled(boolean bool) {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(bool);
        }
    }

    protected void actionBarSetHomeButtonEnabled(boolean bool) {
        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(bool);
        }
    }

    protected void actionBarSetTitle(int titleId) {
        if (getActionBar() != null) {
            getActionBar().setTitle(titleId);
        }
    }

    protected ContentViewEvent buildContentViewEvent() {
        return new ContentViewEvent().putContentName(getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Answers.getInstance().logContentView(buildContentViewEvent());
    }
}

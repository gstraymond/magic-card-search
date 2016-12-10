package fr.gstraymond.android;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gstraymond.db.json.JsonHistoryDataSource;

public abstract class CustomActivity extends AppCompatActivity {

    public CustomApplication getCustomApplication() {
        return (CustomApplication) getApplication();
    }

    public ObjectMapper getObjectMapper() {
        return getCustomApplication().getObjectMapper();
    }

    public JsonHistoryDataSource getJsonHistoryDataSource() {
        return getCustomApplication().getJsonHistoryDataSource();
    }

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
        return new ContentViewEvent()
                .putContentName(getClass().getSimpleName())
                .putCustomAttribute("isTablet", isTablet() + "");
    }

    private boolean isTablet() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Answers.getInstance().logContentView(buildContentViewEvent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}

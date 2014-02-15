package fr.gstraymond.tools;

import android.app.Activity;
import android.content.Intent;
import fr.gstraymond.android.CardDetailActivity;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.android.CardPagerActivity;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.HelpActivity;

public class ActivityUtil {

	public static Intent getIntent(Activity currentActivity,
			Class<? extends Activity> nextActivityClass) {
		Class<? extends Activity> activityClass = nextActivityClass;

		if (isTablet(currentActivity)) {
			activityClass = getTabletActivity(nextActivityClass);
		}
		return new Intent(currentActivity, activityClass);
	}

	private static Class<? extends Activity> getTabletActivity(
			Class<? extends Activity> nextActivityClass) {
		if (CardListActivity.class.equals(nextActivityClass)) {
			return fr.gstraymond.android.tablet.CardListActivity.class;
		}

		if (CardDetailActivity.class.equals(nextActivityClass)) {
			return fr.gstraymond.android.tablet.CardDetailActivity.class;
		}

		if (CardPagerActivity.class.equals(nextActivityClass)) {
			return fr.gstraymond.android.tablet.CardPagerActivity.class;
		}

		if (HelpActivity.class.equals(nextActivityClass)) {
			return fr.gstraymond.android.tablet.HelpActivity.class;
		}

		return null;
	}
	
	private static boolean isTablet(Activity activity) {
		CustomApplication application = (CustomApplication) activity.getApplication();
		return application.isTablet();
	}
}

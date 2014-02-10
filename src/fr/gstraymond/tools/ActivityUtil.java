package fr.gstraymond.tools;

import android.app.Activity;
import android.content.Intent;
import fr.gstraymond.R;
import fr.gstraymond.android.HelpActivity;
import fr.gstraymond.android.MagicCardDetailActivity;
import fr.gstraymond.android.MagicCardListActivity;
import fr.gstraymond.android.MagicCardPagerActivity;

public class ActivityUtil {

	private static final String TABLET = "tablet";

	public static Intent getIntent(Activity currentActivity,
			Class<? extends Activity> nextActivityClass) {
		Class<? extends Activity> activityClass = nextActivityClass;

		String mode = currentActivity.getString(R.string.mode);
		if (TABLET.equals(mode)) {
			activityClass = getTabletActivity(nextActivityClass);
		}
		return new Intent(currentActivity, activityClass);
	}

	private static Class<? extends Activity> getTabletActivity(
			Class<? extends Activity> nextActivityClass) {
		if (MagicCardListActivity.class.equals(nextActivityClass)) {
			return fr.gstraymond.android.tablet.MagicCardListActivity.class;
		}

		if (MagicCardDetailActivity.class.equals(nextActivityClass)) {
			return fr.gstraymond.android.tablet.MagicCardDetailActivity.class;
		}

		if (MagicCardPagerActivity.class.equals(nextActivityClass)) {
			return fr.gstraymond.android.tablet.MagicCardPagerActivity.class;
		}

		if (HelpActivity.class.equals(nextActivityClass)) {
			return fr.gstraymond.android.tablet.HelpActivity.class;
		}

		return null;
	}
}

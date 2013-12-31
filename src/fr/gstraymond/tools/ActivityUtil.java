package fr.gstraymond.tools;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtil {

	public static Intent getIntent(Activity currentActivity,
			Class<? extends Activity> nextActivityClass) {
		Class<? extends Activity> activityClass = nextActivityClass;
		return new Intent(currentActivity, activityClass);
	}
}

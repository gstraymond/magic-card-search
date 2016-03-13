package fr.gstraymond.tools;

import android.content.Context;
import android.os.Build;

import fr.gstraymond.BuildConfig;
import fr.gstraymond.R;

public class VersionUtils {

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getAppName(Context context) {
        return context.getString(R.string.app_name);
    }
}

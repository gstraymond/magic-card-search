package fr.gstraymond.tools;

import android.content.Context;

import fr.gstraymond.android.CustomApplicationKt;

public class LanguageUtil {

    public static boolean showFrench(Context context) {
        return isLocaleFrench(context) && CustomApplicationKt.getPrefs().getFrenchEnabled();
    }

    public static boolean isLocaleFrench(Context context) {
        String language = context.getResources().getConfiguration().locale.getLanguage();
        return "fr".equals(language);
    }
}

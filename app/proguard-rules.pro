-dontwarn okio.**

-keep class fr.gstraymond.** { *; }
-keep class com.magic.card.search.commons.application.** { *; }

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
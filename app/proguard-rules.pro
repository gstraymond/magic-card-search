-dontwarn okio.**

-keep class fr.gstraymond.models.** { *; }
-keep class fr.gstraymond.network.ElasticSearchApi { *; }
-keep class com.magic.card.search.commons.application.** { *; }

-assumenosideeffects class android.util.Log  {
    public static int d(...);
    public static int v(...);
    public static int i(...);
    public static int e(...);
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# crashlytics
-keepattributes SourceFile,LineNumberTable
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# autovalue
-dontwarn javax.lang.**
-dontwarn javax.tools.**
-dontwarn javax.annotation.**
-dontwarn autovalue.shaded.com.**
-dontwarn com.google.auto.value.**

-dontwarn org.apache.velocity.**

-dontnote **
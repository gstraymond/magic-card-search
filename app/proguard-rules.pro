-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature

-keepnames class com.fasterxml.jackson.** { *; }

-dontwarn com.fasterxml.jackson.databind.**

-keepattributes InnerClasses

-keep class org.codehaus.** { *; }

-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *;
 }

-keep class fr.gstraymond.** { *; }
-keepclassmembers class fr.gstraymond.** { *; }

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
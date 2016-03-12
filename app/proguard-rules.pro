-keepattributes *Annotation*,EnclosingMethod,Signature

-keepnames class com.fasterxml.jackson.** { *; }

-dontwarn com.fasterxml.jackson.databind.**

-dontwarn com.amazon.**

-keepattributes InnerClasses

-keep class org.codehaus.** { *; }

-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *;
 }

-keep public class fr.gstraymond.** {
  public void set*(***);
  public *** get*();
}

#-assumenosideeffects class android.util.Log { *; }
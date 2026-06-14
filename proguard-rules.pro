# PDFBox rules
-keep class com.tom_roush.pdfbox.** { *; }
-keep interface com.tom_roush.pdfbox.** { *; }

# Apache POI rules
-keep class org.apache.poi.** { *; }
-keep interface org.apache.poi.** { *; }

# XMLBeans rules
-keep class org.apache.xmlbeans.** { *; }
-keep interface org.apache.xmlbeans.** { *; }

# Commons rules
-keep class org.apache.commons.** { *; }
-keep interface org.apache.commons.** { *; }

# Android rules
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keepclasseswithmembernames class * {
    native <methods>;
}

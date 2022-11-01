# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\angel\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes Signature

-keep class org.apache.** { *; }
-keep class net.grandcentrix.tray.** { *; }
-keep public class * extends android.content.ContentProvider

-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

-keepclassmembers class org.angelmariages.rodalieswidget.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Firebase crashlytics
-keepattributes SourceFile,LineNumberTable

-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }

# Retrofit
-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# Models
-keep public class org.angelmariages.rodalieswidget.timetables.schedules.strategies.rnfe.model.** { *; }
-keep public class org.angelmariages.rodalieswidget.timetables.schedules.strategies.rodalies.model.** { *; }

# (2)Simple XML
-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }

-keepattributes ElementList, Element, Signature, Root, *Annotation*

-keepclassmembers class * {
    @org.simpleframework.xml.* *;
}
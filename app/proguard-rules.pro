# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve file names and line numbers so Crashlytics/AppMetrica can show useful release stacktraces.
-keepattributes SourceFile,LineNumberTable

# Keep custom exception names meaningful in obfuscated crash reports.
-keep public class * extends java.lang.Exception
-keep public class * extends java.lang.RuntimeException

# VK ID references this optional tracing class through library metadata in release R8 builds.
-dontwarn ru.ok.tracer.manifest.TracerLiteManifest

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

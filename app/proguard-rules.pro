# Project-specific ProGuard rules for release build

# General
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses
-dontwarn okio.**
-dontwarn javax.annotation.**

# GSON rules (essential for parsing API responses in release mode)
-keep class com.google.gson.** { *; }
-keep class com.polycampus.android.**.POJO* { *; }
-keep class com.polycampus.android.**.Pojo* { *; }
-keepattributes Signature
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Glide rules (essential for image loading)
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# Android Async Http
-keep class com.loopj.android.http.** { *; }
-dontwarn com.loopj.android.http.**

# Volley
-keep class com.android.volley.** { *; }
-dontwarn com.android.volley.**

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Prevent obfuscating Activity/Fragment names used in introspection
-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
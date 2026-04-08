package com.polycampus.android.common;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private static final String PREF_NAME = "theme_prefs";
    private static final String KEY_IS_DARK_MODE = "is_dark_mode";

    public static void applyTheme(Context context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void toggleTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_IS_DARK_MODE, false);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_DARK_MODE, !isDark);
        editor.apply();
        applyTheme(context);
    }

    public static boolean isDarkMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_DARK_MODE, false);
    }
}

package com.example.spartan13.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by spartan13 on 16. 2. 2015.
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String DEFAULT_RATING = "default_rating";
    public static final String ORDER_BY = "order_by";
    public static final String DESC = "desc";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.settings);
    }

    public static boolean getOrderDesc(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx)
                .getBoolean(DESC, true);
    }

    public static String getStrategy(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getString(DEFAULT_RATING, "4");
    }
}
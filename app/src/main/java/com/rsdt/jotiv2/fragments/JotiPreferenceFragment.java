package com.rsdt.jotiv2.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.rsdt.jotiv2.R;

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 21-10-2015
 * The fragment that shows the preferences.
 */
public class JotiPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public static JotiPreferenceFragment newInstance() { return new JotiPreferenceFragment(); }

    public static final String TAG = "JOTI_PREFERENCE_FRAGMENT";

}

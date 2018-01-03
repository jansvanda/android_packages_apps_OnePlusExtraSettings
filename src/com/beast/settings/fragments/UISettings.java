package com.beast.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.beast.settings.preferences.CustomSeekBarPreference;

import java.util.Arrays;
import java.util.HashSet;

import com.android.settings.SettingsPreferenceFragment;

public class UISettings extends SettingsPreferenceFragment implements
OnPreferenceChangeListener {

    private static final String PREF_ON_THE_GO_ALPHA = "on_the_go_alpha";
 
    private CustomSeekBarPreference mOnTheGoAlphaPref;

    private static final String SYSTEMUI_THEME_STYLE = "systemui_theme_style";

    private ListPreference mSystemUIThemeStyle;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_ui);

        PreferenceScreen prefScreen = getPreferenceScreen(); 
        ContentResolver resolver = getActivity().getContentResolver(); 

       mOnTheGoAlphaPref = (CustomSeekBarPreference) findPreference(PREF_ON_THE_GO_ALPHA);
       float otgAlpha = Settings.System.getFloat(getContentResolver(),
		Settings.System.ON_THE_GO_ALPHA, 0.5f);
       final int alpha = ((int) (otgAlpha * 100));
       mOnTheGoAlphaPref.setValue(alpha);
       mOnTheGoAlphaPref.setOnPreferenceChangeListener(this);
 
        mSystemUIThemeStyle = (ListPreference) findPreference(SYSTEMUI_THEME_STYLE); 
        int systemUIThemeStyle = Settings.System.getInt(resolver, 
                Settings.System.SYSTEM_UI_THEME, 0); 
        int valueIndex = mSystemUIThemeStyle.findIndexOfValue(String.valueOf(systemUIThemeStyle)); 
        mSystemUIThemeStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0); 
        mSystemUIThemeStyle.setSummary(mSystemUIThemeStyle.getEntry()); 
        mSystemUIThemeStyle.setOnPreferenceChangeListener(this); 

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver(); 
        
               if (preference == mSystemUIThemeStyle) { 
                   String value = (String) objValue; 
                   Settings.System.putInt(resolver, 
                           Settings.System.SYSTEM_UI_THEME, Integer.valueOf(value)); 
                   int valueIndex = mSystemUIThemeStyle.findIndexOfValue(value); 
                   mSystemUIThemeStyle.setSummary(mSystemUIThemeStyle.getEntries()[valueIndex]); 
                   return true; 
               } 

           if (preference == mOnTheGoAlphaPref) {
             float val = (Integer) objValue;
             Settings.System.putFloat(getContentResolver(),
 		    Settings.System.ON_THE_GO_ALPHA, val / 100);
             return true;
         }
               
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }
}

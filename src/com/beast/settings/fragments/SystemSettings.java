package com.beast.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;

public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String MEDIA_SCANNER_ON_BOOT = "media_scanner_on_boot";
    private ListPreference mMSOB;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Context mContext = getActivity().getApplicationContext();
        addPreferencesFromResource(R.xml.beast_settings_system);

      mMSOB = (ListPreference) findPreference(MEDIA_SCANNER_ON_BOOT);
       int mMSOBValue = Settings.System.getInt(mContext.getContentResolver(),
               Settings.System.MEDIA_SCANNER_ON_BOOT, 0);
       mMSOB.setValue(String.valueOf(mMSOBValue));
       mMSOB.setSummary(mMSOB.getEntry());
       mMSOB.setOnPreferenceChangeListener(this);
     
    }

   @Override
   public boolean onPreferenceChange(Preference preference, Object newValue) {
       ContentResolver resolver = getActivity().getContentResolver();
       Context mContext = getActivity().getApplicationContext();
   if (preference == mMSOB) {
           int value = Integer.parseInt(((String) newValue).toString());
           Settings.System.putInt(resolver,
                   Settings.System.MEDIA_SCANNER_ON_BOOT, value);
           mMSOB.setValue(String.valueOf(value));
           mMSOB.setSummary(mMSOB.getEntries()[value]);
           return true;
       }
       return false;
   }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }
}

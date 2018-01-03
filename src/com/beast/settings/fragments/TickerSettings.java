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

import java.util.Arrays;
import java.util.HashSet;

import com.android.settings.SettingsPreferenceFragment;

public class TickerSettings extends SettingsPreferenceFragment implements
OnPreferenceChangeListener {

    private ListPreference mTickerMode;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_ticker);

        // mTickerMode = (ListPreference) findPreference("ticker_mode");
        // mTickerMode.setOnPreferenceChangeListener(this);
        // int tickerMode = Settings.System.getIntForUser(getActivity().getContentResolver(),
        //         Settings.System.STATUS_BAR_SHOW_TICKER,
        //         1, UserHandle.USER_CURRENT);
        // mTickerMode.setValue(String.valueOf(tickerMode));
        // mTickerMode.setSummary(mTickerMode.getEntry());

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
          if (preference.equals(mTickerMode)) {
            // int tickerMode = Integer.parseInt(((String) objValue).toString());
            // Settings.System.putIntForUser(getActivity().getContentResolver(),
            //         Settings.System.STATUS_BAR_SHOW_TICKER, tickerMode, UserHandle.USER_CURRENT);
            // int index = mTickerMode.findIndexOfValue((String) objValue);
            // mTickerMode.setSummary(
            //         mTickerMode.getEntries()[index]);
            // return true;
       }
               
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }
}

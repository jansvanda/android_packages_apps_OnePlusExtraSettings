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
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.util.Log;
import android.view.View;
import android.view.WindowManagerGlobal;
import android.os.RemoteException;

import java.util.Arrays;
import java.util.HashSet;

import com.android.settings.SettingsPreferenceFragment;

public class StatusBarBatterySaver extends SettingsPreferenceFragment implements
Preference.OnPreferenceChangeListener {

        private static final String TAG = "LowBatteryColor";
        private static final String STATUS_BAR_BATTERY_SAVER_COLOR = "status_bar_battery_saver_color";
        private ColorPickerPreference mBatterySaverColor;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_statusbatterysaver);
        PreferenceScreen prefSet = getPreferenceScreen();

        ContentResolver resolver = getActivity().getContentResolver();
          int batterySaverColor = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_BATTERY_SAVER_COLOR, 0xfff4511e);
        mBatterySaverColor = (ColorPickerPreference) findPreference("status_bar_battery_saver_color");
        mBatterySaverColor.setNewPreviewColor(batterySaverColor);
        mBatterySaverColor.setOnPreferenceChangeListener(this);
          enableStatusBarBatteryDependents();
      

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        AlertDialog dialog;
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference.equals(mBatterySaverColor)) {
                   int color = ((Integer) objValue).intValue();
                   Settings.Secure.putInt(resolver,
                           Settings.Secure.STATUS_BAR_BATTERY_SAVER_COLOR, color);
                   return true;
               }
       return false;
    }

    private void enableStatusBarBatteryDependents() {
               mBatterySaverColor.setEnabled(true);
          }

    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;

        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }
}

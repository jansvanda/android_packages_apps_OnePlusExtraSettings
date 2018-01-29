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

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_statusbatterysaver);
        PreferenceScreen prefSet = getPreferenceScreen();

        ContentResolver resolver = getActivity().getContentResolver();     

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        AlertDialog dialog;
        ContentResolver resolver = getActivity().getContentResolver();
   
       return false;
    }


    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }
}

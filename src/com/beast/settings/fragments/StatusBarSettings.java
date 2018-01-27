package com.beast.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import android.content.DialogInterface;
import android.provider.Settings.SettingNotFoundException;
import android.app.AlertDialog;
import android.widget.EditText;
import android.text.Spannable;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.beast.settings.preferences.CustomSeekBarPreference;
import com.beast.settings.preferences.SystemSettingSwitchPreference;
import com.android.settings.Utils;
import com.android.internal.util.beast.BeastUtils;
import android.util.Log;
import android.widget.LinearLayout;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private CustomSeekBarPreference mThreshold;
    private SystemSettingSwitchPreference mNetMonitor;

    
    private static final String TAG = "StatusBarWeather";

    private LinearLayout mView;

    private ListPreference mTickerMode;
    private ListPreference mLogoStyle;
    private ColorPickerPreference mStatusBarLogoColor;
    static final int DEFAULT_LOGO_COLOR = 0xff009688;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

           int intColor;
        String hexColor;

        boolean isNetMonitorEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE, 1, UserHandle.USER_CURRENT) == 1;
        mNetMonitor = (SystemSettingSwitchPreference) findPreference("network_traffic_state");
        mNetMonitor.setChecked(isNetMonitorEnabled);
        mNetMonitor.setOnPreferenceChangeListener(this);

        mLogoStyle = (ListPreference) findPreference("status_bar_logo_style");
        mLogoStyle.setOnPreferenceChangeListener(this);
        int logoStyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_LOGO_STYLE,
                0, UserHandle.USER_CURRENT);
        mLogoStyle.setValue(String.valueOf(logoStyle));
        mLogoStyle.setSummary(mLogoStyle.getEntry());

        mStatusBarLogoColor = (ColorPickerPreference) findPreference("status_bar_logo_color");
        mStatusBarLogoColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_LOGO_COLOR, DEFAULT_LOGO_COLOR);
        hexColor = String.format("#%08x", (DEFAULT_LOGO_COLOR & intColor));
        mStatusBarLogoColor.setSummary(hexColor);
        mStatusBarLogoColor.setNewPreviewColor(intColor);

        int value = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT);
        mThreshold = (CustomSeekBarPreference) findPreference("network_traffic_autohide_threshold");
        mThreshold.setValue(value);
        mThreshold.setOnPreferenceChangeListener(this);
        mThreshold.setEnabled(isNetMonitorEnabled);

         mTickerMode = (ListPreference) findPreference("ticker_mode");
         mTickerMode.setOnPreferenceChangeListener(this);
         int tickerMode = Settings.System.getIntForUser(getContentResolver(),
                 Settings.System.STATUS_BAR_SHOW_TICKER,
                 1, UserHandle.USER_CURRENT);
         mTickerMode.setValue(String.valueOf(tickerMode));
         mTickerMode.setSummary(mTickerMode.getEntry());
   
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNetMonitor) {
            boolean value = (Boolean) objValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_STATE, value ? 1 : 0,
                    UserHandle.USER_CURRENT);
            mNetMonitor.setChecked(value);
            mThreshold.setEnabled(value);
            return true;
        } else if (preference == mThreshold) {
            int val = (Integer) objValue;
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, val,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mTickerMode)) {
                int tickerMode = Integer.parseInt(((String) objValue).toString());
                Settings.System.putIntForUser(getContentResolver(),
                        Settings.System.STATUS_BAR_SHOW_TICKER, tickerMode, UserHandle.USER_CURRENT);
                int index = mTickerMode.findIndexOfValue((String) objValue);
                mTickerMode.setSummary(
                        mTickerMode.getEntries()[index]);
                return true;
        } else  if (preference.equals(mLogoStyle)) {
            int logoStyle = Integer.parseInt(((String) objValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO_STYLE, logoStyle, UserHandle.USER_CURRENT);
            int index = mLogoStyle.findIndexOfValue((String) objValue);
            mLogoStyle.setSummary(
                    mLogoStyle.getEntries()[index]);
            return true;
        } else if (preference.equals(mStatusBarLogoColor)) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO_COLOR, intHex);
            return true;
        } 
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }

}

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
 
    private static final String STATUS_BAR_BATTERY_SAVER_COLOR = "status_bar_battery_saver_color";
    private static final String CATEGORY_WEATHER = "weather_category";
    private static final String WEATHER_ICON_PACK = "weather_icon_pack";
    private static final String DEFAULT_WEATHER_ICON_PACKAGE = "org.omnirom.omnijaws";
    private static final String WEATHER_SERVICE_PACKAGE = "org.omnirom.omnijaws";
    private static final String CHRONUS_ICON_PACK_INTENT = "com.dvtonder.chronus.ICON_PACK";

    
    private static final String TAG = "StatusBarWeather";

    private LinearLayout mView;

    private ColorPickerPreference mBatterySaverColor;
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

         mWeatherCategory = (PreferenceCategory) prefSet.findPreference(CATEGORY_WEATHER);
          if (mWeatherCategory != null && !isOmniJawsServiceInstalled()) {
              prefSet.removePreference(mWeatherCategory);
          } else {
              String settingHeaderPackage = Settings.System.getString(getContentResolver(),
                      Settings.System.OMNIJAWS_WEATHER_ICON_PACK);
              if (settingHeaderPackage == null) {
                  settingHeaderPackage = DEFAULT_WEATHER_ICON_PACKAGE;
              }
              mWeatherIconPack = (ListPreference) findPreference(WEATHER_ICON_PACK);
    
              List<String> entries = new ArrayList<String>();
              List<String> values = new ArrayList<String>();
              getAvailableWeatherIconPacks(entries, values);
              mWeatherIconPack.setEntries(entries.toArray(new String[entries.size()]));
              mWeatherIconPack.setEntryValues(values.toArray(new String[values.size()]));
    
              int valueIndex = mWeatherIconPack.findIndexOfValue(settingHeaderPackage);
              if (valueIndex == -1) {
                  // no longer found
                  settingHeaderPackage = DEFAULT_WEATHER_ICON_PACKAGE;
                  Settings.System.putString(getContentResolver(),
                          Settings.System.OMNIJAWS_WEATHER_ICON_PACK, settingHeaderPackage);
                  valueIndex = mWeatherIconPack.findIndexOfValue(settingHeaderPackage);
              }
              mWeatherIconPack.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
              mWeatherIconPack.setSummary(mWeatherIconPack.getEntry());
              mWeatherIconPack.setOnPreferenceChangeListener(this);
          }

        int batterySaverColor = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_BATTERY_SAVER_COLOR, 0xfff4511e);
        mBatterySaverColor = (ColorPickerPreference) findPreference("status_bar_battery_saver_color");
        mBatterySaverColor.setNewPreviewColor(batterySaverColor);
        mBatterySaverColor.setOnPreferenceChangeListener(this);
 
        enableStatusBarBatteryDependents();
   
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
        } else  if (preference.equals(mBatterySaverColor)) {
             int color = ((Integer) objValue).intValue();
             Settings.Secure.putInt(getContentResolver(),
                     Settings.Secure.STATUS_BAR_BATTERY_SAVER_COLOR, color);
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
        } else if (preference == mWeatherIconPack) {
            String value = (String) objValue;
            Settings.System.putString(getContentResolver(),
                    Settings.System.OMNIJAWS_WEATHER_ICON_PACK, value);
            int valueIndex = mWeatherIconPack.findIndexOfValue(value);
            mWeatherIconPack.setSummary(mWeatherIconPack.getEntries()[valueIndex]);
            return true;     
          }
        return false;
    }


 private boolean isOmniJawsServiceInstalled() {
       return BeastUtils.isAvailableApp(WEATHER_SERVICE_PACKAGE, getActivity());
   }

   private void getAvailableWeatherIconPacks(List<String> entries, List<String> values) {
       Intent i = new Intent();
       PackageManager packageManager = getPackageManager();
       i.setAction("org.omnirom.WeatherIconPack");
       for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
           String packageName = r.activityInfo.packageName;
           if (packageName.equals(DEFAULT_WEATHER_ICON_PACKAGE)) {
               values.add(0, r.activityInfo.name);
           } else {
               values.add(r.activityInfo.name);
           }
           String label = r.activityInfo.loadLabel(getPackageManager()).toString();
           if (label == null) {
               label = r.activityInfo.packageName;
           }
           if (packageName.equals(DEFAULT_WEATHER_ICON_PACKAGE)) {
               entries.add(0, label);
           } else {
               entries.add(label);
           }
       }
       i = new Intent(Intent.ACTION_MAIN);
       i.addCategory(CHRONUS_ICON_PACK_INTENT);
       for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
           String packageName = r.activityInfo.packageName;
           values.add(packageName + ".weather");
           String label = r.activityInfo.loadLabel(getPackageManager()).toString();
           if (label == null) {
               label = r.activityInfo.packageName;
           }
           entries.add(label);
       }
   }

   private boolean isOmniJawsEnabled() {
       final Uri SETTINGS_URI
           = Uri.parse("content://org.omnirom.omnijaws.provider/settings");

       final String[] SETTINGS_PROJECTION = new String[] {
           "enabled"
       };

       final Cursor c = getContentResolver().query(SETTINGS_URI, SETTINGS_PROJECTION,
               null, null, null);
       if (c != null) {
           int count = c.getCount();
           if (count == 1) {
               c.moveToPosition(0);
               boolean enabled = c.getInt(0) == 1;
               return enabled;
           }
       }
       return true;
   }

   private void enableStatusBarBatteryDependents() {
         mBatterySaverColor.setEnabled(true);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }

}

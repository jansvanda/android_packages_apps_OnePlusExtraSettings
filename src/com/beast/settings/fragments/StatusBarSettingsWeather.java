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

public class StatusBarSettingsWeather extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    
    private static final String TAG = "StatusBarWeather";

    private LinearLayout mView;

    private static final String STATUS_BAR_TEMPERATURE = "status_bar_temperature";
    private static final String STATUS_BAR_TEMPERATURE_STYLE = "status_bar_temperature_style";
    private static final String PREF_STATUS_BAR_WEATHER_SIZE = "status_bar_weather_size";
    private static final String PREF_STATUS_BAR_WEATHER_FONT_STYLE = "status_bar_weather_font_style";
	private static final String PREF_STATUS_BAR_WEATHER_COLOR = "status_bar_weather_color";
	private static final String PREF_STATUS_BAR_WEATHER_IMAGE_COLOR = "status_bar_weather_image_color";

	private static final String CATEGORY_WEATHER = "weather_category";
	private static final String WEATHER_ICON_PACK = "weather_icon_pack";
	private static final String DEFAULT_WEATHER_ICON_PACKAGE = "org.omnirom.omnijaws";
	private static final String WEATHER_SERVICE_PACKAGE = "org.omnirom.omnijaws";
	private static final String CHRONUS_ICON_PACK_INTENT = "com.dvtonder.chronus.ICON_PACK";

    private ListPreference mStatusBarTemperature;
    private ListPreference mStatusBarTemperatureStyle;
    private CustomSeekBarPreference mStatusBarTemperatureSize;
    private ListPreference mStatusBarTemperatureFontStyle;
    private ColorPickerPreference mStatusBarTemperatureColor;
    private ColorPickerPreference mStatusBarTemperatureImageColor;

    private PreferenceCategory mWeatherCategory;
    private ListPreference mWeatherIconPack;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_statusbar_weather);

        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

           int intColor;
        String hexColor;

        mStatusBarTemperature = (ListPreference) findPreference(STATUS_BAR_TEMPERATURE);
        int temperatureShow = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0,
                UserHandle.USER_CURRENT);
        mStatusBarTemperature.setValue(String.valueOf(temperatureShow));
        mStatusBarTemperature.setSummary(mStatusBarTemperature.getEntry());
        mStatusBarTemperature.setOnPreferenceChangeListener(this);

        mStatusBarTemperatureStyle = (ListPreference) findPreference(STATUS_BAR_TEMPERATURE_STYLE);
        int temperatureStyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_WEATHER_TEMP_STYLE, 0,
                UserHandle.USER_CURRENT);
        mStatusBarTemperatureStyle.setValue(String.valueOf(temperatureStyle));
        mStatusBarTemperatureStyle.setSummary(mStatusBarTemperatureStyle.getEntry());
        mStatusBarTemperatureStyle.setOnPreferenceChangeListener(this);

        mStatusBarTemperatureSize = (CustomSeekBarPreference) findPreference(PREF_STATUS_BAR_WEATHER_SIZE);
        mStatusBarTemperatureSize.setValue(Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_WEATHER_SIZE, 14));
        mStatusBarTemperatureSize.setOnPreferenceChangeListener(this);

        mStatusBarTemperatureFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_WEATHER_FONT_STYLE);
        mStatusBarTemperatureFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarTemperatureFontStyle.setValue(Integer.toString(Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_WEATHER_FONT_STYLE, 0)));
        mStatusBarTemperatureFontStyle.setSummary(mStatusBarTemperatureFontStyle.getEntry());

        mStatusBarTemperatureColor =
            (ColorPickerPreference) findPreference(PREF_STATUS_BAR_WEATHER_COLOR);
        mStatusBarTemperatureColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_WEATHER_COLOR, 0xffffffff);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
            mStatusBarTemperatureColor.setSummary(hexColor);
            mStatusBarTemperatureColor.setNewPreviewColor(intColor);

        mStatusBarTemperatureImageColor =
            (ColorPickerPreference) findPreference(PREF_STATUS_BAR_WEATHER_IMAGE_COLOR);
        mStatusBarTemperatureImageColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_WEATHER_IMAGE_COLOR, 0xffffffff);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mStatusBarTemperatureImageColor.setSummary(hexColor);
        mStatusBarTemperatureImageColor.setNewPreviewColor(intColor);

        initweathercat();
        updateWeatherOptions();   
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mStatusBarTemperature) {
            int temperatureShow = Integer.valueOf((String) objValue);
            int index = mStatusBarTemperature.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(
                    getContentResolver(), Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, temperatureShow,
                    UserHandle.USER_CURRENT);
            mStatusBarTemperature.setSummary(
                    mStatusBarTemperature.getEntries()[index]);
            updateWeatherOptions();
            return true;
        } else if (preference == mStatusBarTemperatureStyle) {
            int temperatureStyle = Integer.valueOf((String) objValue);
            int index = mStatusBarTemperatureStyle.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(
                    getContentResolver(), Settings.System.STATUS_BAR_WEATHER_TEMP_STYLE, temperatureStyle,
                    UserHandle.USER_CURRENT);
            mStatusBarTemperatureStyle.setSummary(
                    mStatusBarTemperatureStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarTemperatureSize) {
            int width = ((Integer)objValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_WEATHER_SIZE, width);
            return true;
        } else if (preference == mStatusBarTemperatureFontStyle) {
            int val = Integer.parseInt((String) objValue);
            int index = mStatusBarTemperatureFontStyle.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_WEATHER_FONT_STYLE, val);
            mStatusBarTemperatureFontStyle.setSummary(mStatusBarTemperatureFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarTemperatureColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_WEATHER_COLOR, intHex);
            return true;
        } else if (preference == mStatusBarTemperatureImageColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_WEATHER_IMAGE_COLOR, intHex);
            return true;
        }
        return false;
    }

    private void updateWeatherOptions() {
        ContentResolver resolver = getActivity().getContentResolver();
        int status = Settings.System.getInt(
                resolver, Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0);
        if (status == 0) {
            mStatusBarTemperatureStyle.setEnabled(false);
            mStatusBarTemperatureColor.setEnabled(false);
            mStatusBarTemperatureSize.setEnabled(false);
            mStatusBarTemperatureFontStyle.setEnabled(false);
            mStatusBarTemperatureImageColor.setEnabled(false);
        } else if (status == 1 || status == 2){
            mStatusBarTemperatureStyle.setEnabled(true);
            mStatusBarTemperatureColor.setEnabled(true);
            mStatusBarTemperatureSize.setEnabled(true);
            mStatusBarTemperatureFontStyle.setEnabled(true);
            mStatusBarTemperatureImageColor.setEnabled(true);
        } else if (status == 3 || status == 4) {
            mStatusBarTemperatureStyle.setEnabled(true);
            mStatusBarTemperatureColor.setEnabled(true);
            mStatusBarTemperatureSize.setEnabled(true);
            mStatusBarTemperatureFontStyle.setEnabled(true);
            mStatusBarTemperatureImageColor.setEnabled(false);
        } else if (status == 5) {
            mStatusBarTemperatureStyle.setEnabled(true);
            mStatusBarTemperatureColor.setEnabled(false);
            mStatusBarTemperatureSize.setEnabled(false);
            mStatusBarTemperatureFontStyle.setEnabled(false);
            mStatusBarTemperatureImageColor.setEnabled(true);
        }
    }

   public void initweathercat() {
        mWeatherCategory = (PreferenceCategory) getPreferenceScreen().findPreference(CATEGORY_WEATHER);
             if (mWeatherCategory != null && !isOmniJawsServiceInstalled()) {
             getPreferenceScreen().removePreference(mWeatherCategory);
             } else {
             String settingsJaws = Settings.System.getString(getContentResolver(),
                     Settings.System.OMNIJAWS_WEATHER_ICON_PACK);
             if (settingsJaws == null) {
                 settingsJaws = DEFAULT_WEATHER_ICON_PACKAGE;
             }
             mWeatherIconPack = (ListPreference) findPreference(WEATHER_ICON_PACK);
 
             List<String> entriesJaws = new ArrayList<String>();
             List<String> valuesJaws = new ArrayList<String>();
             getAvailableWeatherIconPacks(entriesJaws, valuesJaws);
             mWeatherIconPack.setEntries(entriesJaws.toArray(new String[entriesJaws.size()]));
             mWeatherIconPack.setEntryValues(valuesJaws.toArray(new String[valuesJaws.size()]));
 
             int valueJawsIndex = mWeatherIconPack.findIndexOfValue(settingsJaws);
             if (valueJawsIndex == -1) {
                 // no longer found
                 settingsJaws = DEFAULT_WEATHER_ICON_PACKAGE;
                 Settings.System.putString(getContentResolver(),
                         Settings.System.OMNIJAWS_WEATHER_ICON_PACK, settingsJaws);
                 valueJawsIndex = mWeatherIconPack.findIndexOfValue(settingsJaws);
             }
             mWeatherIconPack.setValueIndex(valueJawsIndex >= 0 ? valueJawsIndex : 0);
             mWeatherIconPack.setSummary(mWeatherIconPack.getEntry());
             mWeatherIconPack.setOnPreferenceChangeListener(this);
          }
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

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }

}

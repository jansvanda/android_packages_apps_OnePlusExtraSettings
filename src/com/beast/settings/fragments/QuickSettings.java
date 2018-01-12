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
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.text.TextUtils;
import android.view.View;
import android.content.pm.ResolveInfo;
import android.support.v7.preference.ListPreference;
import com.android.internal.util.beast.BeastUtils;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beast.settings.preferences.SystemSettingSwitchPreference;
import com.beast.settings.preferences.CustomSeekBarPreference;

public class QuickSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    private static final String CUSTOM_HEADER_BROWSE = "custom_header_browse";
    private static final String CUSTOM_HEADER_IMAGE = "status_bar_custom_header";
    private static final String DAYLIGHT_HEADER_PACK = "daylight_header_pack";
    private static final String CUSTOM_HEADER_IMAGE_SHADOW = "status_bar_custom_header_shadow";
    private static final String CUSTOM_HEADER_PROVIDER = "custom_header_provider";
    private static final String STATUS_BAR_CUSTOM_HEADER = "status_bar_custom_header";
    private static final String CUSTOM_HEADER_ENABLED = "status_bar_custom_header";

    private ListPreference mSmartPulldown;
    private Preference mHeaderBrowse;
    private ListPreference mDaylightHeaderPack;
    private CustomSeekBarPreference mHeaderShadow;
    private ListPreference mHeaderProvider;
    private String mDaylightHeaderProvider;
    private SystemSettingSwitchPreference mHeaderEnabled;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_quicksettings);

        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
        mSmartPulldown.setOnPreferenceChangeListener(this);
        int smartPulldown = Settings.System.getInt(resolver,
                Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);

         mHeaderBrowse = findPreference(CUSTOM_HEADER_BROWSE);
         mHeaderBrowse.setEnabled(isBrowseHeaderAvailable());
         mHeaderEnabled = (SystemSettingSwitchPreference) findPreference(CUSTOM_HEADER_ENABLED);
         mHeaderEnabled.setOnPreferenceChangeListener(this);
 
         mDaylightHeaderPack = (ListPreference) findPreference(DAYLIGHT_HEADER_PACK);
 
         List<String> entries = new ArrayList<String>();
         List<String> values = new ArrayList<String>();
         getAvailableHeaderPacks(entries, values);
         mDaylightHeaderPack.setEntries(entries.toArray(new String[entries.size()]));
         mDaylightHeaderPack.setEntryValues(values.toArray(new String[values.size()]));
 
         boolean headerEnabled = Settings.System.getInt(getContentResolver(),
                 Settings.System.STATUS_BAR_CUSTOM_HEADER, 0) != 0;
         updateHeaderProviderSummary(headerEnabled);
         mDaylightHeaderPack.setOnPreferenceChangeListener(this);
 
         mHeaderShadow = (CustomSeekBarPreference) findPreference(CUSTOM_HEADER_IMAGE_SHADOW);
         final int headerShadow = Settings.System.getInt(getContentResolver(),
                 Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, 0);
         mHeaderShadow.setValue((int)(((double) headerShadow / 255) * 100));
         mHeaderShadow.setOnPreferenceChangeListener(this);
 
         mDaylightHeaderProvider = getResources().getString(R.string.daylight_header_provider);
         String providerName = Settings.System.getString(getContentResolver(),
                 Settings.System.STATUS_BAR_CUSTOM_HEADER_PROVIDER);
         if (providerName == null) {
             providerName = mDaylightHeaderProvider;
         }
         mHeaderProvider = (ListPreference) findPreference(CUSTOM_HEADER_PROVIDER);
         int valueIndex = mHeaderProvider.findIndexOfValue(providerName);
         mHeaderProvider.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
         mHeaderProvider.setSummary(mHeaderProvider.getEntry());
         mHeaderProvider.setOnPreferenceChangeListener(this);
         mDaylightHeaderPack.setEnabled(providerName.equals(mDaylightHeaderProvider));
     }
 
     private void updateHeaderProviderSummary(boolean headerEnabled) {
         mDaylightHeaderPack.setSummary(getResources().getString(R.string.header_provider_disabled));
         if (headerEnabled) {
             String settingHeaderPackage = Settings.System.getString(getContentResolver(),
                     Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK);
             int valueIndex = mDaylightHeaderPack.findIndexOfValue(settingHeaderPackage);
             if (valueIndex == -1) {
                 // no longer found
                 Settings.System.putInt(getContentResolver(),
                         Settings.System.STATUS_BAR_CUSTOM_HEADER, 0);
             } else {
                 mDaylightHeaderPack.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
                 mDaylightHeaderPack.setSummary(mDaylightHeaderPack.getEntry());
             }
         }
     }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
            	    Settings.System.QS_SMART_PULLDOWN, smartPulldown);
            updateSmartPulldownSummary(smartPulldown);
        } else if (preference == mDaylightHeaderPack) {
            String value = (String) newValue;
            Settings.System.putString(getContentResolver(),
                    Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK, value);
            int valueIndex = mDaylightHeaderPack.findIndexOfValue(value);
            mDaylightHeaderPack.setSummary(mDaylightHeaderPack.getEntries()[valueIndex]);
        } else if (preference == mHeaderShadow) {
            Integer headerShadow = (Integer) newValue;
            int realHeaderValue = (int) (((double) headerShadow / 100) * 255);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, realHeaderValue);
        } else if (preference == mHeaderProvider) {
            String value = (String) newValue;
            Settings.System.putString(getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_PROVIDER, value);
            int valueIndex = mHeaderProvider.findIndexOfValue(value);
            mHeaderProvider.setSummary(mHeaderProvider.getEntries()[valueIndex]);
            mDaylightHeaderPack.setEnabled(value.equals(mDaylightHeaderProvider));
            mHeaderBrowse.setTitle(valueIndex == 0 ? R.string.custom_header_browse_title : R.string.custom_header_pick_title);
            mHeaderBrowse.setSummary(valueIndex == 0 ? R.string.custom_header_browse_summary_new : R.string.custom_header_pick_summary);
        } else if (preference == mHeaderEnabled) {
            Boolean headerEnabled = (Boolean) newValue;
            updateHeaderProviderSummary(headerEnabled);
        }
        return true;
    }

    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else if (value == 3) {
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_none_summary));
        } else {
            String type = res.getString(value == 1
                    ? R.string.smart_pulldown_dismissable
                    : R.string.smart_pulldown_ongoing);
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
    }

    private boolean isBrowseHeaderAvailable() {
        PackageManager pm = getPackageManager();
        Intent browse = new Intent();
        browse.setClassName("org.omnirom.omnistyle", "org.omnirom.omnistyle.PickHeaderActivity");
        return pm.resolveActivity(browse, 0) != null;
    }

    private void getAvailableHeaderPacks(List<String> entries, List<String> values) {
        Map<String, String> headerMap = new HashMap<String, String>();
        Intent i = new Intent();
        PackageManager packageManager = getPackageManager();
        i.setAction("org.omnirom.DaylightHeaderPack");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            String label = r.activityInfo.loadLabel(getPackageManager()).toString();
            if (label == null) {
                label = r.activityInfo.packageName;
            }
            headerMap.put(label, packageName);
        }
        i.setAction("org.omnirom.DaylightHeaderPack1");
        for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
            String packageName = r.activityInfo.packageName;
            String label = r.activityInfo.loadLabel(getPackageManager()).toString();
            if (r.activityInfo.name.endsWith(".theme")) {
                continue;
            }
            if (label == null) {
                label = packageName;
            }
            headerMap.put(label, packageName  + "/" + r.activityInfo.name);
        }
        List<String> labelList = new ArrayList<String>();
        labelList.addAll(headerMap.keySet());
        Collections.sort(labelList);
        for (String label : labelList) {
            entries.add(label);
            values.add(headerMap.get(label));
        }
    }

    
    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }

}

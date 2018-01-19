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

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.beast.settings.preferences.CustomSeekBarPreference;
import com.beast.settings.preferences.SystemSettingSwitchPreference;
import com.android.settings.Utils;
import android.util.Log;

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
    private static final String TAG = "StatusbarBatteryStyle";
    private static final String KEY_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
 
    private static final String STATUS_BAR_BATTERY_SAVER_COLOR = "status_bar_battery_saver_color";
 
    private ColorPickerPreference mBatterySaverColor;
    private ListPreference mTickerMode;
    private ListPreference mLogoStyle;
    private ColorPickerPreference mStatusBarLogoColor;
    static final int DEFAULT_LOGO_COLOR = 0xff009688;
    private Preference mCustomCarrierLabel;
    private String mCustomCarrierLabelText;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_statusbar);

        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

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
        int intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_LOGO_COLOR, DEFAULT_LOGO_COLOR);
        String hexColor = String.format("#%08x", (DEFAULT_LOGO_COLOR & intColor));
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

       int batterySaverColor = Settings.Secure.getInt(resolver,
                 Settings.Secure.STATUS_BAR_BATTERY_SAVER_COLOR, 0xfff4511e);
         mBatterySaverColor = (ColorPickerPreference) findPreference("status_bar_battery_saver_color");
         mBatterySaverColor.setNewPreviewColor(batterySaverColor);
         mBatterySaverColor.setOnPreferenceChangeListener(this);

        mCustomCarrierLabel = (Preference) findPreference(KEY_CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();
 
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
        }
        return false;
    }

      @Override
      public boolean onPreferenceTreeClick(final Preference preference) {
          super.onPreferenceTreeClick(preference);
          final ContentResolver resolver = getActivity().getContentResolver();
          if (preference.getKey().equals(KEY_CUSTOM_CARRIER_LABEL) {
                if (preference.getKey().equals(KEY_CUSTOM_CARRIER_LABEL)) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle(R.string.custom_carrier_label_title);
                        alert.setMessage(R.string.custom_carrier_label_explain);
                
                        // Set an EditText view to get user input
                        final EditText input = new EditText(getActivity());
                        input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
                        input.setSelection(input.getText().length());
                        alert.setView(input);
                        alert.setPositiveButton(getString(android.R.string.ok),
                                new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        String value = ((Spannable) input.getText()).toString().trim();
                                        Settings.System.putString(resolver, Settings.System.CUSTOM_CARRIER_LABEL, value);
                                        updateCustomLabelTextSummary();
                                        Intent i = new Intent();
                                        i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                                        getActivity().sendBroadcast(i);
                                        }
                                });
                        alert.setNegativeButton(getString(android.R.string.cancel), null);
                        alert.show();
                        return true;
                 }
                 return true;
          }
      
          return false;
      }

   private void updateCustomLabelTextSummary() {
      mCustomCarrierLabelText = Settings.System.getString(
              getActivity().getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);
      if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
          mCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
      } else {
          mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
  }

   private void enableStatusBarBatteryDependents() {
         mBatterySaverColor.setEnabled(true);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }

}

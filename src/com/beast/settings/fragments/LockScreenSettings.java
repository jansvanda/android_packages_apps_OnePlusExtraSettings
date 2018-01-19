/*
 *  Copyright (C) 2015 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.beast.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.widget.SeekBarPreference;
import android.os.Handler;
import android.os.UserHandle;
import android.view.View;
import com.android.settings.Utils;

import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String FP_UNLOCK_KEYSTORE = "fp_unlock_keystore";
    private static final String LOCKSCREEN_MAX_NOTIF_CONFIG = "lockscreen_max_notif_cofig";
    private static final String LOCKSCREEN_VISUALIZATION = "lockscreen_visualizer";
    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";
    private static final String TAG = "LockscreenColorsFonts";
    private static final String LOCKSCREEN_OWNER_INFO_COLOR = "lockscreen_owner_info_color";
    private static final String LOCKSCREEN_ALARM_COLOR = "lockscreen_alarm_color";
    private static final String LOCKSCREEN_CLOCK_COLOR = "lockscreen_clock_color";
    private static final String LOCKSCREEN_CLOCK_DATE_COLOR = "lockscreen_clock_date_color";

    static final int DEFAULT = 0xffffffff;

    private ColorPickerPreference mLockscreenOwnerInfoColorPicker;
    private ColorPickerPreference mLockscreenAlarmColorPicker;
    private ColorPickerPreference mLockscreenClockColorPicker;
    private ColorPickerPreference mLockscreenClockDateColorPicker;
    ListPreference mLockClockFonts;

    private FingerprintManager mFingerprintManager;
    private SwitchPreference mFingerprintVib;
    private SwitchPreference mFpKeystore;
    private SwitchPreference mVisualization;
    private SeekBarPreference mMaxKeyguardNotifConfig;
    ListPreference mLockClockFonts;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.beast_settings_lockscreen);

        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        Resources resources = getResources();

       int intColor;
       String hexColor;

      mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
      mFpKeystore = (SwitchPreference) findPreference(FP_UNLOCK_KEYSTORE);
      if (!mFingerprintManager.isHardwareDetected()){
          prefScreen.removePreference(mFpKeystore);
      } else {
      mFpKeystore.setChecked((Settings.System.getInt(getContentResolver(),
              Settings.System.FP_UNLOCK_KEYSTORE, 0) == 1));
      mFpKeystore.setOnPreferenceChangeListener(this);
      }

      mMaxKeyguardNotifConfig = (SeekBarPreference) findPreference(LOCKSCREEN_MAX_NOTIF_CONFIG);
      int kgconf = Settings.System.getInt(getContentResolver(),
               Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, 3);
      mMaxKeyguardNotifConfig.setProgress(kgconf);
      mMaxKeyguardNotifConfig.setOnPreferenceChangeListener(this);

        mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintVib = (SwitchPreference) findPreference(FINGERPRINT_VIB);
        if (mFingerprintManager == null){
            prefScreen.removePreference(mFingerprintVib);
        } else {
        mFingerprintVib.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.FINGERPRINT_SUCCESS_VIB, 1) == 1));
        mFingerprintVib.setOnPreferenceChangeListener(this);
        }

         mVisualization = (SwitchPreference) findPreference(LOCKSCREEN_VISUALIZATION);
         if (mVisualization == null){
             prefScreen.removePreference(mVisualization);
         } else {
              mVisualization.setChecked((Settings.System.getInt(getContentResolver(),
              Settings.System.LOCKSCREEN_VISUALIZER_ENABLED, 0) == 1));
                mVisualization.setOnPreferenceChangeListener(this);
         }
    
      mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 0)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);

     mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
     mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
             getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 4)));
     mLockClockFonts.setSummary(mLockClockFonts.getEntry());
     mLockClockFonts.setOnPreferenceChangeListener(this);
     mLockscreenOwnerInfoColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_OWNER_INFO_COLOR);
     mLockscreenOwnerInfoColorPicker.setOnPreferenceChangeListener(this);
     intColor = Settings.System.getInt(getContentResolver(),
                 Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, DEFAULT);
     hexColor = String.format("#%08x", (0xffffffff & intColor));
     mLockscreenOwnerInfoColorPicker.setSummary(hexColor);
     mLockscreenOwnerInfoColorPicker.setNewPreviewColor(intColor);
     mLockscreenAlarmColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_ALARM_COLOR);
     mLockscreenAlarmColorPicker.setOnPreferenceChangeListener(this);
     intColor = Settings.System.getInt(getContentResolver(),
                 Settings.System.LOCKSCREEN_ALARM_COLOR, DEFAULT);
     hexColor = String.format("#%08x", (0xffffffff & intColor));
     mLockscreenAlarmColorPicker.setSummary(hexColor);
     mLockscreenAlarmColorPicker.setNewPreviewColor(intColor);
     mLockscreenClockColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CLOCK_COLOR);
     mLockscreenClockColorPicker.setOnPreferenceChangeListener(this);
     intColor = Settings.System.getInt(getContentResolver(),
                 Settings.System.LOCKSCREEN_CLOCK_COLOR, DEFAULT);
     hexColor = String.format("#%08x", (0xffffffff & intColor));
     mLockscreenClockColorPicker.setSummary(hexColor);
     mLockscreenClockColorPicker.setNewPreviewColor(intColor);
     mLockscreenClockDateColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CLOCK_DATE_COLOR);
     mLockscreenClockDateColorPicker.setOnPreferenceChangeListener(this);
     intColor = Settings.System.getInt(getContentResolver(),
                 Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, DEFAULT);
     hexColor = String.format("#%08x", (0xffffffff & intColor));
     mLockscreenClockDateColorPicker.setSummary(hexColor);
     mLockscreenClockDateColorPicker.setNewPreviewColor(intColor);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mFingerprintVib) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.FINGERPRINT_SUCCESS_VIB, value ? 1 : 0);
            return true;
        }  else if (preference == mFpKeystore) {
             boolean value = (Boolean) newValue;
             Settings.System.putInt(getActivity().getContentResolver(),
                     Settings.System.FP_UNLOCK_KEYSTORE, value ? 1 : 0);
             return true;
        } else if (preference == mMaxKeyguardNotifConfig) {
            int kgconf = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, kgconf);
            return true;
        }  else if (preference == mVisualization) {
             boolean value = (Boolean) newValue;
             Settings.System.putInt(getActivity().getContentResolver(),
                     Settings.System.LOCKSCREEN_VISUALIZER_ENABLED, value ? 1 : 0);
             return true;
         } else if (preference == mLockClockFonts) {
             Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CLOCK_FONTS,
                     Integer.valueOf((String) newValue));
             mLockClockFonts.setValue(String.valueOf(newValue));
             mLockClockFonts.setSummary(mLockClockFonts.getEntry());
             return true;
         } else  if (preference == mLockscreenOwnerInfoColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenAlarmColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_ALARM_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenClockColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenClockDateColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, intHex);
            return true;
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }

}
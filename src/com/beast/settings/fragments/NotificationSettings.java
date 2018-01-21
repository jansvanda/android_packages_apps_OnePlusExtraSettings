package com.beast.settings.fragments;

import com.android.internal.logging.nano.MetricsProto;

import android.os.Bundle;
import com.android.settings.R;
import android.content.Context;
import android.os.UserHandle;
import android.content.ContentResolver;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.SettingsPreferenceFragment;

import com.beast.settings.preferences.Utils;

public class NotificationSettings extends SettingsPreferenceFragment {

    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";
    private Preference mChargingLeds;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.beast_settings_notifications);

        PreferenceScreen prefScreen = getPreferenceScreen();

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!Utils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }

        mChargingLeds = (Preference) findPreference("charging_light");
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefScreen.removePreference(mChargingLeds);
        }

    }

    public static void reset(Context mContext) {
         ContentResolver resolver = mContext.getContentResolver();
         Settings.System.putIntForUser(resolver,
                 Settings.System.FORCE_EXPANDED_NOTIFICATIONS, 0, UserHandle.USER_CURRENT);
      }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }
}

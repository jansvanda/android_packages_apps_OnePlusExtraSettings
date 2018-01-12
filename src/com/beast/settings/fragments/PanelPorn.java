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

public class PanelPorn extends SettingsPreferenceFragment implements
OnPreferenceChangeListener {

    // private ListPreference mVolumeDialogStroke;
    // private Preference mVolumeDialogStrokeColor;
    // private Preference mVolumeDialogStrokeThickness;
    // private Preference mVolumeDialogDashWidth;
    // private Preference mVolumeDialogDashGap;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

    // addPreferencesFromResource(R.xml.beast_settings_transparent);

    //  mVolumeDialogStroke =
    //           (ListPreference) findPreference(Settings.System.VOLUME_DIALOG_STROKE);
    //   mVolumeDialogStroke.setOnPreferenceChangeListener(this);
    //   mVolumeDialogStrokeColor = findPreference(Settings.System.VOLUME_DIALOG_STROKE_COLOR);
    //   mVolumeDialogStrokeThickness =
    //           findPreference(Settings.System.VOLUME_DIALOG_STROKE_THICKNESS);
    //   mVolumeDialogDashWidth = findPreference(Settings.System.VOLUME_DIALOG_STROKE_DASH_WIDTH);
    //   mVolumeDialogDashGap = findPreference(Settings.System.VOLUME_DIALOG_STROKE_DASH_GAP);
    //   updateVolumeDialogDependencies(mVolumeDialogStroke.getValue());

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        // if (preference == mVolumeDialogStroke) {
        //     updateVolumeDialogDependencies((String) objValue);
        //     return true;
        // } else {
        //     return false;
        // }
    }

    private void updateVolumeDialogDependencies(String volumeDialogStroke) {
    //   if (volumeDialogStroke.equals("0")) {
    //       mVolumeDialogStrokeColor.setEnabled(false);
    //       mVolumeDialogStrokeThickness.setEnabled(false);
    //       mVolumeDialogDashWidth.setEnabled(false);
    //       mVolumeDialogDashGap.setEnabled(false);
    //   } else if (volumeDialogStroke.equals("1")) {
    //       mVolumeDialogStrokeColor.setEnabled(false);
    //       mVolumeDialogStrokeThickness.setEnabled(true);
    //       mVolumeDialogDashWidth.setEnabled(true);
    //       mVolumeDialogDashGap.setEnabled(true);
    //   } else {
    //       mVolumeDialogStrokeColor.setEnabled(true);
    //       mVolumeDialogStrokeThickness.setEnabled(true);
    //       mVolumeDialogDashWidth.setEnabled(true);
    //       mVolumeDialogDashGap.setEnabled(true);
    //   }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BEAST_SETTINGS;
    }
}

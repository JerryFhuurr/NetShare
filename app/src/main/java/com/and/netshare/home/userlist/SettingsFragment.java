package com.and.netshare.home.userlist;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.and.netshare.R;
import com.and.netshare.home.homepage.HomePageSettingsViewModel;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private HomePageSettingsViewModel hpViewModel;
    private ListPreference themeList;
    private ListPreference languageList;
    private SwitchPreferenceCompat image_loadType;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        initView();
    }

    private void initView() {
        hpViewModel = new HomePageSettingsViewModel();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());

        themeList = getPreferenceManager().findPreference("theme_type");
        themeList.setValue(sharedPreferences.getString("theme_type", "follow_system"));
        themeList.setOnPreferenceChangeListener(this);

        image_loadType = getPreferenceManager().findPreference("image_dateType");
        image_loadType.setOnPreferenceChangeListener(this);
        boolean imageType = sharedPreferences.getBoolean("image_dateType", false);
        if (imageType) {
            image_loadType.setChecked(true);
        } else {
            image_loadType.setChecked(false);
        }

        languageList = getPreferenceManager().findPreference("language_setting");
        languageList.setValue(sharedPreferences.getString("language_setting", "English"));
        languageList.setOnPreferenceChangeListener(this);
    }


    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        if (themeList == preference) {
            switch (newValue.toString()) {
                case "follow_system":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    getActivity().recreate();
                    break;
                case "light":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getActivity().recreate();
                    break;
                case "dark":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getActivity().recreate();
                    break;
            }
            return true;
        } else if (image_loadType == preference) {
            hpViewModel.setSingle(image_loadType.isChecked());
            return true;
        }
        return false;
    }
}
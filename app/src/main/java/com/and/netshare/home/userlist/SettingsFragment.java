package com.and.netshare.home.userlist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.and.netshare.R;
import com.and.netshare.home.homepage.HomePageSettingsViewModel;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    private HomePageSettingsViewModel hpViewModel;
    private ListPreference themeList;
    private SwitchPreferenceCompat view_type;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        initView();
    }

    private void initView() {
        hpViewModel = new HomePageSettingsViewModel();
        themeList = getPreferenceManager().findPreference("theme_type");
        assert themeList != null;
        themeList.setOnPreferenceChangeListener(this);
        themeList.setValue("follow_system");

        view_type = getPreferenceManager().findPreference("type_row");
        assert view_type != null;
        view_type.setOnPreferenceChangeListener(this);
        view_type.setChecked(false);
    }


    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        if (themeList == preference) {
            switch (newValue.toString()) {
                case "follow_system":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case "light":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "dark":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
            }
            return true;
        } else if (view_type == preference) {
            hpViewModel.setSingle(view_type.isChecked());
            return true;
        }
        return false;
    }
}
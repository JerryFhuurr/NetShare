package com.and.netshare.home.homepage;

import androidx.lifecycle.ViewModel;

public class HomePageSettingsViewModel extends ViewModel {
    private HomePageBasicSettings settings;

    public HomePageSettingsViewModel() {
        this.settings = new HomePageBasicSettings();
    }

    public HomePageBasicSettings getSettings() {
        return settings;
    }

    public void setSettings(HomePageBasicSettings settings) {
        this.settings = settings;
    }

    public void setDisplayName(boolean name) {
        settings.setDisplayName(name);
    }

    public void setSingle(boolean single) {
        settings.setSingle(single);
    }

    public boolean getDisplayNameSet() {
        return settings.isDisplayName();
    }

    public boolean isSingleSet() {
        return settings.isSingle();
    }
}

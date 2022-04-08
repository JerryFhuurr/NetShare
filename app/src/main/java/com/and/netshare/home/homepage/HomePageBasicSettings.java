package com.and.netshare.home.homepage;

public class HomePageBasicSettings {
    private boolean displayName;
    private boolean isSingle;

    public HomePageBasicSettings() {
        this.displayName = false;
        this.isSingle = false;
    }

    public boolean isDisplayName() {
        return displayName;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setDisplayName(boolean displayName) {
        this.displayName = displayName;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

}

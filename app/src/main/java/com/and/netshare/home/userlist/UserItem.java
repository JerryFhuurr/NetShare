package com.and.netshare.home.userlist;

public class UserItem {
    private String title;
    private int iconId;

    UserItem(String title, int iconId){
        this.title = title;
        this.iconId = iconId;
    }

    public int getIconId() {
        return iconId;
    }

    public String getTitle() {
        return title;
    }
}

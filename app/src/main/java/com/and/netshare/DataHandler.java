package com.and.netshare;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class DataHandler {
    public static String changeDotToComaEmail(String email){
        return email.replace('.', ',');
    }

}

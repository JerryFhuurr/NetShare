package com.and.netshare.login.ui.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

public class LoginViewModel extends AndroidViewModel {

    private LoginInfoRep info;

    public LoginViewModel(Application app) {
        super(app);
        this.info = LoginInfoRep.getInstance(app);
    }

    public void setInfo(String email, String password, String type){
        info.setInfo(email, password, type);
    }
    public String getEmail() {
        return info.getInfo(0);
    }

    public String getPassword() {
        return info.getInfo(1);
    }

    public String getLoginType() {
        return info.getInfo(2);
    }
}
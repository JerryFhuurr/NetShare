package com.and.netshare.login.ui.main;

import androidx.lifecycle.ViewModel;
public class LoginViewModel extends ViewModel {

    private static String loginType = "";

    private LoginInfo info;

    public LoginViewModel() {
        this.info = new LoginInfo();
    }

    public void setEmail(String email) {
        info.setEmail(email);
    }

    public void setPassword(String password) {
        info.setPassword(password);
    }

    public String getEmail() {
        return info.getEmail();
    }

    public String getPassword() {
        return info.getPassword();
    }

    public static void setLoginType(String loginType) {
        LoginViewModel.loginType = loginType;
    }

    public static String getLoginType() {
        return loginType;
    }
}
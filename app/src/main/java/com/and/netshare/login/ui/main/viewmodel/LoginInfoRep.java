package com.and.netshare.login.ui.main.viewmodel;

import android.app.Application;

public class LoginInfoRep {
    private final LoginInfo currentInfo;
    private final Application app;
    private static LoginInfoRep instance;

    private LoginInfoRep(Application app) {
        this.app = app;
        currentInfo = new LoginInfo();
    }

    public static synchronized LoginInfoRep getInstance(Application app) {
        if (instance == null) {
            instance = new LoginInfoRep(app);
        }
        return instance;
    }

    public void setInfo(String email, String password, String type) {
        if (type.equals("google")) {
            currentInfo.setPassword("");
        } else {
            currentInfo.setPassword(password);
        }
        currentInfo.setEmail(email);
        currentInfo.setType(type);
    }

    public void logout() {
        currentInfo.clear();
    }

    public String getInfo(int c) {
        switch (c) {
            case 0:
                return currentInfo.getEmail();
            case 1:
                return currentInfo.getPassword();
            case 2:
                return currentInfo.getType();
            default:
                return "";
        }
    }
}

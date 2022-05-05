package com.and.netshare.login.ui.main.viewmodel;

public class LoginInfo {
    private String email;
    private String password;
    private String type;

    public LoginInfo() {
        this.email = "";
        this.password = "";
        this.type = "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void clear(){
        this.email = "";
        this.type = "";
        this.password = "";
    }
}

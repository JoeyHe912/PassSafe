package com.joeyhe.passwordmanager;

import android.app.Application;

/**
 * Created by HGY on 2017/7/20.
 */

public class PasswordManager extends Application {

    private String login;
    private String password;

    @Override
    public void onCreate() {
        super.onCreate();
        login = "";
        password = "";
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}

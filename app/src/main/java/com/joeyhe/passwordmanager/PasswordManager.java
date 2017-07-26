package com.joeyhe.passwordmanager;

import android.app.Application;

import com.joeyhe.passwordmanager.db.DatabaseHelper;

/**
 * Created by HGY on 2017/7/20.
 */

public class PasswordManager extends Application {

    private String login;
    private String password;
    private String iLogin;
    private String iPassword;

    @Override
    public void onCreate() {
        super.onCreate();
        login = "";
        password = "";
        iLogin = "";
        iPassword = "";
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

    public void setiLogin(String iLogin) {
        this.iLogin = iLogin;
    }

    public String getiLogin() {
        return iLogin;
    }

    public void setiPassword(String iPassword) {
        this.iPassword = iPassword;
    }

    public String getiPassword() {
        return iPassword;
    }

    @Override
    public void onTerminate() {
        if (DatabaseHelper.getInstance() != null) {
            DatabaseHelper.getInstance().closeDatabase();
        }
        super.onTerminate();
    }
}

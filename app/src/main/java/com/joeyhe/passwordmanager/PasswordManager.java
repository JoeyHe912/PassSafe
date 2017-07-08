package com.joeyhe.passwordmanager;

import android.app.Application;

import com.joeyhe.passwordmanager.db.DaoSession;

/**
 * Created by HGY on 2017/6/27.
 */

public class PasswordManager extends Application {
    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = true;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
//        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("MasterPasswor") : helper.getWritableDb();
//        daoSession = new DaoMaster(db).newSession();
    }
}

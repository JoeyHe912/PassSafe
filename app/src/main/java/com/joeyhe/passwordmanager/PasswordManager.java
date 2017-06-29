package com.joeyhe.passwordmanager;

import android.app.Application;

import com.joeyhe.passwordmanager.models.DaoMaster;
import com.joeyhe.passwordmanager.models.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by HGY on 2017/6/27.
 */

public class PasswordManager extends Application {
    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = false;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}

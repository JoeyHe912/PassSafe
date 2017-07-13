package com.joeyhe.passwordmanager.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

/**
 * Created by HGY on 2017/7/7.
 */

public class DatabaseHelper {
    private static DatabaseHelper instance;
    private DaoSession daoSession;
    private String masterPass;
    private Database db;

    public static void initDatabase(Context context, String masterPass) {
        instance = new DatabaseHelper(context, masterPass);
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    private DatabaseHelper(Context context, String masterPass) {
        this.masterPass = masterPass;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "password-notes-db");
        db = helper.getEncryptedWritableDb(masterPass);
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public String getMasterPass() {
        return masterPass;
    }

    public void changeMasterPass(String newPass) {
        db.execSQL(String.format("PRAGMA rekey = '%s'", newPass));
    }
}

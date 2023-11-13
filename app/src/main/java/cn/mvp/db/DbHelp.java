package cn.mvp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbHelp {

    private static DaoSession daoSession;

    public static void init(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "MDB.db");
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}

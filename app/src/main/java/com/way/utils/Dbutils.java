package com.way.utils;

import org.xutils.DbManager;

import java.io.File;

/**
 * Created by master on 2015-12-14.
 */
public class Dbutils {
    public static DbManager.DaoConfig getConfig(){
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("master_weather")
                .setDbDir(new File("/sdcard"))
                .setDbVersion(1)
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                    }
                });
        return daoConfig;
    }
}

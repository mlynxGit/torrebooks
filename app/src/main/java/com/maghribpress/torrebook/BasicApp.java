package com.maghribpress.torrebook;

import android.support.multidex.MultiDexApplication;

import com.maghribpress.torrebook.db.AppDatabase;

public class BasicApp extends MultiDexApplication {
    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        // Normal app init code...

        mAppExecutors = new AppExecutors();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }
}

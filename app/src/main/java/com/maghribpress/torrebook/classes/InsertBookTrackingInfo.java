package com.maghribpress.torrebook.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.maghribpress.torrebook.DataRepository;
import com.maghribpress.torrebook.db.AppDatabase;
import com.maghribpress.torrebook.db.entity.Tracking;

import java.lang.ref.WeakReference;

public class InsertBookTrackingInfo extends AsyncTask<Void, Void, Void> {
    WeakReference<AppDatabase> mDB;
    WeakReference<DataRepository> mRepository;
    WeakReference<Tracking> mTracking;
    public InsertBookTrackingInfo(WeakReference<Tracking> tracking, final WeakReference<AppDatabase> database) {
        mDB = database;
        mTracking = tracking;
        mRepository = new WeakReference<DataRepository>(DataRepository.getInstance(database.get()));

    }
    @Override
    protected Void doInBackground(Void... voids) {
        mRepository.get().insertTrackingInfo(mTracking.get());
        Log.d("TORRENTTAGS","Tracking Info Inserted :D");
        return null;
    }
}

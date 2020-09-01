package com.maghribpress.torrebook;

import android.app.Application;
import android.os.AsyncTask;

import com.maghribpress.torrebook.db.entity.BookTracking;

public class TrackingInfoAsync extends AsyncTask<Void,Void,BookTracking> {

    private OnTrackingInfoReceived monTrackingInfoReceived;
    private DataRepository mRepository;

    public TrackingInfoAsync(OnTrackingInfoReceived onTrackingInfoReceived, Application app) {
        this.monTrackingInfoReceived = onTrackingInfoReceived;
        mRepository = ((BasicApp) app).getRepository();
    }

    @Override
    protected BookTracking doInBackground(Void... voids) {
        return mRepository.getLastReadBookTrackings();
    }

    @Override
    protected void onPostExecute(BookTracking bookTracking) {
        monTrackingInfoReceived.TrackingInfoReceived(bookTracking);
    }
}

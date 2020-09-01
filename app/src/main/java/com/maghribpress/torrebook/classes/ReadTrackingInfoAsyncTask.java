package com.maghribpress.torrebook.classes;

import android.os.AsyncTask;

import com.maghribpress.torrebook.BasicApp;

import com.maghribpress.torrebook.TaskCompletedListener;

import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.db.entity.Tracking;
import com.maghribpress.torrebook.ui.MainActivity;


import java.lang.ref.WeakReference;

public class ReadTrackingInfoAsyncTask extends AsyncTask<Void, Void, Tracking> {
    private Book mBook;
    private WeakReference<MainActivity> mActivity;
    private TaskCompletedListener mTaskcl;
    public  ReadTrackingInfoAsyncTask(WeakReference<MainActivity> activity, Book book, TaskCompletedListener tcl) {
        mBook =book;
        mTaskcl=tcl;
        mActivity = activity;
    }
    @Override
    protected Tracking doInBackground(Void... Void) {
        if(mBook.getId()==-1) {
            return ((BasicApp) mActivity.get().getApplicationContext()).getRepository().getTrackingForImportedBook(mBook.getImportedid());
        }else {
            return ((BasicApp) mActivity.get().getApplicationContext()).getRepository().getTrackingForBook(mBook.getId());
        }

    }

    @Override
    protected void onPostExecute(Tracking tracking) {
        mTaskcl.onTaskComplete(mBook,tracking);
    }
}

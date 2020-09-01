package com.maghribpress.torrebook.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.maghribpress.torrebook.DataRepository;
import com.maghribpress.torrebook.db.AppDatabase;
import com.maghribpress.torrebook.db.entity.Book;

import java.lang.ref.WeakReference;

public class InsertBookOffline extends AsyncTask<Void, Void, Long> {
    WeakReference<AppDatabase> mDB;
    WeakReference<DataRepository> mRepository;
    WeakReference<Book> mBook;
    public InsertBookOffline(WeakReference<Book> book, final WeakReference<AppDatabase> database) {
        mDB = database;
        mBook = book;
        mRepository = new WeakReference<DataRepository>(DataRepository.getInstance(database.get()));
    }
    @Override
    protected Long doInBackground(Void... voids) {
        Log.d("TORRENTTAGS","do in background insert local");
        return mRepository.get().insertLocal(mBook.get());
    }

    @Override
    protected void onPostExecute(Long aLong) {
    }
}

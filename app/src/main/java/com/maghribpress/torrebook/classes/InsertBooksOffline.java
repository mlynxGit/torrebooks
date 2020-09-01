package com.maghribpress.torrebook.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.maghribpress.torrebook.DataRepository;
import com.maghribpress.torrebook.R;
import com.maghribpress.torrebook.db.AppDatabase;
import com.maghribpress.torrebook.db.entity.Book;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class InsertBooksOffline extends AsyncTask<Void, Void, List<Long>> {
    WeakReference<AppDatabase> mDB;
    WeakReference<DataRepository> mRepository;
    WeakReference<List<Book>> mBooks;
    WeakReference<Context> mContext;
    ProgressDialog progressDialog;
    public InsertBooksOffline(WeakReference<List<Book>> books, final WeakReference<AppDatabase> database,WeakReference<Context> contex) {
        mDB = database;
        mBooks = books;
        mRepository = new WeakReference<DataRepository>(DataRepository.getInstance(database.get()));
        mContext = contex;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mContext.get(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Importing...");
        progressDialog.show();
    }

    @Override
    protected List<Long> doInBackground(Void... voids) {
        Log.d("TORRENTTAGS","do in background insert local");
        List<Long> ids = new ArrayList<>();
        for (Book book: mBooks.get()) {
            ids.add(mRepository.get().insertLocal(book));
        }
        return ids;
    }

    @Override
    protected void onPostExecute(List<Long> aLong) {
        progressDialog.dismiss();
    }
}

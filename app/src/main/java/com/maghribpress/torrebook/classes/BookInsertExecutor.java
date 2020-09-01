package com.maghribpress.torrebook.classes;

import android.os.AsyncTask;

import com.maghribpress.torrebook.DataRepository;
import com.maghribpress.torrebook.OnBookInsertCompleted;
import com.maghribpress.torrebook.db.AppDatabase;
import com.maghribpress.torrebook.db.entity.Book;

public class BookInsertExecutor extends AsyncTask<Void, Void, Integer> implements OnBookInsertCompleted {
    private final AppDatabase mDatabase;
    DataRepository mRepository;
    private final Book mBook;
    private OnBookInsertCompleted mOnBookInsert;
    public BookInsertExecutor(final AppDatabase database, Book book, OnBookInsertCompleted bic) {
        mDatabase = database;
        mOnBookInsert = bic;
        mBook = book;
        mRepository = DataRepository.getInstance(mDatabase);
    }
    @Override
    protected Integer doInBackground(Void... voids) {
        mRepository.insertBookOnlineOffline(mBook,this);
        return 1;
    }

    @Override
    public void onInsertComplete(Book book) {
        mOnBookInsert.onInsertComplete(book);
    }
}

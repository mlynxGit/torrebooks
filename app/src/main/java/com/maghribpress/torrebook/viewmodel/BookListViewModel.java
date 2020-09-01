package com.maghribpress.torrebook.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.maghribpress.torrebook.BasicApp;
import com.maghribpress.torrebook.DataRepository;
import com.maghribpress.torrebook.ProgressListener;
import com.maghribpress.torrebook.db.entity.Book;

import java.util.List;

public class BookListViewModel extends AndroidViewModel {

    private final DataRepository mRepository;

    private final ProgressListener mPl;
    private final String mToken;
    private final int mPage;
    private  LiveData<List<Book>> mOfflineBooks;


    public BookListViewModel(Application application,String token,int page, ProgressListener pl) {
        super(application);
        //mObservableBooks = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        //mObservableBooks.setValue(null);
        mPl= pl;
        mToken=token;
        mPage=page;
        mRepository = ((BasicApp) application).getRepository();
        mOfflineBooks = mRepository.getBooksOffline(mPl);

        // observe the changes of the products from the database and forward them
        //mObservableBooks.addSource(books, mObservableBooks::setValue);
    }
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final DataRepository mRepository;
        private final ProgressListener mPl;
        private final String mToken;
        private final int mPage;

        public Factory(@NonNull Application application,String token,int page, ProgressListener pl) {
            mApplication = application;
            mPl=pl;
            mToken=token;
            mPage=page;
            mRepository = ((BasicApp) application).getRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new BookListViewModel(mApplication,mToken,mPage,mPl);
        }
    }
    public LiveData<List<Book>> getBooksOffline() {
        return  mOfflineBooks;
    }
}

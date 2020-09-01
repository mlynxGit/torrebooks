package com.maghribpress.torrebook.classes;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.masterwok.simpletorrentandroid.TorrentSession;

import java.lang.ref.WeakReference;

public class TorrentDownloader extends AsyncTask<Void, Void, Void> {
    WeakReference<Context> mcontext;
    WeakReference<TorrentSession> mSession;

    final String mMagnet;
    public TorrentDownloader(WeakReference<Context> context, String magnet,WeakReference<TorrentSession> session ) {
        mcontext = context;
        mMagnet = magnet;
        mSession = session;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        mSession.get().start(mcontext.get(), Uri.parse(mMagnet));
        return null;
    }
}

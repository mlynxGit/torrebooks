package com.maghribpress.torrebook;

import android.view.View;

import com.maghribpress.torrebook.db.entity.Book;

public interface TorrentDownloadListener {
    void StartDownload(Book book,String magnet, View view);
}

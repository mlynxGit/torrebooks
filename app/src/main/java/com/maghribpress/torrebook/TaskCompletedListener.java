package com.maghribpress.torrebook;

import com.maghribpress.torrebook.db.entity.Book;
import com.maghribpress.torrebook.db.entity.Tracking;

public interface TaskCompletedListener {
    public void onTaskComplete(Book book, Tracking tracking);
}

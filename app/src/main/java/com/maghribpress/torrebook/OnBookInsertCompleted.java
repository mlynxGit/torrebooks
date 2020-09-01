package com.maghribpress.torrebook;

import com.maghribpress.torrebook.db.entity.Book;

public interface OnBookInsertCompleted {
    public void onInsertComplete(Book book);
}

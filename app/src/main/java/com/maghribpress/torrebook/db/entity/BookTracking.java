package com.maghribpress.torrebook.db.entity;

import android.arch.persistence.room.Embedded;

public class BookTracking {
    @Embedded
    Book book;

    @Embedded(prefix = "trck_")
    Tracking tracking;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }
}

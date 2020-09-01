package com.maghribpress.torrebook.db.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "booksauthors", primaryKeys = {"bookid", "authorid"},
        foreignKeys = {@ForeignKey(entity = Author.class, parentColumns = "id", childColumns = "authorid",onDelete = CASCADE)},indices = {@Index("authorid")})
public class BookAuthor {
    private long bookid;
    private long authorid;

    public BookAuthor() {
    }

    public long getBookid() {
        return bookid;
    }

    public void setBookid(long bookid) {
        this.bookid = bookid;
    }

    public long getAuthorid() {
        return authorid;
    }

    public void setAuthorid(long authorid) {
        this.authorid = authorid;
    }
}

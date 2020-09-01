package com.maghribpress.torrebook.db.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "bookcategories", primaryKeys = {"bookid", "categoryid"},foreignKeys = {@ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryid",onDelete = CASCADE)},indices = {@Index("categoryid")})
public class BookCategory {
    private long bookid;
    private long categoryid;

    public BookCategory() {
    }


    public long getBookid() {
        return bookid;
    }

    public void setBookid(long bookid) {
        this.bookid = bookid;
    }

    public long getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(long categoryid) {
        this.categoryid = categoryid;
    }
}

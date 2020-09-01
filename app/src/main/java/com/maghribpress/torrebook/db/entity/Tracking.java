package com.maghribpress.torrebook.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.maghribpress.torrebook.db.converter.DateConverter;

@Entity(tableName = "tracking",indices = {@Index(value = {"bookid"},
        unique = true)})
@TypeConverters({DateConverter.class})
public class Tracking {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long bookid;
    private String bookimportid;
    private int currentPage;
    private long created_at;
    private String folioBookid;
    private String chapterHref;
    private boolean usingId;
    private String value;

    public Tracking() {
    }

    public String getBookimportid() {
        return bookimportid;
    }

    public void setBookimportid(String bookimportid) {
        this.bookimportid = bookimportid;
    }

    public String getFolioBookid() {
        return folioBookid;
    }

    public void setFolioBookid(String folioBookid) {
        this.folioBookid = folioBookid;
    }

    public String getChapterHref() {
        return chapterHref;
    }

    public void setChapterHref(String chapterHref) {
        this.chapterHref = chapterHref;
    }

    public boolean isUsingId() {
        return usingId;
    }

    public void setUsingId(boolean usingId) {
        this.usingId = usingId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getBookid() {
        return bookid;
    }

    public void setBookid(long bookid) {
        this.bookid = bookid;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
}
